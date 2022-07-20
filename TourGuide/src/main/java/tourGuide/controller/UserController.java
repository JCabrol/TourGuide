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

    /**
     * Read - Get the personal information of a registered user, displaying its userName, its phoneNumber and its emailAddress
     *
     * @param userName the name of the user whose personal information are sought
     * @return JSon file containing user's information or an error message if the user is not found
     */
    @GetMapping("/getUser")
    public String getUser(@RequestParam String userName) {
        UserDTO user = userService.getUserDTO(userName);
        return JsonStream.serialize(user);
    }

    /**
     * Create - Add a new user from the information given in parameter
     *
     * @param userName the name of the user to create
     * @param phoneNumber not required - the phone number of the user to create
     * @param emailAddress not required - the email address of the user to create
     * @return a success message to indicate the user have been created or an error message if it couldn't have been done
     */
    @PostMapping("/createUser")
    public String createUser(@RequestParam String userName, @RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String emailAddress) {
        UserDTO userDTO = new UserDTO(userName, phoneNumber, emailAddress);
        userService.addNewUser(userDTO);
        return "The user with name " + userName + " has been added.";
    }

    /**
     * Update - Update a user from the information given in parameter
     *
     * @param userName the name of the user to update
     * @param phoneNumber not required - the new phone number to update
     * @param emailAddress not required - the new email address to update
     * @return a success message to indicate the user have been updated or an error message if it couldn't have been done
     */
    @PutMapping("/updateUser")
    public String updateUser(@RequestParam String userName, @RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String emailAddress) {
        UserDTO userDTO = new UserDTO(userName, phoneNumber, emailAddress);
        userService.updateUser(userDTO);
        return "The user with name " + userName + " has been updated.";
    }

    /**
     * Delete - Delete a user
     *
     * @param userName the name of the user to delete
     * @return a success message to indicate the user have been deleted or an error message if it couldn't have been done
     */
    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String userName) {
        userService.deleteUser(userName);
        return "The user with name " + userName + " has been deleted.";
    }

    /**
     * Read - Get the user preferences of a registered user.
     * The userPreferences object contains the currency the user wants to use, the minimal and the maximal price he wants to pay,
     * the trip duration (in number of days), the number of adults and children and the number of tickets
     *
     * @param userName the name of the user whose preferences are sought
     * @return JSon file containing user preferences' information or an error message if the user is not found
     */
    @GetMapping("/getUserPreferences")
    public String getUserPreferences(@RequestParam String userName) {
        return JsonStream.serialize(userService.getUserPreferences(userName));
    }

    /**
     * Update - Update user preferences from the information given in parameter
     *
     * @param userName the name of the user whose preferences are to be updated
     * @param attractionProximity not required - the new preferred distance between user and attraction
     * @param currency not required - the new currency the user wants to use to pay trips
     * @param lowerPricePoint not required - the new minimal price the user wants for its trips
     * @param highPricePoint not required - the new maximal price the user wants for its trips
     * @param tripDuration not required - the new duration (in number of days) the user wants for its trips
     * @param numberOfAdults not required - the new number of adults the user wants for its trips
     * @param numberOfChildren not required - the new number of children the user wants for its trips
     * @param ticketQuantity not required - the new quantity of tickets the user wants for the attractions
     * @return a success message to indicate the user have been updated or an error message if it couldn't have been done
     */
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
        return "The preferences for user " + userName + " have been updated.";
    }
}
