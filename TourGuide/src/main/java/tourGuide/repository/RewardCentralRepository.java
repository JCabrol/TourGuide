package tourGuide.repository;

import gpsUtil.GpsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import rewardCentral.RewardCentral;

import java.util.UUID;

public class RewardCentralRepository {

    private final RewardCentral rewardCentral=new RewardCentral();

    public int getAttractionRewardPoints(UUID attractionId, UUID userId){
       return rewardCentral.getAttractionRewardPoints(attractionId, userId);
    }
}
