package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.repository.GpsUtilRepository;

import java.util.List;
import java.util.UUID;

@Service
public class GpsUtilServiceImpl implements GpsUtilService {

    @Autowired
    private GpsUtilRepository gpsUtilRepository;

    /**
     * Get a visited location from a userId
     *
     * @param userId a UUID object which is the identifiant of the user
     * @return a visitedLocation
     */
    @Override
    public VisitedLocation getUserLocation(UUID userId) {
        return gpsUtilRepository.getUserLocation(userId);
    }

    /**
     * Get the list of all existing attractions
     *
     * @return a list of attractions containing all existing attractions
     */
    @Override
    public List<Attraction> getAttractions() {
        return gpsUtilRepository.getAttractions();
    }
}
