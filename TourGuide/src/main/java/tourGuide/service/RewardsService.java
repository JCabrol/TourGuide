package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.RewardCentralRepository;

import java.util.*;
import java.util.concurrent.*;

@Service
public class RewardsService {
    private final Logger logger = LoggerFactory.getLogger(RewardsService.class);
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
//    CountDownLatch rewardsGroup = new CountDownLatch(4);
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;
    private final GpsUtilService gpsUtilService;
    private final RewardCentralRepository rewardCentralRepository;

    ExecutorService executorServiceRewards = Executors.newCachedThreadPool();

    public RewardsService(GpsUtilService gpsUtilService, RewardCentralRepository rewardCentralRepository) {
        this.gpsUtilService = gpsUtilService;
        this.rewardCentralRepository = rewardCentralRepository;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentralRepository.getAttractionRewardPoints(attractionId, userId);
    }
public void calculateRewards(User user, VisitedLocation lastLocation) throws InterruptedException {
        executorServiceRewards.submit(()-> {
            try {
                calculateRewards2(user, lastLocation);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
}

    public void calculateRewards2(User user, VisitedLocation visitedLocation) throws ExecutionException, InterruptedException {
        List<Attraction> fiveClosestAttractions = searchFiveClosestAttractionsMap(visitedLocation);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (Attraction attraction : fiveClosestAttractions) {
          executorService.submit(() -> addUserNewRewards(user, visitedLocation, attraction));
        }
        executorService.shutdown();

    }


    public List<Attraction> searchFiveClosestAttractionsMap(VisitedLocation visitedLocation)  {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        List<Attraction> attractionBeanList = gpsUtilService.getAttractions();
        attractionBeanList.stream().parallel()
                .sorted(Comparator.comparingDouble(attraction -> getDistance(new Location(attraction.longitude, attraction.latitude), visitedLocation.location)))
                .limit(5)
                .forEach(nearbyAttractions::add);
        return nearbyAttractions;
    }


    public void addUserNewRewards(User user, VisitedLocation lastLocation, Attraction attraction) {
            List<UserReward> userRewardList = user.getUserRewards();
            if(isNotInRewardsList(attraction.attractionName, userRewardList)){
            userRewardList.add(new UserReward(lastLocation, attraction, getRewardPoints(attraction, user)));}
            user.setUserRewards(userRewardList);
    }

    private SortedMap<Double, Attraction> getDistanceBetweenVisitedLocationAndAllAttractions(VisitedLocation visitedLocation, List<Attraction> attractions) {
        SortedMap<Double, Attraction> attractionsAndDistancesFromLocation = Collections.synchronizedSortedMap(new TreeMap<>());
        attractions
                .stream()
                .parallel()
                .forEach(attraction -> {
                    Double distance = getDistance(attraction, visitedLocation.location);
                    attractionsAndDistancesFromLocation.put(distance, attraction);
                });
        return attractionsAndDistancesFromLocation;
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

    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

}
