package tourGuide.tracker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import tourGuide.model.User;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Tracker extends Thread {

    /**
     * The time the tracker sleeps between two tracking
     */
    private static final long TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES.toSeconds(5);
    /**
     * The executorService on which the tracker runs, in parallel from the rest of the application
     */
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * The service to which the tracker is attached
     */
    private final TourGuideService tourGuideService;

    /**
     * The service permitting to get the list of all users to track
     */
    private final UserService userService;

    /**
     * Boolean permitting to stop tracker
     */
    private boolean stop = false;

    /**
     * Initialize the tracker
     * @param tourGuideService the service to which the tracker is attached
     * @param userService the service permitting to get all the users to track
     */
    public Tracker(TourGuideService tourGuideService, UserService userService) {
        this.tourGuideService = tourGuideService;
        this.userService = userService;
        executorService.submit(this);
    }

    /**
     * Assures to shut down the Tracker thread
     */
    public void stopTracking() {
        stop = true;
        executorService.shutdownNow();
    }

    /**
     * Runs the tracker.
     * The tracker is running at initialization and all five minutes after finishes, it sleeps between two tracking.
     */
    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        while (true) {
            if (Thread.currentThread().isInterrupted() || stop) {
                log.info("Tracker stopping");
                break;
            }

            List<User> users = userService.getAllUsers();
            log.info("Begin Tracker. Tracking " + users.size() + " users.");
            stopWatch.start();
            tourGuideService.trackAllUsers(users);
            stopWatch.stop();
            log.info("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
            stopWatch.reset();
            try {
                log.info("Tracker sleeping");
                TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
