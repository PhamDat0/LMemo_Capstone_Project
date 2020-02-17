package com.example.lmemo_capstone_project.room_database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.lmemo_capstone_project.room_database.dao.ExampleDAO;
import com.example.lmemo_capstone_project.room_database.dao.FlashcardDAO;
import com.example.lmemo_capstone_project.room_database.dao.KanjiDAO;
import com.example.lmemo_capstone_project.room_database.dao.NoteDAO;
import com.example.lmemo_capstone_project.room_database.dao.RewardDAO;
import com.example.lmemo_capstone_project.room_database.dao.SetFlashcardDAO;
import com.example.lmemo_capstone_project.room_database.dao.UserDAO;
import com.example.lmemo_capstone_project.room_database.dao.WordDAO;
import com.example.lmemo_capstone_project.room_database.data_classes.User;

@Database(entities = {User.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LMemoDatabase extends RoomDatabase {
    private static final String DB_NAME = "lmemoDatabase.db";

    //This part of code make the database object become singleton
    //このコードはデータベースのオブジェクトをシングルトンパータンにする。
    private static volatile LMemoDatabase instance;
    //Make constructor private to not allow create object
    //データベースのオブジェクトをほかのクラスで作れないように構築子を非公開にする。
    private LMemoDatabase() {}
    //This method provides an access to the database object or creates one
    //if there are no database object.
    //この関数はデータベースのオブジェクトがアクセスできる。オブジェクトがない場合には
    //新しいオブジェクトを作る。
    public static LMemoDatabase getInstance(Context context) {
        //check if the database object is created or not
        //データベースのオブジェクトがあるかどうか確認する。
        if (instance == null) {
            //Double checked locking
            //ダブルチェックロッキング
            synchronized (LMemoDatabase.class) {
                if(instance == null)
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

