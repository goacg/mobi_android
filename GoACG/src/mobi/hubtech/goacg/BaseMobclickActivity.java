package mobi.hubtech.goacg;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

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
