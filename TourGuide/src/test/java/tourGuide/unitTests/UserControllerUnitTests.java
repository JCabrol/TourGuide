package tourGuide.unitTests;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.exception.ObjectAlreadyExistingException;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.DTO.UserDTO;
import tourGuide.model.DTO.UserPreferencesDTO;
import tourGuide.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Tag("controllerTests")
@Tag("userTests")
@ActiveProfiles({"unitTest","test"})
public class UserControllerUnitTests {


    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Tag("getUserTest")
    public void getUserExistingTest() throws Exception {

        //GIVEN
        // an existing user
        String username = "name";
        String phoneNumber = "phoneNumber";
        String email = "email";
        UserDTO user = new UserDTO(username, phoneNumber, email);
        when(userService.getUserDTO(username)).thenReturn(user);


        //WHEN
        // the uri "/getUser" is called with the userName parameter
        mockMvc.perform(get("/getUser")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected user information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is(username)))
                .andExpect(jsonPath("$.phoneNumber", is(phoneNumber)))
                .andExpect(jsonPath("$.emailAddress", is(email)));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUserDTO(username);
    }

    @Test
    @Tag("getUserTest")
    public void getUserNonExistingTest() throws Exception {

        //GIVEN
        // a non-existing user
        String username = "name1";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userService.getUserDTO(username)).thenThrow(objectNotFoundException);


        //WHEN
        // the uri "/getUser" is called with the userName parameter
        mockMvc.perform(get("/getUser")
                        .param("userName", username))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUserDTO(username);
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
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).getUserDTO(any(String.class));
    }

    @Test
    @Tag("createUserTest")
    public void createUserAllParametersTest() throws Exception {

        //GIVEN
        // all information for userDTO
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String email = "email";
        final ArgumentCaptor<UserDTO> arg = ArgumentCaptor.forClass(UserDTO.class);
        doNothing().when(userService).addNewUser(any(UserDTO.class));

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
        // and the expected methods have been called with expected arguments
        verify(userService).addNewUser(arg.capture());
        assertEquals(userName, arg.getValue().getUserName());
        assertEquals(phoneNumber, arg.getValue().getPhoneNumber());
        assertEquals(email, arg.getValue().getEmailAddress());
        verify(userService, Mockito.times(1)).addNewUser(any(UserDTO.class));
    }

    @Test
    @Tag("createUserTest")
    public void createUserOnlyUserNameTest() throws Exception {

        //GIVEN
        // only userName for userDTO
        String userName = "name";
        final ArgumentCaptor<UserDTO> arg = ArgumentCaptor.forClass(UserDTO.class);
        doNothing().when(userService).addNewUser(any(UserDTO.class));

        //WHEN
        // the uri "/createUser" is called with only userName parameter
        mockMvc.perform(post("/createUser")
                        .param("userName", userName))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The user with name " + userName + " has been added."));
        // and the expected methods have been called with expected arguments
        verify(userService).addNewUser(arg.capture());
        assertEquals(userName, arg.getValue().getUserName());
        assertNull(arg.getValue().getPhoneNumber());
        assertNull(arg.getValue().getEmailAddress());
        verify(userService, Mockito.times(1)).addNewUser(any(UserDTO.class));
    }

    @Test
    @Tag("createUserTest")
    public void createUserNoParameterTest() throws Exception {

        //GIVEN
        // no parameter given


        //WHEN
        // the uri "/createUser" is called without parameter
        mockMvc.perform(post("/createUser"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).addNewUser(any(UserDTO.class));
    }

    @Test
    @Tag("createUserTest")
    public void createUserAlreadyExistingTest() throws Exception {

        //GIVEN
        // a ObjectAlreadyExistingException thrown by userService
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String email = "email";
        ObjectAlreadyExistingException objectAlreadyExistingException = new ObjectAlreadyExistingException("error message");
        doThrow(objectAlreadyExistingException).when(userService).addNewUser(any(UserDTO.class));

        //WHEN
        // the uri "/createUser" is called with all parameters
        mockMvc.perform(post("/createUser")
                        .param("userName", userName)
                        .param("phoneNumber", phoneNumber)
                        .param("emailAddress", email))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).addNewUser(any(UserDTO.class));
    }

    @Test
    @Tag("updateUserTest")
    public void updateUserAllParametersTest() throws Exception {

        //GIVEN
        // all information for userDTO
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String email = "email";
        final ArgumentCaptor<UserDTO> arg = ArgumentCaptor.forClass(UserDTO.class);
        doNothing().when(userService).updateUser(any(UserDTO.class));

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
        // and the expected methods have been called with expected arguments
        verify(userService).updateUser(arg.capture());
        assertEquals(userName, arg.getValue().getUserName());
        assertEquals(phoneNumber, arg.getValue().getPhoneNumber());
        assertEquals(email, arg.getValue().getEmailAddress());
        verify(userService, Mockito.times(1)).updateUser(any(UserDTO.class));
    }

    @Test
    @Tag("updateUserTest")
    public void updateUserOnlyUserNameTest() throws Exception {

        //GIVEN
        // only userName for userDTO
        String userName = "name";
        final ArgumentCaptor<UserDTO> arg = ArgumentCaptor.forClass(UserDTO.class);
        doNothing().when(userService).updateUser(any(UserDTO.class));

        //WHEN
        // the uri "/updateUser" is called with only userName parameter
        mockMvc.perform(put("/updateUser")
                        .param("userName", userName))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The user with name " + userName + " has been updated."));
        // and the expected methods have been called with expected arguments
        verify(userService).updateUser(arg.capture());
        assertEquals(userName, arg.getValue().getUserName());
        assertNull(arg.getValue().getPhoneNumber());
        assertNull(arg.getValue().getEmailAddress());
        verify(userService, Mockito.times(1)).updateUser(any(UserDTO.class));
    }

    @Test
    @Tag("updateUserTest")
    public void updateUserNoParameterTest() throws Exception {

        //GIVEN
        // no parameter given


        //WHEN
        // the uri "/updateUser" is called without parameter
        mockMvc.perform(put("/updateUser"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).updateUser(any(UserDTO.class));
    }

    @Test
    @Tag("updateUserTest")
    public void updateUserNonExistingTest() throws Exception {

        //GIVEN
        // a ObjectNotFoundException thrown by userService
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String email = "email";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        doThrow(objectNotFoundException).when(userService).updateUser(any(UserDTO.class));

        //WHEN
        // the uri "/updateUser" is called with all parameters
        mockMvc.perform(put("/updateUser")
                        .param("userName", userName)
                        .param("phoneNumber", phoneNumber)
                        .param("emailAddress", email))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).updateUser(any(UserDTO.class));
    }

    @Test
    @Tag("deleteUserTest")
    public void deleteUserTest() throws Exception {

        //GIVEN
        // all information for userDTO
        String userName = "name";
        doNothing().when(userService).deleteUser(userName);

        //WHEN
        // the uri "/deleteUser" is called with userName parameter
        mockMvc.perform(delete("/deleteUser")
                        .param("userName", userName))

                //THEN
                // the status is "isOk" and the expected succeed message is returned
                .andExpect(status().isOk())
                .andExpect(content().string("The user with name " + userName + " has been deleted."));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).deleteUser(userName);
    }

    @Test
    @Tag("deleteUserTest")
    public void deleteUserNotFoundTest() throws Exception {

        //GIVEN
        // all information for userDTO
        String userName = "name";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        doThrow(objectNotFoundException).when(userService).deleteUser(userName);

        //WHEN
        // the uri "/deleteUser" is called with userName parameter
        mockMvc.perform(delete("/deleteUser")
                        .param("userName", userName))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).deleteUser(userName);
    }

    @Test
    @Tag("deleteUserTest")
    public void deleteUserNoParameterTest() throws Exception {

        //GIVEN
        // no parameter


        //WHEN
        // the uri "/deleteUser" is called with no parameter
        mockMvc.perform(delete("/deleteUser"))

                //THEN
                // the status is "isBadRequest" and the expected error message is returned
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The request is not correct : a request parameter is missing or wrong.\n"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).deleteUser(any(String.class));
    }

    @Test
    @Tag("getUserPreferencesTest")
    public void getUserPreferencesTest() throws Exception {

        //GIVEN
        // an existing user
        String username = "name";
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(100, "EUR", 0, 1000, 5, 3, 2, 2);
        when(userService.getUserPreferences(username)).thenReturn(userPreferencesDTO);


        //WHEN
        // the uri "/getUserPreferences" is called with the userName parameter
        mockMvc.perform(get("/getUserPreferences")
                        .param("userName", username))

                //THEN
                // the status is "isOk" and the expected userPreferences information is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attractionProximity", is(100)))
                .andExpect(jsonPath("$.currency", is("EUR")))
                .andExpect(jsonPath("$.lowerPricePoint", is(0)))
                .andExpect(jsonPath("$.highPricePoint", is(1000)))
                .andExpect(jsonPath("$.tripDuration", is(5)))
                .andExpect(jsonPath("$.ticketQuantity", is(3)))
                .andExpect(jsonPath("$.numberOfAdults", is(2)))
                .andExpect(jsonPath("$.numberOfChildren", is(2)));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUserPreferences(username);
    }

    @Test
    @Tag("getUserPreferencesTest")
    public void getUserPreferencesNotFoundTest() throws Exception {

        //GIVEN
        // an existing user
        String userName = "userName";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userService.getUserPreferences(userName)).thenThrow(objectNotFoundException);


        //WHEN
        // the uri "/getUserPreferences" is called with the userName parameter
        mockMvc.perform(get("/getUserPreferences")
                        .param("userName", userName))

                //THEN
                // the status is "isNotFound" and the expected error message is returned
                .andExpect(status().isNotFound())
                .andExpect(content().string("error message"));
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(1)).getUserPreferences(userName);
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
        // and the expected methods have been called with expected arguments
        verify(userService, Mockito.times(0)).getUserPreferences(any(String.class));
    }

    @Test
    @Tag("updateUserPreferencesTest")
    public void updateUserPreferencesTest() throws Exception {

        //GIVEN
        // all information for userPreferencesDTO
        String userName = "userName";
        Integer attractionProximity = 100;
        String currency = "EUR";
        Integer lowerPricePoint = 0;
        Integer highPricePoint = 1000;
        Integer tripDuration = 5;
        Integer ticketQuantity = 3;
        Integer numberOfAdults = 2;
        Integer numberOfChildren = 2;
        final ArgumentCaptor<UserPreferencesDTO> arg = ArgumentCaptor.forClass(UserPreferencesDTO.class);
        doNothing().when(userService).updateUserPreferences(eq(userName), any(UserPreferencesDTO.class));

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
        // and the expected methods have been called with expected arguments
        verify(userService).updateUserPreferences(any(String.class), arg.capture());
        assertEquals(currency, arg.getValue().getCurrency());
        assertEquals(numberOfAdults, arg.getValue().getNumberOfAdults());
        assertEquals(attractionProximity, arg.getValue().getAttractionProximity());
        assertEquals(lowerPricePoint, arg.getValue().getLowerPricePoint());
        assertEquals(highPricePoint, arg.getValue().getHighPricePoint());
        assertEquals(tripDuration, arg.getValue().getTripDuration());
        assertEquals(ticketQuantity, arg.getValue().getTicketQuantity());
        assertEquals(numberOfChildren, arg.getValue().getNumberOfChildren());
        verify(userService, Mockito.times(1)).updateUserPreferences(eq(userName), any(UserPreferencesDTO.class));
    }
}
