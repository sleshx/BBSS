package sg.gov.msf.bbss.apputils.ui.helper;

import android.content.DialogInterface;
import android.widget.PopupWindow;

/**
 * Created by bandaray on 11/6/2015.
 */
public interface MessagePopupButtonClickListener {
    public void onClickPositiveButton(PopupWindow popupWindow);
    public void onClickNegativeButton(PopupWindow popupWindow);
}
