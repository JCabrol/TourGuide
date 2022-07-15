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
    private final GpsUtil gpsUtil=new GpsUtil();

    public VisitedLocation getUserLocation(UUID userId){
        Locale.setDefault(locale);
        return gpsUtil.getUserLocation(userId);
    }

    public List<Attraction> getAttractions(){
        return gpsUtil.getAttractions();
    }
}
