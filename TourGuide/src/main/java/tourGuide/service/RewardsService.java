package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.model.User;

import java.util.List;

@Service
public interface RewardsService {

    /**
     * Method used when the application begins to run, to get all the existing attractions and put it in the attractionList
     */
    void initializeRewardsService();

    /**
     * Getter
     *
     * @return the attractionList
     */
    List<Attraction> getAttractionList();

    /**
     * Setter
     *
     * @param attractionList the attraction list to set
     */
    void setAttractionList(List<Attraction> attractionList);

    /**
     * Setter
     *
     * @param proximityBuffer the proximityBuffer to set
     */
    void setProximityBuffer(int proximityBuffer);

    /**
     * Getter
     *
     * @return the proximityBuffer
     */
    int getProximityBuffer();

    /**
     * Calculate the rewards for a user.
     * This method is running on several threads to be faster,
     * using a forkJoinPool to invoke a recursiveTask called CalculatingRewardsTask which returns the elementRewards to add
     * and an executorService to calculate rewardPoints from the rewardElements and add it to user
     *
     * @param user the user whose rewards are calculated
     */
    void calculateRewards(User user);

    /**
     * Get a list of five attractions, the closest from the visitedLocation given in parameter
     *
     * @param visitedLocation the concerned visitedLocation
     * @return a list of the five closest attractions
     */
    List<Attraction> searchFiveClosestAttractions(VisitedLocation visitedLocation);

    /**
     * Check if an attraction is not already present in a user's reward list
     *
     * @param attractionName the name of the attraction to check
     * @param user           the user whose rewardsList is checked
     * @return true if the attraction is not already in the userReward list, false otherwise
     */
    boolean isNotInRewardsList(String attractionName, User user);

    /**
     * Check if an attraction is closer than the proximityBuffer from a visitedLocation
     *
     * @param visitedLocation the visitedLocation to check
     * @param attraction      the attraction to check
     * @return true if the attraction closer than the proximityBuffer from the visitedLocation, false otherwise
     */
    boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction);

    /**
     * Get a number of points to attribute to a user for visiting an attraction
     *
     * @param user       the concerned user
     * @param attraction the concerned attraction
     * @return an int which is the number of points to attribute
     */
    int getRewardPoints(Attraction attraction, User user);

    /**
     * Calculate the distance in miles between 2 locations
     *
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the distance in miles between the 2 locations, expressed in miles
     */
    double getDistance(Location loc1, Location loc2);

}
