package tourGuide.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CloseAttraction {

    private String attractionName;
    private double attractionLatitude;
    private double attractionLongitude;
    private double distanceFromLocation;
    private int rewardPointsForVisitingAttraction;

}
