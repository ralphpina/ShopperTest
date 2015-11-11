package net.ralphpina.shoppertest.db;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

/**
 * Wraps SnappyDb and provides readable methods
 */
public class DbHelper {

    private static final String DB_NAME = "shopping_test";

    private static final String TIMER_START_TIME = "timer_start_time";
    private static final String LAST_QUESTION    = "last_question";

    private static DbHelper mInstance;
    private        DB       mDb;

    public static void init(Context context) throws SnappydbException {
        mInstance = new DbHelper(context);
    }

    private DbHelper(Context context) throws SnappydbException {
        mDb = DBFactory.open(context, DB_NAME);
    }

    public static DbHelper get() {
        return mInstance;
    }

    // ==== TIMER START TIME =======================================================================

    public void setTimerStart(long time) {
        try {
            mDb.putLong(TIMER_START_TIME, time);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public long getTimerStart() {
        try {
            return mDb.getLong(TIMER_START_TIME);
        } catch (SnappydbException e) {
            return 0l;
        }
    }

    // ===== LAST QUESTION =========================================================================

    public void setLastQuestion(int question) {
        try {
            mDb.putInt(LAST_QUESTION, question);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public int getLastQuestion() {
        try {
            return mDb.getInt(LAST_QUESTION);
        } catch (SnappydbException e) {
            return 0;
        }
    }

    // ===== CLEAR DATA ============================================================================

    public void clearData() {
        try {
            mDb.putLong(TIMER_START_TIME, 0l);
            mDb.putInt(LAST_QUESTION, 0);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }
}
