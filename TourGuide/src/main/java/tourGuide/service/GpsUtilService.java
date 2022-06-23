package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.repository.GpsUtilRepository;

import java.util.List;
import java.util.UUID;

public class GpsUtilService {
    private final GpsUtilRepository gpsUtilRepository;

    public GpsUtilService(GpsUtilRepository gpsUtilRepository) {
        this.gpsUtilRepository = gpsUtilRepository;
    }


    public VisitedLocation getUserLocation(UUID userId) {
        return gpsUtilRepository.getUserLocation(userId);
    }

    public List<Attraction> getAttractions() {
        return gpsUtilRepository.getAttractions();
    }
}
