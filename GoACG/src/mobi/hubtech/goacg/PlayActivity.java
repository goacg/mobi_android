package mobi.hubtech.goacg;

import java.util.TreeMap;

import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PlayActivity extends BaseMobclickActivity {
    
    public static final String EXTRA_URL = "url";
    
    private WebView mWebPlay;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);
        
        String url = getIntent().getStringExtra(EXTRA_URL);

        mWebPlay = (WebView) findViewById(R.id.web_play);
        mWebPlay.setWebChromeClient(new WebChromeClient() {
            
        });
        mWebPlay.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebPlay.getSettings().setJavaScriptEnabled(true);
        mWebPlay.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
        mWebPlay.getSettings().setPluginState(PluginState.ON);
        mWebPlay.getSettings().setBuiltInZoomControls(true);
        mWebPlay.getSettings().setDisplayZoomControls(false);
        mWebPlay.getSettings().setUseWideViewPort(true);
        mWebPlay.getSettings().setAppCacheEnabled(true);
        mWebPlay.setInitialScale(50);
        
        if (url != null) {
            TreeMap<String, String> map = new TreeMap<String, String>();
            map.put("User-Agent", "Mozilla/5.0 (iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
            mWebPlay.loadUrl(url, map);
//            mWebPlay.loadUrl(url);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(url));
//            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebPlay.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mWebPlay.onPause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebPlay.stopLoading();
        mWebPlay.destroy();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
