package mobi.hubtech.goacg;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * 封装友盟各种功能的Activity
 * 所有Activity应该继承此Activity
 */
public class BaseMobclickActivity extends Activity {
    
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
