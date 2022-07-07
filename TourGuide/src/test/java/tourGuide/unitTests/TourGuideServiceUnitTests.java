package tourGuide.unitTests;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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
import tourGuide.model.CloseAttraction;
import tourGuide.model.User;
import tourGuide.model.UserCloseAttractionsInfo;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
    @DisplayName("GIVEN a user with a last VisitedLocation existing " +
            "WHEN getUserLocation is called " +
            "THEN the last VisitedLocation is returned.")
    public void getUserLocationExistingLocationTest() {
        //GIVEN
        //a user with a last VisitedLocation existing
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        visitedLocationList.add(visitedLocation);
        user.setVisitedLocations(visitedLocationList);
        when(userService.getUserLastVisitedLocation(user)).thenReturn(visitedLocation);
        //WHEN
        //the function getUserLocation is called
        VisitedLocation result = tourGuideService.getUserLocation(user);
        //THEN
        //the last VisitedLocation is returned
        assertEquals(result, visitedLocation);
        //and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUserLastVisitedLocation(user);
        //and not any new visitedLocation have been calculated or added, not any rewards have been calculated
        assertEquals(visitedLocationList, user.getVisitedLocations());
        verify(gpsUtilService, Mockito.times(0)).getUserLocation(any(UUID.class));
        verify(rewardsService, Mockito.times(0)).calculateRewards(any(User.class));
    }

    @Test
    @Tag("getUserLocationTest")
    @DisplayName("GIVEN a user with not any VisitedLocation " +
            "WHEN getUserLocation is called " +
            "THEN a new VisitedLocation is returned and added to user's list.")
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
        //WHEN
        //the function getUserLocation is called
        VisitedLocation result = tourGuideService.getUserLocation(user);
        //THEN
        //a new VisitedLocation is returned and added to user's list
        assertEquals(result, visitedLocation);
        assertEquals(1, user.getVisitedLocations().size());
        //and the expected methods have been called with expected arguments
        verify(gpsUtilService, Mockito.times(1)).getUserLocation(id);
        verify(rewardsService, Mockito.times(1)).calculateRewards(user);
        verify(userService, Mockito.times(0)).getUserLastVisitedLocation(any(User.class));
    }

    @Test
    @Tag("trackUserLocationTest")
    @DisplayName("GIVEN an existing user " +
            "WHEN trackUserLocation is called " +
            "THEN a new visitedLocation is returned and added to the user's list.")
    public void trackUserLocationTest() {
        //GIVEN
        //an existing user with not any VisitedLocation
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        user.setVisitedLocations(visitedLocationList);
        when(gpsUtilService.getUserLocation(id)).thenReturn(visitedLocation);
        doNothing().when(rewardsService).calculateRewards(user);
        //WHEN
        //the function trackUserLocation is called
        VisitedLocation result = tourGuideService.trackUserLocation(user);
        //THEN
        //a new VisitedLocation is returned and added to user's list
        assertEquals(result, visitedLocation);
        assertEquals(1, user.getVisitedLocations().size());
        //and the expected methods have been called with expected arguments
        verify(gpsUtilService, Mockito.times(1)).getUserLocation(id);
        verify(rewardsService, Mockito.times(1)).calculateRewards(user);
    }

    @Test
    @Tag("searchFiveClosestAttractionsTest")
    @DisplayName("GIVEN a visitedLocation and an existing user " +
            "WHEN searchFiveClosestAttractions is called " +
            "THEN a UserCloseAttractionsInfo object is returned, containing expected information.")
    public void searchFiveClosestAttractionsTest() {
        //GIVEN
        //a visitedLocation and an existing user
        UUID id = UUID.randomUUID();
        User user = new User(id, "name1", "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Attraction attraction = new Attraction("attractionName" + i, "city" + i, "state" + i, i, i);
            attractionList.add(attraction);
        }
        when(rewardsService.searchFiveClosestAttractionsMap(visitedLocation)).thenReturn(attractionList);
        when(rewardsService.getDistance(any(Location.class), any(Location.class))).thenReturn(10D);
        when(userService.getUserById(id)).thenReturn(user);
        when(rewardsService.getRewardPoints(any(Attraction.class), eq(user))).thenReturn(10);
        //WHEN
        //the function searchFiveClosestAttractions is called
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
        //and the expected methods have been called with expected arguments
        verify(rewardsService, Mockito.times(1)).searchFiveClosestAttractionsMap(visitedLocation);
        verify(rewardsService, Mockito.times(5)).getDistance(any(Location.class), any(Location.class));
        verify(userService, Mockito.times(1)).getUserById(id);
        verify(rewardsService, Mockito.times(5)).getRewardPoints(any(Attraction.class), eq(user));
    }


    @Test
    @Tag("searchFiveClosestAttractionsTest")
    @DisplayName("GIVEN a user non-existing " +
            "WHEN searchFiveClosestAttractions is called " +
            "THEN an ObjectNotFoundException is thrown with the expected error message.")
    public void searchFiveClosestAttractionsNonExistingUserTest() {
        //GIVEN
        //a user non-existing
        UUID id = UUID.randomUUID();
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        List<Attraction> attractionList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Attraction attraction = new Attraction("attractionName" + i, "city" + i, "state" + i, i, i);
            attractionList.add(attraction);
        }
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(rewardsService.searchFiveClosestAttractionsMap(visitedLocation)).thenReturn(attractionList);
        when(userService.getUserById(id)).thenThrow(objectNotFoundException);
        //WHEN
        //the function searchFiveClosestAttractions is called
        //THEN
        // a UserCloseAttractionsInfo object is returned, containing expected information
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> tourGuideService.searchFiveClosestAttractions(visitedLocation));
        Assertions.assertEquals("error message", exception.getMessage());
        //and the expected methods have been called with expected arguments
        verify(rewardsService, Mockito.times(1)).searchFiveClosestAttractionsMap(visitedLocation);
        verify(rewardsService, Mockito.times(0)).getDistance(any(Location.class), any(Location.class));
        verify(userService, Mockito.times(1)).getUserById(id);
        verify(rewardsService, Mockito.times(0)).getRewardPoints(any(Attraction.class),any(User.class));
    }
}
