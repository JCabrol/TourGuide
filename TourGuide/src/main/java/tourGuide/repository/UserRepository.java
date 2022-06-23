package tourGuide.repository;

import tourGuide.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class UserRepository {

    private final Map<String, User> internalUserMap = new HashMap<>();

    public List<User> getUserList() {
        return internalUserMap.values().parallelStream().collect(Collectors.toList());
    }

    public User getUser(String username) throws Exception {
        if (internalUserMap.containsKey(username)) {
            return internalUserMap.get(username);
        } else {
            throw new Exception("The model with userName " + username + " was not found.");
        }
    }

    public void addUser(User user) //throws Exception {
    {
//        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
//        } else {
//            throw new Exception("The model with userName " + user.getUserName() + " was already existing.");
//        }
    }

    public void updateUser(User user) throws Exception {
        if (internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        } else {
            throw new Exception("The model with userName " + user.getUserName() + " was not found, so it couldn't have been updated.");
        }
    }

    public void deleteUser(User user) throws Exception {
        if (internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.remove(user.getUserName());
        } else {
            throw new Exception("The model with userName " + user.getUserName() + " was not found, so it couldn't have been deleted.");
        }
    }

}