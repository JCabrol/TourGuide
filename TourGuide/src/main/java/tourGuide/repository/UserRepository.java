package tourGuide.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tourGuide.dataSource.InternalUserMap;
import tourGuide.exception.ObjectAlreadyExistingException;
import tourGuide.exception.ObjectNotFoundException;
import tourGuide.model.User;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class UserRepository {

    @Autowired
    private InternalUserMap internalUserMap;

    /**
     * Get all user registered in internalUserMap
     *
     * @return a list of all user registered
     */
    public List<User> getUserList() {
        return internalUserMap.getInternalUserMap().values().parallelStream().collect(Collectors.toList());
    }

    /**
     * Get a user from its userName
     *
     * @param username the name of the researched user
     * @return the researched user
     * @throws ObjectNotFoundException if the userName is not found in internalUserMap
     */
    public User getUserByName(String username) throws ObjectNotFoundException {
        if (internalUserMap.getInternalUserMap().containsKey(username)) {
            return internalUserMap.getInternalUserMap().get(username);
        } else {
            throw new ObjectNotFoundException("The user whose name is " + username + " was not found.");
        }
    }

    /**
     * Get a user from its id
     *
     * @param userId the id of the researched user
     * @return the researched user
     * @throws ObjectNotFoundException if the userId is not found in internalUserMap
     */
    public User getUserById(UUID userId) throws ObjectNotFoundException {
        Supplier<Stream<User>> streamSupplier
                = () -> getUserList().stream().parallel()
                .filter(user -> user.getUserId() == userId);
        if (streamSupplier.get().findAny().isPresent()) {
            return streamSupplier.get().collect(Collectors.toList()).get(0);
        } else {
            throw new ObjectNotFoundException("The user whose id is " + userId + " was not found.");
        }
    }

    /**
     * Add a user in the internalUserMap
     *
     * @param user the user to add
     * @throws ObjectAlreadyExistingException if the userName is already registered in internalUserMap
     */
    public void addUser(User user) throws ObjectAlreadyExistingException {

        if (!internalUserMap.getInternalUserMap().containsKey(user.getUserName())) {
            internalUserMap.getInternalUserMap().put(user.getUserName(), user);
        } else {
            throw new ObjectAlreadyExistingException("The user whose name is " + user.getUserName() + " was already existing, so it couldn't have been added.");
        }
    }

    /**
     * Update a user
     *
     * @param user the user to update
     * @throws ObjectNotFoundException if the userName is not found in internalUserMap
     */
    public void updateUser(User user) throws ObjectNotFoundException {
        if (internalUserMap.getInternalUserMap().containsKey(user.getUserName())) {
            internalUserMap.getInternalUserMap().put(user.getUserName(), user);
        } else {
            throw new ObjectNotFoundException("The user whose name is " + user.getUserName() + " was not found, so it couldn't have been updated.");
        }
    }

    /**
     * Delete a user
     *
     * @param user the user to delete
     * @throws ObjectNotFoundException if the userName is not found in internalUserMap
     */
    public void deleteUser(User user) throws ObjectNotFoundException {
        if (internalUserMap.getInternalUserMap().containsKey(user.getUserName())) {
            internalUserMap.getInternalUserMap().remove(user.getUserName());
        } else {
            throw new ObjectNotFoundException("The user whose name is " + user.getUserName() + " was not found, so it couldn't have been deleted.");
        }
    }
}