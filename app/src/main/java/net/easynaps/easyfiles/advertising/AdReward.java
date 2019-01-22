package net.easynaps.easyfiles.advertising;

public class AdReward {
    private final String rewardName;
    private final int rewardAmount;

    public AdReward(String rewardName, int rewardAmount) {
        this.rewardName = rewardName;
        this.rewardAmount = rewardAmount;
    }

    public String getRewardName() {
        return rewardName;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }
}
