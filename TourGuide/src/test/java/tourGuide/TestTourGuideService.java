package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import org.junit.Test;

import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.UserCloseAttractionsInfo;
import tourGuide.model.UserReward;
import tourGuide.repository.GpsUtilRepository;
import tourGuide.repository.RewardCentralRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.*;
import tourGuide.model.User;
import tripPricer.Provider;

public class TestTourGuideService {

	@Test
	public void getUserLocation() throws Exception {
		GpsUtilService gpsUtil = new GpsUtilService(new GpsUtilRepository());
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralRepository());
        UserRepository userRepository = new UserRepository();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = (VisitedLocation) tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertEquals(visitedLocation.userId, user.getUserId());
	}

	@Test
	public void addUser() throws Exception {
		GpsUtilService gpsUtil = new GpsUtilService(new GpsUtilRepository());
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralRepository());
		UserRepository userRepository = new UserRepository();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userRepository);

		User model = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(model);
		tourGuideService.addUser(user2);

		User retrievedUser = tourGuideService.getUser(model.getUserName());
		User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(model, retrievedUser);
		assertEquals(user2, retrievedUser2);
	}

	@Test
	public void getAllUsers() throws Exception {
		GpsUtilService gpsUtil = new GpsUtilService(new GpsUtilRepository());
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralRepository());
		UserRepository userRepository = new UserRepository();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userRepository);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() throws Exception {
		GpsUtilService gpsUtil = new GpsUtilService(new GpsUtilRepository());
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralRepository());
		UserRepository userRepository = new UserRepository();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = (VisitedLocation) tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() throws Exception {
		GpsUtilService gpsUtil = new GpsUtilService(new GpsUtilRepository());
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralRepository());
		UserRepository userRepository = new UserRepository();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = (VisitedLocation) tourGuideService.trackUserLocation(user);
		UserCloseAttractionsInfo closestAttractions = tourGuideService.searchFiveClosestAttractions(visitedLocation);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, closestAttractions.getCloseAttractions().size());
	}

@Test
	public void getTripDeals() {
		GpsUtilService gpsUtil = new GpsUtilService(new GpsUtilRepository());
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralRepository());
		UserRepository userRepository = new UserRepository();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, providers.size());
	}


}
