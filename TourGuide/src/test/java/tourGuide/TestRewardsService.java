//package tourGuide;
//
//import gpsUtil.location.Attraction;
//import gpsUtil.location.VisitedLocation;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import tourGuide.helper.InternalTestHelper;
//import tourGuide.model.User;
//import tourGuide.model.UserReward;
//import tourGuide.repository.GpsUtilRepository;
//import tourGuide.repository.RewardCentralRepository;
//import tourGuide.repository.UserRepository;
//import tourGuide.service.*;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class TestRewardsService {
////	private static final Locale locale = new Locale("en", "US");
//
//	@Autowired
//	TourGuideService tourGuideService;
//
//	@Autowired
//	RewardsService rewardsService;
//
//	@Autowired
//	UserService userService;
//
//
//
////	@Before
////	public void init() {
////		Locale.setDefault(locale);
////
////	}
//	@Ignore
//	@Test
//	public void userGetRewards() throws Exception {
////		GpsUtilServiceImpl gpsUtil = new GpsUtilServiceImpl(new GpsUtilRepository());
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentralRepository());
////        UserRepository userRepository = new UserRepository();
//		InternalTestHelper.setInternalUserNumber(0);
////		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService,userRepository);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		userService.addNewUser(user);
//		Attraction attraction = rewardsService.getAttractionList().get(0);
//		userService.addUserNewVisitedLocation(user,new VisitedLocation(user.getUserId(), attraction, new Date()));
//		tourGuideService.trackUserLocation(user);
//		List<UserReward> userRewards = user.getUserRewards();
//		tourGuideService.getTracker().stopTracking();
////		TimeUnit.MILLISECONDS.sleep(5000);
//        assertEquals(1, userRewards.size());
//	}
//
////	@Test
////	public void isWithinAttractionProximity() {
////		GpsUtil gpsUtil = new GpsUtil();
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
////		Attraction attraction = gpsUtil.getAttractions().get(0);
////		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
////	}
////
//////	@Ignore // Needs fixed - can throw ConcurrentModificationException
////	@Test
////	public void nearAllAttractions() {
////		GpsUtil gpsUtil = new GpsUtil();
////		RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentral());
////		rewardsService.setProximityBuffer(Integer.MAX_VALUE);
////
//////		InternalTestHelper.setInternalUserNumber(1);
////		TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService);
////
////		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
////		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
////		tourGuideService.tracker.stopTracking();
////
////		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
////	}
//
//}
