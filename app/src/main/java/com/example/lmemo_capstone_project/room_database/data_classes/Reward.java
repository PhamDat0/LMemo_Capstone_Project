package com.example.lmemo_capstone_project.room_database.data_classes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Reward {
    @PrimaryKey
    @ColumnInfo(name = "RewardID") private int rewardID;

    @ColumnInfo(name = "RewardName") private String rewardName;
    @ColumnInfo(name = "MinimumPoint") private int minimumReachPoint;

    public int getRewardID() {
        return rewardID;
    }

    public void setRewardID(int rewardID) {
        this.rewardID = rewardID;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public int getMinimumReachPoint() {
        return minimumReachPoint;
    }

    public void setMinimumReachPoint(int minimumReachPoint) {
        this.minimumReachPoint = minimumReachPoint;
    }
}
