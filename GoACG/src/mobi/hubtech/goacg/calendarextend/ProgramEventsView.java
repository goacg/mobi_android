package mobi.hubtech.goacg.calendarextend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import mobi.hubtech.calendarview.MonthWeekEventsView;
import mobi.hubtech.goacg.R;
import mobi.hubtech.goacg.bean.Play;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ProgramEventsView extends MonthWeekEventsView {
    
    protected static final int PADDING = 4;
    protected static final int GAP = 1;
    
    private int mFontSize;
    private int mBellSize;
    
    private Drawable mSubMark;
    private List<List<Play>> mWeekProgramData;
    private List<Map<String, Bitmap>> mIcons;
    private Paint mPaint;
    
    public ProgramEventsView(Context context) {
        super(context);
        mSubMark = getResources().getDrawable(R.drawable.bell_enabled);
        mFontSize = getResources().getDimensionPixelSize(R.dimen.calendar_font_size);
        mBellSize = getResources().getDimensionPixelSize(R.dimen.bell_size);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawWeekNums(canvas);
        drawDaySeparators(canvas);
        if (mHasToday && mAnimateToday) {
            drawToday(canvas);
        }
        if (mShowDetailsInMonth) {
            drawEvents(canvas);
        } else {
            if (mDna == null && mUnsortedEvents != null) {
                createDna(mUnsortedEvents);
            }
            drawDNA(canvas);
        }
        drawGoACGThings(canvas);
        drawClick(canvas);
    }

    @Override
    protected void drawWeekNums(Canvas canvas) {
        int y;

        int i = 0;
        int todayIndex = mTodayIndex;
        int x = 0;
        int numCount = mNumDays;
        if (mShowWeekNum) {
            x = SIDE_PADDING_WEEK_NUMBER + mPadding;
            y = mWeekNumAscentHeight + TOP_PADDING_WEEK_NUMBER;
            canvas.drawText(mDayNumbers[0], x, y, mWeekNumPaint);
            numCount++;
            i++;
            todayIndex++;
        }

//        y = mMonthNumAscentHeight + TOP_PADDING_MONTH_NUMBER;
        y = (int) mMonthNumPaint.getTextSize();
        mMonthNumPaint.setTextAlign(Align.CENTER);

        boolean isFocusMonth = mFocusDay[i];
        boolean isBold = false;
        mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor : mMonthNumOtherColor);
        for (; i < numCount; i++) {
            if (mHasToday && todayIndex == i) {
                mMonthNumPaint.setColor(mMonthNumTodayColor);
                mMonthNumPaint.setFakeBoldText(isBold = true);
                if (i + 1 < numCount) {
                    // Make sure the color will be set back on the next
                    // iteration
                    isFocusMonth = !mFocusDay[i + 1];
                }
            } else if (mFocusDay[i] != isFocusMonth) {
                isFocusMonth = mFocusDay[i];
                mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor : mMonthNumOtherColor);
            }
//            x = computeDayLeftPosition(i - offset) - (SIDE_PADDING_MONTH_NUMBER);
            x = computeDayLeftPosition(i) + (computeDayLeftPosition(i + 1) - computeDayLeftPosition(i)) / 2;
            canvas.drawText(mDayNumbers[i], x, y, mMonthNumPaint);
            if (isBold) {
                mMonthNumPaint.setFakeBoldText(isBold = false);
            }
        }
    }

    private void drawGoACGThings(Canvas canvas) {
        if (mWeekProgramData == null) {
            return;
        }
        drawBell(canvas);
        drawIcon(canvas);
        drawProgramCount(canvas);
/*
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        int y = mMonthNumAscentHeight + TOP_PADDING_MONTH_NUMBER;
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        for (int i = 0; i < 7; i++) {
            int x = computeDayLeftPosition(i);
//            Rect dest = new Rect(x, y, (int) (x + bitmap.getWidth() / 2.0f), (int) (y + bitmap.getHeight()/ 2.0f));
            Paint paint = new Paint();
            Rect dest = new Rect(x, y, x + (SPACING_WEEK_NUMBER + mPadding / 2), y + 32);
            canvas.drawBitmap(bitmap, src, dest, p);
            paint.setColor(0xAAFFAAAA);
            canvas.drawRect(dest, paint);
            dest = new Rect(x + (SPACING_WEEK_NUMBER + mPadding / 2), y, x + (SPACING_WEEK_NUMBER + mPadding / 2) * 2, y + 32);
            canvas.drawBitmap(bitmap, src, dest, p);
            paint.setColor(0xAAAAFFAA);
            canvas.drawRect(dest, paint);
        }
*/
    }
    
    private void drawIcon(Canvas canvas) {
        for (int i = 0; i < 7; i++) {
            Map<String, Bitmap> map = mIcons.get(i);
            int size = map.entrySet().size();
            switch (size) {
            case 1:
                drawIconForOne(canvas, map, i);
                break;
            case 2:
                drawIconForTwo(canvas, map, i);
                break;
            case 3:
                drawIconForThree(canvas, map, i);
                break;
            case 4:
                drawIconForFour(canvas, map, i);
                break;
            default:
                if (size > 4) {
                    drawIconForFour(canvas, map, i);
                }
                break;
            }
        }
    }
    
    private void drawIconForOne(Canvas canvas, Map<String, Bitmap> map, int index) {
        int x = computeDayLeftPosition(index);
        int width = computeDayLeftPosition(index + 1) - x;
        int height = width;
        int y = (int) mMonthNumPaint.getTextSize();
        for (Entry<String, Bitmap> set: map.entrySet()) {
            Bitmap bitmap = set.getValue();
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect dst = new Rect(
                    x + PADDING, 
                    y + PADDING, 
                    x + width - PADDING, 
                    y + height - PADDING);
            canvas.drawBitmap(bitmap, src, dst, mPaint);
        }
    }
    
    private void drawIconForTwo(Canvas canvas, Map<String, Bitmap> map, int index) {
        int x = computeDayLeftPosition(index);
        int width = computeDayLeftPosition(index + 1) - x;
        int height = width;
        int y = (int) mMonthNumPaint.getTextSize();
        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>(2);
        for (Entry<String, Bitmap> set: map.entrySet()) {
            bitmapList.add(set.getValue());
        }
        
        Bitmap bitmap1 = bitmapList.get(0);
        Rect src1 = new Rect(bitmap1.getWidth() / 4 * 1, 0, bitmap1.getWidth() / 4 * 3, bitmap1.getHeight());
        Rect dst1 = new Rect(
                x + PADDING, 
                y + PADDING, 
                x + width / 2 - GAP, 
                y + height - PADDING);
        canvas.drawBitmap(bitmap1, src1, dst1, mPaint);
        
        Bitmap bitmap2 = bitmapList.get(1);
        Rect src2 = new Rect(bitmap2.getWidth() / 4 * 1, 0, bitmap2.getWidth() / 4 * 3, bitmap2.getHeight());
        Rect dst2 = new Rect(
                x + width / 2 + GAP, 
                y + PADDING, 
                x + width / 2 + GAP + width / 2 - PADDING, 
                y + height - PADDING);
        canvas.drawBitmap(bitmap2, src2, dst2, mPaint);
    }

    private void drawIconForThree(Canvas canvas, Map<String, Bitmap> map, int index) {
        int x = computeDayLeftPosition(index);
        int width = computeDayLeftPosition(index + 1) - x;
        int height = width;
        int y = (int) mMonthNumPaint.getTextSize();
        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>(3);
        for (Entry<String, Bitmap> set: map.entrySet()) {
            bitmapList.add(set.getValue());
        }
        
        Bitmap bitmap1 = bitmapList.get(0);
        Rect src1 = new Rect(bitmap1.getWidth() / 4 * 1, 0, bitmap1.getWidth() / 4 * 3, bitmap1.getHeight());
        Rect dst1 = new Rect(
                x + PADDING, 
                y + PADDING, 
                x + width / 2 - GAP, 
                y + height - PADDING);
        canvas.drawBitmap(bitmap1, src1, dst1, mPaint);
        
        Bitmap bitmap2 = bitmapList.get(1);
        Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        Rect dst2 = new Rect(
                x + width / 2 + GAP, 
                y + PADDING, 
                x + width / 2 + GAP + width / 2 - PADDING, 
                y + height / 2 - GAP);
        canvas.drawBitmap(bitmap2, src2, dst2, mPaint);

        Bitmap bitmap3 = bitmapList.get(2);
        Rect src3 = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());
        Rect dst3 = new Rect(
                x + width / 2 + GAP, 
                y + height / 2 + GAP, 
                x + width / 2 + GAP + width / 2 - PADDING, 
                y + height / 2 + GAP + height / 2 - PADDING);
        canvas.drawBitmap(bitmap3, src3, dst3, mPaint);
    }

    private void drawIconForFour(Canvas canvas, Map<String, Bitmap> map, int index) {
        int x = computeDayLeftPosition(index);
        int width = computeDayLeftPosition(index + 1) - x;
        int height = width;
        int y = (int) mMonthNumPaint.getTextSize();
        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>(4);
        for (Entry<String, Bitmap> set: map.entrySet()) {
            bitmapList.add(set.getValue());
        }
        
        Bitmap bitmap1 = bitmapList.get(0);
        Rect src1 = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());
        Rect dst1 = new Rect(
                x + PADDING, 
                y + PADDING, 
                x + width / 2 - GAP, 
                y + height / 2 - GAP);
        canvas.drawBitmap(bitmap1, src1, dst1, mPaint);
        
        Bitmap bitmap2 = bitmapList.get(1);
        Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        Rect dst2 = new Rect(
                x + PADDING, 
                y + height / 2 + GAP, 
                x + width / 2 - GAP, 
                y + height / 2 + GAP + height / 2 - PADDING);
        canvas.drawBitmap(bitmap2, src2, dst2, mPaint);

        Bitmap bitmap3 = bitmapList.get(2);
        Rect src3 = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());
        Rect dst3 = new Rect(
                x + width / 2 + GAP, 
                y + PADDING, 
                x + width / 2 + GAP + width / 2 - PADDING, 
                y + height / 2 - GAP);
        canvas.drawBitmap(bitmap3, src3, dst3, mPaint);

        Bitmap bitmap4 = bitmapList.get(3);
        Rect src4 = new Rect(0, 0, bitmap4.getWidth(), bitmap4.getHeight());
        Rect dst4 = new Rect(
                x + width / 2 + GAP, 
                y + height / 2 + GAP, 
                x + width / 2 + GAP + width / 2 - PADDING, 
                y + height / 2 + GAP + height / 2 - PADDING);
        canvas.drawBitmap(bitmap4, src4, dst4, mPaint);
    }
    
    private void drawProgramCount(Canvas canvas) {
        for (int i = 0; i < 7; i++) {
            List<Play> playList = mWeekProgramData.get(i);
            if (playList == null) {
                continue;
            }
            
            int subCount = getSubCount(i);
            int num = 0;
            
            TextPaint paint = new TextPaint();
            paint.setTextSize(mFontSize);
            if (subCount != 0) {
                num = subCount;
                paint.setColor(Color.rgb(221, 75, 57));
            } else {
                num = playList.size();
                paint.setColor(Color.BLACK);
            }
            // 取中点x坐标
            int x = computeDayLeftPosition(i + 1) - PADDING;
            paint.setTextAlign(Align.RIGHT);
            canvas.drawText(num + "番", x, mHeight - SIDE_PADDING_MONTH_NUMBER, paint);
        }
    }
    
    private void drawBell(Canvas canvas) {
        for (int i = 0; i < 7; i++) {
            int numSub = getSubCount(i);
            if (numSub != 0) {
                int x = computeDayLeftPosition(i);
                mSubMark.setBounds(
                        x + PADDING + GAP, 
                        mHeight - 3 - mBellSize, 
                        x + PADDING + GAP + mBellSize, 
                        mHeight - 3 - mBellSize + mBellSize);
                mSubMark.draw(canvas);
            }
        }
    }
    
    public int getSubCount(int index) {
        List<Play> playList = mWeekProgramData.get(index);
        if (playList == null) {
            return 0;
        }
        int subCount = 0;
        for (Play play: playList) {
            if (play.getAlbum().isSub()) {
                subCount++;
            }
        }
        return subCount;
    }
    
    public void getIcons() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        for (int i = 0; i < mWeekProgramData.size(); i++) {
            List<Play> playList = mWeekProgramData.get(i);
            if (playList == null) {
                continue;
            }
            final Map<String, Bitmap> iconMap = mIcons.get(i);
            for (Play play: playList) {
                if (play.getAlbum() == null) {
                    play.setAlbum(null);
                }
                imageLoader.loadImage(play.getAlbum().getIcon_32x32(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        iconMap.put(imageUri, loadedImage);
                        invalidate();
                    }
                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
            }
        }
    }
    
    public List<List<Play>> getData() {
        return mWeekProgramData;
    }

    public void setData(List<List<Play>> data) {
        this.mWeekProgramData = data;
        mIcons = new ArrayList<Map<String, Bitmap>>(data.size());
        for (int i = 0; i < data.size(); i++) {
            mIcons.add(new TreeMap<String, Bitmap>());
        }
        getIcons();
    }
}
