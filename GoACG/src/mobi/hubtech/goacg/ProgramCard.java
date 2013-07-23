package mobi.hubtech.goacg;

import java.sql.SQLException;

import mobi.hubtech.goacg.bean.Album;
import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.bean.UserInfo;
import mobi.hubtech.goacg.global.UserInfoUtils;
import mobi.hubtech.goacg.request.RequestTask;
import mobi.hubtech.goacg.request.bean.SubAlbumRequest;
import mobi.hubtech.goacg.request.bean.SubAlbumResponse;
import mobi.hubtech.goacg.request.bean.UnSubAlbumRequest;
import mobi.hubtech.goacg.request.bean.UnSubAlbumResponse;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 显示每一个番目的Card
 */
public class ProgramCard extends Card {
    
    /** 番目信息 */
    private Play mPlay;

    /** 番目封面 */
    private ImageView mImgCover;
    /** 番目名字 */
    private TextView mTxtName;
    /** 提醒用的铃铛 */
    private ImageButton mBtnNotify;
    /** 观看按钮 */
    private Button mBtnWatch;
    
    /** 访问番组数据的dao */
    private Dao<Album, Long> mAlbumDao;
    private Dao<Play, Long> mPlayDao;
    
    @Override
    public View getCardContent(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.program_item, null);
        mImgCover = (ImageView) view.findViewById(R.id.img_cover);
        mTxtName = (TextView) view.findViewById(R.id.txt_name);
        mBtnNotify = (ImageButton) view.findViewById(R.id.btn_notify);
        mBtnWatch = (Button) view.findViewById(R.id.btn_watch);
        
        mTxtName.setText(mPlay.getAlbum().getTitle());
        
        // 如果番目没有图，就用番组的图
        if (mPlay.getBigcover() != null && !"".equals(mPlay.getBigcover())) {
            ImageLoader.getInstance().displayImage(mPlay.getBigcover(), mImgCover);
        } else {
            ImageLoader.getInstance().displayImage(mPlay.getAlbum().getBigcover(), mImgCover);
        }
        
        setBtnNotifyState(mPlay.getAlbum().isSub());
        
        if (mPlay.getShow_url() == null || "".equals(mPlay.getShow_url())) {
            mBtnWatch.setVisibility(View.INVISIBLE);
        }
        
        mBtnNotify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo info = UserInfoUtils.getUserInfo(v.getContext());
                if (info.getUserID() == -1) {
                    return;
                }
                if (mPlay.getAlbum().isSub()) {
                    setBtnNotifyState(false);
                    mPlay.getAlbum().setSub(false);
                    try {
                        mAlbumDao.update(mPlay.getAlbum());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    UnSubAlbumRequest unSubReq = new UnSubAlbumRequest();
                    unSubReq.setUser_id(info.getUserID());
                    unSubReq.setAlbum_id(mPlay.getAlbum().getId());
                    new RequestTask<UnSubAlbumResponse>(UnSubAlbumResponse.class) {
                        @Override
                        protected void onPostExecute(UnSubAlbumResponse result) {
                            if (result == null) {
                                return;
                            }
                            if (result.getError_code() == 0) {
                            } else {
                            }
                        }
                    }.execute(unSubReq);
                } else {
                    setBtnNotifyState(true);
                    mPlay.getAlbum().setSub(true);
                    try {
                        mAlbumDao.update(mPlay.getAlbum());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                    SubAlbumRequest subReq = new SubAlbumRequest();
                    subReq.setUser_id(info.getUserID());
                    subReq.setAlbum_id(mPlay.getAlbum().getId());
                    new RequestTask<SubAlbumResponse>(SubAlbumResponse.class) {
                        @Override
                        protected void onPostExecute(SubAlbumResponse result) {
                            if (result == null) {
                                return;
                            }
                            if (result.getError_code() == 0) {
                            } else {
                            }
                        }
                    }.execute(subReq);
                }
            }
        });
        mBtnWatch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mPlay.getShow_url();
                if (url == null || "".equals(url)) {
                    return;
                }
                Intent intent = new Intent(mTxtName.getContext(), PlayActivity.class);
                intent.putExtra(PlayActivity.EXTRA_URL, url);
                mTxtName.getContext().startActivity(intent);
            }
        });
        
        return view;
    }
    
    /**
     * 番目信息由外部设置
     * @param play 此Card的番目信息
     */
    public void setPlay(Play play) {
        mPlay = play;
    }

    /**
     * dao由外部设置
     * @param albumDao 番组dao
     * @param playDao 番目dao
     */
    public void setDao(Dao<Album, Long> albumDao, Dao<Play, Long> playDao) {
        mAlbumDao = albumDao;
        mPlayDao = playDao;
    }
    
    private void setBtnNotifyState(boolean isSubed) {
        if (isSubed) {
            mBtnNotify.setImageResource(R.drawable.bell_enabled);
        } else {
            mBtnNotify.setImageResource(R.drawable.bell_disabled);
        }
    }
    
    @Override
    protected int getCardLayout() {
        return R.layout.program_card;
    }
    
    @Override
    protected int getFirstCardLayout() {
        return R.layout.program_card;
    }
    
    @Override
    protected int getLastCardLayout() {
        return R.layout.program_card;
    }
}
