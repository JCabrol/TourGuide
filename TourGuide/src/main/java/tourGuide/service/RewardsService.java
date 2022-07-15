package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.model.User;

import java.util.List;

@Service
public interface RewardsService {


    void initializeRewardsService();

    List<Attraction> getAttractionList();

    void setAttractionList(List<Attraction> attractionList);

    void setProximityBuffer(int proximityBuffer);

    int getProximityBuffer();

    void setDefaultProximityBuffer();

    void calculateRewards(User user);

    List<Attraction> searchFiveClosestAttractions(VisitedLocation visitedLocation);

    boolean isNotInRewardsList(String attractionName, User user);

    boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction);

    int getRewardPoints(Attraction attraction, User user);

    double getDistance(Location loc1, Location loc2);

}
