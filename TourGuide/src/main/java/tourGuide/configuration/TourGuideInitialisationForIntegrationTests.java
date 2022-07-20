package tourGuide.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tourGuide.dataSource.InternalUserMap;
import tourGuide.service.RewardsService;

@Slf4j
//This configuration is only for integration tests which are running without tracker but with initializing InternalUserMap and attractionList
@Profile("integrationTest")
@Component
public class TourGuideInitialisationForIntegrationTests implements ApplicationRunner {

    private final RewardsService rewardsService;
    private final InternalUserMap internalUserMap;


    @Autowired
    public TourGuideInitialisationForIntegrationTests(RewardsService rewardsService, InternalUserMap internalUserMap) {
        this.rewardsService = rewardsService;
        this.internalUserMap = internalUserMap;
    }

    /**
     * Callback used to run the bean.
     * Permit to initialize the internalUserMap with the chosen user number,
     * and load Attractions into RewardsService's attractionList
     * but doesn't run the tracker when the beans are created.
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
        rewardsService.initializeRewardsService();
    }
}
