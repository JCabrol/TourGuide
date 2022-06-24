package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.repository.GpsUtilRepository;

import java.util.List;
import java.util.UUID;

public class GpsUtilServiceImpl implements GpsUtilService {
    private final GpsUtilRepository gpsUtilRepository;


    public GpsUtilServiceImpl(GpsUtilRepository gpsUtilRepository) {
        this.gpsUtilRepository = gpsUtilRepository;
    }

    @Override
    public VisitedLocation getUserLocation(UUID userId) {
        return gpsUtilRepository.getUserLocation(userId);
    }

    @Override
    public List<Attraction> getAttractions() {
        return gpsUtilRepository.getAttractions();
    }
}
