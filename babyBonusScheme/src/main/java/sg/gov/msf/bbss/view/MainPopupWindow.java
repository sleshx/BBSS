package sg.gov.msf.bbss.view;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import sg.gov.msf.bbss.R;

/**
 * Created by chuanhe
 */
public class MainPopupWindow {

    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private Context context;
    private TextView textView;

    public MainPopupWindow(Context context){
        this.context = context;
    }

    public PopupWindow getPopupWindow() {
        layoutInflater = LayoutInflater.from(context);

        View popupWindow = layoutInflater.inflate(R.layout.sibling_check_layout, null);
        Button check_ok = (Button)popupWindow.findViewById(R.id.check_ok_Btn);

        textView = (TextView)popupWindow.findViewById(R.id.pop_tv);
        check_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainPopupWindow.this.popupWindow.dismiss();
            }
        });

        this.popupWindow = new PopupWindow(popupWindow, ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        this.popupWindow.setFocusable(false);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setBackgroundDrawable(new BitmapDrawable());
        this.popupWindow.setOutsideTouchable(true);

        return this.popupWindow;
    }

    public void setTextView(String text) {
        this.textView.setText(text);
    }
}
