//package tourGuide;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
//
//import java.util.List;
//import java.util.UUID;
//
//import org.junit.Ignore;
//import org.junit.Test;
//
//import gpsUtil.location.VisitedLocation;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.junit4.SpringRunner;
//import tourGuide.helper.InternalTestHelper;
//import tourGuide.model.UserCloseAttractionsInfo;
//import tourGuide.repository.GpsUtilRepository;
//import tourGuide.repository.RewardCentralRepository;
//import tourGuide.repository.UserRepository;
//import tourGuide.service.*;
//import tourGuide.model.User;
//import tripPricer.Provider;
//
//@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class TestTourGuideService {
//
//	@Autowired
//	UserService userService;
//	@Autowired
//	TourGuideService tourGuideService;
//	@Autowired
//	TripPricerService tripPricerService;
//
//
//	@Test
//	public void getUserLocation() throws Exception {
////		GpsUtilServiceImpl gpsUtil = new GpsUtilServiceImpl(new GpsUtilRepository());
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtilService, new RewardCentralRepository());
////        UserRepository userRepository = new UserRepository();
//		InternalTestHelper.setInternalUserNumber(0);
////		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtilService, rewardsService,userRepository);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//
//
//		System.out.println("Before adding user, map size: "+userService.getInternalUserMap().size());
//		userService.addNewUser(user);
//		System.out.println("After adding user, map size: "+userService.getInternalUserMap().size());
//
//
//		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
//
//		tourGuideService.getTracker().stopTracking();
//		assertEquals(visitedLocation.userId, user.getUserId());
//	}
//@Ignore
//	@Test
//	public void addUser() throws Exception {
////		GpsUtilServiceImpl gpsUtil = new GpsUtilServiceImpl(new GpsUtilRepository());
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtilService, new RewardCentralRepository());
////		UserRepository userRepository = new UserRepository();
//		InternalTestHelper.setInternalUserNumber(0);
////		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService,userRepository);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
//
//		userService.addNewUser(user);
//		userService.addNewUser(user2);
//
//		User retrievedUser = userService.getUser(user.getUserName());
//		User retrievedUser2 = userService.getUser(user2.getUserName());
//
//		tourGuideService.getTracker().stopTracking();
//
//		assertEquals(user, retrievedUser);
//		assertEquals(user2, retrievedUser2);
//	}
//
//	@Test
//	public void getAllUsers() {
////		GpsUtilServiceImpl gpsUtil = new GpsUtilServiceImpl(new GpsUtilRepository());
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentralRepository());
////		UserRepository userRepository = new UserRepository();
//		InternalTestHelper.setInternalUserNumber(0);
////		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService,userRepository);
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
//
//		userService.addNewUser(user);
//		userService.addNewUser(user2);
//
//		List<User> allUsers = userService.getAllUsers();
//
//		tourGuideService.getTracker().stopTracking();
//
//		assertTrue(allUsers.contains(user));
//		assertTrue(allUsers.contains(user2));
//	}
//
//	@Test
//	public void trackUser() throws Exception {
////		GpsUtilServiceImpl gpsUtil = new GpsUtilServiceImpl(new GpsUtilRepository());
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentralRepository());
////		UserRepository userRepository = new UserRepository();
//		InternalTestHelper.setInternalUserNumber(0);
////		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService,userRepository);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
//		tourGuideService.getTracker().stopTracking();
//		assertEquals(user.getUserId(), visitedLocation.userId);
//	}
//
//	@Test
//	public void getNearbyAttractions() throws Exception {
////		GpsUtilServiceImpl gpsUtil = new GpsUtilServiceImpl(new GpsUtilRepository());
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentralRepository());
////		UserRepository userRepository = new UserRepository();
//		InternalTestHelper.setInternalUserNumber(0);
////		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService,userRepository);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		userService.addNewUser(user);
//		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
//		UserCloseAttractionsInfo closestAttractions = tourGuideService.searchFiveClosestAttractions(visitedLocation);
//
//		tourGuideService.getTracker().stopTracking();
//
//		assertEquals(5, closestAttractions.getCloseAttractions().size());
//	}
//
//@Test
//	public void getTripDeals() {
////		GpsUtilServiceImpl gpsUtil = new GpsUtilServiceImpl(new GpsUtilRepository());
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentralRepository());
////		UserRepository userRepository = new UserRepository();
////		TripPricerServiceImpl tripPricerService = new TripPricerServiceImpl();
//		InternalTestHelper.setInternalUserNumber(0);
////		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService,userRepository);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//
//		List<Provider> providers = tripPricerService.getTripDeals(user);
//
//		tourGuideService.getTracker().stopTracking();
//
//		assertEquals(5, providers.size());
//	}
//
//
//}
