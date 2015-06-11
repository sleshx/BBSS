package sg.gov.msf.bbss.apputils.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.CheckBox;
public class CustomRadioButton extends CheckBox {

	public CustomRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomRadioButton(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyShortcut(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		return false;
	}
}
