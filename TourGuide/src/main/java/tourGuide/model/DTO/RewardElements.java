package tourGuide.model.DTO;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RewardElements {

    private VisitedLocation visitedLocation;
    private Attraction attraction;
}
