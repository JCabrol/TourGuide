package tourGuide.unitTests;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.DTO.CloseAttraction;
import tourGuide.model.DTO.UserCloseAttractionsInfo;
import tourGuide.model.User;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Tag("serviceTests")
@Tag("tourGuideTests")
@ActiveProfiles("unitTest")
public class TourGuideServiceUnitTests {

    @Autowired
    private TourGuideService tourGuideService;

    @MockBean
    private GpsUtilService gpsUtilService;
    @MockBean
    private RewardsService rewardsService;
    @MockBean
    private UserService userService;

    @Test
    @Tag("getUserLocationTest")
    public void getUserLocationExistingLocationTest()  {

        //GIVEN
        // a user with a last VisitedLocation existing
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        visitedLocationList.add(visitedLocation);
        user.setVisitedLocations(visitedLocationList);
        when(userService.getUserLastVisitedLocation(user)).thenReturn(visitedLocation);

        //WHEN
        // the function getUserLocation is called
        VisitedLocation result = tourGuideService.getUserLocation(user);

        //THEN
        // the last VisitedLocation is returned
        assertEquals(result, visitedLocation);
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUserLastVisitedLocation(user);
        // and not any new visitedLocation have been calculated or added, not any rewards have been calculated
        assertEquals(visitedLocationList, user.getVisitedLocations());
        verify(gpsUtilService, Mockito.times(0)).getUserLocation(any(UUID.class));
        verify(rewardsService, Mockito.times(0)).calculateRewards(any(User.class));
        verify(userService, Mockito.times(0)).addUserNewVisitedLocation(any(User.class), any(VisitedLocation.class));
    }

    @Test
    @Tag("getUserLocationTest")
    public void getUserLocationNonExistingLocationTest() {

        //GIVEN
        //a user with not any VisitedLocation
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        user.setVisitedLocations(visitedLocationList);
        when(gpsUtilService.getUserLocation(id)).thenReturn(visitedLocation);
        doNothing().when(rewardsService).calculateRewards(user);
        doNothing().when(userService).addUserNewVisitedLocation(user, visitedLocation);

        //WHEN
        //the function getUserLocation is called
        VisitedLocation result = tourGuideService.getUserLocation(user);

        //THEN
        // a new VisitedLocation is returned and added to user's list
        assertEquals(result, visitedLocation);
        // and the expected methods have been called with expected arguments
        verify(gpsUtilService, Mockito.times(1)).getUserLocation(id);
        verify(rewardsService, Mockito.times(1)).calculateRewards(user);
        verify(userService, Mockito.times(1)).addUserNewVisitedLocation(user, visitedLocation);
        verify(userService, Mockito.times(0)).getUserLastVisitedLocation(any(User.class));

    }

    @Test
    @Tag("trackUserLocationTest")
    public void trackUserLocationTest() throws Exception {

        //GIVEN
        // an existing user with not any VisitedLocation
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        user.setVisitedLocations(visitedLocationList);
        when(gpsUtilService.getUserLocation(id)).thenReturn(visitedLocation);
        doNothing().when(rewardsService).calculateRewards(user);
        doNothing().when(userService).addUserNewVisitedLocation(user, visitedLocation);

        //WHEN
        // the function trackUserLocation is called
        VisitedLocation result = tourGuideService.trackUserLocation(user);

        //THEN
        // a new VisitedLocation is returned
        assertEquals(result, visitedLocation);
        // and the expected methods have been called with expected arguments
        verify(gpsUtilService, Mockito.times(1)).getUserLocation(id);
        verify(rewardsService, Mockito.times(1)).calculateRewards(user);
        verify(userService, Mockito.times(1)).addUserNewVisitedLocation(user, visitedLocation);
    }

    @Test
    @Tag("searchFiveClosestAttractionsTest")
    public void searchFiveClosestAttractionsTest() {

        //GIVEN
        // a visitedLocation and an existing user
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Attraction attraction = new Attraction("attractionName" + i, "city" + i, "state" + i, i, i);
            attractionList.add(attraction);
        }
        when(rewardsService.searchFiveClosestAttractions(visitedLocation)).thenReturn(attractionList);
        when(rewardsService.getDistance(any(Location.class), any(Location.class))).thenReturn(10D);
        when(userService.getUserById(id)).thenReturn(user);
        when(rewardsService.getRewardPoints(any(Attraction.class), eq(user))).thenReturn(10);

        //WHEN
        // the function searchFiveClosestAttractions is called
        UserCloseAttractionsInfo result = tourGuideService.searchFiveClosestAttractions(visitedLocation);

