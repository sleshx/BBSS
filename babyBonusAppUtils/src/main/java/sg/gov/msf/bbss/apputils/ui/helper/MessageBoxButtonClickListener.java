package sg.gov.msf.bbss.apputils.ui.helper;

import android.content.DialogInterface;

/**
 * Created by bandaray on 30/1/2015.
 */
public interface MessageBoxButtonClickListener {
    public void onClickPositiveButton(DialogInterface dialog,int id);
    public void onClickNegativeButton(DialogInterface dialog,int id);
}
