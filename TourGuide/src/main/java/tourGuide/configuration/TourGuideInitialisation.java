package tourGuide.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tourGuide.dataSource.InternalUserMap;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

@Slf4j
//This configuration is disabled for unit tests which are running without initializing InternalUserMap and without tracker
//This configuration is disabled for integration tests which are running without tracker
//This configuration is used for performance tests and for running application
@Profile("!test")
@Component
public class TourGuideInitialisation implements ApplicationRunner {

    private final TourGuideService tourGuideService;
    private final RewardsService rewardsService;
    private final InternalUserMap internalUserMap;


    @Autowired
    public TourGuideInitialisation(TourGuideService tourGuideService, RewardsService rewardsService, InternalUserMap internalUserMap) {
        this.tourGuideService = tourGuideService;
        this.rewardsService = rewardsService;
        this.internalUserMap = internalUserMap;
    }

    /**
     * Callback used to run the bean.
     * Permit to initialize the internalUserMap with the chosen user number,
     * begin the tracker running
     * and load Attractions into RewardsService's attractionList
     * when the beans are created.
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        if (internalUserMap.isTestMode()) {
            log.info("TestMode enabled");
            log.info("Initializing users");
            internalUserMap.initializeInternalUsers();
            log.info("Finished initializing users");
        }
        tourGuideService.initializeTourGuideService();
        rewardsService.initializeRewardsService();
    }
}
