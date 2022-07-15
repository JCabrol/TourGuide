package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tourGuide.model.DTO.UserDTO;
import tourGuide.model.DTO.UserPreferencesDTO;
import tourGuide.service.UserService;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/getUser")
    public String getUser(@RequestParam String userName) {
        UserDTO user = userService.getUserDTO(userName);
        return JsonStream.serialize(user);
    }

    @PostMapping("/createUser")
    public String createUser(@RequestParam String userName, @RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String emailAddress) {
        UserDTO userDTO = new UserDTO(userName, phoneNumber, emailAddress);
        userService.addNewUser(userDTO);
        return "The user with name " + userName + " has been added.";
    }


    @PutMapping("/updateUser")
    public String updateUser(@RequestParam String userName, @RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String emailAddress) {
        UserDTO userDTO = new UserDTO(userName, phoneNumber, emailAddress);
        userService.updateUser(userDTO);
        return "The user with name " + userName + " has been updated.";
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String userName) {
        userService.deleteUser(userName);
        return "The user with name " + userName + " has been deleted.";
    }

    @GetMapping("/getUserPreferences")
    public String getUserPreferences(@RequestParam String userName) {
        return JsonStream.serialize(userService.getUserPreferences(userName));
    }

    @PutMapping("/updateUserPreferences")
    public String updateUserPreferences(@RequestParam String userName,
                                        @RequestParam(required = false) Integer attractionProximity,
                                        @RequestParam(required = false) String currency,
                                        @RequestParam(required = false) Integer lowerPricePoint,
                                        @RequestParam(required = false) Integer highPricePoint,
                                        @RequestParam(required = false) Integer tripDuration,
                                        @RequestParam(required = false) Integer ticketQuantity,
                                        @RequestParam(required = false) Integer numberOfAdults,
                                        @RequestParam(required = false) Integer numberOfChildren) {
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(attractionProximity, currency, lowerPricePoint, highPricePoint, tripDuration, ticketQuantity, numberOfAdults, numberOfChildren);
        userService.updateUserPreferences(userName, userPreferencesDTO);
        return "The preferences for user " + userName + " has been updated.";
    }
}
