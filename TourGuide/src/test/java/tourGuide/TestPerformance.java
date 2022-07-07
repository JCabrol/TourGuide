package tourGuide;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;


@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPerformance {

    @Autowired
    private TourGuideService tourGuideService;

    @Autowired
    private UserService userService;

    @Autowired
    private RewardsService rewardsService;


    @Before
    public void init() {
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

    //    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    public void highVolumeTrackLocation() {

        // Users should be incremented up to 100,000, and test finishes within 15 minutes

        List<User> allUsers = userService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<VisitedLocation> allVisitedLocations = tourGuideService.trackAllUsers(allUsers);
        stopWatch.stop();
        tourGuideService.getTracker().stopTracking();
        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

        assertEquals(allUsers.size(), allVisitedLocations.size());
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    //    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    public void highVolumeGetRewards() throws Exception {

        // Users should be incremented up to 100,000, and test finishes within 20 minutes

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Attraction attraction = rewardsService.getAttractionList().get(0);
        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());
            userService.addUserNewVisitedLocation(user, visitedLocation);

        }

        allUsers.forEach(rewardsService::calculateRewards);

        TimeUnit.MILLISECONDS.sleep(5000);

        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0);
        }

        stopWatch.stop();
        tourGuideService.getTracker().stopTracking();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}
