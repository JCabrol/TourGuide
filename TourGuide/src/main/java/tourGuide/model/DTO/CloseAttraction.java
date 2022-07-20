package tourGuide.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CloseAttraction {

    /**
     * The Attraction's name.
     */
    private String attractionName;

    /**
     * The Attraction's latitude.
     */
    private double attractionLatitude;

    /**
     * The Attraction's longitude.
     */
    private double attractionLongitude;

    /**
     * The distance between the Attraction and the location where the user is.
     */
    private double distanceFromLocation;

    /**
     * The reward points accorded to the user if he visits this attraction.
     */
    private int rewardPointsForVisitingAttraction;

}
