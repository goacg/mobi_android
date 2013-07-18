package mobi.hubtech.goacg.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class DAO {
    
    private static DAO sInstance;
    
    public static DAO getInstance() {
        if (sInstance == null) {
            sInstance = new DAO();
        }
        return sInstance;
    }
    
    private DAO() {};
    
    private GoACGDBOpenHelper mOpenHelper;
    
    public SQLiteOpenHelper getOpenHelper() {
        return mOpenHelper;
    }
    
    public <D extends Dao<T, ?>, T> D getDao(Context context, Class<T> clazz) {
        try {
            if (mOpenHelper == null) {
                mOpenHelper = OpenHelperManager.getHelper(context, GoACGDBOpenHelper.class);
            }
            return mOpenHelper.getDao(clazz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void destroy() {
        if (mOpenHelper != null) {
            OpenHelperManager.releaseHelper();
            mOpenHelper = null;
        }
    }
}
