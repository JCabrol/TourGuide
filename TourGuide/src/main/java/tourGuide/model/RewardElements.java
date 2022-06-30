package tourGuide.model;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public class RewardElements {
    private User user;
    private VisitedLocation visitedLocation;
    private Attraction attraction;

    public RewardElements(User user, VisitedLocation visitedLocation, Attraction attraction) {
        this.user = user;
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;

    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VisitedLocation getVisitedLocation() {
        return visitedLocation;
    }

    public void setVisitedLocation(VisitedLocation visitedLocation) {
        this.visitedLocation = visitedLocation;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }
}
