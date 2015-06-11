package sg.gov.msf.bbss.apputils.ui.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import sg.gov.msf.bbss.apputils.R;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.ui.helper.MessagePopupButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;

/**
 * Created by bandaray on 30/1/2015.
 */
public class MessageBox {

    public static AlertDialog show(Context context, String message, boolean isCancelable,
                                   boolean isPositiveButtonRequired, int positiveButtonTextId,
                                   boolean isNegativeButtonRequired, int negativeButtonTextId,
                                   MessageBoxButtonClickListener onClickListener) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        final MessageBoxButtonClickListener listener = onClickListener;

        alertDialogBuilder
                .setTitle("Alert!")
                .setMessage(message)
                .setCancelable(isCancelable);

        if (isPositiveButtonRequired) {
            alertDialogBuilder.setPositiveButton(StringHelper.getStringByResourceId(context,
                    positiveButtonTextId), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    if(listener  != null){
                        listener.onClickPositiveButton(dialog, id);
                    } else {
                        dialog.dismiss();
                    }
                }
            });
        }

        if (isNegativeButtonRequired) {
            alertDialogBuilder.setNegativeButton(StringHelper.getStringByResourceId(context,
                    negativeButtonTextId), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    if(listener  != null) {
                        listener.onClickNegativeButton(dialog, id);
                    } else {
                        dialog.dismiss();
                    }
                }
            });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return  alertDialog;
    }

    public static void popup(Context context, String message, View rootView,
                             boolean isPositiveButtonRequired, int positiveButtonTextId,
                             boolean isNegativeButtonRequired, int negativeButtonTextId,
                             MessagePopupButtonClickListener onClickListener) {

        final MessagePopupButtonClickListener listener = onClickListener;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        final PopupWindow popupWindow;
        View popupView;

        if (isPositiveButtonRequired && isNegativeButtonRequired) {
            popupView = layoutInflater.inflate(R.layout.layout_pop_message_two_button, null);
        } else {
            popupView = layoutInflater.inflate(R.layout.layout_pop_message_one_button, null);
        }

        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        TextView tvMessage = (TextView) popupView.findViewById(R.id.tvPopup);
        tvMessage.setText(message);

        if (isPositiveButtonRequired) {
            Button btnPositive = (Button) popupView.findViewById(R.id.btnPopup);
            btnPositive.setText(positiveButtonTextId);
            btnPositive.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener  != null) {
                        listener.onClickPositiveButton(popupWindow);
                    } else {
                        popupWindow.dismiss();
                    }
                }
            });
        }

        if (isNegativeButtonRequired) {
            Button btnNegative = (Button) popupView.findViewById(R.id.btnPopup2);
            btnNegative.setText(negativeButtonTextId);
            btnNegative.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener  != null) {
                        listener.onClickNegativeButton(popupWindow);
                    } else {
                        popupWindow.dismiss();
                    }
                }
            });
        }

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

}
