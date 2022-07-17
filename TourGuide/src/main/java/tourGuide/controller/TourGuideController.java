package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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


    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @GetMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        User user = userService.getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        return JsonStream.serialize(visitedLocation.location);
    }

    @GetMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        User user = userService.getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        return JsonStream.serialize(tourGuideService.searchFiveClosestAttractions(visitedLocation));
    }

    @GetMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        User user = userService.getUser(userName);
        return JsonStream.serialize(userService.getUserRewards(user));
    }

    @GetMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        return JsonStream.serialize(tourGuideService.getAllCurrentLocations());
    }

    @GetMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        User user = userService.getUser(userName);
        List<Provider> providers = tripPricerService.getTripDeals(user);
        return JsonStream.serialize(providers);
    }

}