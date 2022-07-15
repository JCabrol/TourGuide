package tourGuide.repository;

import gpsUtil.GpsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import rewardCentral.RewardCentral;

import java.util.UUID;

@Repository
public class RewardCentralRepository {

    private final RewardCentral rewardCentral=new RewardCentral();

    public int getAttractionRewardPoints(UUID attractionId, UUID userId){
       return rewardCentral.getAttractionRewardPoints(attractionId, userId);
    }
}
