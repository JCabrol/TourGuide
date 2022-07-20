package tourGuide.model.DTO;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardElements {

    private VisitedLocation visitedLocation;
    private Attraction attraction;
}
