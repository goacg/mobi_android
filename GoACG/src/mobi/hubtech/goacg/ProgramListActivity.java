package mobi.hubtech.goacg;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mobi.hubtech.goacg.bean.Album;
import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.db.GoACGDBOpenHelper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fima.cardsui.views.CardUI;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class ProgramListActivity extends BaseMobclickActivity {
    
    private static final String DATA_FORMAT = "yyyy年M月d日";
    
    public static final String EXTRA_TIMESTAMP = "timestamp";
    
    private CardUI mCardTodayProgram;
    private TextView mTxtDate;
    private View mBtnPrev;
    private View mBtnNext;
    
    private Calendar mCalendarCurrentDay;
    private SimpleDateFormat mSdf; 
    private Dao<Play, Long> mPlayDao;
    private Dao<Album, Long> mAlbumDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.program_list);

        long time = getIntent().getLongExtra(EXTRA_TIMESTAMP, -1);
        if (time == -1) {
            finish();
            return;
        }
        mCalendarCurrentDay = Calendar.getInstance();
        mCalendarCurrentDay.setTimeInMillis(time);
        mCalendarCurrentDay.set(Calendar.HOUR_OF_DAY, 0);
        mCalendarCurrentDay.set(Calendar.MINUTE, 0);
        mCalendarCurrentDay.set(Calendar.SECOND, 0);
        mCalendarCurrentDay.set(Calendar.MILLISECOND, 0);
        mSdf = new SimpleDateFormat(DATA_FORMAT, Locale.US);
        
        initUI();
        initEvent();
        setData();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    
    private void setData() {
        try {
            mTxtDate.setText(mSdf.format(mCalendarCurrentDay.getTime()));
            long unixTimestamp = mCalendarCurrentDay.getTimeInMillis() / 1000;
            mPlayDao.setObjectCache(false);
            List<Play> playList = mPlayDao.queryForEq(Play.SHOW_TIME, unixTimestamp);
            
            mCardTodayProgram.clearCards();
            if (playList != null && playList.size() != 0) {
                for (Play play: playList) {
                    ProgramCard card = new ProgramCard();
                    card.setPlay(play);
                    card.setDao(mAlbumDao, mPlayDao);
                    mCardTodayProgram.addCard(card);
                }
            } else {
                mCardTodayProgram.addCard(new EmptyCard());
            }
            
            mCardTodayProgram.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        mCardTodayProgram = (CardUI) findViewById(R.id.cards_today_program);
        mTxtDate = (TextView) findViewById(R.id.txt_date);
        mBtnPrev = findViewById(R.id.btn_prev);
        mBtnNext = findViewById(R.id.btn_next);
    }
    
    private void initEvent() {
        try {
            GoACGDBOpenHelper dboh = OpenHelperManager.getHelper(this, GoACGDBOpenHelper.class);
            mPlayDao = dboh.getDao(Play.class);
            mAlbumDao = dboh.getDao(Album.class);
            OpenHelperManager.releaseHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mBtnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarCurrentDay.add(Calendar.DAY_OF_YEAR, -1);
                setData();
                mCardTodayProgram.getListView().setSelection(0);
            }
        });
        mBtnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarCurrentDay.add(Calendar.DAY_OF_YEAR, 1);
                setData();
                mCardTodayProgram.getListView().setSelection(0);
            }
        });
    }
}
