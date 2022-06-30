package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.model.User;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public interface RewardsService {

    List<Attraction> getAttractionList();

    void setAttractionList(List<Attraction> attractionList);

    void setProximityBuffer(int proximityBuffer);

    void setDefaultProximityBuffer();

    int getAttractionRewardPoints(UUID attractionId, UUID userId);

    void calculateRewards(User user);

    List<Attraction> searchFiveClosestAttractionsMap(VisitedLocation visitedLocation);

    void addUserNewRewards(User user, VisitedLocation lastLocation, Attraction attraction);

    boolean isNotInRewardsList(String attractionName, User user);

    boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction);

    int getRewardPoints(Attraction attraction, User user);

    double getDistance(Location loc1, Location loc2);

}
