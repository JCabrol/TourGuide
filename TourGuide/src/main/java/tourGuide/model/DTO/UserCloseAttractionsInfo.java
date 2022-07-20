package tourGuide.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserCloseAttractionsInfo {

    /**
     * The latitude of the user's actual location.
     */
    private double userLatitude;
    /**
     * The longitude of the user's actual location.
     */
    private double userLongitude;
    /**
     * The list of five closest attraction from the user's location, with their information.
     */
    private List<CloseAttraction> closeAttractions = new ArrayList<>();


    public UserCloseAttractionsInfo(double userLatitude, double userLongitude) {
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }
}
