package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.model.DTO.RewardElements;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.recursiveTask.CalculatingRewardsTask;
import tourGuide.repository.RewardCentralRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;


@Service
public class RewardsServiceImpl implements RewardsService {

    @Autowired
    private GpsUtilService gpsUtilService;
    @Autowired
    private UserService userService;
    @Autowired
    private RewardCentralRepository rewardCentralRepository;

    /**
     * A constant used to calculate distance in miles
     */
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    /**
     * The maximal distance between a user and an attraction to permit user to get rewards, the default value is 200 miles
     */
    private int proximityBuffer = 200;

    /**
     * The list of all existing attractions, which is returned by gpsUtil when the application begins to run and can be updated
     */
    private List<Attraction> attractionList;

    /**
     * ExecutorService used to submit rewardElements to add. It's a cachedThreadPool executorService so the necessary threads are automatically running and stopping.
     */
    private final ExecutorService executorServiceRewards = Executors.newCachedThreadPool();

    /**
     * A ForkJoinPool used to invoke CalculatingRewardsTask to select the elementRewards to add
     */
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(20);

    /**
     * Method used when the application begins to run, to get all the existing attractions and put it in the attractionList
     *
     */
    @Override
    public void initializeRewardsService() {
        this.attractionList = gpsUtilService.getAttractions();
    }

    /**
     * Getter
     * @return the attractionList
     */
    @Override
    public List<Attraction> getAttractionList() {
        return attractionList;
    }

    /**
     * Setter
     * @param attractionList the attraction list to set
     */
    @Override
    public void setAttractionList(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    /**
     * Setter
     * @param proximityBuffer the proximityBuffer to set
     */
    @Override
    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    /**
     * Getter
     * @return the proximityBuffer
     */
    @Override
    public int getProximityBuffer() {
        return this.proximityBuffer;
    }

    /**
     * Calculate the rewards for a user.
     * This method is running on several threads to be faster,
     * using a forkJoinPool to invoke a recursiveTask called CalculatingRewardsTask which returns the elementRewards to add
     * and an executorService to calculate rewardPoints from the rewardElements and add it to user
     *
     * @param user the user whose rewards are calculated
     */
    @Override
    public void calculateRewards(User user) {
        List<RewardElements> rewardElementsList = forkJoinPool.invoke(new CalculatingRewardsTask(user, attractionList, this));
        if (rewardElementsList.size() != 0) {
            executorServiceRewards.submit(() -> {
                for (RewardElements rewardElements : rewardElementsList) {
                    userService.addUserRewards(user,new UserReward(rewardElements.getVisitedLocation(), rewardElements.getAttraction(), getRewardPoints(rewardElements.getAttraction(), user)));
                }
            });
        }
    }

    /**
     * Get a list of five attractions, the closest from the visitedLocation given in parameter
     *
     * @param visitedLocation the concerned visitedLocation
     * @return a list of the five closest attractions
     */
    @Override
    public List<Attraction> searchFiveClosestAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        if(attractionList.size()!=0){
        attractionList.stream().parallel()
                .sorted(Comparator.comparingDouble(attraction -> getDistance(new Location(attraction.longitude, attraction.latitude), visitedLocation.location)))
                .limit(5)
                .forEach(nearbyAttractions::add);}
        return nearbyAttractions;
    }

    /**
     * Check if an attraction is not already present in a user's reward list
     *
     * @param attractionName the name of the attraction to check
     * @param user the user whose rewardsList is checked
     * @return true if the attraction is not already in the userReward list, false otherwise
     */
    @Override
    public boolean isNotInRewardsList(String attractionName, User user) {
        List<UserReward> userRewardsList = user.getUserRewards();
        if (userRewardsList.size() != 0) {
            return userRewardsList
                    .stream()
                    .parallel()
                    .noneMatch(userReward -> userReward.attraction.attractionName.equals(attractionName));
        } else {
            return true;
        }
    }

    /**
     * Check if an attraction is closer than the proximityBuffer from a visitedLocation
     *
     * @param visitedLocation the visitedLocation to check
     * @param attraction the attraction to check
     * @return true if the attraction closer than the proximityBuffer from the visitedLocation, false otherwise
     */
    @Override
    public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return (getDistance(attraction, visitedLocation.location) < proximityBuffer);
    }

    /**
     * Get a number of points to attribute to a user for visiting an attraction
     *
     * @param user the concerned user
     * @param attraction the concerned attraction
     * @return an int which is the number of points to attribute
     */
    @Override
    public int getRewardPoints(Attraction attraction, User user) {
        return rewardCentralRepository.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

    /**
     * Calculate the distance in miles between 2 locations
     *
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the distance in miles between the 2 locations, expressed in miles
     */
    @Override
    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }
}