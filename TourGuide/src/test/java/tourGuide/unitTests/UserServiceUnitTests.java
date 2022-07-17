package tourGuide.unitTests;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.exception.ObjectAlreadyExistingException;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.DTO.UserDTO;
import tourGuide.model.DTO.UserPreferencesDTO;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.UserRepository;
import tourGuide.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Tag("serviceTests")
@Tag("userTests")
@ActiveProfiles("unitTest")
public class UserServiceUnitTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;


    @Test
    @Tag("getUserByIdTest")
    public void getUserByIdExistingTest() {

        //GIVEN
        // an existing user
        UUID id = UUID.randomUUID();
        String name = "name1";
        String phoneNumber = "phoneNumber1";
        String mail = "mail1";
        User user = new User(id, name, phoneNumber, mail);

        when(userRepository.getUserById(id)).thenReturn(user);

        //WHEN
        // the function getUserById is called
        User result = userService.getUserById(id);

        //THEN
        // the expected user is returned
        assertEquals(id, result.getUserId());
        assertEquals(name, result.getUserName());
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertEquals(mail, result.getEmailAddress());
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserById(id);
    }

    @Test
    @Tag("getUserByIdTest")
    public void getUserByIdNonExistingTest() {

        //GIVEN
        // a user non-existing
        UUID id = UUID.randomUUID();

        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserById(id)).thenThrow(objectNotFoundException);

        //WHEN
        // the function getUserById is called

        //THEN
        // an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(id));
        assertEquals("error message", exception.getMessage());
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserById(id);
    }

    @Tag("getUserTest")
    @Test
    public void getUserExistingTest() {

        //GIVEN
        // an existing user
        UUID id = UUID.randomUUID();
        String name = "name1";
        String phoneNumber = "phoneNumber1";
        String mail = "mail1";
        User user = new User(id, name, phoneNumber, mail);

        when(userRepository.getUserByName(name)).thenReturn(user);

        //WHEN
        // the function getUser is called
        User result = userService.getUser(name);

        //THEN
        // the expected user is returned
        assertEquals(id, result.getUserId());
        assertEquals(name, result.getUserName());
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertEquals(mail, result.getEmailAddress());
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(name);
    }

    @Tag("getUserTest")
    @Test
    public void getUserNonExistingTest() {

        //GIVEN
        // a user non-existing
        String name = "name1";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserByName(name)).thenThrow(objectNotFoundException);

        //WHEN
        // the function getUser is called

        //THEN
        // an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.getUser(name));
        assertEquals("error message", exception.getMessage());
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(name);
    }

    @Tag("getUserDTOTest")
    @Test
    public void getUserDTOExistingTest() {

        //GIVEN
        // an existing user
        UUID id = UUID.randomUUID();
        String name = "name1";
        String phoneNumber = "phoneNumber1";
        String mail = "mail1";
        User user = new User(id, name, phoneNumber, mail);

        when(userRepository.getUserByName(name)).thenReturn(user);

        //WHEN
        // the function getUserDTO is called
        UserDTO result = userService.getUserDTO(name);

        //THEN
        // the expected userDTO is returned
        assertEquals(name, result.getUserName());
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertEquals(mail, result.getEmailAddress());
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(name);
    }

    @Tag("getUserDTOTest")
    @Test
    public void getUserDTONonExistingTest() {

        //GIVEN
        // a user non-existing
        String name = "name1";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserByName(name)).thenThrow(objectNotFoundException);

        //WHEN
        // the function getUserDTO is called

        //THEN
        // an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.getUserDTO(name));
        assertEquals("error message", exception.getMessage());
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(name);
    }

    @Tag("getAllUserTest")
    @Test
    public void getAllUserExistingTest() {

        //GIVEN
        // an existing list of users
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UUID id = UUID.randomUUID();
            String name = "name" + i;
            String phoneNumber = "phoneNumber" + i;
            String mail = "mail" + i;
            userList.add(new User(id, name, phoneNumber, mail));
        }
        when(userRepository.getUserList()).thenReturn(userList);

        //WHEN
        // getAllUser is called
        List<User> result = userService.getAllUsers();

        //THEN
        // the expected list of user is returned
        assertEquals(3, result.size());
        assertEquals(userList, result);
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserList();
    }

    @Tag("getAllUserTest")
    @Test
    public void getAllUserEmptyTest() {

        //GIVEN
        // an empty list of users
        List<User> userList = new ArrayList<>();
        when(userRepository.getUserList()).thenReturn(userList);

        //WHEN
        // getAllUser is called
        List<User> result = userService.getAllUsers();

        //THEN
        // an empty list is returned
        assertEquals(0, result.size());
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserList();
    }

    @Tag("addNewUserTest")
    @Test
    public void addNewUserTest() {

        //GIVEN
        // a new user
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String emailAddress = "emailAddress";
        UserDTO userDTO = new UserDTO(userName, phoneNumber, emailAddress);
        final ArgumentCaptor<User> arg = ArgumentCaptor.forClass(User.class);
        doNothing().when(userRepository).addUser(any(User.class));

        //WHEN
        // addNewUser is called
        userService.addNewUser(userDTO);

        //THEN
        // the expected methods have been called with expected arguments
        verify(userRepository).addUser(arg.capture());
        assertNotNull(arg.getValue().getUserId());
        assertEquals(userName, arg.getValue().getUserName());
        assertEquals(phoneNumber, arg.getValue().getPhoneNumber());
        assertEquals(emailAddress, arg.getValue().getEmailAddress());
        assertEquals(new ArrayList<>(), arg.getValue().getVisitedLocations());
        assertEquals(new ArrayList<>(), arg.getValue().getUserRewards());
        assertEquals(new ArrayList<>(), arg.getValue().getTripDeals());
        assertNull(arg.getValue().getLatestLocationTimestamp());
        verify(userRepository, Mockito.times(1)).addUser(any(User.class));
    }

    @Tag("addNewUserTest")
    @Test
    public void addNewUserAlreadyExistingTest() {

        //GIVEN
        // a new user with an already existing userName
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String emailAddress = "emailAddress";
        UserDTO userDTO = new UserDTO(userName, phoneNumber, emailAddress);
        ObjectAlreadyExistingException objectAlreadyExistingException = new ObjectAlreadyExistingException("error message");
        doThrow(objectAlreadyExistingException).when(userRepository).addUser(any(User.class));

        //WHEN
        // addNewUser is called

        //THEN
        // an ObjectAlreadyExistingException is thrown with the expected error message
        Exception exception = assertThrows(ObjectAlreadyExistingException.class, () -> userService.addNewUser(userDTO));
        assertEquals("error message", exception.getMessage());
        // and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).addUser(any(User.class));
    }

    @Tag("updateUserTest")
    @Test
    public void updateUserAllInformationTest() {

        //GIVEN
        // a userDTO object with all information
        UUID id = UUID.randomUUID();
        String userName = "name";
        String previousPhoneNumber = "oldPhoneNumber";
        String newPhoneNumber = "newPhoneNumber";
        String previousEmailAddress = "oldEmailAddress";
        String newEmailAddress = "newEmailAddress";
        UserDTO userDTO = new UserDTO(userName, newPhoneNumber, newEmailAddress);
        User user = new User(id, userName, previousPhoneNumber, previousEmailAddress);
        when(userRepository.getUserByName(userName)).thenReturn(user);
        final ArgumentCaptor<User> arg = ArgumentCaptor.forClass(User.class);
        doNothing().when(userRepository).updateUser(any(User.class));

        //WHEN
        // updateUser is called
        userService.updateUser(userDTO);

        //THEN
        // the expected methods have been called with expected arguments
        verify(userRepository).updateUser(arg.capture());
        assertEquals(userName, arg.getValue().getUserName());
        assertEquals(newPhoneNumber, arg.getValue().getPhoneNumber());
        assertEquals(newEmailAddress, arg.getValue().getEmailAddress());
        assertEquals(id, arg.getValue().getUserId());
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(1)).updateUser(any(User.class));
    }

    @Tag("updateUserTest")
    @Test
    public void updateUserWithoutPhoneNumberTest() {

        //GIVEN
        // a userDTO object without phone number
        UUID id = UUID.randomUUID();
        String userName = "name";
        String previousPhoneNumber = "oldPhoneNumber";
        String previousEmailAddress = "oldEmailAddress";
        String newEmailAddress = "newEmailAddress";
        UserDTO userDTO = new UserDTO(userName, null, newEmailAddress);
        User user = new User(id, userName, previousPhoneNumber, previousEmailAddress);
        when(userRepository.getUserByName(userName)).thenReturn(user);
        final ArgumentCaptor<User> arg = ArgumentCaptor.forClass(User.class);
        doNothing().when(userRepository).updateUser(any(User.class));

        //WHEN
        // updateUser is called
        userService.updateUser(userDTO);

        //THEN
        // the expected methods have been called with expected arguments
        verify(userRepository).updateUser(arg.capture());
        assertEquals(userName, arg.getValue().getUserName());
        assertEquals(previousPhoneNumber, arg.getValue().getPhoneNumber());
        assertEquals(newEmailAddress, arg.getValue().getEmailAddress());
        assertEquals(id, arg.getValue().getUserId());
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(1)).updateUser(any(User.class));
    }

    @Tag("updateUserTest")
    @Test
    public void updateUserWithoutEmailAddressTest() {

        //GIVEN
        // a userDTO object without email address
        UUID id = UUID.randomUUID();
        String userName = "name";
        String previousPhoneNumber = "oldPhoneNumber";
        String newPhoneNumber = "newPhoneNumber";
        String previousEmailAddress = "oldEmailAddress";
        UserDTO userDTO = new UserDTO(userName, newPhoneNumber, null);
        User user = new User(id, userName, previousPhoneNumber, previousEmailAddress);
        when(userRepository.getUserByName(userName)).thenReturn(user);
        final ArgumentCaptor<User> arg = ArgumentCaptor.forClass(User.class);
        doNothing().when(userRepository).updateUser(any(User.class));

        //WHEN
        // updateUser is called
        userService.updateUser(userDTO);

        //THEN
        // the expected methods have been called with expected arguments
        verify(userRepository).updateUser(arg.capture());
        assertEquals(userName, arg.getValue().getUserName());
        assertEquals(newPhoneNumber, arg.getValue().getPhoneNumber());
        assertEquals(previousEmailAddress, arg.getValue().getEmailAddress());
        assertEquals(id, arg.getValue().getUserId());
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(1)).updateUser(any(User.class));
    }

    @Tag("updateUserTest")
    @Test
    public void updateUserNonExistingTest() {

        //GIVEN
        // a non-existing user
        String userName = "name";
        String newPhoneNumber = "newPhoneNumber";
        String newEmailAddress = "newEmailAddress";
        UserDTO userDTO = new UserDTO(userName, newPhoneNumber, newEmailAddress);
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserByName(userName)).thenThrow(objectNotFoundException);

        //WHEN
        // updateUser is called

        //THEN
        // an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(userDTO));
        assertEquals("error message", exception.getMessage());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(0)).updateUser(any(User.class));
    }

    @Tag("deleteUserTest")
    @Test
    public void deleteUserTest() {

        //GIVEN
        // an existing user
        UUID id = UUID.randomUUID();
        String userName = "name";
        String phoneNumber = "phoneNumber";
        String emailAddress = "emailAddress";
        User user = new User(id, userName, phoneNumber, emailAddress);
        when(userRepository.getUserByName(userName)).thenReturn(user);
        doNothing().when(userRepository).deleteUser(any(User.class));

        //WHEN
        // deleteUser is called
        userService.deleteUser(userName);

        //THEN
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(1)).deleteUser(any(User.class));
    }

    @Tag("deleteUserTest")
    @Test
    public void deleteUserNonExistingTest() {

        //GIVEN
        // a non-existing user
        String userName = "name";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserByName(userName)).thenThrow(objectNotFoundException);

        //WHEN
        // deleteUser is called

        //THEN
        // an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(userName));
        assertEquals("error message", exception.getMessage());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(0)).deleteUser(any(User.class));
    }

    @Tag("getUserPreferencesTest")
    @Test
    public void getUserPreferencesTest() {

        //GIVEN
        // an existing user
        String userName = "name";
        User user = new User(UUID.randomUUID(), userName, "phoneNumber", "mail");
        when(userRepository.getUserByName(userName)).thenReturn(user);

        //WHEN
        // getUserPreferences is called
        UserPreferencesDTO result = userService.getUserPreferences(userName);

        //THEN
        // a UserPreferencesDTO object is returned with default preferences
        assertEquals(Integer.MAX_VALUE, (int) result.getAttractionProximity());
        assertEquals("USD", result.getCurrency());
        assertEquals(0, (int) result.getLowerPricePoint());
        assertEquals(Integer.MAX_VALUE, (int) result.getHighPricePoint());
        assertEquals(5, (int) result.getTripDuration());
        assertEquals(1, (int) result.getTicketQuantity());
        assertEquals(2, (int) result.getNumberOfChildren());
        assertEquals(2, (int) result.getNumberOfAdults());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
    }

    @Tag("getUserPreferencesTest")
    @Test
    public void getUserPreferencesUserNonExistingTest() {

        //GIVEN
        // a non-existing existing user
        String userName = "name";
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserByName(userName)).thenThrow(objectNotFoundException);

        //WHEN
        // getUserPreferences is called


        //THEN
        // an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.getUserPreferences(userName));
        assertEquals("error message", exception.getMessage());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
    }

    @Tag("updateUserPreferencesTest")
    @Test
    public void updateUserPreferencesEverythingToUpdateTest() {

        //GIVEN
        // an existing user with all updatable information
        String userName = "name";
        User user = new User(UUID.randomUUID(), userName, "phoneNumber", "mail");
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(600, "EUR", 100, 500, 3, 3, 1, 3);
        when(userRepository.getUserByName(userName)).thenReturn(user);
        doNothing().when(userRepository).updateUser(user);

        //WHEN
        // updateUserPreferences is called
        userService.updateUserPreferences(userName, userPreferencesDTO);

        //THEN
        // the user's preferences have been updated
        assertEquals(600, user.getUserPreferences().getAttractionProximity());
        assertEquals("EUR", user.getUserPreferences().getCurrency().toString());
        assertEquals(100, user.getUserPreferences().getLowerPricePoint().getNumber().intValueExact());
        assertEquals(500, user.getUserPreferences().getHighPricePoint().getNumber().intValueExact());
        assertEquals(3, user.getUserPreferences().getTripDuration());
        assertEquals(3, user.getUserPreferences().getTicketQuantity());
        assertEquals(1, user.getUserPreferences().getNumberOfAdults());
        assertEquals(3, user.getUserPreferences().getNumberOfChildren());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(1)).updateUser(user);
    }

    @Tag("updateUserPreferencesTest")
    @Test
    public void updateUserPreferencesNothingToUpdateTest() {

        //GIVEN
        // an existing user with no updatable information
        String userName = "name";
        User user = new User(UUID.randomUUID(), userName, "phoneNumber", "mail");
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
        when(userRepository.getUserByName(userName)).thenReturn(user);
        doNothing().when(userRepository).updateUser(user);

        //WHEN
        // updateUserPreferences is called
        userService.updateUserPreferences(userName, userPreferencesDTO);

        //THEN
        // the user's preferences are unchanged
        assertEquals(Integer.MAX_VALUE, user.getUserPreferences().getAttractionProximity());
        assertEquals("USD", user.getUserPreferences().getCurrency().toString());
        assertEquals(0, user.getUserPreferences().getLowerPricePoint().getNumber().intValueExact());
        assertEquals(Integer.MAX_VALUE, user.getUserPreferences().getHighPricePoint().getNumber().intValueExact());
        assertEquals(5, user.getUserPreferences().getTripDuration());
        assertEquals(1, user.getUserPreferences().getTicketQuantity());
        assertEquals(2, user.getUserPreferences().getNumberOfAdults());
        assertEquals(2, user.getUserPreferences().getNumberOfChildren());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(1)).updateUser(user);
    }

    @Tag("updateUserPreferencesTest")
    @Test
    public void updateUserPreferencesUserNonExistingTest() {

        //GIVEN
        // a non-existing user
        String userName = "name";
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserByName(userName)).thenThrow(objectNotFoundException);

        //WHEN
        // updateUserPreferences is called

        //THEN
        // an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.updateUserPreferences(userName, userPreferencesDTO));
        assertEquals("error message", exception.getMessage());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(userName);
        verify(userRepository, Mockito.times(0)).updateUser(any(User.class));
    }

    @Tag("addUserRewardsTest")
    @Test
    public void addUserRewardsTest() {

        //GIVEN
        // an existing user and rewards to add
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "name", "phoneNumber", "mail");
        VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(2D, 2D), new Date());
        Attraction attraction = new Attraction("attractionName", "city", "state", 3D, 3D);
        UserReward userReward = new UserReward(visitedLocation, attraction, 5);
        doNothing().when(userRepository).updateUser(user);

        //WHEN
        // addUserRewards is called
        userService.addUserRewards(user, userReward);

        //THEN
        // new reward has been added to the user's list, with expected information
        assertEquals(1, user.getUserRewards().size());
        assertEquals(visitedLocation, user.getUserRewards().get(0).visitedLocation);
        assertEquals(attraction, user.getUserRewards().get(0).attraction);
        assertEquals(5, user.getUserRewards().get(0).getRewardPoints());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).updateUser(user);
    }

    @Tag("getUserLastVisitedLocationTest")
    @Test
    public void getUserLastVisitedLocationTest() {

        //GIVEN
        // an existing user with a list of visitedLocations
        UUID userId = UUID.randomUUID();
        Date date = new Date();
        User user = new User(userId, "name", "phoneNumber", "mail");
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        VisitedLocation lastVisitedLocation = new VisitedLocation(userId, new Location(2D, 2D), date);
        visitedLocationList.add(lastVisitedLocation);
        for (int i = 0; i < 5; i++) {
            VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(i, i), new Date());
            visitedLocationList.add(visitedLocation);
        }
        user.setVisitedLocations(visitedLocationList);
        user.setLatestLocationTimestamp(date);

        //WHEN
        // getUserLastVisitedLocation is called
        VisitedLocation result = userService.getUserLastVisitedLocation(user);

        //THEN
        // the visitedLocation whose date is corresponding to latestLocationTimestamp is returned
        assertEquals(lastVisitedLocation, result);
    }

    @Tag("getUserLastVisitedLocationTest")
    @Test
    public void getUserLastVisitedLocationWithoutVisitedLocationTest() {

        //GIVEN
        // an existing user without any visitedLocation
        User user = new User(UUID.randomUUID(), "name", "phoneNumber", "mail");

        //WHEN
        // getUserLastVisitedLocation is called
        VisitedLocation result = userService.getUserLastVisitedLocation(user);

        //THEN
        // null is returned
        assertNull(result);
    }

    @Tag("addUserNewVisitedLocationTest")
    @Test
    public void addUserNewVisitedLocationTest() {

        //GIVEN
        // an existing user and a visitedLocation to add
        UUID userId = UUID.randomUUID();
        Date date = new Date();
        User user = new User(userId, "name", "phoneNumber", "mail");
        VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(2D, 2D), date);
        doNothing().when(userRepository).updateUser(user);

        //WHEN
        // addUserVisitedVisitedLocation is called
        userService.addUserNewVisitedLocation(user, visitedLocation);

        //THEN
        // the visitedLocation have been added to the user's list and the latestLocationTimestamp have been updated
        assertEquals(1, user.getVisitedLocations().size());
        assertTrue(user.getVisitedLocations().contains(visitedLocation));
        assertEquals(date, user.getLatestLocationTimestamp());
        // the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).updateUser(user);
    }
}
