package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.model.User;
import tourGuide.service.TourGuideService;
import tourGuide.service.TourGuideServiceImpl;
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
        return JsonStream.serialize(tourGuideService.getAllCurrentLocations());
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws Exception {
        User user = tourGuideService.getUser(userName);
        List<Provider> providers = tourGuideService.getTripDeals(user);
        return JsonStream.serialize(providers);
    }
}