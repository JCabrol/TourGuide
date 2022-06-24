package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.model.User;

import java.util.List;
import java.util.UUID;

public interface RewardsService {

    void setProximityBuffer(int proximityBuffer);

    void setDefaultProximityBuffer();

    int getAttractionRewardPoints(UUID attractionId, UUID userId);

    void calculateRewards(User user, VisitedLocation visitedLocation) throws InterruptedException;

    List<Attraction> searchFiveClosestAttractionsMap(VisitedLocation visitedLocation);

    void addUserNewRewards(User user, VisitedLocation lastLocation, Attraction attraction);

    double getDistance(Location loc1, Location loc2);
}
