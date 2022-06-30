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
public class TourGuideServiceImpl implements TourGuideService {

    private final Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);
    private final GpsUtilServiceImpl gpsUtilService;
    private final RewardsServiceImpl rewardsService;
    private final UserRepository userRepository;
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    ExecutorService executorServiceTourGuide = Executors.newCachedThreadPool();
    boolean testMode = true;
    public static final int USER_CONSUMER_COUNT = 30;
    ForkJoinPool forkJoinPool = new ForkJoinPool(15);


    public TourGuideServiceImpl(GpsUtilServiceImpl gpsUtilService, RewardsServiceImpl rewardsService, UserRepository userRepository) {
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

    @Override
    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    @Override
    public VisitedLocation getUserLocation(String userName) throws Exception {
        User user = getUser(userName);
        return trackUserLocation(user);
    }

    @Override
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

    @Override
    public void addUserNewVisitedLocation(User user, VisitedLocation newVisitedLocation) {
        List<VisitedLocation> userVisitedLocationList = user.getVisitedLocations();
        userVisitedLocationList.add(newVisitedLocation);
        user.setVisitedLocations(userVisitedLocationList);
        user.setLatestLocationTimestamp(newVisitedLocation.timeVisited);
    }

    @Override
    public UserCloseAttractionsInfo searchFiveClosestAttractions(VisitedLocation visitedLocation) {
        List<Attraction> attractionsAndDistancesFromFiveClosestLocation = rewardsService.searchFiveClosestAttractionsMap(visitedLocation);
        UserCloseAttractionsInfo userCloseAttractionsInfo = new UserCloseAttractionsInfo(visitedLocation.location.latitude, visitedLocation.location.longitude);
        List<CloseAttraction> fiveClosestAttractions = new ArrayList<>();
        attractionsAndDistancesFromFiveClosestLocation
                .forEach((attraction)
                        -> fiveClosestAttractions.add(new CloseAttraction(attraction.attractionName, attraction.latitude, attraction.longitude, rewardsService.getDistance(attraction, visitedLocation.location), rewardsService.getAttractionRewardPoints(attraction.attractionId, visitedLocation.userId))));
        userCloseAttractionsInfo.setCloseAttractions(fiveClosestAttractions);
        return userCloseAttractionsInfo;
    }

    @Override
    public User getUser(String userName) throws Exception {
        return userRepository.getUser(userName);
    }

    private User getUserFromId(UUID userId){
        return userRepository.getUserFromId(userId);
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.getUserList();
    }

    @Override
    public void addUser(User user) {
        userRepository.addUser(user);
    }

    @Override
    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    @Override
    public VisitedLocation trackUserLocation(User user)  {
        UUID userId = user.getUserId();
        VisitedLocation visitedLocation = gpsUtilService.getUserLocation(userId);
        rewardsService.calculateRewards(user);
        addUserNewVisitedLocation(user, visitedLocation);
        return visitedLocation;
    }

    @Override
    public Map<String, Location> getAllCurrentLocations() {
        Map<String, Location> result = new HashMap<>();
        getAllUsers()
                .stream()
                .parallel()
                .forEach(user -> result.put(user.getUserId().toString(), getUserLastVisitedLocation(user).location));
        return result;
    }


    @Override
    public List<VisitedLocation> trackAllUsers(List<User> allUsers)  {
        BlockingQueue<VisitedLocation> visitedLocationQueue = new LinkedBlockingQueue<>();
       List<VisitedLocation> visitedLocations = forkJoinPool.invoke(new CalculateLocationTask(allUsers, this.gpsUtilService,visitedLocationQueue));
      consumeVisitedLocationQueue(visitedLocationQueue);
      return visitedLocations;
    }

    private void consumeVisitedLocationQueue(BlockingQueue<VisitedLocation> visitedLocationQueue) {
        for (int futureNumber = 0; futureNumber < USER_CONSUMER_COUNT; futureNumber++) {
            executorServiceTourGuide.submit(() -> {
                try {
                    while (!visitedLocationQueue.isEmpty()) {
                        VisitedLocation visitedLocation = visitedLocationQueue.take();

                            User user = getUserFromId(visitedLocation.userId);
                            addUserNewVisitedLocation(user,visitedLocation);
                            rewardsService.calculateRewards(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
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
                            allVisitedLocations.add(visitedLocation);}
                        finally {
                            userLatch.countDown();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return allVisitedLocations;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
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
        IntStream.range(0, 3).forEach(i -> addUserNewVisitedLocation(user, new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime())));
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