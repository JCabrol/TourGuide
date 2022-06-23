package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.CloseAttraction;
import tourGuide.model.User;
import tourGuide.model.UserCloseAttractionsInfo;
import tourGuide.model.UserReward;
import tourGuide.repository.UserRepository;
import tourGuide.tracker.Tracker;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {

    private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final GpsUtilService gpsUtilService;
    private final RewardsService rewardsService;
    private final UserRepository userRepository;
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    ExecutorService executorServiceTourGuide = Executors.newCachedThreadPool();
    boolean testMode = true;
    public static final int USER_CONSUMER_COUNT = 50;

    public TourGuideService(GpsUtilService gpsUtilService, RewardsService rewardsService, UserRepository userRepository) {
        this.gpsUtilService = gpsUtilService;
        this.rewardsService = rewardsService;
        this.userRepository = userRepository;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(String userName) throws Exception {
        User user = getUser(userName);
        return trackUserLocation(user);
    }

    public VisitedLocation getUserLastVisitedLocation(User user) {
        VisitedLocation lastVisitedLocation = null;
        List<VisitedLocation> userVisitedLocationList = user.getVisitedLocations();
        if (userVisitedLocationList.size() != 0) {
            lastVisitedLocation = userVisitedLocationList
                    .stream()
                    .parallel()
                    .filter(visitedLocation -> visitedLocation.timeVisited.equals(user.getLatestLocationTimestamp()))
                    .collect(Collectors.toList())
                    .get(0);
        }
        return lastVisitedLocation;
    }

    public void addUserNewVisitedLocation(User user, VisitedLocation newVisitedLocation) {
        List<VisitedLocation> userVisitedLocationList = user.getVisitedLocations();
        userVisitedLocationList.add(newVisitedLocation);
        user.setVisitedLocations(userVisitedLocationList);
        user.setLatestLocationTimestamp(newVisitedLocation.timeVisited);
    }

    public UserCloseAttractionsInfo searchFiveClosestAttractions(VisitedLocation visitedLocation) throws ExecutionException, InterruptedException {
        List<Attraction> attractionsAndDistancesFromFiveClosestLocation = rewardsService.searchFiveClosestAttractionsMap(visitedLocation);
        UserCloseAttractionsInfo userCloseAttractionsInfo = new UserCloseAttractionsInfo(visitedLocation.location.latitude, visitedLocation.location.longitude);
        List<CloseAttraction> fiveClosestAttractions = new ArrayList<>();
        attractionsAndDistancesFromFiveClosestLocation
                .forEach((attraction)
                        -> fiveClosestAttractions.add(new CloseAttraction(attraction.attractionName, attraction.latitude, attraction.longitude, rewardsService.getDistance(attraction, visitedLocation.location), rewardsService.getAttractionRewardPoints(attraction.attractionId, visitedLocation.userId))));
        userCloseAttractionsInfo.setCloseAttractions(fiveClosestAttractions);
        return userCloseAttractionsInfo;
    }


    public User getUser(String userName) throws Exception {
        return userRepository.getUser(userName);
    }

    public List<User> getAllUsers() {
        return userRepository.getUserList();
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }

    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(User user) throws InterruptedException {
        UUID userId = user.getUserId();
        VisitedLocation visitedLocation = gpsUtilService.getUserLocation(userId);
        rewardsService.calculateRewards(user, visitedLocation);
        addUserNewVisitedLocation(user, visitedLocation);
        return visitedLocation;
    }

    public Map<String, Location> getAllCurrentLocations() {
        Map<String, Location> result = new HashMap<>();
        getAllUsers()
                .stream()
                .parallel()
                .forEach(user -> result.put(user.getUserId().toString(), getUserLastVisitedLocation(user).location));
        return result;
    }

    public List<VisitedLocation> trackAllUserLocation(List<User> allUsers) throws Exception {
        BlockingQueue<User> userQueue = new LinkedBlockingQueue<>();
        int userCount = InternalTestHelper.getInternalUserNumber();
        publishUser(allUsers, userQueue);
        CountDownLatch userLatch = new CountDownLatch(userCount);
        List<VisitedLocation> allVisitedLocations = calculateUserLocationFromUserQueue(userLatch, userQueue);
        userLatch.await();
        return allVisitedLocations;
    }

    private void publishUser(List<User> allUsers, BlockingQueue<User> userQueue) {
        allUsers
                .forEach(user -> {
                    try {
                        userQueue.put(user);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }

    private List<VisitedLocation> calculateUserLocationFromUserQueue(CountDownLatch userLatch, BlockingQueue<User> userQueue) {
       List<VisitedLocation> allVisitedLocations = new ArrayList<>();
        for (int futureNumber = 0; futureNumber < USER_CONSUMER_COUNT; futureNumber++) {
            executorServiceTourGuide.submit(() -> {
                try {
                    while (userLatch.getCount() != 0) {
                        User user = userQueue.take();
                        try {
                            VisitedLocation visitedLocation = trackUserLocation(user);
                            allVisitedLocations.add(visitedLocation);
                        } finally {
                            userLatch.countDown();
//                            System.out.println("Waiting for " + userLatch.getCount() + " users to finish");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return allVisitedLocations;
    }

//    public void calculateAllRewards(List<User> allUsers) throws Exception {
//        BlockingQueue<User> userQueue = new LinkedBlockingQueue<>();
//        int userCount = InternalTestHelper.getInternalUserNumber();
//        publishUser(allUsers, userQueue);
//        CountDownLatch userLatch = new CountDownLatch(userCount);
//        calculateRewardsFromUserQueue(userLatch, userQueue);
//        userLatch.await();
//    }
//
//    private void calculateRewardsFromUserQueue(CountDownLatch userLatch, BlockingQueue<User> userQueue) throws InterruptedException {
//        for (int futureNumber = 0; futureNumber < USER_CONSUMER_COUNT; futureNumber++) {
//            executorServiceTourGuide.submit(() -> {
//                try {
//                    while (userLatch.getCount() != 0) {
//                        User user = userQueue.take();
//                        try {
//                            rewardsService.calculateRewards2(user, getUserLastVisitedLocation(user));
//                        } finally {
//                            userLatch.countDown();
////                            System.out.println("Waiting for " + userLatch.getCount() + " users to finish");
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//        TimeUnit.MILLISECONDS.sleep(30000);
//    }


    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";

    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            userRepository.addUser(user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            addUserNewVisitedLocation(user, new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}