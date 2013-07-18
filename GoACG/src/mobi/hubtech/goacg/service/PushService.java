package mobi.hubtech.goacg.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import mobi.hubtech.goacg.ProgramListActivity;
import mobi.hubtech.goacg.R;
import mobi.hubtech.goacg.bean.Album;
import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.db.GoACGDBOpenHelper;
import mobi.hubtech.goacg.global.C;
import mobi.hubtech.goacg.request.RequestTask;
import mobi.hubtech.goacg.request.bean.GetAlbumRequest;
import mobi.hubtech.goacg.request.bean.GetAlbumResponse;
import mobi.hubtech.goacg.request.bean.GetPlayRequest;
import mobi.hubtech.goacg.request.bean.GetPlayResponse;
import mobi.hubtech.goacg.utils.BitmapUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class PushService extends Service {

    private static final int NOTIFY_BAR_ICON_WIDTH_IN_DP = 64;
    private static final int NOTIFY_BAR_ICON_HEIGHT_IN_DP = 64;
    private static final int NOTIFICATION_ID = 2;
    
    private Handler mHandler = new Handler();
    private Dao<Album, Long> mAlbumDao;
    private Dao<Play, Long> mPlayDao;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            GoACGDBOpenHelper oh = OpenHelperManager.getHelper(this, GoACGDBOpenHelper.class);
            mAlbumDao = oh.getDao(Album.class);
            mPlayDao = oh.getDao(Play.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.removeCallbacks(mRunnableUpdate);
        mHandler.post(mRunnableUpdate);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OpenHelperManager.releaseHelper();
    }
    
    private Runnable mRunnableUpdate = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 60 * 60 * 1000);
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (8 > hour && hour > 21) {
                return;
            }
            
            try {
                mAlbumDao.setObjectCache(false);
                final List<Album> albums = mAlbumDao.queryForEq(Album.SUB, true);
                final ArrayList<Album> countList = new ArrayList<Album>(albums.size());
                for (final Album album: albums) {
                    
                    if (C.DEBUG) Log.d(C.TAG, album.getTitle());
                    
                    GetAlbumRequest req = new GetAlbumRequest();
                    req.setAlbum_id(album.getId());
                    req.setUpdate_time(album.getUpdate_time());
                    
                    new RequestTask<GetAlbumResponse>(GetAlbumResponse.class) {
                        @Override
                        protected void onPostExecute(GetAlbumResponse result) {
                            if (result == null) {
                                countList.add(null);
                                return;
                            }
                            if (result.getError_code() == 0) {
                                Album respAlbum = result.getAlbum();
                                countList.add(album);// 使用旧的，需要旧的更新时间
                                try {
                                    respAlbum.setSub(album.isSub());// 使用旧的订阅数据，其实肯定是true
                                    mAlbumDao.createOrUpdate(respAlbum);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else if (result.getError_code() == 2) {
                                countList.add(null);
                            } else {
                                countList.add(null);
                            }
                            if (countList.size() == albums.size()) {
                                updatePlay(countList);
                            }
                        };
                    }.execute(req);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };
    
    private void updatePlay(List<Album> albumList) {
        final ArrayList<Play> allPlayList = new ArrayList<Play>(32);
        for (Album album: albumList) {
            if (album == null) {
                continue;
            }
            try {
                List<Play> playList = mPlayDao.queryForEq(Album.ALBUM_ID, album.getId());
                if (playList != null) {
                    allPlayList.addAll(playList);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        final int size = allPlayList.size();
        final ArrayList<Boolean> countList = new ArrayList<Boolean>(size);
        for (Play play: allPlayList) {
            GetPlayRequest req = new GetPlayRequest();
            req.setPlay_id(play.getPlay_id());
            req.setUpdate_time(play.getUpdate_time());
            new RequestTask<GetPlayResponse>(GetPlayResponse.class) {
                @Override
                protected void onPostExecute(GetPlayResponse result) {
                    countList.add(true);
                    if (result != null) {
                        if (result.getError_code() == GetPlayResponse.ERROR_CODE_SUCCESS) {
                            Play play = result.getPlay();
                            try {
                                mPlayDao.createOrUpdate(play);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (countList.size() == size) {
                        prepareNotification(allPlayList);
                    }
                };
            }.execute(req);
        }
    }
    
    private void startNotification(List<Album> albumList, List<Play> playList, Bitmap bitmap) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long time = playList.get(0).getShow_time();
        
        Intent i = new Intent(PushService.this, ProgramListActivity.class);
        i.putExtra(ProgramListActivity.EXTRA_TIMESTAMP, time * 1000);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pending = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Notification noti = new Notification();
        String notifyString = makeString(albumList);
        noti.tickerText = notifyString;
        noti.contentIntent = pending;
        noti.defaults = Notification.DEFAULT_SOUND;
        noti.icon = R.drawable.ic_launcher;
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.setLatestEventInfo(this, getText(R.string.app_name), notifyString, pending);
        noti.contentView.setImageViewBitmap(android.R.id.icon, bitmap);
        
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
        nm.notify(NOTIFICATION_ID, noti);
    }
    
    private List<Album> getAlbums(List<Play> playList) {
        HashMap<Long, Album> map = new HashMap<Long, Album>(playList.size() * 2);
        for (Play play: playList) {
            map.put(play.getAlbum().getId(), play.getAlbum());
        }
        ArrayList<Album> albumList = new ArrayList<Album>(map.values());
        return albumList;
    }
    
    private void prepareNotification(final List<Play> playList) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        final List<Album> albumList = getAlbums(playList);
        final int count = albumList.size() < 4 ? albumList.size() : 4;
        final ArrayList<Boolean> countList = new ArrayList<Boolean>(4);
        final ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>(4);
        for (int i = 0; i < count; i++) {
            Album play = albumList.get(i);
            String url = play.getBigcover();
            imageLoader.loadImage(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }
                @Override
                public void onLoadingFailed(String imageUri, View view,
                        FailReason failReason) {
                    countList.add(null);
                    if (countList.size() == count) {
                        Bitmap bitmap = drawBitmap(bitmapList);
                        startNotification(albumList, playList, bitmap);
                    }
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    bitmapList.add(loadedImage);
                    countList.add(null);
                    if (countList.size() == count) {
                        Bitmap bitmap = drawBitmap(bitmapList);
                        startNotification(albumList, playList, bitmap);
                    }
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    countList.add(null);
                    if (countList.size() == count) {
                        Bitmap bitmap = drawBitmap(bitmapList);
                        startNotification(albumList, playList, bitmap);
                    }
                }
            });
        }
    }
    
    private String makeString(List<Album> playList) {
        StringBuilder sb = new StringBuilder();
        sb.append("刚刚更新了");
        for (Album album: playList) {
            sb.append(album.getTitle());
            break;
        }
        if (playList.size() > 1) {
            sb.append("等").append(playList.size()).append("个新番");
        }
        return sb.toString();
    }

    private Bitmap drawBitmap(ArrayList<Bitmap> bitmapList) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Rect howBig = new Rect(0, 0, (int) (NOTIFY_BAR_ICON_WIDTH_IN_DP * dm.density), (int) (NOTIFY_BAR_ICON_HEIGHT_IN_DP * dm.density));
        Bitmap bitmap = BitmapUtils.DrawFourBlock(bitmapList, howBig);
        return bitmap;
    }
}
