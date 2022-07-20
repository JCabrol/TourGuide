package tourGuide.repository;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Repository
public class GpsUtilRepository {

    private static final Locale locale = new Locale("en", "US");
    private final GpsUtil gpsUtil = new GpsUtil();

    /**
     * Get a visited location from a userId
     *
     * @param userId a UUID object which is the identifiant of the user
     * @return a visitedLocation
     */
    public VisitedLocation getUserLocation(UUID userId) {
        Locale.setDefault(locale);
        return gpsUtil.getUserLocation(userId);
    }

    /**
     * Get the list of all existing attractions
     *
     * @return a list of attractions containing all existing attractions
     */
    public List<Attraction> getAttractions() {
        return gpsUtil.getAttractions();
    }
}
