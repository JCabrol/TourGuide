package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.TripPricerRepository;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

@Service
public class TripPricerServiceImpl implements TripPricerService {

    @Autowired
    private TripPricerRepository tripPricerRepository;
    private static final String tripPricerApiKey = "test-server-api-key";

//    @Override
//    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) {
//        return tripPricerRepository.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
//    }
//
//    @Override
//    public String getProviderName(String apiKey, int adults) {
//        return tripPricerRepository.getProviderName(apiKey, adults);
//    }

    @Override
    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        List<Provider> providers = tripPricerRepository.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }
}
