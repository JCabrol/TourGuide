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

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    // proximity in miles
    private final int defaultProximityBuffer = 200;
    private int proximityBuffer = defaultProximityBuffer;

    private List<Attraction> attractionList;

    private final ExecutorService executorServiceRewards = Executors.newCachedThreadPool();
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(20);

    @Autowired
    private GpsUtilService gpsUtilService;

    @Autowired
    private UserService userService;

    @Autowired
    private RewardCentralRepository rewardCentralRepository;


    @Override
    public void initializeRewardsService() {
        this.attractionList = gpsUtilService.getAttractions();
    }


    @Override
    public List<Attraction> getAttractionList() {
        return attractionList;
    }

    @Override
    public void setAttractionList(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    @Override
    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    @Override
    public int getProximityBuffer() {
        return this.proximityBuffer;
    }

    @Override
    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

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

    @Override
    public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return (getDistance(attraction, visitedLocation.location) < proximityBuffer);
    }

    @Override
    public int getRewardPoints(Attraction attraction, User user) {
        return rewardCentralRepository.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

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