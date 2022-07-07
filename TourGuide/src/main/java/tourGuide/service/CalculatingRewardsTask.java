package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.model.RewardElements;
import tourGuide.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class CalculatingRewardsTask extends RecursiveTask<List<RewardElements>> {


    private final User user;
    private final List<Attraction> attractionList;
    private final RewardsService rewardsService;


    public CalculatingRewardsTask(User user, List<Attraction> attractionList, RewardsService rewardsService) {
        this.user = user;
        this.attractionList = attractionList;
        this.rewardsService = rewardsService;
    }

    @Override
    protected List<RewardElements> compute() {
        List<RewardElements> rewardElementsList = new ArrayList<>();

        if (attractionList.size() == 1) {
            for (VisitedLocation visitedLocation : user.getVisitedLocations()) {

                if (rewardsService.nearAttraction(visitedLocation, attractionList.get(0))) {
                    if (rewardsService.isNotInRewardsList(attractionList.get(0).attractionName, user)) {
                        Attraction attraction = attractionList.get(0);
                        rewardElementsList.add(new RewardElements(visitedLocation, attraction));
                    }
                }
            }
        } else {

            int midPoint = attractionList.size() / 2;
            CalculatingRewardsTask left =
                    new CalculatingRewardsTask(
                            user, attractionList.subList(0, midPoint), rewardsService);

            CalculatingRewardsTask right =
                    new CalculatingRewardsTask(
                            user, attractionList.subList(midPoint, attractionList.size()), rewardsService);
            left.fork();
            List<RewardElements> rightResult = right.compute();
            List<RewardElements> leftResult = left.join();
            rewardElementsList.addAll(rightResult);
            rewardElementsList.addAll(leftResult);
        }
        return rewardElementsList;
    }
}
