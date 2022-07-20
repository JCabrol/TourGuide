package tourGuide.service;

import org.springframework.stereotype.Service;
import tourGuide.model.User;
import tripPricer.Provider;

import java.util.List;

@Service
public interface TripPricerService {

    /**
     * Get a list of Provider objects for a user, calculated from its preferences and its already obtained points
     * A provider object contains an id, a name and a price to pay, corresponding to a trip proposition made by an agency
     *
     * @param user the user whose providers are calculated
     * @return a list of 5 providers
     */
    List<Provider> getTripDeals(User user);
}
