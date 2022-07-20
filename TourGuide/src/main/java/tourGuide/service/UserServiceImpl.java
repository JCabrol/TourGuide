package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.exception.ObjectAlreadyExistingException;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.DTO.UserDTO;
import tourGuide.model.DTO.UserPreferencesDTO;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
import tourGuide.model.UserReward;
import tourGuide.repository.UserRepository;

import javax.money.Monetary;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns the list of all the registered users, and an empty list if there is no user registered
     *
     * @return a list of User containing all the registered users
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.getUserList();
    }

    /**
     * Get a User from its name
     *
     * @param userName the name of the researched user
     * @return the researched User
     * @throws ObjectNotFoundException when the user is not found
     */
    @Override
    public User getUser(String userName) throws ObjectNotFoundException {
        return userRepository.getUserByName(userName);
    }

    /**
     * Get a User from its id
     *
     * @param userId the id of the researched user
     * @return the researched User
     * @throws ObjectNotFoundException when the user is not found
     */
    @Override
    public User getUserById(UUID userId) throws ObjectNotFoundException {
        return userRepository.getUserById(userId);
    }

    /**
     * Get a UserDTO from its name
     *
     * @param userName the name of the researched user
     * @return a UserDTO object containing personal information about the researched user (userName, phoneNumber and emailAddress)
     * @throws ObjectNotFoundException when the user is not found
     */
    @Override
    public UserDTO getUserDTO(String userName) throws ObjectNotFoundException {
        User user = userRepository.getUserByName(userName);
        return new UserDTO(user.getUserName(), user.getPhoneNumber(), user.getEmailAddress());
    }

    /**
     * Create a new user and add it to registered data
     *
     * @param userDTO a UserDTO object containing information to create a new user (userName, phoneNumber and emailAddress)
     * @throws ObjectAlreadyExistingException when the userName is already registered
     */
    @Override
    public void addNewUser(UserDTO userDTO) throws ObjectAlreadyExistingException {
        User user = new User(UUID.randomUUID(), userDTO.getUserName(), userDTO.getPhoneNumber(), userDTO.getEmailAddress());
        userRepository.addUser(user);
        String message = "The user with name " + userDTO.getUserName() + " have been created.";
        log.debug(message);
    }

    /**
     * Update user personal information
     *
     * @param userDTO a UserDTO object containing information to update a user (userName, phoneNumber and emailAddress)
     * @throws ObjectNotFoundException when the user is not found
     */
    @Override
    public void updateUser(UserDTO userDTO) throws ObjectNotFoundException {
        User user = getUser(userDTO.getUserName());
        if (userDTO.getEmailAddress() != null) {
            user.setEmailAddress(userDTO.getEmailAddress());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        userRepository.updateUser(user);
        String message = "The user with name " + userDTO.getUserName() + " have been updated.";
        log.debug(message);
    }

    /**
     * Delete an user
     *
     * @param userName the name of the user to delete
     * @throws ObjectNotFoundException when the user is not found
     */
    @Override
    public void deleteUser(String userName) throws ObjectNotFoundException {
        User user = getUser(userName);
        userRepository.deleteUser(user);
        String message = "The user with name " + userName + " have been deleted.";
        log.debug(message);
    }

    /**
     * Get the user's preferences
     *
     * @param userName the name of the user to delete
     * @return a UserPreferenceDTO object containing all information about the user's preferences
     * @throws ObjectNotFoundException when the user is not found
     */
    @Override
    public UserPreferencesDTO getUserPreferences(String userName) throws ObjectNotFoundException {
        User user = userRepository.getUserByName(userName);
        UserPreferences userPreferences = user.getUserPreferences();
        return new UserPreferencesDTO(userPreferences.getAttractionProximity(),
                userPreferences.getCurrency().toString(),
                userPreferences.getLowerPricePoint().getNumber().intValueExact(),
                userPreferences.getHighPricePoint().getNumber().intValueExact(),
                userPreferences.getTripDuration(),
                userPreferences.getTicketQuantity(),
                userPreferences.getNumberOfAdults(),
                userPreferences.getNumberOfChildren());
    }

    /**
     * Update the user's preferences
     *
     * @param userName           the name of the user whose preferences have to be updated
     * @param userPreferencesDTO a UserPreferencesDTO object containing information to update about user's preferences
     * @throws ObjectNotFoundException when the user is not found
     */
    @Override
    public void updateUserPreferences(String userName, UserPreferencesDTO userPreferencesDTO) throws ObjectNotFoundException {
        User user = userRepository.getUserByName(userName);
        UserPreferences userPreferences = user.getUserPreferences();
        if (userPreferencesDTO.getAttractionProximity() != null) {
            userPreferences.setAttractionProximity((userPreferencesDTO.getAttractionProximity()));
        }
        if (userPreferencesDTO.getCurrency() != null) {
            userPreferences.setCurrency(Monetary.getCurrency(userPreferencesDTO.getCurrency()));
        }
        if (userPreferencesDTO.getHighPricePoint() != null) {
            userPreferences.setHighPricePoint(Money.of(userPreferencesDTO.getHighPricePoint(), userPreferences.getCurrency()));
        }
        if (userPreferencesDTO.getLowerPricePoint() != null) {
            userPreferences.setLowerPricePoint(Money.of(userPreferencesDTO.getLowerPricePoint(), userPreferences.getCurrency()));
        }
        if (userPreferencesDTO.getAttractionProximity() != null) {
            userPreferences.setAttractionProximity(userPreferencesDTO.getAttractionProximity());
        }
        if (userPreferencesDTO.getNumberOfAdults() != null) {
            userPreferences.setNumberOfAdults(userPreferencesDTO.getNumberOfAdults());
        }
        if (userPreferencesDTO.getNumberOfChildren() != null) {
            userPreferences.setNumberOfChildren(userPreferencesDTO.getNumberOfChildren());
        }
        if (userPreferencesDTO.getTicketQuantity() != null) {
            userPreferences.setTicketQuantity(userPreferencesDTO.getTicketQuantity());
        }
        if (userPreferencesDTO.getTripDuration() != null) {
            userPreferences.setTripDuration(userPreferencesDTO.getTripDuration());
        }
        user.setUserPreferences(userPreferences);
        userRepository.updateUser(user);
        String message = "The preferences for user " + userName + " have been updated.";
        log.debug(message);
    }

    /**
     * Get the list of rewards for the given user
     *
     * @param user the user whose rewards are researched
     * @return the list of userRewards for the user, an empty list if there are not any userRewards
     */
    @Override
    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    /**
     * Add rewards to a user
     *
     * @param user       the user whose rewards have to be added
     * @param userReward the rewards to add
     */
    @Override
    public void addUserRewards(User user, UserReward userReward) {
        List<UserReward> userRewardList = user.getUserRewards();
        userRewardList.add(userReward);
        user.setUserRewards(userRewardList);
        userRepository.updateUser(user);
        String message = "New rewards have been added for user " + user.getUserName() + ".";
        log.debug(message);
    }

    /**
     * Get the user's last visited location
     *
     * @param user the user whose last visited location is researched
     * @return a VisitedLocation object (returns null if the user doesn't have any visited location)
     */
    @Override
    public VisitedLocation getUserLastVisitedLocation(User user) {
        VisitedLocation lastVisitedLocation = null;
        List<VisitedLocation> userVisitedLocationList = user.getVisitedLocations();
        if (userVisitedLocationList.size() != 0) {
            if (userVisitedLocationList.stream().parallel().anyMatch(visitedLocation -> visitedLocation.timeVisited.equals(user.getLatestLocationTimestamp()))) {
                lastVisitedLocation = userVisitedLocationList
                        .stream()
                        .parallel()
                        .filter(visitedLocation -> visitedLocation.timeVisited.equals(user.getLatestLocationTimestamp()))
                        .collect(Collectors.toList())
                        .get(0);
            } else {
                lastVisitedLocation = userVisitedLocationList.get(userVisitedLocationList.size() - 1);
            }
        }
        return lastVisitedLocation;
    }

    /**
     * Add a new VisitedLocation to the given user
     *
     * @param user               the user to which a visitedLocation have to be added
     * @param newVisitedLocation the visitedLocation to add
     */
    @Override
    public void addUserNewVisitedLocation(User user, VisitedLocation newVisitedLocation) {
        List<VisitedLocation> userVisitedLocationList = user.getVisitedLocations();
        userVisitedLocationList.add(newVisitedLocation);
        user.setVisitedLocations(userVisitedLocationList);
        user.setLatestLocationTimestamp(newVisitedLocation.timeVisited);
        userRepository.updateUser(user);
        String message = "A new VisitedLocation has been added for user " + user.getUserName() + ".";
        log.debug(message);
    }
}
