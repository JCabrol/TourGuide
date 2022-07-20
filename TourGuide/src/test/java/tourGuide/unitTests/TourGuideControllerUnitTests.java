package tourGuide.unitTests;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.DTO.CloseAttraction;
import tourGuide.model.DTO.UserCloseAttractionsInfo;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripPricerService;
import tourGuide.service.UserService;
import tripPricer.Provider;

import java.util.*;

import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Tag("controllerTests")
@Tag("tourGuideTests")
@ActiveProfiles({"unitTest","test"})
public class TourGuideControllerUnitTests {

    @MockBean
    private TourGuideService tourGuideService;
    @MockBean
    private TripPricerService tripPricerService;
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Tag("indexTest")
    public void indexTest() throws Exception {

        //GIVEN
        //

        //WHEN
        // the uri "/" is called
        mockMvc.perform(get("/"))

                //THEN
                // the status is "isOk" and the expected welcome message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("Greetings from TourGuide!"));
    }

    @Test
    @Tag("getLocationTest")
    public void getLocationExistingUserTest() throws Exception {

        //GIVEN
        // an existing user
        UUID id = UUID.randomUUID();
        String username = "name1";
        User user = new User(id, username, "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        when(userService.getUser(username)).thenReturn(user);
        when(tourGuideService.getUserLocation(user)).thenReturn(visitedLocation);

        //WHEN
        // the uri "/getLocation" is called with the userName parameter
        mockMvc.perform(get("/getLocation")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected longitude and latitude are returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.longitude", is(2)))
                .andExpect(jsonPath("$.latitude", is(2)));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUser(username);
        verify(tourGuideService, Mockito.times(1)).getUserLocation(user);
    }

    @Test
    @Tag("getLocationTest")
    public void getLocationNonExistingUserTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userService.getUser(username)).thenThrow(objectNotFoundException);

        //WHEN
        // the uri "/getLocation" is called with the userName parameter
        mockMvc.perform(get("/getLocation")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUser(username);
        verify(tourGuideService, Mockito.times(0)).getUserLocation(any(User.class));
    }

    @Test
    @Tag("getLocationTest")
    public void getLocationMissingParameterTest() throws Exception {

        //GIVEN
        // no user given in parameter

        //WHEN
        // the uri "/getLocation" is called
        mockMvc.perform(get("/getLocation"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).getUser(any(String.class));
        verify(tourGuideService, Mockito.times(0)).getUserLocation(any(User.class));
    }

    @Test
    @Tag("getNearByAttractionsTest")
    public void getNearbyAttractionsTest() throws Exception {

        //GIVEN
        // an existing user
        UUID id = UUID.randomUUID();
        String username = "name1";
        User user = new User(id, username, "phoneNumber1", "mail1");
        Location location = new Location(2D, 2D);
        VisitedLocation visitedLocation = new VisitedLocation(id, location, new Date());
        UserCloseAttractionsInfo userCloseAttractionsInfo = new UserCloseAttractionsInfo(location.latitude, location.longitude);
        List<CloseAttraction> closeAttractionList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CloseAttraction closeAttraction = new CloseAttraction("name" + i, i, i, 3 * i, 5 * i);
            closeAttractionList.add(closeAttraction);
        }
        userCloseAttractionsInfo.setCloseAttractions(closeAttractionList);
        when(userService.getUser(username)).thenReturn(user);
        when(tourGuideService.getUserLocation(user)).thenReturn(visitedLocation);
        when(tourGuideService.searchFiveClosestAttractions(visitedLocation)).thenReturn(userCloseAttractionsInfo);

        //WHEN
        // the uri "/getNearbyAttractions" is called with the userName parameter
        mockMvc.perform(get("/getNearbyAttractions")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userLatitude", is(2)))
                .andExpect(jsonPath("$.userLongitude", is(2)))
                .andExpect(jsonPath("$.closeAttractions", iterableWithSize(5)))
                .andExpect(jsonPath("$.closeAttractions.[0].attractionName", is("name0")))
                .andExpect(jsonPath("$.closeAttractions.[1].attractionLatitude", is(1)))
                .andExpect(jsonPath("$.closeAttractions.[2].attractionLongitude", is(2)))
                .andExpect(jsonPath("$.closeAttractions.[3].distanceFromLocation", is(9)))
                .andExpect(jsonPath("$.closeAttractions.[4].rewardPointsForVisitingAttraction", is(20)));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUser(username);
        verify(tourGuideService, Mockito.times(1)).getUserLocation(user);
        verify(tourGuideService, Mockito.times(1)).searchFiveClosestAttractions(visitedLocation);
    }

    @Test
    @Tag("getNearByAttractionsTest")
    public void getNearbyAttractionsUserNonExistingTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userService.getUser(username)).thenThrow(objectNotFoundException);


        //WHEN
        // the uri "/getNearbyAttractions" is called with the userName parameter
        mockMvc.perform(get("/getNearbyAttractions")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUser(username);
        verify(tourGuideService, Mockito.times(0)).getUserLocation(any(User.class));
        verify(tourGuideService, Mockito.times(0)).searchFiveClosestAttractions(any(VisitedLocation.class));
    }

    @Test
    @Tag("getNearByAttractionsTest")
    public void getNearbyAttractionsNoParameterTest() throws Exception {

        //GIVEN
        // no user given in parameter

        //WHEN
        // the uri "/getNearbyAttractions" is called with no parameter
        mockMvc.perform(get("/getNearbyAttractions"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).getUser(any(String.class));
        verify(tourGuideService, Mockito.times(0)).getUserLocation(any(User.class));
        verify(tourGuideService, Mockito.times(0)).searchFiveClosestAttractions(any(VisitedLocation.class));
    }

    @Test
    @Tag("getRewardsTest")
    public void getRewardsTest() throws Exception {

        //GIVEN
        // an existing user
        UUID id = UUID.randomUUID();
        String username = "name1";
        User user = new User(id, username, "phoneNumber1", "mail1");
        VisitedLocation visitedLocation = new VisitedLocation(id, new Location(2D, 2D), new Date());
        Attraction attraction = new Attraction("name", "city", "state", 5D, 5D);
        List<UserReward> userRewardList = of(new UserReward(visitedLocation, attraction, 10));
        when(userService.getUser(username)).thenReturn(user);
        when(userService.getUserRewards(user)).thenReturn(userRewardList);

        //WHEN
        // the uri "/getRewards" is called with the userName parameter
        mockMvc.perform(get("/getRewards")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(1)))
                .andExpect(jsonPath("$.[0].visitedLocation.location.longitude", is(2)))
                .andExpect(jsonPath("$.[0].visitedLocation.location.latitude", is(2)))
                .andExpect(jsonPath("$.[0].attraction.attractionName", is("name")))
                .andExpect(jsonPath("$.[0].attraction.longitude", is(5)))
                .andExpect(jsonPath("$.[0].attraction.latitude", is(5)))
                .andExpect(jsonPath("$.[0].rewardPoints", is(10)));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUser(username);
        verify(userService, Mockito.times(1)).getUserRewards(user);
    }

    @Test
    @Tag("getRewardsTest")
    public void getRewardsUserNonExistingTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userService.getUser(username)).thenThrow(objectNotFoundException);

