package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.model.User;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripPricerService;
import tourGuide.service.UserService;
import tripPricer.Provider;

import java.util.List;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;
    @Autowired
    UserService userService;
    @Autowired
    TripPricerService tripPricerService;

    /**
     * Displays a welcome message
     *
     * @return a welcome message
     */
    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Read - Get the location of a registered user, displaying its latitude and its longitude
     *
     * @param userName the name of the user whose location is sought
     * @return JSon file containing user's latitude and user's longitude or an error message if the user is not found
     */
    @GetMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        User user = userService.getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        return JsonStream.serialize(visitedLocation.location);
    }

    /**
     * Read - Get the five closest attractions from the user's location, whatever the distance.
     * For each attraction found, the attraction's name, latitude and longitude are displayed
     * as well as the distance between the user and the attraction and the reward points granted for visiting this attraction
     * The user's latitude and longitude are also displayed
     *
     * @param userName the name of the user whose five closest attractions are sought
     * @return JSon file containing all information about the five closest attractions
     */
    @GetMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        User user = userService.getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        return JsonStream.serialize(tourGuideService.searchFiveClosestAttractions(visitedLocation));
    }

    /**
     * Read - Get the rewards obtained by a registered user. The reward object contains an attraction, a visitedLocation and a number of rewardsPoints
     *
     * @param userName the name of the user whose rewards are sought
     * @return JSon file containing user's rewards, or an empty file if there are not any rewards for this user yet, or an error message if the user is not found
     */
    @GetMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        User user = userService.getUser(userName);
        return JsonStream.serialize(userService.getUserRewards(user));
    }

    /**
     * Read - Get the location of all registered user, displaying their userId, their latitude and their longitude
     *
     * @return JSon file containing all user's id, latitude and longitude or an empty file if there is no user registered
     */
    @GetMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        return JsonStream.serialize(tourGuideService.getAllCurrentLocations());
    }

    /**
     * Read - Get the 5 providers offers to a user. A provider object contains a name, a price and an id.
     *
     * @param userName the name of the user whose providers are sought
     * @return JSon file containing five providers
     */
    @GetMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        User user = userService.getUser(userName);
        List<Provider> providers = tripPricerService.getTripDeals(user);
        return JsonStream.serialize(providers);
    }

}