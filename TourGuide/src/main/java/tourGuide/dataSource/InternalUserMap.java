package tourGuide.dataSource;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

@Setter
@Getter
@Service
public class InternalUserMap {

    private boolean testMode = true;
    private HashMap<String, User> internalUserMap = new HashMap<>();

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/


    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    public void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);
            internalUserMap.put(userName, user);
        });
    }

    private void generateUserLocationHistory(User user) {
        List<VisitedLocation> visitedLocationList = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> {
            Date date = getRandomTime();
            visitedLocationList.add(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()),date ));
            user.setLatestLocationTimestamp(date);
        });
        user.setVisitedLocations(visitedLocationList);
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }
}
