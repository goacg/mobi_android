package mobi.hubtech.goacg.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import mobi.hubtech.goacg.ProgramListActivity;
import mobi.hubtech.goacg.R;
import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.db.GoACGDBOpenHelper;
import mobi.hubtech.goacg.request.RequestTask;
import mobi.hubtech.goacg.request.bean.GetPlaysRequest;
import mobi.hubtech.goacg.request.bean.GetPlaysResponse;
import mobi.hubtech.goacg.utils.AlarmUtils;
import mobi.hubtech.goacg.utils.BitmapUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class NotifyService extends Service {

    private static final int NOTIFY_BAR_ICON_WIDTH_IN_DP = 64;
    private static final int NOTIFY_BAR_ICON_HEIGHT_IN_DP = 64;
    private static final int NOTIFICATION_ID = 1;
    
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
            mPlayDao = oh.getDao(Play.class);
            OpenHelperManager.releaseHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            final long unixTimestamp = calendar.getTimeInMillis() / 1000;
            mPlayDao.setObjectCache(false);
            final List<Play> playList = mPlayDao.queryForEq(Play.SHOW_TIME, unixTimestamp);
            
            if (playList == null || playList.size() == 0) {
                return START_NOT_STICKY;
            }
            
            makeBitmap(playList, new OnMakeBitmapFinishListener() {
                @Override
                public void onFinish(Bitmap bitmap) {
                    Intent i = new Intent(NotifyService.this, ProgramListActivity.class);
                    i.putExtra(ProgramListActivity.EXTRA_TIMESTAMP, unixTimestamp * 1000);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pending = PendingIntent.getActivity(NotifyService.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    
                    Notification noti = new Notification();
                    String notifyString = makeString(playList);
                    noti.tickerText = notifyString;
                    noti.contentIntent = pending;
                    noti.defaults = Notification.DEFAULT_SOUND;
                    noti.icon = R.drawable.ic_launcher;
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    noti.setLatestEventInfo(NotifyService.this, getText(R.string.app_name), notifyString, pending);
                    noti.contentView.setImageViewBitmap(android.R.id.icon, bitmap);
                    
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.cancel(NOTIFICATION_ID);
                    nm.notify(NOTIFICATION_ID, noti);
                    
                    AlarmUtils.setAlarmTomorrow(NotifyService.this);
                    
                    // 提醒后更新下一话，如果缓存了下一话，就不请求了
                    for (Play play: playList) {
                        try {
                            QueryBuilder<Play, Long> builder = mPlayDao.queryBuilder();
                            Where<Play, Long> where = builder.where().gt(Play.VOL, play.getVol());
                            builder.setWhere(where);
                            if (builder.query().size() > 0) {
                                continue;
                            }
                            
                            GetPlaysRequest getPlayReq = new GetPlaysRequest();
                            getPlayReq.setAlbum_id(play.getAlbum().getId());
                            getPlayReq.setVol(play.getVol());
                            getPlayReq.setN(1);
                            new RequestTask<GetPlaysResponse>(GetPlaysResponse.class) {
                                @Override
                                protected void onPostExecute(final GetPlaysResponse result) {
                                    if (result == null) {
                                        return;
                                    }
                                    if (result.getError_code() == 0) {
                                        try {
                                            TransactionManager.callInTransaction(mPlayDao.getConnectionSource(), new Callable<Void>() {
                                                @Override
                                                public Void call() throws Exception {
                                                    for (Play play: result.getPlays()) {
                                                        mPlayDao.createOrUpdate(play);
                                                    }
                                                    return null;
                                                }
                                            });
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }.execute(getPlayReq);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    stopSelfResult(startId);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return START_NOT_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void makeBitmap(List<Play> playList, final OnMakeBitmapFinishListener onFinishListener) {
        final int count = playList.size() < 4 ? playList.size() : 4;
        final ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>(4);
        final ArrayList<Boolean> countList = new ArrayList<Boolean>(4);
        for (int i = 0; i < count; i++) {
            String url = playList.get(i).getAlbum().getIcon_32x32();
            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }
                @Override
                public void onLoadingFailed(String imageUri, View view,
                        FailReason failReason) {
                    countList.add(true);
                    if (countList.size() == count) {
                        Bitmap bitmap = drawBitmap(bitmapList);
                        onFinishListener.onFinish(bitmap);
                    }
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    countList.add(true);
                    bitmapList.add(loadedImage);
                    if (countList.size() == count) {
                        Bitmap bitmap = drawBitmap(bitmapList);
                        onFinishListener.onFinish(bitmap);
                    }
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    countList.add(true);
                    if (countList.size() == count) {
                        Bitmap bitmap = drawBitmap(bitmapList);
                        onFinishListener.onFinish(bitmap);
                    }
                }
            });
        }
    }
    
    private Bitmap drawBitmap(ArrayList<Bitmap> bitmapList) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Rect howBig = new Rect(0, 0, (int) (NOTIFY_BAR_ICON_WIDTH_IN_DP * dm.density), (int) (NOTIFY_BAR_ICON_HEIGHT_IN_DP * dm.density));
        Bitmap bitmap = BitmapUtils.DrawFourBlock(bitmapList, howBig);
        return bitmap;
    }
    
    private String makeString(List<Play> playList) {
        StringBuilder sb = new StringBuilder();
        for (Play play: playList) {
            sb.append(play.getAlbum().getTitle());
            break;
        }
        if (playList.size() > 1) {
            sb.append("等").append(playList.size()).append("个新番");
        }
        sb.append("今日放送");
        return sb.toString();
    }
    
    static interface OnMakeBitmapFinishListener {
        public void onFinish(Bitmap bitmap);
    }
}
