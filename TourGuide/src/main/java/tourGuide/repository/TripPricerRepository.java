package tourGuide.repository;

import org.springframework.stereotype.Repository;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

@Repository
public class TripPricerRepository {

    private final TripPricer tripPricer = new TripPricer();

    /**
     * Get a list of Provider objects for a user, calculated from its preferences and its already obtained points
     * A provider object contains an id, a name and a price to pay, corresponding to a trip proposition made by an agency
     *
     * @param apiKey        a String which is a code to connect with the api tripPricer
     * @param userId        a UUID object which is the identifiant of user
     * @param adults        the number of adults for the trip
     * @param children      the number of children for the trip
     * @param nightsStay    the duration of the trip
     * @param rewardsPoints the total of points obtained by the user
     * @return a list of 5 providers
     */
    public List<Provider> getPrice(String apiKey, UUID userId, int adults, int children, int nightsStay, int rewardsPoints) {
        return tripPricer.getPrice(apiKey, userId, adults, children, nightsStay, rewardsPoints);
    }

}
