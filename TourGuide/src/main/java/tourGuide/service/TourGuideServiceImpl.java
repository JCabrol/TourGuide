package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.DTO.CloseAttraction;
import tourGuide.model.DTO.UserCloseAttractionsInfo;
import tourGuide.model.User;
import tourGuide.recursiveTask.CalculateLocationTask;
import tourGuide.tracker.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;


@Service
public class TourGuideServiceImpl implements TourGuideService {

    @Autowired
    private GpsUtilService gpsUtilService;
    @Autowired
    private RewardsService rewardsService;
    @Autowired
    private UserService userService;

    /**
     * The tracker is used to track all users locations.
     * It starts when the application begin to run.
     * After finish, it sleeps five minutes and starts again.
     */
    private Tracker tracker;

    /**
     * A cachedThreadPool executorService so the necessary threads are automatically running and stopping.
     */
    public ExecutorService executorServiceTourGuide = Executors.newCachedThreadPool();

    /**
     * A ForkJoinPool used to invoke a RecursiveTask called CalculatingRewardsTask.
     */
    public ForkJoinPool forkJoinPool = new ForkJoinPool(30);

    /**
     * A constant used to limit the number of threads working at the same time in the executorService when consuming visitedLocationQueue
     */
    public static final int USER_CONSUMER_COUNT = 15;


    /**
     * Permit to start a new tracker which is tracking regularly all user's locations.
     * The function also add shutDownHook to permit closing tracker's executorService.
     */
    @Override
    public void initializeTourGuideService() {
        tracker = new Tracker(this, this.userService);
        addShutDownHook();
    }

    /**
     * Returns the service's tracker
     *
     * @return the tracker attached to this class
     */
    @Override
    public Tracker getTracker() {
        return tracker;
    }

    /**
     * This method adds ShutDownHook to tracker to permit closing tracker's thread
     */
    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
    }

    /**
     * Get a visitedLocation by user,
     * either by getting its last visitedLocation in its visitedLocation's list (the list is updated all fifteen minutes by tracker),
     * either by tracking the actual user's location (if its visitedLocation's list is empty).
     *
     * @param user the user whose location is sought
     * @return actual user location if its list of visitedLocation is empty otherwise its last visitedLocation
     */
    @Override
    public VisitedLocation getUserLocation(User user) {
        return (user.getVisitedLocations().size() > 0) ?
                userService.getUserLastVisitedLocation(user) :
                trackUserLocation(user);
    }

    /**
     * Track a user's location calling gpsUtilService and add it to its visitedLocation list,
     * rewards are calculating calling rewardsService and added if applicable.
     *
     * @param user the user whose location is tracked
     * @return the actual user VisitedLocation
     */
    @Override
    public VisitedLocation trackUserLocation(User user) {
        UUID userId = user.getUserId();
        VisitedLocation visitedLocation = gpsUtilService.getUserLocation(userId);
        rewardsService.calculateRewards(user);
        try {
            userService.addUserNewVisitedLocation(user, visitedLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return visitedLocation;
    }

    /**
     * Get the five closest attractions from a visitedLocation, whatever the distance,
     * and return the user's location,
     * and, for each attraction, its name, its location, the distance from the user's location
     * and the reward points awarded for this attraction
     *
     * @param visitedLocation a VisitedLocation object
     * @return a UserCloseAttractionsInfo object containing all information about the five closest locations
     * @throws ObjectNotFoundException when the user is not found by its id
     */
    @Override
    public UserCloseAttractionsInfo searchFiveClosestAttractions(VisitedLocation visitedLocation) throws ObjectNotFoundException {
        List<Attraction> attractionsAndDistancesFromFiveClosestLocation = rewardsService.searchFiveClosestAttractions(visitedLocation);
        UserCloseAttractionsInfo userCloseAttractionsInfo = new UserCloseAttractionsInfo(visitedLocation.location.latitude, visitedLocation.location.longitude);
        List<CloseAttraction> fiveClosestAttractions = new ArrayList<>();
        User user = userService.getUserById(visitedLocation.userId);
        attractionsAndDistancesFromFiveClosestLocation
                .forEach((attraction)
                        -> fiveClosestAttractions.add(new CloseAttraction(attraction.attractionName, attraction.latitude, attraction.longitude, rewardsService.getDistance(attraction, visitedLocation.location), rewardsService.getRewardPoints(attraction, user))));
        userCloseAttractionsInfo.setCloseAttractions(fiveClosestAttractions);
        return userCloseAttractionsInfo;
    }


    /**
     * Get the current location for each user and returns the result in a hashmap.
     * The current location is returned by the function getUserLocation,
     * which returns the last VisitedLocation if there is one (its actualised all fifteen minutes)
     * or track the location if there is not any VisitedLocation registered
     *
     * @return a HashMap containing all the users' id as key and all their location as value
     */
    @Override
    public HashMap<String, Location> getAllCurrentLocations() {
        HashMap<String, Location> result = new HashMap<>();

        userService.getAllUsers()
                .stream()
                .parallel()
                .forEach(user -> result.put(user.getUserId().toString(), getUserLocation(user).location));
        return result;
    }

    /**
     * Track the location of all the users in the given list,
     * this method is used by the tracker.
     * It uses the forkJoinPool and a LinkedBlockingQueue to run threads concurrently,
     * so the function is faster and permit tracking 100 000 users in about 10 minutes
     *
     * @param allUsers the user list containing all users to track
     * @return a list of all the visitedLocation tracked
     */
    @Override
    public List<VisitedLocation> trackAllUsers(List<User> allUsers) {
        if (allUsers.size() != 0) {
            BlockingQueue<VisitedLocation> visitedLocationQueue = new LinkedBlockingQueue<>();
            List<VisitedLocation> visitedLocations = forkJoinPool.invoke(new CalculateLocationTask(allUsers, this.gpsUtilService));
            executorServiceTourGuide.submit(() -> publishVisitedLocationQueue(visitedLocations, visitedLocationQueue));
            consumeVisitedLocationQueue(visitedLocationQueue);
            return visitedLocations;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * This method is a producer method,
     * it publishes all visitedLocations from the given list in a linkedBlockingQueue,
     * so the data can be consumed from this queue, controlling the number of threads.
     * If the queue is full, the method will wait and continue when there will be places again.
     *
     * @param visitedLocations     the visitedLocation list to publish into the queue
     * @param visitedLocationQueue the linkedBlockingQueue within visitedLocations are published
     */
    private void publishVisitedLocationQueue(List<VisitedLocation> visitedLocations, BlockingQueue<VisitedLocation> visitedLocationQueue) {
        visitedLocations
                .forEach(visitedLocation -> {
                    try {
                        visitedLocationQueue.put(visitedLocation);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * This method is a consumer method,
     * it takes all visitedLocations from the linkedBlockingQueue and add it to the concerned user.
     * It also calculates rewards for the user.
     *
     * @param visitedLocationQueue the linkedBlockingQueue within visitedLocations are consumed
     */
    private void consumeVisitedLocationQueue(BlockingQueue<VisitedLocation> visitedLocationQueue) {
        for (int futureNumber = 0; futureNumber < USER_CONSUMER_COUNT; futureNumber++) {
            executorServiceTourGuide.submit(() -> {
                try {
                    while (!visitedLocationQueue.isEmpty()) {
                        VisitedLocation visitedLocation = visitedLocationQueue.take();
                        User user = userService.getUserById(visitedLocation.userId);
                        userService.addUserNewVisitedLocation(user, visitedLocation);
                        rewardsService.calculateRewards(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


}