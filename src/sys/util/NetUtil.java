package sys.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	/**
	 * �ж��Ƿ�����������
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
	 * �ж��Ƿ�Ϊwifi����
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
