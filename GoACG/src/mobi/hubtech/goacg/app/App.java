package mobi.hubtech.goacg.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class App extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory()
            .cacheOnDisc()
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .discCacheFileCount(1024)
            .defaultDisplayImageOptions(options)
            .memoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 2))
        .build();
        ImageLoader.getInstance().init(config);
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        ImageLoader.getInstance().destroy();
    }
}
