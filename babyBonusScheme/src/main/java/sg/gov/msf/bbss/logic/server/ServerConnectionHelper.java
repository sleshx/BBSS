package sg.gov.msf.bbss.logic.server;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;

/**
 * Created by bandaray
 */
public class ServerConnectionHelper {

    public static void createDeviceOfflineMessageBox(Context context,
                                                     MessageBoxButtonClickListener listener) {

        MessageBox.show(context,
                StringHelper.getStringByResourceId(context, R.string.alert_device_offline),
                false, true, R.string.btn_ok, false, 0, listener);
    }

    public static void createConnectionIssueMessageBox(Context context,
                                                       MessageBoxButtonClickListener listener) {

        MessageBox.show(context,
                StringHelper.getStringByResourceId(context, R.string.alert_server_connection_issue),
                false, true, R.string.btn_ok, false, 0,  listener);
    }
}
