package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.User;
import tourGuide.repository.UserRepository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class TourGuideServiceAnalyser {

    private final BlockingQueue<User> userQueue;
    private final CountDownLatch userLatch;
    private final BlockingQueue<VisitedLocation> visitedLocationQueue;
    private final TourGuideService tourGuideService;

    public TourGuideServiceAnalyser(BlockingQueue<User> userQueue, CountDownLatch userLatch, BlockingQueue<VisitedLocation> visitedLocationQueue, TourGuideService tourGuideService) {

        this.userQueue = userQueue;
        this.userLatch = userLatch;
        this.visitedLocationQueue = visitedLocationQueue;
        this.tourGuideService = tourGuideService;
    }

    public void consumeUsersWhenReady() throws Exception {
        while (userLatch.getCount() != 0)
        {
            User user = userQueue.take();
            try{
                publishUser(user);
            }finally {
                userLatch.countDown();
                System.out.println("Waiting for "+userLatch.getCount()+" users to finish");
            }
        }
    }

    private void publishUser(User user) throws Exception {
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        visitedLocationQueue.put(visitedLocation);
    }
}
