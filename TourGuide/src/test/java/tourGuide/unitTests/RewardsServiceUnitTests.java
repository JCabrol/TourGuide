package tourGuide.unitTests;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.RewardCentralRepository;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Tag("serviceTests")
@Tag("rewardsTests")
@ActiveProfiles({"unitTest","test"})
public class RewardsServiceUnitTests {

    @Autowired
    private RewardsService rewardsService;

    @MockBean
    private RewardCentralRepository rewardCentralRepository;

    @MockBean
    private UserService userService;

    @Test
    @Tag("isNotInRewardListTest")
    public void isNotInRewardListUserWithoutRewardTest() {

        //GIVEN
        // a user without reward
        User user = new User(UUID.randomUUID(), "name1", "phoneNumber1", "mail1");
        String attractionName = "attraction name";

        //WHEN
        // isNotInRewardList is called
        boolean result = rewardsService.isNotInRewardsList(attractionName, user);

        //THEN
        // it returns true
        assertTrue(result);
    }

    @Test
    @Tag("isNotInRewardListTest")
    public void isNotInRewardListAttractionNotInListTest() {

        //GIVEN
        // an attraction name not in the user's rewards list
        User user = new User(UUID.randomUUID(), "name1", "phoneNumber1", "mail1");
        List<UserReward> userRewardList = new ArrayList<>();
        UserReward userReward = new UserReward(null, new Attraction("another attraction name", "", "", 0, 0), 0);
        userRewardList.add(userReward);
        user.setUserRewards(userRewardList);
        String attractionName = "attraction name";

        //WHEN
        // isNotInRewardList is called
        boolean result = rewardsService.isNotInRewardsList(attractionName, user);

        //THEN
        // it returns true
        assertTrue(result);
    }

    @Test
    @Tag("isNotInRewardListTest")
    public void isNotInRewardListAttractionInListTest() {

        //GIVEN
        // an attraction name in the user's rewards list
        User user = new User(UUID.randomUUID(), "name1", "phoneNumber1", "mail1");
        List<UserReward> userRewardList = new ArrayList<>();
        UserReward userReward = new UserReward(null, new Attraction("attraction name", "", "", 0, 0), 0);
        userRewardList.add(userReward);
        user.setUserRewards(userRewardList);
        String attractionName = "attraction name";

        //WHEN
        // isNotInRewardList is called
        boolean result = rewardsService.isNotInRewardsList(attractionName, user);

        //THEN
        // it returns false
        assertFalse(result);
    }

