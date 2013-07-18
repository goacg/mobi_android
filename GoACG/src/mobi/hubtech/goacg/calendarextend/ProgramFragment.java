package mobi.hubtech.goacg.calendarextend;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import mobi.hubtech.calendarview.CalendarController;
import mobi.hubtech.calendarview.CalendarController.EventHandler;
import mobi.hubtech.calendarview.CalendarController.EventInfo;
import mobi.hubtech.calendarview.CalendarController.EventType;
import mobi.hubtech.calendarview.MonthByWeekAdapter;
import mobi.hubtech.calendarview.MonthByWeekFragment;
import mobi.hubtech.calendarview.SimpleWeeksAdapter;
import mobi.hubtech.calendarview.Utils;
import mobi.hubtech.goacg.ProgramListActivity;
import mobi.hubtech.goacg.bean.Album;
import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.bean.TimeSegment;
import mobi.hubtech.goacg.bean.UserInfo;
import mobi.hubtech.goacg.db.DAO;
import mobi.hubtech.goacg.global.C;
import mobi.hubtech.goacg.global.UserInfoUtils;
import mobi.hubtech.goacg.request.BaseResponse;
import mobi.hubtech.goacg.request.RequestTask;
import mobi.hubtech.goacg.request.bean.GetAlbumsRequest;
import mobi.hubtech.goacg.request.bean.GetAlbumsResponse;
import mobi.hubtech.goacg.utils.DateTimeUtils;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProgramFragment extends MonthByWeekFragment {

    private CalendarController mController;
    private Calendar mCalendarTool;
    private GetAlbumsRequestTask mGetAlbumsRequestTask;
    private Dao<Album, Long> mAlbumDao;
    private Dao<Play, Long> mPlayDao;
    
    public ProgramFragment() {
        this(System.currentTimeMillis(), true);
        Constructor();
    }
    
    public ProgramFragment(long initialTime, boolean isMiniMonth) {
        super(initialTime, isMiniMonth);
        Constructor();
    }
    
    private void Constructor() {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mController = CalendarController.getInstance(getActivity());
        mController.registerEventHandler(0, mEventHandler);
        
        mCalendarTool = Calendar.getInstance();
        mGetAlbumsRequestTask = new GetAlbumsRequestTask();
        mAlbumDao = DAO.getInstance().getDao(getActivity(), Album.class);
        mPlayDao = DAO.getInstance().getDao(getActivity(), Play.class);
//        mController.registerFirstEventHandler(0, mEventHandler);
        
//        requestAlbums();
    }
    
    @Override
    protected void setUpHeader() {
        mDayLabels = new String[7];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat chineseDateFormat = new SimpleDateFormat("E", Locale.CHINESE);
        SimpleDateFormat japaneseDateFormat = new SimpleDateFormat("E", Locale.JAPANESE);
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i);
            String chineseDay = chineseDateFormat.format(calendar.getTime());
            String japaneseDay = japaneseDateFormat.format(calendar.getTime());
            mDayLabels[i - Calendar.SUNDAY] = chineseDay.substring(1, chineseDay.length()) + "(" + japaneseDay + ")";
        }
    }
    
    @Override
    protected void setUpAdapter() {
        mFirstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mShowWeekNumber = Utils.getShowWeekNumber(mContext);

        HashMap<String, Integer> weekParams = new HashMap<String, Integer>();
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_NUM_WEEKS, mNumWeeks);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_SHOW_WEEK, mShowWeekNumber ? 1 : 0);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_WEEK_START, mFirstDayOfWeek);
        weekParams.put(MonthByWeekAdapter.WEEK_PARAMS_IS_MINI, mIsMiniMonth ? 1 : 0);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_JULIAN_DAY,
                Time.getJulianDay(mSelectedDay.toMillis(true), mSelectedDay.gmtoff));
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_DAYS_PER_WEEK, mDaysPerWeek);
        if (mAdapter == null) {
            mAdapter = new ProgramAdapter(getActivity(), weekParams);
            mAdapter.registerDataSetObserver(mObserver);
        } else {
            mAdapter.updateParams(weekParams);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setMonthDisplayed(Time time, boolean updateHighlight) {
        super.setMonthDisplayed(time, updateHighlight);
    }

    private EventHandler mEventHandler = new EventHandler() {
        @Override
        public void handleEvent(EventInfo event) {
            if (event.eventType == EventType.GO_TO) {
                handleGoto(event);
            } else if (event.eventType == EventType.VIEW_EVENT) {
                
            }
        }
        @Override
        public long getSupportedEventTypes() {
            return EventType.GO_TO | EventType.VIEW_EVENT | EventType.UPDATE_TITLE;
        }
        @Override
        public void eventsChanged() {
        }
    };
    
    private void handleGoto(EventInfo event) {
        Intent intent = new Intent(getActivity(), ProgramListActivity.class);
        intent.putExtra("timestamp", event.selectedTime.toMillis(true));
        startActivity(intent);
    }
    
    private List<Play> queryPlaysByTime(TimeSegment timeSegment) {
        try {
            long begin = timeSegment.getBegin();
            long end = timeSegment.getEnd();
            QueryBuilder<Play, Long> qb = mPlayDao.queryBuilder();
            qb.setWhere(qb.where()
                    .ge(Play.SHOW_TIME, begin)
                    .and()
                    .le(Play.SHOW_TIME, end));
            return qb.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void requestAlbums() {
        int year = mTempTime.year;
        int month = mTempTime.month;
        UserInfo userInfo = UserInfoUtils.getUserInfo(getActivity());
        if (userInfo.getUserID() == -1) {
            return;
        }
        TimeSegment ts = DateTimeUtils.getMonthTimeSegment(year, month);
        Log.i(C.TAG, "" + ts);
        GetAlbumsRequest request = new GetAlbumsRequest();
        request.setUser_id(userInfo.getUserID());
        request.setBegin(ts.getBegin() / 1000);
        request.setEnd(ts.getEnd() / 1000);
        mGetAlbumsRequestTask.cancel(false);
        mGetAlbumsRequestTask = new GetAlbumsRequestTask();
        mGetAlbumsRequestTask.execute(request);
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            requestAlbums();
        }
    };
    
    class GetAlbumsRequestTask extends RequestTask<GetAlbumsResponse> {
        public GetAlbumsRequestTask() {
            super(GetAlbumsResponse.class);
        }
        
        @Override
        protected void onAfertRequest(GetAlbumsResponse result) {
            if (result != null && result.getError_code() == BaseResponse.ERROR_CODE_SUCCESS) {
                if (result.getTag() == BaseResponse.TAG_CACHEED) {
                    return;
                }
                try {
                    final Album[] albums = result.getAlbums();
                    final ArrayList<Play> plays = new ArrayList<Play>(64);
                    TransactionManager.callInTransaction(mAlbumDao.getConnectionSource(), new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            final ImageLoader loader = ImageLoader.getInstance();
                            for (Album album: albums) {
                                final String iconPath = album.getIcon_32x32();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loader.loadImage(iconPath, null);
                                    }
                                });
                                mAlbumDao.createOrUpdate(album);
                                for (Play play: album.getPlays()) {
                                    play.setAlbum(album);
                                    plays.add(play);
                                }
                            }
                            return null;
                        }
                    });
                    
                    GetAlbumsRequest req = (GetAlbumsRequest) getRequest();
                    List<Play> originPlayList = queryPlaysByTime(new TimeSegment(req.getBegin(), req.getEnd()));
                    HashMap<Long, Play> map = new HashMap<Long, Play>(originPlayList.size() * 2);
                    for (Play play: plays) {
                        long id = play.getPlay_id();
                        map.put(id, play);
                    }
                    for (Play play: originPlayList) {
                        Play oldPlay = map.get(play.getPlay_id());
                        if (oldPlay == null) {
                            mPlayDao.delete(play);
                        }
                    }
                    
                    TransactionManager.callInTransaction(mPlayDao.getConnectionSource(), new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            for (Play play: plays) {
                                mPlayDao.createOrUpdate(play);
                            }
                            return null;
                        }
                    });
                    
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
