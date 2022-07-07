package tourGuide.service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.User;
import tourGuide.model.UserCloseAttractionsInfo;
import tourGuide.tracker.Tracker;

import java.util.List;
import java.util.Map;

@Service
public interface TourGuideService {


    /**
     * Permit to start a new tracker which is tracking all user's locations every fifteen minutes,
     * the function also add shutDownHook to permit closing tracker's executorService
     */
    void initializeTourGuideService();

    /**
     * Returns the service's tracker
     *
     * @return the tracker attached to this class
     */
    Tracker getTracker();

    /**
     * Get a visitedLocation by user,
     * either by getting its last visitedLocation in its visitedLocation's list (the list is updated all fifteen minutes by tracker),
     * either by tracking the actual user's location (if its visitedLocation's list is empty).
     *
     * @param user the user whose location is sought
     * @return actual user location if its list of visitedLocation is empty otherwise its last visitedLocation
     */
    VisitedLocation getUserLocation(User user);

    /**
     * Track a user's location calling gpsUtilService and add it to its visitedLocation list,
     * rewards are calculating calling rewardsService and added if applicable.
     *
     * @param user the user whose location is tracked
     * @return the actual user VisitedLocation
     */
    VisitedLocation trackUserLocation(User user);

    /**
     * Get the five closest attractions from a visitedLocation, whatever the distance,
     * and return the user's location,
     * and, for each attraction, its name, its location, the distance from the user's location
     * and the reward points awarded for this attraction
     *
     * @param visitedLocation a VisitedLocation object
     * @throws ObjectNotFoundException when the user is not found by its id
     * @return a UserCloseAttractionsInfo object containing all information about the five closest locations
     */
    UserCloseAttractionsInfo searchFiveClosestAttractions(VisitedLocation visitedLocation) throws ObjectNotFoundException;

    /**
     * Get all the user registered
     *
     * @return a list of user containing all users registered
     */
    List<User> getAllUsers();

    /**
     * Get the current location for each user and returns the result in a hashmap.
     * The current location is returned by the function getUserLocation,
     * which returns the last VisitedLocation if there is one (its actualised all fifteen minutes)
     * or track the location if there is not any VisitedLocation registered
     *
     * @return a HashMap containing all the users' id as key and all their location as value
     */
    Map<String, Location> getAllCurrentLocations();

    /**
     * Track the location of all the users in the given list,
     * this method is used by the tracker.
     * It uses the forkJoinPool and a LinkedBlockingQueue to run threads concurrently,
     * so the function is faster and permit tracking 100 000 users in about 10 minutes
     *
     * @param allUsers the user list containing all users to track
     * @return a list of all the visitedLocation tracked
     */
    List<VisitedLocation> trackAllUsers(List<User> allUsers);

}
