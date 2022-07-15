package tourGuide.service;

import org.springframework.stereotype.Service;
import tourGuide.model.User;
import tripPricer.Provider;

import java.util.List;

@Service
public interface TripPricerService {

    List<Provider> getTripDeals(User user);
}