        //WHEN
        // the uri "/getRewards" is called with the userName parameter
        mockMvc.perform(get("/getRewards")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUser(username);
        verify(userService, Mockito.times(0)).getUserRewards(any(User.class));
    }

    @Test
    @Tag("getRewardsTest")
    public void getRewardsUserNoParameterTest() throws Exception {

        //GIVEN
        // no user given in parameter

        //WHEN
        // the uri "/getRewards" is called with the userName parameter
        mockMvc.perform(get("/getRewards"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).getUser(any(String.class));
        verify(userService, Mockito.times(0)).getUserRewards(any(User.class));
    }

    @Test
    @Tag("getAllCurrentLocationsTest")
    public void getAllCurrentLocationsTest() throws Exception {

        //GIVEN
        //A hashMap returned by tourGuidesService
        HashMap<String, Location> allVisitedLocation = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            UUID id = UUID.randomUUID();
            Location location = new Location(i, i);
            allVisitedLocation.put(id.toString(), location);
        }
        when(tourGuideService.getAllCurrentLocations()).thenReturn(allVisitedLocation);

        //WHEN
        // the uri "/getAllCurrentLocations" is called
        mockMvc.perform(get("/getAllCurrentLocations"))
                //THEN
                // the status is "isOk"
                .andExpect(status().isOk());
        // and the expected methods have been called with expected arguments
        verify(tourGuideService, Mockito.times(1)).getAllCurrentLocations();
    }

    @Test
    @Tag("getTripDealsTest")
    public void getTripDealsTest() throws Exception {

        //GIVEN
        // an existing user
        UUID id = UUID.randomUUID();
        String username = "name1";
        User user = new User(id, username, "phoneNumber1", "mail1");
        List<Provider> providerList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Provider provider = new Provider(UUID.randomUUID(), "name" + i, 5 * i);
            providerList.add(provider);
        }
        when(userService.getUser(username)).thenReturn(user);
        when(tripPricerService.getTripDeals(user)).thenReturn(providerList);

        //WHEN
        // the uri "/getTripDeals" is called with the userName parameter
        mockMvc.perform(get("/getTripDeals")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(5)))
                .andExpect(jsonPath("$.[0].name", is("name0")))
                .andExpect(jsonPath("$.[1].price", is(5)))
                .andExpect(jsonPath("$.[2].name", is("name2")))
                .andExpect(jsonPath("$.[3].price", is(15)))
                .andExpect(jsonPath("$.[4].name", is("name4")));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUser(username);
        verify(tripPricerService, Mockito.times(1)).getTripDeals(user);
    }

    @Test
    @Tag("getTripDealsTest")
    public void getTripDealsUserNonExistingTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userService.getUser(username)).thenThrow(objectNotFoundException);

        //WHEN
        // the uri "/getTripDeals" is called with the userName parameter
        mockMvc.perform(get("/getTripDeals")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUser(username);
        verify(tripPricerService, Mockito.times(0)).getTripDeals(any(User.class));
    }

    @Test
    @Tag("getTripDealsTest")
    public void getTripDealsNoParameterTest() throws Exception {

        //GIVEN
        // no user given in parameter

        //WHEN
        // the uri "/getTripDeals" is called
        mockMvc.perform(get("/getTripDeals"))


                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).getUser(any(String.class));
        verify(tripPricerService, Mockito.times(0)).getTripDeals(any(User.class));
    }

    @Test
    @Tag("errorPageTest")
    public void errorPageTest() throws Exception {

        //GIVEN
        //

        //WHEN
        // a non-existing uri is called
        mockMvc.perform(get("/nonExistingUri"))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The researched page was not found : No handler found for GET /nonExistingUri"));
    }

}
