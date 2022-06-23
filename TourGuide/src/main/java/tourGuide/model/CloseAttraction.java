package tourGuide.model;

public class CloseAttraction {

    private String attractionName;
    private double attractionLatitude;
    private double attractionLongitude;
    private double distanceFromLocation;
    private int rewardPointsForVisitingAttraction;

    public CloseAttraction(String attractionName, double attractionLatitude, double attractionLongitude, double distanceFromLocation, int rewardPointsForVisitingAttraction){
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.distanceFromLocation = distanceFromLocation;
        this.rewardPointsForVisitingAttraction = rewardPointsForVisitingAttraction;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public double getAttractionLatitude() {
        return attractionLatitude;
    }

    public void setAttractionLatitude(double attractionLatitude) {
        this.attractionLatitude = attractionLatitude;
    }

    public double getAttractionLongitude() {
        return attractionLongitude;
    }

    public void setAttractionLongitude(double attractionLongitude) {
        this.attractionLongitude = attractionLongitude;
    }

    public double getDistanceFromLocation() {
        return distanceFromLocation;
    }

    public void setDistanceFromLocation(double distanceFromLocation) {
        this.distanceFromLocation = distanceFromLocation;
    }

    public int getRewardPointsForVisitingAttraction() {
        return rewardPointsForVisitingAttraction;
    }

    public void setRewardPointsForVisitingAttraction(int rewardPointsForVisitingAttraction) {
        this.rewardPointsForVisitingAttraction = rewardPointsForVisitingAttraction;
    }


}
