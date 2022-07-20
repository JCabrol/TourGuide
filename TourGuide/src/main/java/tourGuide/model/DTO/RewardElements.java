package tourGuide.model.DTO;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardElements {

    /**
     * The VisitedLocation for which reward has to be added.
     */
    private VisitedLocation visitedLocation;
    /**
     * The attraction for which reward has to be added.
     */
    private Attraction attraction;
}
