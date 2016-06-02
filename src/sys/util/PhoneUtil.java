package sys.util;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;

public class PhoneUtil {

	static final String PhoneNumber = "18818217702";//业主号码
	static final String MessagCenterPhoneNumber = "+8613800210500";//短信中心号码
	/**
	 * 发送短信
	 * @param context
	 * @param phoneNumber
	 * @param content
	 */
	public static void sendSms(Context context, String phoneNumber,
            String content) {
        Uri uri = Uri.parse("smsto:"
                + (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", TextUtils.isEmpty(content) ? "" : content);
        context.startActivity(intent);
		}
	/**
	 * 后台发短信
	 * @param message
	 * @param Number
	 */
	public static void sendSmsBackground(String message,String Number)
	{
		
		
		
		// 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供 的短信工具。
		if (message != null) 
		{
			SmsManager sms = SmsManager.getDefault();
			// 如果短信没有超过限制长度，则返回一个长度的List。
			List<String> texts = sms.divideMessage(message);
			for (String text : texts) 
			{
				sms.sendTextMessage( Number,MessagCenterPhoneNumber,message,  null, null);
				//sms.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
			}
		}
	}
	/**
	 * 拨打电话
	 * @param context
	 * @param phoneNumber
	 */
	public static void call(Context context, String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
    }
	/**
	 * 判断当前是否是手机
	 * @param context
	 * @return
	 */
	public static boolean isPhone(Context context) {
        TelephonyManager telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        } else {
            return true;
        }
    }
	/**
	 * 获取当前设备的MAC地址
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
        String macAddress;
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        macAddress = info.getMacAddress();
        if (null == macAddress) {
            return "";
        }
        macAddress = macAddress.replace(":", "");
        return macAddress;
    }
	/**
	 * 判断当前程序的版本号
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
        String version = "0";
        try {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
	
	public static void msgCloseDoorEShark(Context context)
	{
		new AlertDialog.Builder(context).setTitle("E-Shark提醒您")//设置对话框标题
		 .setMessage("正在向安防系统发送关门短信，将产生正常信息费用。")//设置显示的内容
		 .setPositiveButton("关门",new DialogInterface.OnClickListener() {//添加确定按钮
			 @Override
			 public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

				 // TODO Auto-generated method stub
				 PhoneUtil.sendSmsBackground("关门", PhoneNumber);

			 }

		 }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮

			 @Override

			 public void onClick(DialogInterface dialog, int which) {//响应事件

				 // TODO Auto-generated method stub
			 }

		 }).show();//在按键响应事件中显示此对话框
		
	}
	public static void msgOpenDoorEShark(Context context)
	{
		new AlertDialog.Builder(context).setTitle("E-Shark提醒您")//设置对话框标题
		 .setMessage("正在向安防系统发送开门短信，将产生正常信息费用。")//设置显示的内容
		 .setPositiveButton("开门",new DialogInterface.OnClickListener() {//添加确定按钮
			 @Override
			 public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

				 // TODO Auto-generated method stub
				 PhoneUtil.sendSmsBackground("开门", PhoneNumber);

			 }

		 }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮

			 @Override

			 public void onClick(DialogInterface dialog, int which) {//响应事件

				 // TODO Auto-generated method stub
			 }

		 }).show();//在按键响应事件中显示此对话框
		
	}
}