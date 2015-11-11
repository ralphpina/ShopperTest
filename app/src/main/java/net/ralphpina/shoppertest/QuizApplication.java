package net.ralphpina.shoppertest;

import android.app.Application;

import com.snappydb.SnappydbException;

import net.ralphpina.shoppertest.db.DbHelper;

public class QuizApplication extends Application {
    
    private static QuizApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        try {
            DbHelper.init(this);
        } catch (SnappydbException e) {
            throw new RuntimeException("Wut? Our db is not initialized. Someone should look at that...");
        }
    }

    public static QuizApplication get() {
        return mInstance;
    }
}
