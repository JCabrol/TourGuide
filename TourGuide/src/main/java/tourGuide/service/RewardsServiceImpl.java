package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.model.RewardElements;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.RewardCentralRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@Service
public class RewardsServiceImpl implements RewardsService {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    // proximity in miles

    private final int defaultProximityBuffer = 30;
    private int proximityBuffer = defaultProximityBuffer;
    private final int attractionProximityRange = 200;
    private final GpsUtilService gpsUtilService;
    private final RewardCentralRepository rewardCentralRepository;
    private List<Attraction> attractionList;
    private final ExecutorService executorServiceRewards = Executors.newCachedThreadPool();
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(20);


    public RewardsServiceImpl(GpsUtilService gpsUtilService, RewardCentralRepository rewardCentralRepository) {
        this.gpsUtilService = gpsUtilService;
        this.rewardCentralRepository = rewardCentralRepository;
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
    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    @Override
    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentralRepository.getAttractionRewardPoints(attractionId, userId);
    }

    @Override
    public void calculateRewards(User user) {
        List<RewardElements> rewardElementsList = forkJoinPool.invoke(new CalculatingRewardsTask(user, attractionList, this));
        if (rewardElementsList.size() != 0) {
            executorServiceRewards.submit(() -> {
                for (RewardElements rewardElements : rewardElementsList) {
                    user.addUserReward(new UserReward(rewardElements.getVisitedLocation(), rewardElements.getAttraction(), getRewardPoints(rewardElements.getAttraction(), user)));
                }
            });
        }
    }

    @Override
    public List<Attraction> searchFiveClosestAttractionsMap(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        List<Attraction> attractionList = gpsUtilService.getAttractions();
        attractionList.stream().parallel()
                .sorted(Comparator.comparingDouble(attraction -> getDistance(new Location(attraction.longitude, attraction.latitude), visitedLocation.location)))
                .limit(5)
                .forEach(nearbyAttractions::add);
        return nearbyAttractions;
    }


    @Override
    public void addUserNewRewards(User user, VisitedLocation lastLocation, Attraction attraction) {
        if (isNotInRewardsList(attraction.attractionName, user)) {
            if (nearAttraction(lastLocation, attraction)) {
                user.addUserReward(new UserReward(lastLocation, attraction, getRewardPoints(attraction, user)));
            }
        }
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