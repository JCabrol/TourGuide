package tourGuide.integrationTests;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Tag("controllerTests")
@Tag("userTests")
public class UserControllerIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Tag("getUserTest")
    public void getUserExistingTest() throws Exception {

        //GIVEN
        // an existing user
        String username = "internalUser1";

        //WHEN
        // the uri "/getUser" is called with the userName parameter
        mockMvc.perform(get("/getUser")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected user information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is(username)))
                .andExpect(jsonPath("$.phoneNumber", is("000")))
                .andExpect(jsonPath("$.emailAddress", is(username + "@tourGuide.com")));
    }

    @Test
    @Tag("getUserTest")
    public void getUserNonExistingTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";

        //WHEN
        // the uri "/getUser" is called with the userName parameter
        mockMvc.perform(get("/getUser")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The user whose name is " + username + " was not found."));

    }

    @Test
    @Tag("getUserTest")
    public void getUserNoParameterTest() throws Exception {

        //GIVEN
        // no user given in parameter

        //WHEN
        // the uri "/getUser" is called with the userName parameter
        mockMvc.perform(get("/getUser"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
    }

    @Test
    @Tag("createUserTest")
    public void createUserAllParametersTest() throws Exception {

        //GIVEN
        // all information for userDTO
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String email = "email";
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/createUser" is called with all parameters
        mockMvc.perform(post("/createUser")
                        .param("userName", userName)
                        .param("phoneNumber", phoneNumber)
                        .param("emailAddress", email))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The user with name " + userName + " has been added."));
        assertEquals(userService.getUser(userName).getUserName(), userName);
        assertEquals(userService.getUser(userName).getPhoneNumber(), phoneNumber);
        assertEquals(userService.getUser(userName).getEmailAddress(), email);
        assertEquals(numberOfUser+1, userService.getAllUsers().size());
    }

    @Test
    @Tag("createUserTest")
    public void createUserOnlyUserNameTest() throws Exception {

        //GIVEN
        // only userName for userDTO
        String userName = "name";
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/createUser" is called with only userName parameter
        mockMvc.perform(post("/createUser")
                        .param("userName", userName))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The user with name " + userName + " has been added."));
        assertEquals(userService.getUser(userName).getUserName(), userName);
        assertNull(userService.getUser(userName).getPhoneNumber());
        assertNull(userService.getUser(userName).getEmailAddress());
        assertEquals(numberOfUser+1, userService.getAllUsers().size());
    }

    @Test
    @Tag("createUserTest")
    public void createUserNoParameterTest() throws Exception {

        //GIVEN
        // no parameter given
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/createUser" is called without parameter
        mockMvc.perform(post("/createUser"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        assertEquals(numberOfUser, userService.getAllUsers().size());
    }

    @Test
    @Tag("createUserTest")
    public void createUserAlreadyExistingTest() throws Exception {

        //GIVEN
        // a user already existing
        String userName = "internalUser1";
        String phoneNumber = "phoneNumber";
        String email = "email";
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/createUser" is called with all parameters
        mockMvc.perform(post("/createUser")
                        .param("userName", userName)
                        .param("phoneNumber", phoneNumber)
                        .param("emailAddress", email))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The user whose name is " + userName + " was already existing, so it couldn't have been added."));
        assertEquals(numberOfUser, userService.getAllUsers().size());
    }

    @Test
    @Tag("updateUserTest")
    public void updateUserAllParametersTest() throws Exception {

        //GIVEN
        // all information for userDTO
        String userName = "internalUser1";
        String phoneNumber = "phoneNumber";
        String email = "email";
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/updateUser" is called with all parameters
        mockMvc.perform(put("/updateUser")
                        .param("userName", userName)
                        .param("phoneNumber", phoneNumber)
                        .param("emailAddress", email))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The user with name " + userName + " has been updated."));
        assertEquals(userService.getUser(userName).getUserName(), userName);
        assertEquals(userService.getUser(userName).getPhoneNumber(), phoneNumber);
        assertEquals(userService.getUser(userName).getEmailAddress(), email);
        assertEquals(numberOfUser, userService.getAllUsers().size());
    }

    @Test
    @Tag("updateUserTest")
    public void updateUserOnlyUserNameTest() throws Exception {

        //GIVEN
        // only userName for userDTO
        String userName = "internalUser1";
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/updateUser" is called with only userName parameter
        mockMvc.perform(put("/updateUser")
                        .param("userName", userName))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The user with name " + userName + " has been updated."));
        assertEquals(userService.getUser(userName).getUserName(), userName);
        assertEquals(userService.getUser(userName).getPhoneNumber(), "000");
        assertEquals(userService.getUser(userName).getEmailAddress(), userName + "@tourGuide.com");
        assertEquals(numberOfUser, userService.getAllUsers().size());
    }

    @Test
    @Tag("updateUserTest")
    public void updateUserNoParameterTest() throws Exception {

        //GIVEN
        // no parameter given
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/updateUser" is called without parameter
        mockMvc.perform(put("/updateUser"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        assertEquals(numberOfUser, userService.getAllUsers().size());
    }

    @Test
    @Tag("updateUserTest")
    public void updateUserNonExistingTest() throws Exception {

        //GIVEN
        // a ObjectNotFoundException thrown by userService
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String email = "email";
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/updateUser" is called with all parameters
        mockMvc.perform(put("/updateUser")
                        .param("userName", userName)
                        .param("phoneNumber", phoneNumber)
                        .param("emailAddress", email))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The user whose name is " + userName + " was not found."));
        assertEquals(numberOfUser, userService.getAllUsers().size());
    }

    @Test
    @Tag("deleteUserTest")
    public void deleteUserTest() throws Exception {

        //GIVEN
        // all information for userDTO
        String userName = "internalUser1";
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/deleteUser" is called with userName parameter
        mockMvc.perform(delete("/deleteUser")
                        .param("userName", userName))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The user with name " + userName + " has been deleted."));
        assertEquals(numberOfUser-1, userService.getAllUsers().size());
    }

    @Test
    @Tag("deleteUserTest")
    public void deleteUserNotFoundTest() throws Exception {

        //GIVEN
        // all information for userDTO
        String userName = "name";
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/deleteUser" is called with userName parameter
        mockMvc.perform(delete("/deleteUser")
                        .param("userName", userName))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The user whose name is " + userName + " was not found."));
        assertEquals(numberOfUser, userService.getAllUsers().size());

    }

    @Test
    @Tag("deleteUserTest")
    public void deleteUserNoParameterTest() throws Exception {

        //GIVEN
        // no parameter
        int numberOfUser = userService.getAllUsers().size();

        //WHEN
        // the uri "/deleteUser" is called with no parameter
        mockMvc.perform(delete("/deleteUser"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        assertEquals(numberOfUser, userService.getAllUsers().size());
    }

    @Test
    @Tag("getUserPreferencesTest")
    public void getUserPreferencesTest() throws Exception {

        //GIVEN
        // an existing user
        String username = "internalUser1";

        //WHEN
        // the uri "/getUserPreferences" is called with the userName parameter
        mockMvc.perform(get("/getUserPreferences")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected userPreferences information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attractionProximity", is(Integer.MAX_VALUE)))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.lowerPricePoint", is(0)))
                .andExpect(jsonPath("$.highPricePoint", is(Integer.MAX_VALUE)))
                .andExpect(jsonPath("$.tripDuration", is(5)))
                .andExpect(jsonPath("$.ticketQuantity", is(1)))
                .andExpect(jsonPath("$.numberOfAdults", is(2)))
                .andExpect(jsonPath("$.numberOfChildren", is(2)));
    }

    @Test
    @Tag("getUserPreferencesTest")
    public void getUserPreferencesNotFoundTest() throws Exception {

        //GIVEN
        // a non-existing user
        String userName = "userName";

        //WHEN
        // the uri "/getUserPreferences" is called with the userName parameter
        mockMvc.perform(get("/getUserPreferences")
                        .param("userName", userName))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("The user whose name is " + userName + " was not found."));
    }

    @Test
    @Tag("getUserPreferencesTest")
    public void getUserPreferencesNoParameterTest() throws Exception {

        //GIVEN
        // no parameter

        //WHEN
        // the uri "/getUserPreferences" is called without parameter
        mockMvc.perform(get("/getUserPreferences"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
    }

    @Test
    @Tag("updateUserPreferencesTest")
    public void updateUserPreferencesTest() throws Exception {

        //GIVEN
        // all information for userPreferencesDTO
        String userName = "internalUser1";
        int attractionProximity = 100;
        String currency = "EUR";
        int lowerPricePoint = 10;
        int highPricePoint = 1000;
       int tripDuration = 4;
        int ticketQuantity = 8;
        int numberOfAdults = 1;
        int numberOfChildren = 1;

        //WHEN
        // the uri "/updateUserPreferences" is called with all parameters
        mockMvc.perform(put("/updateUserPreferences")
                        .param("userName", userName)
                        .param("currency", currency)
                        .param("attractionProximity", String.valueOf(attractionProximity))
                        .param("lowerPricePoint", String.valueOf(lowerPricePoint))
                        .param("highPricePoint", String.valueOf(highPricePoint))
                        .param("tripDuration", String.valueOf(tripDuration))
                        .param("ticketQuantity", String.valueOf(ticketQuantity))
                        .param("numberOfAdults", String.valueOf(numberOfAdults))
                        .param("numberOfChildren", String.valueOf(numberOfChildren)))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The preferences for user " + userName + " have been updated."));
        assertEquals(currency, userService.getUser(userName).getUserPreferences().getCurrency().getCurrencyCode());
        assertEquals(numberOfAdults, userService.getUser(userName).getUserPreferences().getNumberOfAdults());
        assertEquals(attractionProximity, userService.getUser(userName).getUserPreferences().getAttractionProximity());
        assertEquals(lowerPricePoint, userService.getUser(userName).getUserPreferences().getLowerPricePoint().getNumber().intValueExact());
        assertEquals(highPricePoint, userService.getUser(userName).getUserPreferences().getHighPricePoint().getNumber().intValueExact());
        assertEquals(tripDuration,userService.getUser(userName).getUserPreferences().getTripDuration());
        assertEquals(ticketQuantity, userService.getUser(userName).getUserPreferences().getTicketQuantity());
        assertEquals(numberOfChildren, userService.getUser(userName).getUserPreferences().getNumberOfChildren());
    }
}
