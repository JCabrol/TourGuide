package tourGuide;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.repository.GpsUtilRepository;
import tourGuide.repository.RewardCentralRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.GpsUtilServiceImpl;
import tourGuide.service.RewardsServiceImpl;
import tourGuide.service.TourGuideServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class TestPerformance {
    private static final Locale locale = new Locale("en", "US");

    @Before
    public void init() {
        Locale.setDefault(locale);
        InternalTestHelper.setInternalUserNumber(10000);
    }
    /*
     * A note on performance improvements:
     *
     *     The number of users generated for the high volume tests can be easily adjusted via this method:
     *
     *     		InternalTestHelper.setInternalUserNumber(100000);
     *
     *
     *     These tests can be modified to suit new solutions, just as long as the performance metrics
     *     at the end of the tests remains consistent.
     *
     *     These are performance metrics that we are trying to hit:
     *
     *     highVolumeTrackLocation: 100,000 users within 15 minutes:
     *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
     *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */


    @Test
    public void highVolumeTrackLocation() {
        GpsUtilServiceImpl gpsUtilService = new GpsUtilServiceImpl(new GpsUtilRepository());
        RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtilService, new RewardCentralRepository());
        UserRepository userRepository = new UserRepository();

        // Users should be incremented up to 100,000, and test finishes within 15 minutes

        TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtilService, rewardsService, userRepository);
        List<User> allUsers = tourGuideService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
      tourGuideService.trackAllUsers(allUsers);
        stopWatch.stop();
        tourGuideService.tracker.stopTracking();
        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void highVolumeGetRewards() throws Exception {
        GpsUtilServiceImpl gpsUtil = new GpsUtilServiceImpl(new GpsUtilRepository());
        RewardsServiceImpl rewardsService = new RewardsServiceImpl(gpsUtil, new RewardCentralRepository());
        UserRepository userRepository = new UserRepository();

        // Users should be incremented up to 100,000, and test finishes within 20 minutes

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TourGuideServiceImpl tourGuideService = new TourGuideServiceImpl(gpsUtil, rewardsService, userRepository);

        Attraction attraction = gpsUtil.getAttractions().get(0);
        List<User> allUsers = tourGuideService.getAllUsers();
        for (User user : allUsers) {
            VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());
            tourGuideService.addUserNewVisitedLocation(user, visitedLocation);

        }

        allUsers.forEach(rewardsService::calculateRewards);

        TimeUnit.MILLISECONDS.sleep(5000);

        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0);
        }

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}
