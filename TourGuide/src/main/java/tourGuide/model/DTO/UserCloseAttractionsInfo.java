package tourGuide.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserCloseAttractionsInfo {

    private double userLatitude;
    private double userLongitude;
    private List<CloseAttraction> closeAttractions = new ArrayList<>();


    public UserCloseAttractionsInfo(double userLatitude, double userLongitude) {
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }
}
