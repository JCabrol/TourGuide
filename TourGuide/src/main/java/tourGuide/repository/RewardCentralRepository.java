package tourGuide.repository;

import org.springframework.stereotype.Repository;
import rewardCentral.RewardCentral;

import java.util.UUID;

@Repository
public class RewardCentralRepository {

    private final RewardCentral rewardCentral = new RewardCentral();

    /**
     * Get a number of points to attribute to a user for visiting an attraction
     *
     * @param userId       a UUID object which is the identifiant of the user
     * @param attractionId a UUID object which is the identifiant of the attraction
     * @return an int which is the number of points to attribute
     */
    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentral.getAttractionRewardPoints(attractionId, userId);
    }
}
