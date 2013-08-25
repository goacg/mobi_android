package mobi.hubtech.goacg;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WeekCalendarFragment extends Fragment {
    
    private final static int[] COLOR_IDS = {
        android.R.color.holo_red_light,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_purple,
        android.R.color.holo_blue_light,
    };
    
    private ListView mListWeek;
    private TextView mTxtBackToTodayTop;
    private TextView mTxtBackToTodayBottom;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.week_calendar, container, false);
        mListWeek = (ListView) view.findViewById(R.id.list_day);
        
        mTxtBackToTodayTop = (TextView) view.findViewById(R.id.txt_back_to_today_top);
        mTxtBackToTodayBottom = (TextView) view.findViewById(R.id.txt_back_to_today_bottom);
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mListWeek.setAdapter(new ListWeekAdapter());
    }
    
    class ListWeekAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.week_calendar_item, parent, false);
            }
            
            View viewColorMark = convertView.findViewById(R.id.view_color_mark);
            
            viewColorMark.setBackgroundColor(getResources().getColor(COLOR_IDS[position % 5]));
            
            return convertView;
        }
    }
}
