package tourGuide.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tourGuide.repository.GpsUtilRepository;
import tourGuide.repository.RewardCentralRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.GpsUtilService;
import tourGuide.service.GpsUtilServiceImpl;
import tourGuide.service.RewardsService;
import tourGuide.service.RewardsServiceImpl;

@Configuration
public class TourGuideModule {

	@Bean
	public GpsUtilServiceImpl getGpsUtil() {
		return new GpsUtilServiceImpl(new GpsUtilRepository());
	}

//	@Bean
//	public RewardsServiceImpl getRewardsService() {
//		return new RewardsServiceImpl(getGpsUtil(), getRewardCentralRepository());
//	}

	@Bean
	public RewardCentralRepository getRewardCentralRepository() {
		return new RewardCentralRepository();
	}

	@Bean
	public UserRepository getUserRepository() {
		return new UserRepository();
	}
}
