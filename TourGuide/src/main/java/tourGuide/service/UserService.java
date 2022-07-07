package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.exception.ObjectAlreadyExistingException;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.User;
import tourGuide.model.UserReward;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    User getUserById(UUID userId) throws ObjectNotFoundException;

    void addNewUser(User user) throws ObjectAlreadyExistingException;

    void updateUser(User user) throws ObjectAlreadyExistingException;

    void deleteUser(User user) throws ObjectNotFoundException;

    List<UserReward> getUserRewards(User user);

    VisitedLocation getUserLastVisitedLocation(User user);

    void addUserNewVisitedLocation(User user, VisitedLocation newVisitedLocation) throws Exception;

    User getUser(String userName) throws ObjectNotFoundException;

    List<User> getAllUsers();

    HashMap<String,User> getInternalUserMap();
}
