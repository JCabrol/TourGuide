package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.exception.ObjectAlreadyExistingException;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.DTO.UserDTO;
import tourGuide.model.DTO.UserPreferencesDTO;
import tourGuide.model.User;
import tourGuide.model.UserReward;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    /**
     * Returns the list of all the registered users, and an empty list if there is no user registered
     *
     * @return a list of User containing all the registered users
     */
    List<User> getAllUsers();

    /**
     * Get a User from its name
     *
     * @param userName the name of the researched user
     * @return the researched User
     * @throws ObjectNotFoundException when the user is not found
     */
    User getUser(String userName) throws ObjectNotFoundException;

    /**
     * Get a User from its id
     *
     * @param userId the id of the researched user
     * @return the researched User
     * @throws ObjectNotFoundException when the user is not found
     */
    User getUserById(UUID userId) throws ObjectNotFoundException;

    /**
     * Get a UserDTO from its name
     *
     * @param userName the name of the researched user
     * @return a UserDTO object containing personal information about the researched user (userName, phoneNumber and emailAddress)
     * @throws ObjectNotFoundException when the user is not found
     */
    UserDTO getUserDTO(String userName) throws ObjectNotFoundException;

    /**
     * Create a new user and add it to registered data
     *
     * @param userDTO a UserDTO object containing information to create a new user (userName, phoneNumber and emailAddress)
     * @throws ObjectAlreadyExistingException when the userName is already registered
     */
    void addNewUser(UserDTO userDTO) throws ObjectAlreadyExistingException;

    /**
     * Update user personal information
     *
     * @param userDTO a UserDTO object containing information to update a user (userName, phoneNumber and emailAddress)
     * @throws ObjectNotFoundException when the user is not found
     */
    void updateUser(UserDTO userDTO) throws ObjectAlreadyExistingException;

    /**
     * Delete an user
     *
     * @param userName the name of the user to delete
     * @throws ObjectNotFoundException when the user is not found
     */
    void deleteUser(String userName) throws ObjectNotFoundException;

    /**
     * Get the user's preferences
     *
     * @param userName the name of the user to delete
     * @return a UserPreferenceDTO object containing all information about the user's preferences
     * @throws ObjectNotFoundException when the user is not found
     */
    UserPreferencesDTO getUserPreferences(String userName) throws ObjectNotFoundException;

    /**
     * Update the user's preferences
     *
     * @param userName the name of the user whose preferences have to be updated
     * @param userPreferencesDTO a UserPreferencesDTO object containing information to update about user's preferences
     * @throws ObjectNotFoundException when the user is not found
     */
    void updateUserPreferences(String userName, UserPreferencesDTO userPreferencesDTO) throws ObjectNotFoundException;

    /**
     * Get the list of rewards for the given user
     *
     * @param user the user whose rewards are researched
     */
    List<UserReward> getUserRewards(User user);

    /**
     * Add rewards to a user
     *
     * @param user the user whose rewards have to be added
     * @param userReward the rewards to add
     */
    void addUserRewards(User user, UserReward userReward);

    /**
     * Get the user's last visited location
     *
     * @param user the user whose last visited location is researched
     * @return a VisitedLocation object (returns null if the user doesn't have any visited location)
     */
    VisitedLocation getUserLastVisitedLocation(User user);

    /**
     * Add a new VisitedLocation to the given user
     *
     * @param user the user to which a visitedLocation have to be added
     * @param newVisitedLocation the visitedLocation to add
     */
    void addUserNewVisitedLocation(User user, VisitedLocation newVisitedLocation);
}
