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
