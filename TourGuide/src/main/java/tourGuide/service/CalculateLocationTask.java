package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveTask;

public class CalculateLocationTask extends RecursiveTask<List<VisitedLocation>> {


    private final List<User> userList;
    private final GpsUtilService gpsUtilService;
    private final BlockingQueue visitedLocationQueue;


    public CalculateLocationTask(List<User> userList, GpsUtilService gpsUtilService, BlockingQueue visitedLocationQueue) {
        this.userList = userList;
        this.gpsUtilService = gpsUtilService;
        this.visitedLocationQueue = visitedLocationQueue;
    }

    @Override
    protected List<VisitedLocation> compute() {
        List<VisitedLocation> result = new ArrayList<>();
        if (userList.size() == 1) {
            User user = userList.get(0);
            VisitedLocation visitedLocation =gpsUtilService.getUserLocation(user.getUserId());
            result.add(visitedLocation);
            try {
                visitedLocationQueue.put(visitedLocation);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            int midPoint = userList.size() / 2;
            CalculateLocationTask left =
                    new CalculateLocationTask(
                            userList.subList(0, midPoint), gpsUtilService,visitedLocationQueue);

            CalculateLocationTask right =
                    new CalculateLocationTask(
                            userList.subList(midPoint, userList.size()), gpsUtilService,visitedLocationQueue);

            left.fork();
            List<VisitedLocation> rightResult = right.compute();
            List<VisitedLocation> leftResult = left.join();
            result.addAll(rightResult);
            result.addAll(leftResult);
        }
        return result;
    }
}
