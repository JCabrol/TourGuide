package tourGuide.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferencesDTO {

    /**
     * The user's preferred distance for attractions.
     */
    private Integer attractionProximity;

    /**
     * A String representing the currency used by the user. Has to be one of the java's CurrencyUnit code.
     */
    private String currency;

    /**
     * The user's minimal price for trips.
     */
    private Integer lowerPricePoint;

    /**
     * The user's maximal price for trips.
     */
    private Integer highPricePoint;

    /**
     * The user's chosen number of days for a trip.
     */
    private Integer tripDuration;

    /**
     * The user's chosen number of tickets for attractions.
     */
    private Integer ticketQuantity;
    /**
     * The user's chosen number of adults for a trip.
     */
    private Integer numberOfAdults;
    /**
     * The user's chosen number of children for a trip.
     */
    private Integer numberOfChildren;
}