        //THEN
        // a UserCloseAttractionsInfo object is returned, containing expected information
        assertEquals(2D, result.getUserLatitude(), 0);
        assertEquals(2D, result.getUserLongitude(), 0);
        assertEquals(5, result.getCloseAttractions().size());
        for (CloseAttraction closeAttraction : result.getCloseAttractions()) {
            assertEquals(10, closeAttraction.getRewardPointsForVisitingAttraction());
            assertEquals(10D, closeAttraction.getDistanceFromLocation(), 0);
        }
        // and the expected methods have been called with expected arguments
        verify(rewardsService, Mockito.times(1)).searchFiveClosestAttractions(visitedLocation);
        verify(rewardsService, Mockito.times(5)).getDistance(any(Location.class), any(Location.class));
        verify(userService, Mockito.times(1)).getUserById(id);
        verify(rewardsService, Mockito.times(5)).getRewardPoints(any(Attraction.class), eq(user));
    }


    @Test
    @Tag("searchFiveClosestAttractionsTest")
    public void searchFiveClosestAttractionsNonExistingUserTest() {

        //GIVEN
        // a user non-existing
        UUID id = UUID.randomUUID();
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Attraction attraction = new Attraction("attractionName" + i, "city" + i, "state" + i, i, i);
            attractionList.add(attraction);
        }
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(rewardsService.searchFiveClosestAttractions(visitedLocation)).thenReturn(attractionList);
        when(userService.getUserById(id)).thenThrow(objectNotFoundException);

        //WHEN
        // the function searchFiveClosestAttractions is called

        //THEN
        // a UserCloseAttractionsInfo object is returned, containing expected information
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> tourGuideService.searchFiveClosestAttractions(visitedLocation));
        assertEquals("error message", exception.getMessage());
        // and the expected methods have been called with expected arguments
        verify(rewardsService, Mockito.times(1)).searchFiveClosestAttractions(visitedLocation);
        verify(rewardsService, Mockito.times(0)).getDistance(any(Location.class), any(Location.class));
        verify(userService, Mockito.times(1)).getUserById(id);
        verify(rewardsService, Mockito.times(0)).getRewardPoints(any(Attraction.class), any(User.class));
    }


    @Test
    @Tag("getAllCurrentLocationsTest")
    public void getAllCurrentLocationsTest() {

        //GIVEN
        // existing users with existing visitedLocations
        List<User> userList = new ArrayList<>();

        UUID id1 = UUID.randomUUID();
        String name1 = "name1";
        String phoneNumber1 = "phoneNumber1";
        String mail1 = "mail1";

        UUID id2 = UUID.randomUUID();
        String name2 = "name2";
        String phoneNumber2 = "phoneNumber2";
        String mail2 = "mail2";

        UUID id3 = UUID.randomUUID();
        String name3 = "name3";
        String phoneNumber3 = "phoneNumber3";
        String mail3 = "mail3";

        User user1 = new User(id1, name1, phoneNumber1, mail1);
        User user2 = new User(id2, name2, phoneNumber2, mail2);
        User user3 = new User(id3, name3, phoneNumber3, mail3);

        VisitedLocation visitedLocation1 = new VisitedLocation(id1, new Location(1D, 1D), new Date());
        VisitedLocation visitedLocation2 = new VisitedLocation(id2, new Location(2D, 2D), new Date());
        VisitedLocation visitedLocation3 = new VisitedLocation(id3, new Location(3D, 3D), new Date());

        user1.setVisitedLocations(of(visitedLocation1));
        user2.setVisitedLocations(of(visitedLocation2));
        user3.setVisitedLocations(of(visitedLocation3));

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        when(userService.getUserLastVisitedLocation(user1)).thenReturn(visitedLocation1);
        when(userService.getUserLastVisitedLocation(user2)).thenReturn(visitedLocation2);
        when(userService.getUserLastVisitedLocation(user3)).thenReturn(visitedLocation3);
        when(userService.getAllUsers()).thenReturn(userList);

        //WHEN
        // getAllCurrentLocation is called
        HashMap<String, Location> result = tourGuideService.getAllCurrentLocations();

        //THEN
        // the expected hashmap is returned.
        assertEquals(3, result.size());
        assertEquals(result.get(id1.toString()), visitedLocation1.location);
        assertEquals(result.get(id2.toString()), visitedLocation2.location);
        assertEquals(result.get(id3.toString()), visitedLocation3.location);
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getAllUsers();
        verify(userService, Mockito.times(1)).getUserLastVisitedLocation(user1);
        verify(userService, Mockito.times(1)).getUserLastVisitedLocation(user2);
        verify(userService, Mockito.times(1)).getUserLastVisitedLocation(user3);
        verify(gpsUtilService, Mockito.times(0)).getUserLocation(any(UUID.class));
    }

    @Test
    @Tag("getAllCurrentLocationsTest")
    public void getAllCurrentLocationsUsersWithoutLocationTest() {

        //GIVEN
        // existing users without existing visitedLocations
        List<User> userList = new ArrayList<>();

        UUID id1 = UUID.randomUUID();
        String name1 = "name1";
        String phoneNumber1 = "phoneNumber1";
        String mail1 = "mail1";

        UUID id2 = UUID.randomUUID();
        String name2 = "name2";
        String phoneNumber2 = "phoneNumber2";
        String mail2 = "mail2";

        UUID id3 = UUID.randomUUID();
        String name3 = "name3";
        String phoneNumber3 = "phoneNumber3";
        String mail3 = "mail3";

        User user1 = new User(id1, name1, phoneNumber1, mail1);
        User user2 = new User(id2, name2, phoneNumber2, mail2);
        User user3 = new User(id3, name3, phoneNumber3, mail3);

        VisitedLocation visitedLocation1 = new VisitedLocation(id1, new Location(1D, 1D), new Date());
        VisitedLocation visitedLocation2 = new VisitedLocation(id2, new Location(2D, 2D), new Date());
        VisitedLocation visitedLocation3 = new VisitedLocation(id3, new Location(3D, 3D), new Date());


        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        when(gpsUtilService.getUserLocation(id1)).thenReturn(visitedLocation1);
        when(gpsUtilService.getUserLocation(id2)).thenReturn(visitedLocation2);
        when(gpsUtilService.getUserLocation(id3)).thenReturn(visitedLocation3);
        when(userService.getAllUsers()).thenReturn(userList);

        //WHEN
        // getAllCurrentLocation is called
        HashMap<String, Location> result = tourGuideService.getAllCurrentLocations();

        //THEN
        // the expected hashmap is returned.
        assertEquals(3, result.size());
        assertEquals(result.get(id1.toString()), visitedLocation1.location);
        assertEquals(result.get(id2.toString()), visitedLocation2.location);
        assertEquals(result.get(id3.toString()), visitedLocation3.location);
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getAllUsers();
        verify(userService, Mockito.times(0)).getUserLastVisitedLocation(any(User.class));
        verify(gpsUtilService, Mockito.times(1)).getUserLocation(id1);
        verify(gpsUtilService, Mockito.times(1)).getUserLocation(id2);
        verify(gpsUtilService, Mockito.times(1)).getUserLocation(id3);
    }

    @Test
    @Tag("getAllCurrentLocationsTest")
    public void getAllCurrentLocationsEmptyListOfUsersTest() {

        //GIVEN
        // an empty list of users
        List<User> userList = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(userList);

        //WHEN
        // getAllCurrentLocation is called
        HashMap<String, Location> result = tourGuideService.getAllCurrentLocations();

        //THEN
        // an empty hashmap is returned.
        assertEquals(0, result.size());
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getAllUsers();
        verify(userService, Mockito.times(0)).getUserLastVisitedLocation(any(User.class));
        verify(gpsUtilService, Mockito.times(0)).getUserLocation(any(UUID.class));
    }

    @Test
    @Tag("TrackAllUsersTest")
    public void trackAllUsersTest() throws Exception {

        //GIVEN
        // a list of users
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(new User(UUID.randomUUID(), "name" + i, "phoneNumber" + i, "mail" + i));
        }
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            UUID id = userList.get(i).getUserId();
            visitedLocationList.add(new VisitedLocation(id, new Location(i, i), new Date()));
        }

        when(gpsUtilService.getUserLocation(any(UUID.class)))
                .thenReturn(visitedLocationList.get(0))
                .thenReturn(visitedLocationList.get(1))
                .thenReturn(visitedLocationList.get(2))
                .thenReturn(visitedLocationList.get(3))
                .thenReturn(visitedLocationList.get(4));
        when(userService.getUserById(any(UUID.class)))
                .thenReturn(userList.get(0))
                .thenReturn(userList.get(1))
                .thenReturn(userList.get(2))
                .thenReturn(userList.get(3))
                .thenReturn(userList.get(4));
        doNothing().when(rewardsService).calculateRewards(any(User.class));
        doNothing().when(userService).addUserNewVisitedLocation(any(User.class), any(VisitedLocation.class));

        //WHEN
        // trackAllUsers is called
        List<VisitedLocation> result = tourGuideService.trackAllUsers(userList);
        int numberOfThread = Thread.currentThread().getThreadGroup().activeCount();
        TimeUnit.MILLISECONDS.sleep(100);

        //THEN
        // the expected list of visitedLocations is returned and the method is running with several threads
        assertEquals(5, result.size());
        assertTrue(numberOfThread > 10);
        // and the expected methods have been called with expected arguments
        verify(gpsUtilService, Mockito.times(5)).getUserLocation(any(UUID.class));
        verify(userService, Mockito.times(5)).getUserById(any(UUID.class));
        verify(rewardsService, Mockito.times(5)).calculateRewards(any(User.class));
        verify(userService, Mockito.times(5)).addUserNewVisitedLocation(any(User.class), any(VisitedLocation.class));
    }


    @Test
    @Tag("TrackAllUsersTest")
    public void trackAllUsersEmptyUsersTest() throws Exception {

        //GIVEN
        // a list of users
        List<User> userList = new ArrayList<>();

        //WHEN
        // trackAllUsers is called
        List<VisitedLocation> result = tourGuideService.trackAllUsers(userList);

        //THEN
        // an empty list is returned
        assertEquals(0, result.size());
        // and the expected methods have been called with expected arguments
        verify(gpsUtilService, Mockito.times(0)).getUserLocation(any(UUID.class));
        verify(userService, Mockito.times(0)).getUserById(any(UUID.class));
        verify(rewardsService, Mockito.times(0)).calculateRewards(any(User.class));
        verify(userService, Mockito.times(0)).addUserNewVisitedLocation(any(User.class), any(VisitedLocation.class));
    }


}
