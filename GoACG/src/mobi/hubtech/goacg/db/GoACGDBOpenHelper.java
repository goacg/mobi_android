package mobi.hubtech.goacg.db;

import java.sql.SQLException;

import mobi.hubtech.goacg.bean.Album;
import mobi.hubtech.goacg.bean.Play;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class GoACGDBOpenHelper extends OrmLiteSqliteOpenHelper {
    
    private final static int VERSION = 2;
    private final static String NAME = "goacg.db";
    
    public GoACGDBOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Album.class);
            TableUtils.createTable(connectionSource, Play.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, 
            int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Album.class, true);
            TableUtils.dropTable(connectionSource, Play.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
