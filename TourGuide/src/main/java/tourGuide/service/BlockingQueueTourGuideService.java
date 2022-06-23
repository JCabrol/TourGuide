package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BlockingQueueTourGuideService {
    public static final int USER_CONSUMER_COUNT = 5;
    private final TourGuideService tourGuideService;

    public BlockingQueueTourGuideService(TourGuideService tourGuideService){
        this.tourGuideService = tourGuideService;
    }

    public CopyOnWriteArrayList<VisitedLocation> runBlockingQueue(CopyOnWriteArrayList<User> allUsers) throws InterruptedException, ExecutionException {
        System.out.println("Begin runBlockingQueue.");
        BlockingQueue<User> userQueue = new LinkedBlockingQueue<>();
        BlockingQueue<VisitedLocation> visitedLocationQueue = new LinkedBlockingQueue<>();

//        int userCount = InternalTestHelper.getInternalUserNumber();
        int userCount = allUsers.size();
        System.out.println("There are " + userCount + " users.");
//        publishUser(tourGuideService.getAllUsers(),userQueue);
        publishUser(allUsers,userQueue);
        CountDownLatch userLatch = new CountDownLatch(userCount);
        TourGuideServiceAnalyser tourGuideServiceAnalyser = new TourGuideServiceAnalyser(userQueue,userLatch,visitedLocationQueue, this.tourGuideService);
        CompletableFuture<Void> userAnalyzingFutures = createUserAnalyzingFutures(tourGuideServiceAnalyser);

        CountDownLatch visitedLocationLatch = new CountDownLatch(userCount);
        CompletableFuture<CopyOnWriteArrayList<VisitedLocation>> allVisitedLocationFuture = consumeUsers(visitedLocationQueue, visitedLocationLatch);

        userLatch.await();
        visitedLocationLatch.await();

        CopyOnWriteArrayList<VisitedLocation> allVisitedLocations = allVisitedLocationFuture.get();

        if (!userAnalyzingFutures.isDone()) {
            userAnalyzingFutures.cancel(true);
        }

        return allVisitedLocations;
    }

    private CompletableFuture<CopyOnWriteArrayList<VisitedLocation>> consumeUsers(BlockingQueue<VisitedLocation> visitedLocationQueue, CountDownLatch visitedLocationLatch) {
            return CompletableFuture.supplyAsync(()->{
                CopyOnWriteArrayList<VisitedLocation> allVisitedLocation = new CopyOnWriteArrayList<>();
                while (visitedLocationLatch.getCount() != 0) {
                    try {
                        VisitedLocation visitedLocation = visitedLocationQueue.take();
                        allVisitedLocation.add(visitedLocation);

                    } catch (InterruptedException e) {
                    } finally {
                        visitedLocationLatch.countDown();
                        System.out.println("Waiting for "+visitedLocationLatch.getCount()+" visitedLocations to finish");
                    }
                }
                return allVisitedLocation;
            });
    }

    private CompletableFuture<Void> createUserAnalyzingFutures(TourGuideServiceAnalyser tourGuideServiceAnalyser) {
        CopyOnWriteArrayList<CompletableFuture<Void>> futures = new CopyOnWriteArrayList<>();
        for(int futureNumber = 0; futureNumber< USER_CONSUMER_COUNT; futureNumber++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    ()->{
                        try {
                            tourGuideServiceAnalyser.consumeUsersWhenReady();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
            futures.add(future);
        }
        return CompletableFuture.allOf( futures.toArray(new CompletableFuture[futures.size()]) );
    }

    private void publishUser(CopyOnWriteArrayList<User> allUsers, BlockingQueue<User> userQueue) {
        allUsers
                .forEach(user -> {
                    try {
                        userQueue.put(user);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }});
    }
}
