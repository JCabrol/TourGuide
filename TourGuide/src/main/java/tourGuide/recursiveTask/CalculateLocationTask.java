package tourGuide.recursiveTask;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.User;
import tourGuide.service.GpsUtilService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class CalculateLocationTask extends RecursiveTask<List<VisitedLocation>> {

    private final List<User> userList;
    private final GpsUtilService gpsUtilService;

    public CalculateLocationTask(List<User> userList, GpsUtilService gpsUtilService) {
        this.userList = userList;
        this.gpsUtilService = gpsUtilService;
    }

    /**
     * Calculate recursively the userLocation for all users in the userList, by calling gpsUtilService, and put it in a visitedLocationList
     *
     * @return a list of visitedLocations containing the actual location and the id of each user present in the given list as well as the date of being at this location
     */
    @Override
    protected List<VisitedLocation> compute() {
        List<VisitedLocation> result = new ArrayList<>();
        if (userList.size() == 1) {
            User user = userList.get(0);
            VisitedLocation visitedLocation = gpsUtilService.getUserLocation(user.getUserId());
            result.add(visitedLocation);

        } else {

            int midPoint = userList.size() / 2;
            CalculateLocationTask left =
                    new CalculateLocationTask(
                            userList.subList(0, midPoint), gpsUtilService);

            CalculateLocationTask right =
                    new CalculateLocationTask(
                            userList.subList(midPoint, userList.size()), gpsUtilService);

            left.fork();
            List<VisitedLocation> rightResult = right.compute();
            List<VisitedLocation> leftResult = left.join();
            result.addAll(rightResult);
            result.addAll(leftResult);
        }
        return result;
    }
}
