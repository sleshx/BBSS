package sg.gov.msf.bbss.apputils.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import sg.gov.msf.bbss.apputils.ui.gesture.SimpleGestureDetector;
import sg.gov.msf.bbss.apputils.ui.gesture.SimpleGestureListener;

/**
 * Created by bandaray on 19/1/2015.
 */
public class SwipeableActivity extends Activity implements SimpleGestureListener {

    private SimpleGestureDetector detector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Detect touched area
        detector = new SimpleGestureDetector(this,this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureDetector.SWIPE_RIGHT : str = "Swipe Right";
                break;
            case SimpleGestureDetector.SWIPE_LEFT :  str = "Swipe Left";
                break;
            case SimpleGestureDetector.SWIPE_DOWN :  str = "Swipe Down";
                break;
            case SimpleGestureDetector.SWIPE_UP :    str = "Swipe Up";
                break;

        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }
}