    @Test
    @Tag("nearAttractionTest")
    public void nearAttractionWhenIsNearTest() {

        //GIVEN
        // an attraction closer than the proximityBuffer from a visitedLocation
        double latitude = 0;
        double longitude = 0;
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), new Location(latitude, longitude), new Date());
        Attraction attraction = new Attraction("attraction name", "", "", latitude, longitude);

        //WHEN
        // nearAttraction is called
        boolean result = rewardsService.nearAttraction(visitedLocation, attraction);

        //THEN
        // it returns true
        assertTrue(result);
    }

    @Test
    @Tag("nearAttractionTest")
    public void nearAttractionWhenIsNotNearTest() {

        //GIVEN
        // an attraction further than the proximityBuffer from a visitedLocation
        double latitude = 0;
        double longitude = 0;
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), new Location(latitude, longitude), new Date());
        Attraction attraction = new Attraction("attraction name", "", "", latitude + rewardsService.getProximityBuffer(), longitude + rewardsService.getProximityBuffer());

        //WHEN
        // nearAttraction is called
        boolean result = rewardsService.nearAttraction(visitedLocation, attraction);

        //THEN
        // it returns false
        assertFalse(result);
    }

    @Test
    @Tag("searchFiveClosestAttractionTest")
    public void searchFiveClosestAttractionTest() throws InterruptedException {

        //GIVEN
        // a full list of attraction
        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Attraction attraction = new Attraction("name" + i, "", "", i, i);
            attractionList.add(attraction);
        }
        rewardsService.setAttractionList(attractionList);
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), new Location(0, 0), new Date());

        //WHEN
        // searchFiveClosestAttraction is called
        List<Attraction> result = rewardsService.searchFiveClosestAttractions(visitedLocation);
        TimeUnit.MILLISECONDS.sleep(500);
        //THEN
        // it returns a list containing the five closest attractions
        assertEquals(5, result.size());
        assertTrue(result.containsAll(attractionList.subList(0, 4)));
        for (int i = 5; i < 10; i++)
            assertFalse(result.contains(attractionList.get(i)));
    }

    @Test
    @Tag("searchFiveClosestAttractionTest")
    public void searchFiveClosestAttractionLessThanFiveAttractionTest() {

        //GIVEN
        // a list of attraction containing less than 5 attractions
        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Attraction attraction = new Attraction("name" + i, "", "", i, i);
            attractionList.add(attraction);
        }
        rewardsService.setAttractionList(attractionList);
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), new Location(0, 0), new Date());

        //WHEN
        // searchFiveClosestAttraction is called
        List<Attraction> result = rewardsService.searchFiveClosestAttractions(visitedLocation);

        //THEN
        // it returns a list containing all the attractions
        assertEquals(3, result.size());
        assertTrue(result.containsAll(attractionList));
    }

    @Test
    @Tag("searchFiveClosestAttractionTest")
    public void searchFiveClosestAttractionEmptyAttractionTest() {

        //GIVEN
        // an empty list of attraction
        List<Attraction> attractionList = new ArrayList<>();
        rewardsService.setAttractionList(attractionList);
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), new Location(0, 0), new Date());

        //WHEN
        // searchFiveClosestAttraction is called
        List<Attraction> result = rewardsService.searchFiveClosestAttractions(visitedLocation);

        //THEN
        // it returns an empty list
        assertEquals(0, result.size());
    }

    @Test
    @Tag("calculateRewardsTest")
    public void calculateRewardsTest() throws InterruptedException {

        //GIVEN
        // a user with several visitedLocations
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        double latitude = 0;
        double longitude = 0;
        double farLatitude = 0;
        double farLongitude = 0;
        while (rewardsService.getDistance(new Location(latitude, longitude), new Location(farLatitude, farLongitude)) <= rewardsService.getProximityBuffer()) {
            latitude++;
            longitude++;
            farLatitude--;
            farLongitude--;
        }
        for (int i = 0; i < 10; i++) {
            VisitedLocation visitedLocation = new VisitedLocation(id, new Location(latitude + i, longitude + i), new Date());
            visitedLocationList.add(visitedLocation);
        }
        List<Attraction> attractionList = new ArrayList<>();
        Attraction closeAttraction = new Attraction("name", "city", "state", farLatitude+2, farLongitude+2);
        attractionList.add(closeAttraction);
        for (int i = 0; i < 10; i++) {
            Attraction attraction = new Attraction("name" + i, "city" + i, "state" + i, farLatitude - i, farLongitude - i);
            attractionList.add(attraction);
        }
        rewardsService.setAttractionList(attractionList);
        user.setVisitedLocations(visitedLocationList);
        when(rewardCentralRepository.getAttractionRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(5);
        doNothing().when(userService).addUserRewards(any(User.class), any(UserReward.class));

        //WHEN
        // calculateRewards is called
        rewardsService.calculateRewards(user);
        int numberOfThread = Thread.currentThread().getThreadGroup().activeCount();
        TimeUnit.MILLISECONDS.sleep(1000);

        //THEN
        // The method is running on several threads
        assertTrue(numberOfThread > 15);
        // and the expected methods have been called with expected arguments
        verify(rewardCentralRepository, Mockito.times(1)).getAttractionRewardPoints(any(UUID.class), any(UUID.class));
        verify(userService, Mockito.times(1)).addUserRewards(any(User.class), any(UserReward.class));
    }

    @Test
    @Tag("calculateRewardsTest")
    public void calculateRewardsWithoutVisitedLocationTest() throws InterruptedException {

        //GIVEN
        // a user without any visitedLocation
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Attraction attraction = new Attraction("name" + i, "city" + i, "state" + i, 100 * i, -100 * i);
            attractionList.add(attraction);
        }
        rewardsService.setAttractionList(attractionList);
        doNothing().when(userService).addUserRewards(any(User.class), any(UserReward.class));

        //WHEN
        // calculateRewards is called
        rewardsService.calculateRewards(user);
        TimeUnit.MILLISECONDS.sleep(500);

        //THEN
        // and the expected methods have been called with expected arguments
        verify(rewardCentralRepository, Mockito.times(0)).getAttractionRewardPoints(any(UUID.class), any(UUID.class));
        verify(userService, Mockito.times(0)).addUserRewards(any(User.class), any(UserReward.class));
    }

    @Test
    @Tag("calculateRewardsTest")
    public void calculateRewardsNotEnoughCLoseAttractionToAddRewardsTest() throws InterruptedException {

        //GIVEN
        // a user with several visitedLocations
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        double latitude = 0;
        double longitude = 0;
        double farLatitude = 0;
        double farLongitude = 0;
        while (rewardsService.getDistance(new Location(latitude, longitude), new Location(farLatitude, farLongitude)) <= rewardsService.getProximityBuffer()) {
            latitude++;
            longitude++;
            farLatitude--;
            farLongitude--;
        }
        for (int i = 0; i < 10; i++) {
            VisitedLocation visitedLocation = new VisitedLocation(id, new Location(latitude + i, longitude + i), new Date());
            visitedLocationList.add(visitedLocation);
        }
        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Attraction attraction = new Attraction("name" + i, "city" + i, "state" + i, farLatitude - i, farLongitude - i);
            attractionList.add(attraction);
        }
        rewardsService.setAttractionList(attractionList);
        user.setVisitedLocations(visitedLocationList);

        //WHEN
        // calculateRewards is called
        rewardsService.calculateRewards(user);
        TimeUnit.MILLISECONDS.sleep(500);

        //THEN
        // the expected methods have been called with expected arguments
        verify(rewardCentralRepository, Mockito.times(0)).getAttractionRewardPoints(any(UUID.class), any(UUID.class));
        verify(userService, Mockito.times(0)).addUserRewards(any(User.class), any(UserReward.class));
    }

    @Test
    @Tag("calculateRewardsTest")
    public void calculateRewardsCloseAttractionsAlreadyInUserRewardListTest() throws InterruptedException {

        //GIVEN
        // a user with several visitedLocations
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            VisitedLocation visitedLocation = new VisitedLocation(id, new Location( i,  i), new Date());
            visitedLocationList.add(visitedLocation);
        }
        user.setVisitedLocations(visitedLocationList);

        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Attraction attraction = new Attraction("name" + i, "city" + i, "state" + i, i, i);
            attractionList.add(attraction);
        }
        rewardsService.setAttractionList(attractionList);

        List<UserReward> userRewardList = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
           UserReward userReward = new UserReward(visitedLocationList.get(i),attractionList.get(i),i);
           userRewardList.add(userReward);
        }
        user.setUserRewards(userRewardList);


        //WHEN
        // calculateRewards is called
        rewardsService.calculateRewards(user);
        TimeUnit.MILLISECONDS.sleep(500);

        //THEN
        // the expected methods have been called with expected arguments
        verify(rewardCentralRepository, Mockito.times(0)).getAttractionRewardPoints(any(UUID.class), any(UUID.class));
        verify(userService, Mockito.times(0)).addUserRewards(any(User.class), any(UserReward.class));
    }
}
