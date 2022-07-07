package tourGuide.service;

import org.springframework.stereotype.Service;
import tourGuide.model.User;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

@Service
public interface TripPricerService {


//    List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints);

//    String getProviderName(String apiKey, int adults);
//
    List<Provider> getTripDeals(User user);
}
