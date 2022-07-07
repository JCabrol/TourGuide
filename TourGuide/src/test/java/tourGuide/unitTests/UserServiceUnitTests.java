package tourGuide.unitTests;

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
import tourGuide.model.User;
import tourGuide.repository.UserRepository;
import tourGuide.service.UserService;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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

//    @Nested
//    @Tag("UserServiceTests")
//    @DisplayName("getUserById tests:")
//    class GetUserByIdTest {

    @Test
    @Tag("getUserByIdTest")
    @DisplayName("GIVEN an existing user " +
            "WHEN getUserById is called " +
            "THEN the expected user is returned.")
    public void getUserByIdExistingTest() {
        //GIVEN
        //an existing user
        UUID id = UUID.randomUUID();
        String name = "name1";
        String phoneNumber = "phoneNumber1";
        String mail = "mail1";
        User user = new User(id, name, phoneNumber, mail);

        when(userRepository.getUserById(id)).thenReturn(user);

        //WHEN
        //the function getUserById is called
        User result = userService.getUserById(id);

        //THEN
        //the expected user is returned
        assertEquals(id, result.getUserId());
        assertEquals(name, result.getUserName());
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertEquals(mail, result.getEmailAddress());
        //and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserById(id);
    }

    @Test
    @Tag("getUserByIdTest")
    @DisplayName("GIVEN a user non-existing " +
            "WHEN getUserById is called " +
            "THEN an ObjectNotFoundException is thrown with the expected error message.")
    public void getUserByIdNonExistingTest() {
        //GIVEN
        //a user non-existing
        UUID id = UUID.randomUUID();

        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserById(id)).thenThrow(objectNotFoundException);
        //WHEN
        //the function getUserById is called
        //THEN
        //an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(id));
        assertEquals("error message", exception.getMessage());
        //and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserById(id);
    }
//    }

//    @Nested
//    @Tag("UserServiceTests")
//    @DisplayName("getUser tests:")
//    class GetUserTest {

    @DisplayName("GIVEN an existing user " +
            "WHEN getUser is called " +
            "THEN the expected user is returned.")
    @Tag("getUserTest")
    @Test
    public void getUserExistingTest() {
        //GIVEN
        //an existing user
        UUID id = UUID.randomUUID();
        String name = "name1";
        String phoneNumber = "phoneNumber1";
        String mail = "mail1";
        User user = new User(id, name, phoneNumber, mail);

        when(userRepository.getUserByName(name)).thenReturn(user);
        //WHEN
        //the function getUser is called
        User result = userService.getUser(name);
        //THEN
        //the expected user is returned
        assertEquals(id, result.getUserId());
        assertEquals(name, result.getUserName());
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertEquals(mail, result.getEmailAddress());
        //and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(name);
    }

    @Tag("getUserTest")
    @DisplayName("GIVEN a user non-existing " +
            "WHEN getUser is called " +
            "THEN an ObjectNotFoundException is thrown with the expected error message.")
    @Test
    public void getUserNonExistingTest() {
        //GIVEN
        //a user non-existing
        String name = "name1";

        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
        when(userRepository.getUserByName(name)).thenThrow(objectNotFoundException);
        //WHEN
        //the function getUser is called
        //THEN
        //an ObjectNotFoundException is thrown with the expected error message
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.getUser(name));
        assertEquals("error message", exception.getMessage());
        //and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserByName(name);
    }
//    }

    @DisplayName("GIVEN an existing list of users " +
            "WHEN getAllUser is called " +
            "THEN the expected list of user is returned.")
    @Tag("getAllUserTest")
    @Test
    public void getAllUserExistingTest() {
        //GIVEN
        //an existing list of users
        List<User> userList = new ArrayList<>();
        for(int i = 0;i<3;i++)
        {
            UUID id = UUID.randomUUID();
            String name = "name"+i;
            String phoneNumber = "phoneNumber"+i;
            String mail = "mail"+i;
            userList.add(new User(id, name, phoneNumber, mail));
        }
        when(userRepository.getUserList()).thenReturn(userList);

        //WHEN
        //getAllUser is called
        List<User> result = userService.getAllUsers();

        //THEN
        //the expected list of user is returned
        assertEquals(3,result.size());
        assertEquals(userList, result);
        //and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserList();
    }

    @DisplayName("GIVEN an empty list of users " +
            "WHEN getAllUser is called " +
            "THEN an empty list is returned.")
    @Tag("getAllUserTest")
    @Test
    public void getAllUserEmptyTest() {
        //GIVEN
        //an empty list of users
        List<User> userList = new ArrayList<>();
        when(userRepository.getUserList()).thenReturn(userList);

        //WHEN
        //getAllUser is called
        List<User> result = userService.getAllUsers();

        //THEN
        //an empty list is returned
        assertEquals(0,result.size());
        //and the expected methods have been called with expected arguments
        verify(userRepository, Mockito.times(1)).getUserList();
    }


}
