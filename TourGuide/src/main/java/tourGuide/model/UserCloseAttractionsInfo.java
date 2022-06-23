package tourGuide.model;

import java.util.ArrayList;
import java.util.List;

public class UserCloseAttractionsInfo {

    private double userLatitude;
    private double userLongitude;
    private List<CloseAttraction> closeAttractions = new ArrayList<>();

public UserCloseAttractionsInfo(){}
    public UserCloseAttractionsInfo(double userLatitude,double userLongitude){
    this.userLatitude = userLatitude;
    this.userLongitude = userLongitude;
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public List<CloseAttraction> getCloseAttractions() {
        return closeAttractions;
    }

    public void setCloseAttractions(List<CloseAttraction> closeAttractions) {
        this.closeAttractions = closeAttractions;
    }
}
