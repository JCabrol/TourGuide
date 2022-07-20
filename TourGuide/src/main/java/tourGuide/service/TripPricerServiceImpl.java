package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.repository.TripPricerRepository;
import tripPricer.Provider;

import java.util.List;

@Service
public class TripPricerServiceImpl implements TripPricerService {

    @Autowired
    private TripPricerRepository tripPricerRepository;

    /**
     * A constant used to connect with tripPricer Api
     */
    private static final String TRIP_PRICER_API_KEY = "test-server-api-key";

    /**
     * Get a list of Provider objects for a user, calculated from its preferences and its already obtained points
     * A provider object contains an id, a name and a price to pay, corresponding to a trip proposition made by an agency
     *
     * @param user the user whose providers are calculated
     * @return a list of 5 providers
     */
    @Override
    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        List<Provider> providers = tripPricerRepository.getPrice(TRIP_PRICER_API_KEY, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }
}
