package mobi.hubtech.goacg.utils;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BitmapUtils {

    private static final int GAP = 1;
    
    public static Bitmap DrawFourBlock(List<Bitmap> bitmapList, Rect howBig) {
        int width = howBig.right - howBig.left;
        int height = howBig.right - howBig.left;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int count = bitmapList.size() < 4 ? bitmapList.size() : 4;
        switch (count) {
        case 1:
            DrawForOne(canvas, bitmapList, howBig);
            break;
        case 2:
            DrawForTwo(canvas, bitmapList, howBig);
            break;
        case 3:
            DrawForThree(canvas, bitmapList, howBig);
            break;
        case 4:
            DrawForFour(canvas, bitmapList, howBig);
            break;
        default:
            break;
        }
        return bitmap;
    }
    
    private static void DrawForOne(Canvas canvas, List<Bitmap> bitmapList, Rect howBig) {
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        for (Bitmap bitmap: bitmapList) {
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, howBig, paint);
        }
    }
    
    private static void DrawForTwo(Canvas canvas, List<Bitmap> bitmapList, Rect howBig) {
        int x = howBig.left;
        int y = howBig.top;
        int width = howBig.right - howBig.left;
        int height = howBig.bottom - howBig.top;
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        
        Bitmap bitmap1 = bitmapList.get(0);
        Rect src1 = new Rect(bitmap1.getWidth() / 4 * 1, 0, bitmap1.getWidth() / 4 * 3, bitmap1.getHeight());
        Rect dst1 = new Rect(
                x, 
                y, 
                x + width / 2 - 1, 
                y + height);
        canvas.drawBitmap(bitmap1, src1, dst1, paint);
        
        Bitmap bitmap2 = bitmapList.get(1);
        Rect src2 = new Rect(bitmap2.getWidth() / 4 * 1, 0, bitmap2.getWidth() / 4 * 3, bitmap2.getHeight());
        Rect dst2 = new Rect(
                x + width / 2 + 1, 
                y, 
                x + width / 2 + 1 + width / 2, 
                y + height);
        canvas.drawBitmap(bitmap2, src2, dst2, paint);
    }
    
    private static void DrawForThree(Canvas canvas, List<Bitmap> bitmapList, Rect howBig) {
        int x = howBig.left;
        int y = howBig.top;
        int width = howBig.right - howBig.left;
        int height = howBig.bottom - howBig.top;
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);

        Bitmap bitmap1 = bitmapList.get(0);
        Rect src1 = new Rect(bitmap1.getWidth() / 4 * 1, 0, bitmap1.getWidth() / 4 * 3, bitmap1.getHeight());
        Rect dst1 = new Rect(
                x, 
                y, 
                x + width / 2 - GAP, 
                y + height);
        canvas.drawBitmap(bitmap1, src1, dst1, paint);
        
        Bitmap bitmap2 = bitmapList.get(1);
        Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        Rect dst2 = new Rect(
                x + width / 2 + GAP, 
                y, 
                x + width / 2 + GAP + width / 2, 
                y + height / 2 - GAP);
        canvas.drawBitmap(bitmap2, src2, dst2, paint);
        
        Bitmap bitmap3 = bitmapList.get(2);
        Rect src3 = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());
        Rect dst3 = new Rect(
                x + width / 2 + GAP, 
                y + height / 2 + GAP, 
                x + width / 2 + GAP + width / 2, 
                y + height / 2 + GAP + height / 2);
        canvas.drawBitmap(bitmap3, src3, dst3, paint);
    }
    
    private static void DrawForFour(Canvas canvas, List<Bitmap> bitmapList, Rect howBig) {
        int x = howBig.left;
        int y = howBig.top;
        int width = howBig.right - howBig.left;
        int height = howBig.bottom - howBig.top;
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        
        Bitmap bitmap1 = bitmapList.get(0);
        Rect src1 = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());
        Rect dst1 = new Rect(
                x, 
                y, 
                x + width / 2 - GAP, 
                y + height / 2 - GAP);
        canvas.drawBitmap(bitmap1, src1, dst1, paint);
        
        Bitmap bitmap2 = bitmapList.get(1);
        Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        Rect dst2 = new Rect(
                x, 
                y + height / 2 + GAP, 
                x + width / 2 - GAP, 
                y + height / 2 + GAP + height / 2);
        canvas.drawBitmap(bitmap2, src2, dst2, paint);

        Bitmap bitmap3 = bitmapList.get(2);
        Rect src3 = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());
        Rect dst3 = new Rect(
                x + width / 2 + GAP, 
                y, 
                x + width / 2 + GAP + width / 2, 
                y + height / 2 - GAP);
        canvas.drawBitmap(bitmap3, src3, dst3, paint);

        Bitmap bitmap4 = bitmapList.get(3);
        Rect src4 = new Rect(0, 0, bitmap4.getWidth(), bitmap4.getHeight());
        Rect dst4 = new Rect(
                x + width / 2 + GAP, 
                y + height / 2 + GAP, 
                x + width / 2 + GAP + width / 2, 
                y + height / 2 + GAP + height / 2);
        canvas.drawBitmap(bitmap4, src4, dst4, paint);
    }
}
