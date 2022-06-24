package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.RewardCentralRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RewardsServiceImpl implements RewardsService {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
//    CountDownLatch rewardsGroup = new CountDownLatch(4);
    private final int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private final int attractionProximityRange = 200;
    private final GpsUtilService gpsUtilService;
    private final RewardCentralRepository rewardCentralRepository;

    ExecutorService executorServiceRewards = Executors.newCachedThreadPool();

    public RewardsServiceImpl(GpsUtilService gpsUtilService, RewardCentralRepository rewardCentralRepository) {
        this.gpsUtilService = gpsUtilService;
        this.rewardCentralRepository = rewardCentralRepository;
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
    public void calculateRewards(User user, VisitedLocation visitedLocation) throws InterruptedException {
        executorServiceRewards.submit(() -> {
            try {
                calculateRewardsFuture(user, visitedLocation);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void calculateRewardsFuture(User user, VisitedLocation visitedLocation) throws ExecutionException, InterruptedException {
        List<Attraction> fiveClosestAttractions = searchFiveClosestAttractionsMap(visitedLocation);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (Attraction attraction : fiveClosestAttractions) {
            executorService.submit(() -> addUserNewRewards(user, visitedLocation, attraction));
        }
        executorService.shutdown();
    }

    @Override
    public List<Attraction> searchFiveClosestAttractionsMap(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        List<Attraction> attractionBeanList = gpsUtilService.getAttractions();
        attractionBeanList.stream().parallel()
                .sorted(Comparator.comparingDouble(attraction -> getDistance(new Location(attraction.longitude, attraction.latitude), visitedLocation.location)))
                .limit(5)
                .forEach(nearbyAttractions::add);
        return nearbyAttractions;
    }

    @Override
    public void addUserNewRewards(User user, VisitedLocation lastLocation, Attraction attraction) {
        List<UserReward> userRewardList = user.getUserRewards();
        if (isNotInRewardsList(attraction.attractionName, userRewardList)) {
            userRewardList.add(new UserReward(lastLocation, attraction, getRewardPoints(attraction, user)));
        }
        user.setUserRewards(userRewardList);
    }


    private boolean isNotInRewardsList(String attractionName, List<UserReward> userRewardsList) {
        if (userRewardsList.size() != 0) {
            return userRewardsList
                    .stream()
                    .parallel()
                    .noneMatch(userReward -> userReward.attraction.attractionName.equals(attractionName));
        } else {
            return true;
        }
    }

    private int getRewardPoints(Attraction attraction, User user) {
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
