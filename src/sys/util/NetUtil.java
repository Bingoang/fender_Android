package sys.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	/**
	 * 判断是否有网络连接
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }
	/**
	 * 判断是否为wifi连接
	 * @param context
	 * @return
	 */
	 public static boolean isWifiConnected(Context context) {
	        ConnectivityManager connectivityManager = (ConnectivityManager) context
	                .getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo wifiNetworkInfo = connectivityManager
	                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        if (wifiNetworkInfo.isConnected()) {
	            return true;
	        }
	        return false;
	    }
}
