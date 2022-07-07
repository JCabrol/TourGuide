package tourGuide.repository;

import org.springframework.stereotype.Repository;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

@Repository
public class TripPricerRepository {

    private final TripPricer tripPricer = new TripPricer();

    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) {
        return tripPricer.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

    public String getProviderName(String apiKey, int adults) {
        return tripPricer.getProviderName(apiKey, adults);
    }
}
