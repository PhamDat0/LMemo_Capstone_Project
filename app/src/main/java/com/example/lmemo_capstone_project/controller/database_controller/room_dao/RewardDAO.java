package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lmemo_capstone_project.model.room_db_entity.Reward;

@Dao
public interface RewardDAO {
    @Insert
    void insertReward(Reward reward);

    @Query("SELECT * FROM Reward")
    Reward[] getRewards();

    @Query("SELECT * FROM Reward WHERE MinimumPoint <= :minimumPoint order by MinimumPoint desc LIMIT 1")
    Reward[] getBestReward(int minimumPoint);

    @Query("SELECT * FROM Reward WHERE MinimumPoint > :minimumPoint order by MinimumPoint asc LIMIT 1")
    Reward[] getNextReward(int minimumPoint);
}
