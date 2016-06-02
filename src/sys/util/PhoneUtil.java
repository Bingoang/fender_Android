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

	static final String PhoneNumber = "18818217702";//ҵ������
	static final String MessagCenterPhoneNumber = "+8613800210500";//�������ĺ���
	/**
	 * ���Ͷ���
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
	 * ��̨������
	 * @param message
	 * @param Number
	 */
	public static void sendSmsBackground(String message,String Number)
	{
		
		
		
		// �ƶ���Ӫ������ÿ�η��͵��ֽ��������ޣ����ǿ���ʹ��Android�������ṩ �Ķ��Ź��ߡ�
		if (message != null) 
		{
			SmsManager sms = SmsManager.getDefault();
			// �������û�г������Ƴ��ȣ��򷵻�һ�����ȵ�List��
			List<String> texts = sms.divideMessage(message);
			for (String text : texts) 
			{
				sms.sendTextMessage( Number,MessagCenterPhoneNumber,message,  null, null);
				//sms.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
			}
		}
	}
	/**
	 * ����绰
	 * @param context
	 * @param phoneNumber
	 */
	public static void call(Context context, String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
    }
	/**
	 * �жϵ�ǰ�Ƿ����ֻ�
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
	 * ��ȡ��ǰ�豸��MAC��ַ
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
	 * �жϵ�ǰ����İ汾��
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
		new AlertDialog.Builder(context).setTitle("E-Shark������")//���öԻ������
		 .setMessage("�����򰲷�ϵͳ���͹��Ŷ��ţ�������������Ϣ���á�")//������ʾ������
		 .setPositiveButton("����",new DialogInterface.OnClickListener() {//���ȷ����ť
			 @Override
			 public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�

				 // TODO Auto-generated method stub
				 PhoneUtil.sendSmsBackground("����", PhoneNumber);

			 }

		 }).setNegativeButton("����",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť

			 @Override

			 public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�

				 // TODO Auto-generated method stub
			 }

		 }).show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���
		
	}
	public static void msgOpenDoorEShark(Context context)
	{
		new AlertDialog.Builder(context).setTitle("E-Shark������")//���öԻ������
		 .setMessage("�����򰲷�ϵͳ���Ϳ��Ŷ��ţ�������������Ϣ���á�")//������ʾ������
		 .setPositiveButton("����",new DialogInterface.OnClickListener() {//���ȷ����ť
			 @Override
			 public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�

				 // TODO Auto-generated method stub
				 PhoneUtil.sendSmsBackground("����", PhoneNumber);

			 }

		 }).setNegativeButton("����",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť

			 @Override

			 public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�

				 // TODO Auto-generated method stub
			 }

		 }).show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���
		
	}
}