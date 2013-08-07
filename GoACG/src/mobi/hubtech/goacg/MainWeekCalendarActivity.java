package mobi.hubtech.goacg;

import mobi.hubtech.goacg.global.C;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;

import com.umeng.analytics.MobclickAgent;
import com.umeng.common.Log;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainWeekCalendarActivity extends BaseMobclickActivity
    implements OnClickListener, OnItemSelectedListener, OnItemClickListener {

    private FeedbackAgent mFeedbackAgent;
    
    private ListView mListDay;
    private Spinner mSpinnerMenu;
    private View mBtnSearch;
    private View mImgHead;
    private Spinner mSpinnerLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_calendar);
        
        initUmeng();
        initUI();
        initEvent();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
           long id) {
        switch (parent.getId()) {
        case R.id.spinner_location:
            onSpinnerLocationItemSelected(parent, view, position, id);
            break;
        default:
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        switch (parent.getId()) {
        case R.id.list_day:
            onListWeekItemClick(parent, view, position, id);
            break;
        default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_menu:
            onBtnMenuClick(v);
            break;
        case R.id.btn_search:
            onBtnSearchClick(v);
            break;
        case R.id.img_head:
            onImgHeadClick(v);
            break;
        default:
        }
    }

    private void initUmeng() {
        Log.LOG = C.DEBUG;
        MobclickAgent.setDebugMode(C.DEBUG);
        MobclickAgent.onError(this);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        mFeedbackAgent = new FeedbackAgent(this);
        mFeedbackAgent.sync();
        /*
        UmengUpdateAgent.setOnDownloadListener(new UmengDownloadListener() {
            @Override
            public void OnDownloadEnd(int result) {
                switch (result) {
                case 0: // 失败
                    break;
                }
            }
        });
        */
        /*
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                case 0: // has update
                    UmengUpdateAgent.showUpdateDialog(getApplicationContext(), updateInfo);
                    break;
                case 1: // has no update
                    Toast.makeText(getApplicationContext(), "没有更新", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // none wifi
                    Toast.makeText(getApplicationContext(), "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                    break;
                case 3: // time out
                    Toast.makeText(getApplicationContext(), "超时", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });
        */
    }
    
    private void initUI() {
        mListDay = (ListView) findViewById(R.id.list_day);
        mSpinnerMenu = (Spinner) findViewById(R.id.spinner_menu);
        mBtnSearch = findViewById(R.id.btn_search);
        mImgHead = findViewById(R.id.img_head);
        mSpinnerLocation = (Spinner) findViewById(R.id.spinner_location);
    }

    private void initEvent() {
        mListDay.setOnItemClickListener(this);
        mBtnSearch.setOnClickListener(this);
        mImgHead.setOnClickListener(this);
        mSpinnerLocation.setOnItemSelectedListener(this);
        mSpinnerMenu.setOnItemSelectedListener(this);
    }

    private void onImgHeadClick(View v) {
        
    }

    private void onBtnSearchClick(View v) {
        
    }

    private void onBtnMenuClick(View v) {
        
    }

    private void onListWeekItemClick(AdapterView<?> parent, View view,
            int position, long id) {
        
    }

    private void onSpinnerLocationItemSelected(AdapterView<?> parent, View view,
            int position, long id) {
        
    }
}
