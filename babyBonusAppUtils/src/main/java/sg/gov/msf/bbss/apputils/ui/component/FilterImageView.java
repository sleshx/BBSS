package sg.gov.msf.bbss.apputils.ui.component;

/**
 * Created by chuanhe on 6/3/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class FilterImageView extends ImageView implements GestureDetector.OnGestureListener{

    private GestureDetector mGestureDetector;
    private Bitmap bitmap;
    public FilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector=new GestureDetector(context, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getActionMasked()== MotionEvent.ACTION_CANCEL){
            removeFilter();
        }
        return mGestureDetector.onTouchEvent(event);
    }

    private void setFilter() {
        Drawable drawable=getDrawable();

        if (drawable==null) {
            drawable=getBackground();
        }
        if(drawable!=null){

            bitmap = drawableToBitmap(drawable);
            setImageBitmap(grey(drawableToBitmap(drawable)));
            //drawable.setColorFilter(Color.GRAY,PorterDuff.Mode.MULTIPLY);
        }
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() !=
                PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap grey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();

        canvas.drawBitmap(bitmap, 0, 0, paint);
        Paint paintC = new Paint();
        paintC.setAlpha(50);
        canvas.drawCircle(width/2,height/2,width/2,paintC);
        return faceIconGreyBitmap;
    }

    public Bitmap noGrey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();

        canvas.drawBitmap(bitmap, 0, 0, paint);

        return faceIconGreyBitmap;
    }

    private void removeFilter() {
            setImageBitmap(noGrey(bitmap));
    }

    @Override
    public boolean onDown(MotionEvent e) {
        setFilter();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        removeFilter();
        performClick();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        removeFilter();
        performLongClick();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }
}
