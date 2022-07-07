package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.exception.ObjectAlreadyExistingException;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

@Autowired
    private UserRepository userRepository;

//    public UserServiceImpl(){
//        userRepository = new UserRepository();
//    }
    @Override
    public HashMap<String,User> getInternalUserMap(){
        return userRepository.getUserMap();
    }

    @Override
    public User getUser(String userName) throws ObjectNotFoundException {
        return userRepository.getUserByName(userName);
    }

    @Override
    public User getUserById(UUID userId) throws ObjectNotFoundException {
        return userRepository.getUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getUserList();
    }

    @Override
    public void addNewUser(User user) throws ObjectAlreadyExistingException {
        userRepository.addUser(user);
    }

    @Override
    public void updateUser(User user) throws ObjectNotFoundException {
        userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(User user) throws ObjectNotFoundException {
        userRepository.addUser(user);
    }


    @Override
    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    @Override
    public VisitedLocation getUserLastVisitedLocation(User user) {
        VisitedLocation lastVisitedLocation = null;
        List<VisitedLocation> userVisitedLocationList = user.getVisitedLocations();
        if (userVisitedLocationList.size() != 0) {
            lastVisitedLocation = userVisitedLocationList
                    .stream()
                    .parallel()
                    .filter(visitedLocation -> visitedLocation.timeVisited.equals(user.getLatestLocationTimestamp()))
                    .collect(Collectors.toList())
                    .get(0);
        }
        return lastVisitedLocation;
    }

    @Override
    public void addUserNewVisitedLocation(User user, VisitedLocation newVisitedLocation) throws Exception {
        List<VisitedLocation> userVisitedLocationList = user.getVisitedLocations();
        userVisitedLocationList.add(newVisitedLocation);
        user.setVisitedLocations(userVisitedLocationList);
        user.setLatestLocationTimestamp(newVisitedLocation.timeVisited);
    }

}
