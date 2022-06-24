package tourGuide.service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.model.User;
import tourGuide.model.UserCloseAttractionsInfo;
import tourGuide.model.UserReward;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;

public interface TourGuideService {
    List<UserReward> getUserRewards(User user);

    VisitedLocation getUserLocation(String userName) throws Exception;

    VisitedLocation getUserLastVisitedLocation(User user);

    void addUserNewVisitedLocation(User user, VisitedLocation newVisitedLocation);

    UserCloseAttractionsInfo searchFiveClosestAttractions(VisitedLocation visitedLocation);

    User getUser(String userName) throws Exception;

    List<User> getAllUsers();

    void addUser(User user);

    List<Provider> getTripDeals(User user);

    VisitedLocation trackUserLocation(User user) throws InterruptedException;

    Map<String, Location> getAllCurrentLocations();

    List<VisitedLocation> trackAllUserLocation(List<User> allUsers) throws Exception;
}
