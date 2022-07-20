package tourGuide.performanceTests;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPerformance {

    @Autowired
    private TourGuideService tourGuideService;

    @Autowired
    private UserService userService;

    @Autowired
    private RewardsService rewardsService;

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
    @Before
    public void init() {
        InternalTestHelper.setInternalUserNumber(100000);
    }

    @Test
    public void highVolumeTrackLocation() {

        //Given
        //A list of 100 000 users
        List<User> allUsers = userService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //When
        //The function trackAllUsers is called
        List<VisitedLocation> allVisitedLocations = tourGuideService.trackAllUsers(allUsers);
        stopWatch.stop();
        tourGuideService.getTracker().stopTracking();
        log.info("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

        //Then
        //The test finishes within 15 minutes and the number of visitedLocations returned corresponds to the number of users
        assertEquals(allUsers.size(), allVisitedLocations.size());
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void highVolumeGetRewards() throws Exception {

        //Given
        //A list of 100 000 users and a new close visitedLocation added for each user
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Attraction attraction = rewardsService.getAttractionList().get(0);
        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), attraction, new Date());
            userService.addUserNewVisitedLocation(user, visitedLocation);
        }

        //When
        //The function calculateRewards is called for every user
        allUsers.forEach(rewardsService::calculateRewards);
        TimeUnit.MILLISECONDS.sleep(5000);

        //Then
        //There are rewards for every user and the test finishes within 20 minutes
        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0);
        }
        stopWatch.stop();
        tourGuideService.getTracker().stopTracking();
        log.info("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}
