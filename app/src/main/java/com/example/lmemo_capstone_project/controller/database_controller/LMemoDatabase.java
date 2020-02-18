package com.example.lmemo_capstone_project.controller.database_controller;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.lmemo_capstone_project.controller.database_controller.dao.ExampleDAO;
import com.example.lmemo_capstone_project.controller.database_controller.dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.dao.KanjiDAO;
import com.example.lmemo_capstone_project.controller.database_controller.dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.dao.SetFlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.dao.UserDAO;
import com.example.lmemo_capstone_project.controller.database_controller.dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

@Database(entities = {User.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LMemoDatabase extends RoomDatabase {
    private static final String DB_NAME = "lmemoDatabase.db";

    //This part of code make the database object become singleton
    //このコードはデータベースのオブジェクトをシングルトンパータンにする。
    private static volatile LMemoDatabase instance;

    /**
     * Make constructor private to not allow create object
     * データベースのオブジェクトをほかのクラスで作れないように構築子を非公開にする。
     */
    private LMemoDatabase() {
    }

    /**
     * @return an object where we can take DAO from.
     * この関数はデータベースのオブジェクトを返します。オブジェクトがない場合には新しいオブジェクトを作りません。
     */
    public static LMemoDatabase getInstance() {
        if (instance == null) {
            throw new NullPointerException();
        }
        return instance;
    }

    /**
     * @param context : The context that contains this database
     * @return an object where we can take DAO from.
     * This method provides an access to the database object or creates one　if there are no database
     * object.
     * この関数はデータベースのオブジェクトを返します。オブジェクトがない場合には新しいオブジェクトを作ります。
     */
    public static LMemoDatabase getInstance(Context context) {
        //check if the database object is created or not
        //データベースのオブジェクトがあるかどうか確認する。
        if (instance == null) {
            //Double checked locking
            //ダブルチェックロッキング
            synchronized (LMemoDatabase.class) {
                if (instance == null)
                    instance = createDBObject(context);
            }
        }
        return instance;
    }

    //Create a database object
    private static LMemoDatabase createDBObject(Context context) {
        return Room.databaseBuilder(context, LMemoDatabase.class, DB_NAME).build();
    }
    // End of singleton part.
    // シングルトンパータンにするのが終った。

    public abstract ExampleDAO exampleDAO();

    public abstract FlashcardDAO flashcardDAO();

    public abstract KanjiDAO kanjiDAO();

    public abstract NoteDAO noteDAO();

    public abstract RewardDAO rewardDAO();

    public abstract SetFlashcardDAO setFlashcardDAO();

    public abstract UserDAO userDAO();

    public abstract WordDAO wordDAO();

}
