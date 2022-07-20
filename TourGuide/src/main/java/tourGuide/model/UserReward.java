package tourGuide.model;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserReward {

	/**
	 * The visitedLocation where the user went to get this reward.
	 */
	public final VisitedLocation visitedLocation;

	/**
	 * The attraction close from the visitedLocation where the user went to get this reward.
	 */
	public final Attraction attraction;

	/**
	 * The number of points the user won with this reward.
	 */
	private int rewardPoints;

}
