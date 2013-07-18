package mobi.hubtech.goacg;

import java.io.UnsupportedEncodingException;

import mobi.hubtech.goacg.bean.UserInfo;
import mobi.hubtech.goacg.calendarextend.ProgramFragment;
import mobi.hubtech.goacg.db.DAO;
import mobi.hubtech.goacg.global.C;
import mobi.hubtech.goacg.global.RequestCache;
import mobi.hubtech.goacg.global.UserInfoUtils;
import mobi.hubtech.goacg.request.RequestTask;
import mobi.hubtech.goacg.request.bean.UserRegiterRequest;
import mobi.hubtech.goacg.request.bean.UserRegiterResponse;
import mobi.hubtech.goacg.service.PushService;
import mobi.hubtech.goacg.umeng.ConversationActivity;
import mobi.hubtech.goacg.utils.AlarmUtils;
import mobi.hubtech.goacg.utils.EncodeUtils;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.common.Log;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseMobclickActivity {
    
    private ProgramFragment mProgramFragment;
    
    private Button mBtnGotoToday;
    private Button mBtnFeedback;
    private View mBtnMenu;
    
    private FeedbackAgent mFeedbackAgent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initUmeng();
        initUI();
        initEvent();
        
        try {
/*            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            final String tmDevice, tmSerial, tmPhone, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());*/
            
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                if (info.getType() != ConnectivityManager.TYPE_WIFI) {
                }
            } else {
                Toast.makeText(this, "木有网呀", Toast.LENGTH_SHORT).show();
                return;
            }
            
            UserInfo userInfo = UserInfoUtils.getUserInfo(this);
            if (userInfo.getUserID() == -1) {
                registerForNew();
            }
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    private void initUmeng() {
        Log.LOG = C.DEBUG;
        MobclickAgent.setDebugMode(C.DEBUG);
        MobclickAgent.onError(this);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
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
        mBtnGotoToday = (Button) findViewById(R.id.btn_goto_today);
        mBtnFeedback = (Button) findViewById(R.id.btn_feedback);
        mBtnMenu = findViewById(R.id.btn_menu);
        
        mProgramFragment = new ProgramFragment(System.currentTimeMillis(), false);
        getFragmentManager().beginTransaction()
            .replace(R.id.layout_program, mProgramFragment)
        .commit();
    }

    private void initEvent() {
        mBtnGotoToday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgramFragment.goTo(System.currentTimeMillis(), true, true, true);
            }
        });
        mBtnFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnFeedbackClick(v);
            }
        });
        mBtnMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnMenuClick(v);
            }
        });
        
        mFeedbackAgent = new FeedbackAgent(this);
        mFeedbackAgent.sync();
        
        AlarmUtils.setAlarm(this);
        Intent service = new Intent(this, PushService.class);
        startService(service);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        DAO.getInstance().destroy();
        RequestCache.getInstance().clear();
    }

    private void onBtnFeedbackClick(View v) {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }

    private void onBtnMenuClick(View v) {
        MenuDialogFragment f = new MenuDialogFragment();
        f.show(getFragmentManager(), "MenuDialogFragment");
    }
    
    private void registerForNew() throws UnsupportedEncodingException {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String mac = wm.getConnectionInfo().getMacAddress();
        if (mac != null) {
            final String userMAC = EncodeUtils.toMD5String(mac.getBytes(HTTP.UTF_8));
            UserRegiterRequest request = new UserRegiterRequest();
            request.setUser_push(userMAC);
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
                        userInfo.setUserMAC(userMAC);
                        UserInfoUtils.setUserInfo(getApplicationContext(), userInfo);
                        mProgramFragment.requestAlbums();
                        break;
                    default:
                        break;
                    }
                }
            }.execute(request);
        } else {
        }
    }
}
