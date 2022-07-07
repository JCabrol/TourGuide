package tourGuide.dataSource;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class InternalUserMap {

    private boolean testMode = true;
    private HashMap<String, User> internalUserMap = new HashMap<>();

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public HashMap<String, User> getInternalUserMap() {
        return internalUserMap;
    }

    public void setInternalUserMap(HashMap<String, User> internalUserMap) {
        this.internalUserMap = internalUserMap;}

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
            internalUserMap.put(userName,user);
        });
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime())));
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
