package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.Before;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.GpsUtilRepository;
import tourGuide.repository.RewardCentralRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRewardsService {
	private static final Locale locale = new Locale("en", "US");
	@Before
	public void init() {
		Locale.setDefault(locale);

	}
	@Test
	public void userGetRewards() throws Exception {
		GpsUtilService gpsUtil = new GpsUtilService(new GpsUtilRepository());
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralRepository());
        UserRepository userRepository = new UserRepository();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		tourGuideService.addUserNewVisitedLocation(user,new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
        assertEquals(1, userRewards.size());
	}

//	@Test
//	public void isWithinAttractionProximity() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		Attraction attraction = gpsUtil.getAttractions().get(0);
//		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
//	}
//
////	@Ignore // Needs fixed - can throw ConcurrentModificationException
//	@Test
//	public void nearAllAttractions() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		rewardsService.setProximityBuffer(Integer.MAX_VALUE);
//
////		InternalTestHelper.setInternalUserNumber(1);
//		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//
//		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
//		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
//		tourGuideService.tracker.stopTracking();
//
//		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
//	}

}
