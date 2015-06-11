package sg.gov.msf.bbss.apputils.connect;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

/**
 * Created by bandaray on 2/3/2015.
 */
public class ConnectionHelper {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            // No active network
            return false;
        } else
            return true;
    }

    public static boolean isInternetAvailable(String appUrl) {
        try {
            InetAddress ipAddr = InetAddress.getByName(appUrl);

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
}
