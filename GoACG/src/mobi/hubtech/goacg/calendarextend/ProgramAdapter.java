package mobi.hubtech.goacg.calendarextend;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import mobi.hubtech.calendarview.MonthByWeekAdapter;
import mobi.hubtech.calendarview.MonthWeekEventsView;
import mobi.hubtech.calendarview.SimpleWeekView;
import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.db.DAO;
import android.content.Context;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class ProgramAdapter extends MonthByWeekAdapter {

    private Dao<Play, Long> mPlayDao;
    
    public ProgramAdapter(Context context, HashMap<String, Integer> params) {
        super(context, params);
        mPlayDao = DAO.getInstance().getDao(context, Play.class);
    }
    
    @Override
    public void updateFocusMonth(int month) {
        super.updateFocusMonth(month);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProgramEventsView v;
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        HashMap<String, Integer> drawingParams = null;
        boolean isAnimatingToday = false;
        if (convertView != null) {
            v = (ProgramEventsView) convertView;
            // Checking updateToday uses the current params instead of the new
            // params, so this is assuming the view is relatively stable
            if (mAnimateToday && v.updateToday(mSelectedDay.timezone)) {
                long currentTime = System.currentTimeMillis();
                // If it's been too long since we tried to start the animation
                // don't show it. This can happen if the user stops a scroll
                // before reaching today.
                if (currentTime - mAnimateTime > ANIMATE_TODAY_TIMEOUT) {
                    mAnimateToday = false;
                    mAnimateTime = 0;
                } else {
                    isAnimatingToday = true;
                    // There is a bug that causes invalidates to not work some
                    // of the time unless we recreate the view.
                    v = new ProgramEventsView(mContext);
                }
            } else {
                drawingParams = (HashMap<String, Integer>) v.getTag();
            }
        } else {
            v = new ProgramEventsView(mContext);
        }
        if (drawingParams == null) {
            drawingParams = new HashMap<String, Integer>();
        }
        drawingParams.clear();

        v.setLayoutParams(params);
        v.setClickable(true);
        v.setOnTouchListener(this);

        int selectedDay = -1;
        if (mSelectedWeek == position) {
            selectedDay = mSelectedDay.weekDay;
        }

        drawingParams.put(SimpleWeekView.VIEW_PARAMS_HEIGHT,
                (parent.getHeight() + parent.getTop()) / mNumWeeks);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_SHOW_WK_NUM, mShowWeekNumber ? 1 : 0);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_WEEK_START, mFirstDayOfWeek);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_NUM_DAYS, mDaysPerWeek);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_WEEK, position);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_FOCUS_MONTH, mFocusMonth);
        drawingParams.put(MonthWeekEventsView.VIEW_PARAMS_ORIENTATION, mOrientation);

        if (isAnimatingToday) {
            drawingParams.put(MonthWeekEventsView.VIEW_PARAMS_ANIMATE_TODAY, 1);
            mAnimateToday = false;
        }

        int julianMonday = SimpleWeekView.getJulianMondayFromWeeksSinceEpoch(position);
        Time time = new Time(mSelectedDay.timezone);
        time.setJulianDay(julianMonday);
        Calendar c = Calendar.getInstance();
        c.set(time.year, time.month, time.monthDay, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, -1);// 从周日开始，所以-1
        
        // 一周7天的数据，从周日开始
        ArrayList<List<Play>> playList1_7 = new ArrayList<List<Play>>(7);
        try {
            mPlayDao.setObjectCache(true);
            QueryBuilder<Play, Long> builder = mPlayDao.queryBuilder();
            Where<Play, Long> where = builder.where();
            where.ge("show_time", c.getTimeInMillis() / 1000);
            where.and();
            c.add(Calendar.DAY_OF_YEAR, 7);
            where.le("show_time", c.getTimeInMillis() / 1000);
            builder.setWhere(where);
            List<Play> playList = mPlayDao.query(builder.prepare());
            HashMap<Long, List<Play>> map = new HashMap<Long, List<Play>>(32);
            for (Play play: playList) {
                List<Play> list = map.get(play.getShow_time());
                if (list == null) {
                    list = new ArrayList<Play>(8);
                    map.put(play.getShow_time(), list);
                }
                list.add(play);
            }
            
            c.add(Calendar.DAY_OF_YEAR, -7);// 复原
            for (int i = 0; i < 7; i++) {
                long unixTimestamp = c.getTimeInMillis() / 1000;
                List<Play> list = map.get(unixTimestamp);
                playList1_7.add(list);
                c.add(Calendar.DAY_OF_YEAR, 1);
            }
            v.setData(playList1_7);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        v.setWeekParams(drawingParams, mSelectedDay.timezone);
        sendEventsToView(v);
        
        return v;
    }
}
