package mobi.hubtech.goacg;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import mobi.hubtech.goacg.bean.Album;
import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.bean.TimeSegment;
import mobi.hubtech.goacg.bean.UserInfo;
import mobi.hubtech.goacg.global.C;
import mobi.hubtech.goacg.global.UserInfoUtils;
import mobi.hubtech.goacg.request.BaseResponse;
import mobi.hubtech.goacg.request.RequestTask;
import mobi.hubtech.goacg.request.bean.GetAlbumsRequest;
import mobi.hubtech.goacg.request.bean.GetAlbumsResponse;
import mobi.hubtech.goacg.request.bean.UserRegiterRequest;
import mobi.hubtech.goacg.request.bean.UserRegiterResponse;
import mobi.hubtech.goacg.utils.DateTimeUtils;
import mobi.hubtech.goacg.utils.EncodeUtils;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainWeekCalendarActivity extends BaseMobclickActivity
    implements OnClickListener, OnItemSelectedListener, OnItemClickListener {
    
    private final static int GET_COUNT = 21;
    
    private ListView mListDay;
    private Spinner mSpinnerMenu;
    private View mBtnSearch;
    private View mImgHead;
    private Spinner mSpinnerLocation;
    
    private GetAlbumsRequestTask mGetAlbumsRequestTask;
    private Dao<Album, Long> mAlbumDao;
    private Dao<Play, Long> mPlayDao;
    private FeedbackAgent mFeedbackAgent;
    private Handler mHandler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_week_calendar);
        
        initUmeng();
        initUI();
        initEvent();
        initOther();
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
        com.umeng.common.Log.LOG = C.DEBUG;
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

    private void initOther() {
        mGetAlbumsRequestTask = new GetAlbumsRequestTask();
        
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (info.getType() != ConnectivityManager.TYPE_WIFI) {
                // 当前网络非wifi网络，暂时不做处理
            } else {
                // 当前使用wifi网络，暂时不做处理
            }
        } else {
            Toast.makeText(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        
        UserInfo userInfo = UserInfoUtils.getUserInfo(this);
        if (userInfo.getUserID() == -1) {
            registerForNew();
        } else {
            requestAlbums();
        }
    }

    private void registerForNew() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String mac = wm.getConnectionInfo().getMacAddress();
        if (mac != null) {
            String md5Mac = null;
            try {
                md5Mac = EncodeUtils.toMD5String(mac.getBytes(HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
            
            final String userMac = md5Mac;
            UserRegiterRequest request = new UserRegiterRequest(md5Mac);
            new RequestTask<UserRegiterResponse>(UserRegiterResponse.class) {
                @Override
                protected void onPostExecute(UserRegiterResponse result) {
                    if (result == null) {
                        return;
                    }
                    switch (result.getError_code()) {
                    case UserRegiterResponse.ERROR_CODE_SUCCESS:
                    case UserRegiterResponse.ERROR_CODE_ALREADY_REGISTER:
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUserID(result.getUid());
                        userInfo.setUserMAC(userMac);
                        UserInfoUtils.setUserInfo(getApplicationContext(), userInfo);
                        requestAlbums();
                        break;
                    default:
                        break;
                    }
                }
            }.execute(request);
        } else {
            // no wifi mac address
            return;
        }
    }
    
    private void requestAlbums() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        UserInfo userInfo = UserInfoUtils.getUserInfo(this);
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

    private List<Play> queryPlaysByTime(long begin, long end) {
        try {
            List<Play> list = mPlayDao.queryBuilder().where()
                .ge(Play.SHOW_TIME, begin)
                .and()
                .le(Play.SHOW_TIME, end)
            .query();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    class GetAlbumsRequestTask extends RequestTask<GetAlbumsResponse> {
        public GetAlbumsRequestTask() {
            super(GetAlbumsResponse.class);
        }
        
        @Override
        protected void onAfertRequest(GetAlbumsResponse result) {
            if (result != null 
                    && result.getError_code() == BaseResponse.ERROR_CODE_SUCCESS) {
                if (result.getFlag() == BaseResponse.FLAG_CACHEED) {
                    // 如果是cache过，就证明已经从服务器取过了，不需要再有其他动作
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
                    List<Play> originPlayList = queryPlaysByTime(req.getBegin(), req.getEnd());
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
                            // TODO 刷新UI
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
