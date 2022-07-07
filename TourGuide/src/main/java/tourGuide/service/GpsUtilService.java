package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.repository.GpsUtilRepository;

import java.util.List;
import java.util.UUID;

@Service
public interface GpsUtilService {

    VisitedLocation getUserLocation(UUID userId);

    List<Attraction> getAttractions();
}
