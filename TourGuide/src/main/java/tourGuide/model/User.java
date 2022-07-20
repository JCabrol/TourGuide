package tourGuide.model;

import gpsUtil.location.VisitedLocation;
import lombok.Getter;
import lombok.Setter;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class User {

    /**
     * The user's id
     */
    private final UUID userId;
    /**
     * The user's name
     */
    private final String userName;
    /**
     * The user's phone number
     */
    private String phoneNumber;
    /**
     * The user's emailAddress
     */
    private String emailAddress;
    /**
     * The moment when the latest location has been tracked for the user.
     * The latestLocationTimestamp is automatically updated when a visitedLocation is added by UserService
     */
    private Date latestLocationTimestamp;
    /**
     * The list of all the visitedLocations where the user went
     */
    private List<VisitedLocation> visitedLocations = new ArrayList<>();
    /**
     * The list of all the UserRewards owned by the user
     */
    private List<UserReward> userRewards = new ArrayList<>();
    /**
     * The preferences of the user for its trips
     */
    private UserPreferences userPreferences = new UserPreferences();
    /**
     * The list of all the Providers proposed to the user
     */
    private List<Provider> tripDeals = new ArrayList<>();

    public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }
}
