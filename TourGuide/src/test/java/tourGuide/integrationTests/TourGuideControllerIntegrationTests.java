package tourGuide.integrationTests;

import gpsUtil.location.VisitedLocation;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.model.User;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Tag("controllerTests")
@Tag("tourGuideTests")
@ActiveProfiles({"integrationTest","test"})
public class TourGuideControllerIntegrationTests {


    @Autowired
    private UserService userService;
    @Autowired
    private RewardsService rewardsService;


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
        String userName = "internalUser1";
        //WHEN
        // the uri "/getLocation" is called with the userName parameter
        mockMvc.perform(get("/getLocation")
                        .param("userName", userName))

                //THEN
                // the status is "isOk" and a longitude and a latitude
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.longitude").isNumber())
                .andExpect(jsonPath("$.latitude").isNumber());
    }

    @Test
    @Tag("getLocationTest")
    public void getLocationNonExistingUserTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";
        //WHEN
        // the uri "/getLocation" is called with the userName parameter
        mockMvc.perform(get("/getLocation")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The user whose name is " + username + " was not found."));
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
    }

    @Test
    @Tag("getNearByAttractionsTest")
    public void getNearbyAttractionsTest() throws Exception {

        //GIVEN
        // an existing user
        String username = "internalUser1";

        //WHEN
        // the uri "/getNearbyAttractions" is called with the userName parameter
        mockMvc.perform(get("/getNearbyAttractions")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userLatitude").isNumber())
                .andExpect(jsonPath("$.userLongitude").isNumber())
                .andExpect(jsonPath("$.closeAttractions", iterableWithSize(5)))
                .andExpect(jsonPath("$.closeAttractions.[0].attractionName").isString())
                .andExpect(jsonPath("$.closeAttractions.[1].attractionLatitude").isNumber())
                .andExpect(jsonPath("$.closeAttractions.[2].attractionLongitude").isNumber())
                .andExpect(jsonPath("$.closeAttractions.[3].distanceFromLocation").isNumber())
                .andExpect(jsonPath("$.closeAttractions.[4].rewardPointsForVisitingAttraction").isNumber());
    }

    @Test
    @Tag("getNearByAttractionsTest")
    public void getNearbyAttractionsUserNonExistingTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";
        //WHEN
        // the uri "/getNearbyAttractions" is called with the userName parameter
        mockMvc.perform(get("/getNearbyAttractions")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The user whose name is " + username + " was not found."));
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
    }

    @Test
    @Tag("getRewardsTest")
    public void getRewardsUserWithRewardsTest() throws Exception {

        //GIVEN
        // an existing user with rewards
        String username = "internalUser2";
        User user = userService.getUser(username);
        userService.addUserNewVisitedLocation(user, new VisitedLocation(user.getUserId(), rewardsService.getAttractionList().get(1), new Date()));
        rewardsService.calculateRewards(user);
        TimeUnit.MILLISECONDS.sleep(2000);

        //WHEN
        // the uri "/getRewards" is called with the userName parameter
        mockMvc.perform(get("/getRewards")
                        .param("userName", username))
                //THEN
                // the status is "isOk" and the expected information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(1)))
                .andExpect(jsonPath("$.[0].visitedLocation.location.longitude", is(rewardsService.getAttractionList().get(1).longitude)))
                .andExpect(jsonPath("$.[0].visitedLocation.location.latitude", is(rewardsService.getAttractionList().get(1).latitude)))
                .andExpect(jsonPath("$.[0].attraction.attractionName", is(rewardsService.getAttractionList().get(1).attractionName)))
                .andExpect(jsonPath("$.[0].attraction.longitude", is(rewardsService.getAttractionList().get(1).longitude)))
                .andExpect(jsonPath("$.[0].attraction.latitude", is(rewardsService.getAttractionList().get(1).latitude)))
                .andExpect(jsonPath("$.[0].rewardPoints").isNumber());
    }

    @Test
    @Tag("getRewardsTest")
    public void getRewardsUserWithoutRewardTest() throws Exception {

        //GIVEN
        // an existing user with rewards
        String username = "internalUser1";

        //WHEN
        // the uri "/getRewards" is called with the userName parameter
        mockMvc.perform(get("/getRewards")
                        .param("userName", username))
                //THEN
                // the status is "isOk" and the expected information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(0)));
    }

    @Test
    @Tag("getRewardsTest")
    public void getRewardsUserNonExistingTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";


        //WHEN
        // the uri "/getRewards" is called with the userName parameter
        mockMvc.perform(get("/getRewards")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The user whose name is " + username + " was not found."));
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
    }

    @Test
    @Tag("getAllCurrentLocationsTest")
    public void getAllCurrentLocationsTest() throws Exception {

        //GIVEN
        //A hashMap returned by tourGuidesService

        //WHEN
        // the uri "/getAllCurrentLocations" is called
        mockMvc.perform(get("/getAllCurrentLocations"))

                //THEN
                // the status is "isOk"
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    @Tag("getTripDealsTest")
    public void getTripDealsTest() throws Exception {

        //GIVEN
        // an existing user
        String username = "internalUser1";

        //WHEN
        // the uri "/getTripDeals" is called with the userName parameter
        mockMvc.perform(get("/getTripDeals")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", iterableWithSize(5)));
    }

    @Test
    @Tag("getTripDealsTest")
    public void getTripDealsUserNonExistingTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";

        //WHEN
        // the uri "/getTripDeals" is called with the userName parameter
        mockMvc.perform(get("/getTripDeals")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The user whose name is " + username + " was not found."));
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
