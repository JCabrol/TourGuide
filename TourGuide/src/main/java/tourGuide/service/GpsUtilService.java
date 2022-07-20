package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface GpsUtilService {

    /**
     * Get a visited location from a userId
     *
     * @param userId a UUID object which is the identifiant of the user
     * @return a visitedLocation
     */
    VisitedLocation getUserLocation(UUID userId);

    /**
     * Get the list of all existing attractions
     *
     * @return a list of attractions containing all existing attractions
     */
    List<Attraction> getAttractions();
}
