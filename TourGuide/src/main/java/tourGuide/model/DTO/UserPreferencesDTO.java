package tourGuide.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferencesDTO {


    private Integer attractionProximity;
    private String currency;
    private Integer lowerPricePoint;
    private Integer highPricePoint;
    private Integer tripDuration;
    private Integer ticketQuantity;
    private Integer numberOfAdults;
    private Integer numberOfChildren;
}
