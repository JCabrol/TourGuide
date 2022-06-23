package tourGuide.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.repository.GpsUtilRepository;
import tourGuide.repository.RewardCentralRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {

	@Bean
	public GpsUtilService getGpsUtil() {
		return new GpsUtilService(new GpsUtilRepository());
	}

	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsUtil(), getRewardCentralRepository());
	}

	@Bean
	public RewardCentralRepository getRewardCentralRepository() {
		return new RewardCentralRepository();
	}

	@Bean
	public UserRepository getUserRepository() {
		return new UserRepository();
	}
}
