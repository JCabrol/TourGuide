package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tripPricer.Provider;

import java.util.List;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) throws Exception {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(userName);
        return JsonStream.serialize(visitedLocation.location);
    }

    //  TODO: Change this method to no longer return a List of Attractions.
    //  Instead: Get the closest five tourist attractions to the model - no matter how far away they are.
    //  Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The model's location lat/long,
    // The distance in miles between the model's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws Exception {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(userName);
        return JsonStream.serialize(tourGuideService.searchFiveClosestAttractions(visitedLocation));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) throws Exception {
        User user = tourGuideService.getUser(userName);
        return JsonStream.serialize(tourGuideService.getUserRewards(user));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        // TODO: Get a list of every model's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the model's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }
        return JsonStream.serialize(tourGuideService.getAllCurrentLocations());
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws Exception {
        User user = tourGuideService.getUser(userName);
        List<Provider> providers = tourGuideService.getTripDeals(user);
        return JsonStream.serialize(providers);
    }

//    private User getUser(String userName) throws Exception {
//        return tourGuideService.getUser(userName);
//    }


}