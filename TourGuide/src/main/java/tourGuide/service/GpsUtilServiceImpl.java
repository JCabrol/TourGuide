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

//    public GpsUtilServiceImpl(){
//        gpsUtilRepository = new GpsUtilRepository();
//    }

    @Override
    public VisitedLocation getUserLocation(UUID userId) {
        return gpsUtilRepository.getUserLocation(userId);
    }

    @Override
    public List<Attraction> getAttractions() {
        return gpsUtilRepository.getAttractions();
    }
}
