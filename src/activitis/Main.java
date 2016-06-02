package activitis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import listener.OnTouchListenerUpDownEffect;

import com.example.fender.R;

import communicate.bluetooth.Bluetooth;
import communicate.bluetooth.Bluetooth.IBluetooth;
import communicate.net.MyServiceView;
import communicate.net.NetThread;
import sys.util.PhoneUtil;
import treeview.adapter.SimpleTreeListViewAdapter;
import treeview.bean.FileBean;
import treeview.bean.OrgBean;
import treeview.utils.Node;
import treeview.utils.adapter.TreeListViewAdapter.OnTreeNodeClickListener;
import activitis.adapater.MyPagerAdapterTitle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import animation.ZSRadioButton;

public class Main extends Activity implements OnClickListener, IBluetooth {

	// *******************TreeView************************/
	public ListView mTree;
	public SimpleTreeListViewAdapter<OrgBean> mAdapter;
	private List<FileBean> mDatas;
	public List<OrgBean> mDatas2;
	public int idOfTreeList = 1;
	// *************wifi/bluetooth��־λ 0:δ�����κ�״̬ 1������״̬ 2��wifi״̬ 3 : ������������ 4
	// ����״̬
	private static byte WIFIOPENDOOR = 0x01;// ���͸���λ���� ����
	private static byte WIFICLOSEDOOR = 0x02;// ����
	private static byte WIFIOPENALARM = 0x03;// ������
	private static byte WIFICLOSEALARM = 0x04;// �ؾ���
	private int flagForWifiBluetooth = 0;
	private boolean flagCarmeraIsOpened = false;// ���Զ���Ƶ�Ƿ��Ѿ��� ��Ϊtrue
	// *********wifi***********//
	private WifiManager wifiManager = null;
	private NetThread netThread = null;// �������ӷ�����
	// **********************//
	private long mExitTime; // �˳�ʱ��
	// ��ť����
	private ZSRadioButton zs_RadioButton = new ZSRadioButton();
	private int[] res = { R.id.a_imageView, R.id.b_imageView, R.id.c_imageView,
			R.id.d_imageView, R.id.e_imageView, R.id.f_imageView,
			R.id.g_imageView };
	private List<ImageView> imageViewList = new ArrayList<ImageView>();
	// ***************����**********************//
	private int flag_bluetooth_count = 0;// ���������Ƿ�ر����� ÿ�������ӵ����ݰ�����λ0
											// ����Լӵ�10˵�������Ͽ��ˣ���ô�Ͽ�����
	private byte[] buffer;// ����������������������
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();// ����������
	private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();//
	private BluetoothSocket bluetoothSocket;
	// private static byte[] SENDMASSAGE = {0x0D,0x0A,0x0A,0x0D};
	private static byte[] BLUEOPENDOOR = { 0x01 };// ����
	private static byte[] BLUECLOSEDOOR = { 0x02 };// ����
	private static byte[] BLUECLOSEALARM = { 0x03 };// �ر���
	private static byte[] BLUEOPENALARM = { 0x04 };// ������
	// private static String MAC_BLUETOOTH="20:15:03:27:06:72";//Ҫ���ӵ��������豸�����ַ
	private static String MAC_BLUETOOTH = "00:0E:EA:CA:01:98";// Ҫ���ӵ��������豸�����ַ
	private Bluetooth bluetooth = new Bluetooth(this, adapter);

	// *******************************************//
	// ��������ʱ���handler
	private Handler handler = new Handler();
	private View view1;
	private View view2;
	private View view3;
	private List<View> viewList;
	private ViewPager pager;
	private PagerTabStrip tabStrip;
	private List<String> titleList;
	// **********����ؼ�**************//
	private TextView titleTextView;
	private TextView mouthTextView;
	private TextView dayTextView;
	private TextView timeTextView;
	private TextView amPmTextView;
	public ImageView doorStationImageView;
	public TextView wasiTextView;// CPU�¶�
	public TextView sysStationTextView;
	public TextView fanghuotishiTextView;
	public TextView temputerTextView;
	public TextView zhongheTextView;
	public ImageView carmeraImageView;
	private ImageButton searchButton;
	private ImageButton openDoorButton;
	private ImageButton closeDoorButton;
	private ImageButton openAlarmButton;
	private ImageButton closeAlarmButton;
	private ImageButton msgOpenDoorButton;
	private ImageButton msgCloseDoorButton;
	private ImageButton startCarmeraButton;
	private ImageButton tab2_openCarmeraButton;
	private ImageButton tab2_closeCarmeraButton;
	private MyServiceView myServiceView;
	// *************�洢ʱ��****************//
	private int hour;
	private int minute;
	private int day;
	private int mouth;
	private int date;
	private int sec;
	String hourString = "";
	String minuteString = "";
	String secString = "";
	private Calendar calendar;
	private TimeThread timeThread;

	@Override
	protected void onDestroy() {
		exitMe();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialAdapter();// ��ʼ��viewPager
		setButtonEffect();// ���ð�ť��Ч
		initialTextView();// ��ʼ��ʱ��
		refreshTime();// ˢ��ϵͳʱ��
		timeThread.start(); // ��ʱ�����
		initialDonghua();// �󶨶�����ť
		// ***************����******************//
		// ����intentFliter����,����actionָ��ΪBluetoothDevice.ACTION_FOUND
		IntentFilter intentFilter = new IntentFilter(
				BluetoothDevice.ACTION_FOUND);
		bluetooth.addIBluetooth(this);
		registerReceiver(bluetoothReceiver, intentFilter);
		// ***************wifi*********************//
		initialWifi();
		// ****************TreeView*******************//
		initialTreeView();
		// ***************************//

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// *******************�ұߵİ�ť*****************//
		case R.id.a_imageView:
			zs_RadioButton.start();
			break;
		case R.id.b_imageView:
			zs_RadioButton.clickOne(imageViewList.get(1));
			bluetoothClick();
			break;
		case R.id.c_imageView:
			zs_RadioButton.clickOne(imageViewList.get(2));
			netClick();
			break;
		case R.id.d_imageView:
			zs_RadioButton.clickOne(imageViewList.get(3));
			datapicker();
			break;
		case R.id.e_imageView:
			zs_RadioButton.clickOne(imageViewList.get(4));
			setting();
			break;
		case R.id.f_imageView:
			aboutUs();
			zs_RadioButton.clickOne(imageViewList.get(5));
			break;
		case R.id.g_imageView:
			exitMe();
			zs_RadioButton.clickOne(imageViewList.get(6));
			break;
		case R.id.h_imageView:
			zs_RadioButton.clickOne(imageViewList.get(7));
			break;
		// *******************tab1��ť*****************//
		case R.id.id_openDoorView:
			openDoorClick();
			break;
		case R.id.id_closeDoorView:
			closeDoorClick();
			break;
		case R.id.id_closeAlarm:
			closeAlarmClick();
			break;
		case R.id.id_openAlarm:
			openAlarmClick();
			break;
		case R.id.id_msgOpenDoor:
			PhoneUtil.msgOpenDoorEShark(this);
			break;
		case R.id.id_msgCloseDoor:
			PhoneUtil.msgCloseDoorEShark(this);
			break;
		// *******************tab3**********************//
		case R.id.id_SearchImageButton:
			searchClick();
			break;
		// *****************tab2*************************//
		case R.id.id_carmeraButton:
			try {
				startCarmeraClick();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.id_closeCarmeraButton:
			try {
				closeCarmeraClick();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.id_openCarmeraButton:
			try {
				openCarmeraClick();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private void openAlarmClick() {

		if (flagForWifiBluetooth == 2)// wifiģʽ
		{
			try {
				netThread.send("to computer&��ʼ����");
				netThread.send("to RAM&" + WIFIOPENALARM);
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else if (flagForWifiBluetooth == 1)// ����ģʽ
		{
			if (adapter.isEnabled()) {
				bluetooth.sendMassage(BLUEOPENALARM);
			}
		} else {
			Toast.makeText(this, "���������������ģʽ", Toast.LENGTH_SHORT).show();
		}
	}

	private void closeAlarmClick() {

		if (flagForWifiBluetooth == 2)// wifiģʽ
		{
			try {
				netThread.send("to computer&ֹͣ����");
				netThread.send("to RAM&" + WIFICLOSEALARM);// ���͸���λ�� ֹͣ����
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else if (flagForWifiBluetooth == 1)// ����ģʽ
		{
			if (adapter.isEnabled()) {
				bluetooth.sendMassage(BLUECLOSEALARM);
			}
		} else {
			Toast.makeText(this, "���������������ģʽ", Toast.LENGTH_SHORT).show();
		}
	}

	private void openCarmeraClick() throws InterruptedException {
		myServiceView.start();
		Log.i("AL", "����Ƶ����");
	}

	private void closeCarmeraClick() throws InterruptedException {
		myServiceView.suspend();
	}

	/**
	 * ��ͼ
	 * 
	 * @throws InterruptedException
	 */
	private void startCarmeraClick() throws InterruptedException {
		if (myServiceView.saveBitmap()) {
			Toast.makeText(this, "ͼƬ����ɹ���ͼƬλ��:"+Environment.getExternalStorageDirectory().getPath(),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "ͼƬ����ʧ��", Toast.LENGTH_SHORT).show();
		}
	}

	private void searchClick() {
		if (flagForWifiBluetooth == 2)// wifiģʽ
		{
			try {
				netThread.send("Phone get all records");
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "�������ģʽ", Toast.LENGTH_SHORT).show();
		}

	}

	private void closeDoorClick() {
		if (flagForWifiBluetooth == 2)// wifiģʽ
		{
			try {
				netThread.send("to RAM&" + WIFICLOSEDOOR);
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else if (flagForWifiBluetooth == 1)// ����ģʽ
		{
			if (adapter.isEnabled()) {
				bluetooth.sendMassage(BLUECLOSEDOOR);
			}
		} else {
			Toast.makeText(this, "���������������ģʽ", Toast.LENGTH_SHORT).show();
		}
	}

	private void openDoorClick() {
		if (flagForWifiBluetooth == 2)// wifiģʽ
		{
			try {
				netThread.send("to RAM&" + WIFIOPENDOOR);// wifiģʽ�·���RAM������
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (flagForWifiBluetooth == 1)// ����ģʽ
		{
			if (adapter.isEnabled()) {
				bluetooth.sendMassage(BLUEOPENDOOR);
			}
		} else {
			Toast.makeText(this, "���������������ģʽ", Toast.LENGTH_SHORT).show();
		}
	}

	private void bluetoothClick() {
		if (flagForWifiBluetooth != 1) {
			if (!adapter.isEnabled())// ������
			{
				Toast.makeText(this, "�������", Toast.LENGTH_SHORT).show();// �ȴ�������
				bluetooth.openBluetooth();
			} else {
				// flagForWifiBluetooth=1;//������ģʽ
				Toast.makeText(this, "�������ӡ�����", Toast.LENGTH_SHORT).show();// �ȴ�������
				bluetooth.startDiscovery();
			}
		} else {
			flagForWifiBluetooth = 0;// ����״̬
			bluetooth.breakLink();
		}
	}

	/**
	 * ������������
	 */
	private void netClick() {
		if (flagForWifiBluetooth == 3)// �����������
		{
			Toast.makeText(this, "����ҿ�������ƴ��������~", Toast.LENGTH_SHORT).show();
		} else if (flagForWifiBluetooth != 2)// ���û��WIFIģʽ
		{
			flagForWifiBluetooth = 3;
			netThread = new NetThread(handler, this);// ��������
			netThread.start();
		} else {
			{
				netThread.interrupt();
				flagForWifiBluetooth = 0;// Ĭ��״̬
			}
		}
	}

	/**
	 * ��ʼ��TreeView
	 */
	private void initialTreeView() {
		mTree = (ListView) view3.findViewById(R.id.id_listview);
		initDatas();
		try {
			mAdapter = new SimpleTreeListViewAdapter<OrgBean>(mTree, this,
					mDatas2, 0);
			mTree.setAdapter(mAdapter);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ��������ť
	 */
	private void initialDonghua() {
		for (int i = 0; i < res.length; i++)// �󶨶�����ť
		{
			ImageView imageView = (ImageView) findViewById(res[i]);
			imageView.setOnClickListener(this);
			imageViewList.add(imageView);
		}
		zs_RadioButton.addListView(imageViewList);
		zs_RadioButton.start();// �ڴ򿪵�ʱ����չ��һ�ΰ�ť
	}

	/**
	 * ��ʼ��WIFI
	 */
	private void initialWifi() {
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	}

	public void setClickAnimation(MotionEvent event, ImageButton but) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			but.setBackgroundResource(R.drawable.touchdown_bg);
		} else {
			but.setBackgroundResource(R.drawable.touchup_bg);
		}
	}

	/**
	 * ���ð�ť��Ч
	 */
	private void setButtonEffect() {
		openDoorButton = (ImageButton) view1.findViewById(R.id.id_openDoorView);
		closeDoorButton = (ImageButton) view1
				.findViewById(R.id.id_closeDoorView);
		searchButton = (ImageButton) view3
				.findViewById(R.id.id_SearchImageButton);
		openAlarmButton = (ImageButton) view1.findViewById(R.id.id_openAlarm);
		closeAlarmButton = (ImageButton) view1.findViewById(R.id.id_closeAlarm);
		msgCloseDoorButton = (ImageButton) view1
				.findViewById(R.id.id_msgCloseDoor);
		msgOpenDoorButton = (ImageButton) view1
				.findViewById(R.id.id_msgOpenDoor);
		startCarmeraButton = (ImageButton) view2
				.findViewById(R.id.id_carmeraButton);
		myServiceView = (MyServiceView) view2.findViewById(R.id.id_myCarmera);// ������������ͷ
		tab2_closeCarmeraButton = (ImageButton) view2
				.findViewById(R.id.id_closeCarmeraButton);
		tab2_openCarmeraButton = (ImageButton) view2
				.findViewById(R.id.id_openCarmeraButton);

		Vector<ImageButton> imageButVect = getImageButList();
		for (int i = 0; i < imageButVect.size(); i++) {
			ImageButton imageBut = imageButVect.get(i);
			imageBut.setOnClickListener(this);
			imageBut.setOnTouchListener(new OnTouchListenerUpDownEffect(
					imageButVect.get(i)));
		}
	}

	/**
	 * ��ʼ��viewpager
	 * 
	 */
	private void initialAdapter() {
		viewList = new ArrayList<View>();
		titleList = new ArrayList<String>();
		tabStrip = (PagerTabStrip) findViewById(R.id.tab);
		view1 = View.inflate(this, R.layout.tab1, null);
		view2 = View.inflate(this, R.layout.tab2, null);
		view3 = View.inflate(this, R.layout.tab3, null);
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);
		titleList.add("��ϵͳ");
		titleList.add("���");
		titleList.add("��¼��ѯ");
		tabStrip.setTextColor(Color.WHITE);
		// ��ʼ��viewpager
		pager = (ViewPager) findViewById(R.id.pager_main);
		// ����������
		MyPagerAdapterTitle adapter = new MyPagerAdapterTitle(viewList,
				titleList);
		// Viewpager ����������
		pager.setAdapter(adapter);
	}

	/**
	 * ��ʼ��ʱ�� ���ҳ�ʼ������textview
	 */
	private void initialTextView() {
		mouthTextView = (TextView) view1.findViewById(R.id.mouthText);
		dayTextView = (TextView) view1.findViewById(R.id.dayText);
		timeTextView = (TextView) view1.findViewById(R.id.timeText);
		amPmTextView = (TextView) view1.findViewById(R.id.amPmText);
		titleTextView = (TextView) findViewById(R.id.id_Titile);
		doorStationImageView = (ImageView) view1
				.findViewById(R.id.id_doorStationImageView);
		temputerTextView = (TextView) view1
				.findViewById(R.id.id_temputerTextView);
		wasiTextView = (TextView) view1.findViewById(R.id.id_wasiTextview);
		sysStationTextView = (TextView) view1
				.findViewById(R.id.id_sysSationTextView);
		fanghuotishiTextView = (TextView) view1
				.findViewById(R.id.id_fanghuoTextView);
		zhongheTextView = (TextView) view1
				.findViewById(R.id.id_zhongheTextView);
		timeThread = new TimeThread();
	}

	/**
	 * ˢ��ʱ��
	 */
	private void refreshTime() {

		calendar = Calendar.getInstance();
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (hour >= 12) {
			amPmTextView.setText("PM");
		} else {
			amPmTextView.setText("AM");
		}
		hour = calendar.get(Calendar.HOUR);
		minute = calendar.get(Calendar.MINUTE);
		mouth = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		date = calendar.get(Calendar.DAY_OF_WEEK);
		sec = calendar.get(Calendar.SECOND);
		mouthTextView.setText("" + mouth + "��" + day + "��");

		if (hour < 10) {
			hourString = "0" + hour;
		} else {
			hourString = "" + hour;
		}
		if (minute < 10) {
			minuteString = "0" + minute;
		} else {
			minuteString = "" + minute;
		}
		if (sec < 10) {
			secString = "0" + sec;
		} else {
			secString = "" + sec;
		}
		timeTextView.setText("" + hourString + ":" + minuteString + ":"
				+ secString);
		String xinqiString = "";
		switch (date) {
		case 1:
			xinqiString = "Sunday";
			break;
		case 2:
			xinqiString = "Monday";
			break;
		case 3:
			xinqiString = "Tuesday";
			break;
		case 4:
			xinqiString = "Wednesday";
			break;
		case 5:
			xinqiString = "Thursday";
			break;
		case 6:
			xinqiString = "Friday";
			break;
		case 7:
			xinqiString = "Saturday";
			break;
		default:
			break;
		}
		dayTextView.setText(xinqiString);
	}

	/**
	 * ˢ��ʱ���õ��߳�
	 */
	private class TimeThread extends Thread {
		int index = 1;
		Vector<String> titleStrings = new Vector<String>();

		public TimeThread() {
			super();
			String str = "E-Shark���ܼҾ�  ������";
			for (int i = 0; i < 4; i++) {
				str = str + ".";
				titleStrings.add(str);
			}
		}

		@Override
		public void run() {
			while (true) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						refreshTime();
						// Log.i("AL", "��������ʱ���߳���");
						// *************����ˢ��������ť״̬***********//
						switch (flagForWifiBluetooth) {
						case 0:
							titleTextView.setText("E-Shark���ܼҾ�");
							imageViewList.get(1).setImageResource(R.drawable.b);
							imageViewList.get(2).setImageResource(
									R.drawable.wifi);
							break;
						case 1:
							titleTextView.setText("E-Shark���ܼҾӣ�����ģʽ��");
							imageViewList.get(1)
									.setImageResource(R.drawable.b_);
							imageViewList.get(2).setImageResource(
									R.drawable.wifi);
							if (!adapter.isEnabled())// �������ģʽ�򿪵�������ȴû��,��ʱ���Ĭ��ģʽ
							{
								flagForWifiBluetooth = 0;
								bluetooth.breakLink();// �Ͽ��������豸
							}
							break;
						case 2:
							titleTextView.setText("E-Shark���ܼҾӣ�WIFIģʽ��");
							imageViewList.get(1).setImageResource(R.drawable.b);
							imageViewList.get(2).setImageResource(
									R.drawable.wificlose);
							break;
						case 3:
							titleTextViewWifiChange();
							imageViewList.get(1).setImageResource(R.drawable.b);
							imageViewList.get(2).setImageResource(
									R.drawable.wifi);
							break;
						default:
							break;
						}
						flag_bluetooth_count++;
						if (flag_bluetooth_count > 10)// 5��û���յ����� ˵�������Ѿ��Ͽ�
														// ��ô�Ͽ�����
						{
							flag_bluetooth_count = 11;
							if (flagForWifiBluetooth == 1) {
								flagForWifiBluetooth = 0;// �ر�����ģʽ
								Toast.makeText(Main.this, "��ע�⣬�����ѶϿ���",
										Toast.LENGTH_SHORT).show();
							}

						}
					}

					private void titleTextViewWifiChange() {
						handler.post(new Runnable() {
							@Override
							public void run() {

								titleTextView.setText(titleStrings.get(index));
								if (index == 3) {
									index = 0;
								} else {
									index++;
								}
							}
						});
					}
				});
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * ����setting����
	 */
	private void setting() {
		Intent intent = new Intent();
		intent.setClass(Main.this, SettingActivity.class);
		startActivity(intent);
	}

	/**
	 * ����ѡ��ʱ�������
	 */
	private void datapicker() {
		new DatePickerDialog(this, 3, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
				.get(Calendar.DAY_OF_MONTH)).show();
		new TimePickerDialog(this, 3, new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
			}
		}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
				true).show();
	}

	/**
	 * ��������
	 */
	private void aboutUs() {
		new AlertDialog.Builder(Main.this)
				.setTitle("����E-Shark")
				// ���öԻ������
				.setMessage(
						"E-Shark�Ŷӳ�Ա��Ҷ���������ˣ�����������������˼������ϵ��ʽ��yejianshanghai@163.com")// ������ʾ������
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {// ���ȷ����ť
							@Override
							public void onClick(DialogInterface dialog,
									int which) {// ȷ����ť����Ӧ�¼�
							}
						}).show();// �ڰ�����Ӧ�¼�����ʾ�˶Ի���
	}

	/**
	 * �˳�ϵͳ�ܹ���ʾ
	 */
	private boolean exitMe() {
		boolean result = false;
		new AlertDialog.Builder(Main.this)
				.setTitle("E-Shark������")
				// ���öԻ������
				.setMessage("��ȷ���������ݶ���������˳�ϵͳ��")
				// ������ʾ������
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {// ���ȷ����ť
							@Override
							public void onClick(DialogInterface dialog,
									int which) {// ȷ����ť����Ӧ�¼�
								finish();
							}
						})
				.setNegativeButton("����", new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť
							@Override
							public void onClick(DialogInterface dialog,
									int which) {// ��Ӧ�¼�
								Log.i("alertdialog", " �뱣�����ݣ�");
							}
						}).show();// �ڰ�����Ӧ�¼�����ʾ�˶Ի���
		return result;
	}

	// ********����**************//
	// ��ʾ��⵽�������豸
	private class BluetoothReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context contex, Intent intent) {

			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			bluetooth.connectDevice(device, MAC_BLUETOOTH);

		}
	}

	// ���ܵ�����������������
	@Override
	public void getConnectedThreadReceiveBuffer(byte[] buffer) {
		flag_bluetooth_count = 0;// ��������Ƿ�Ͽ� ����
		this.buffer = buffer;
		handler.post(new Runnable() {
			@Override
			public void run() {
				setStationByBluetooth(Main.this.buffer);
				// Toast.makeText(Main.this, new String(Main.this.buffer),
				// Toast.LENGTH_SHORT).show();
				// temputerTextView.setText(new String(Main.this.buffer));
			}
		});

	}

	/**
	 * ���ڽ����������������ݲ���ʾ���������ϵĺ��� ��������
	 * 
	 * @param buffer
	 */
	private void setStationByBluetooth(byte[] buffer) {
		// ��������BUG��
		int index = 0;// ��Ϊ���յ������ݵ�һλ������ʧ ����һ��λΪ��ͷ Ϊff ff ���� ��
						// ��ͷֻ��һλ��ʱ�� ��ֱ����ʾ ����λ ����ԭ���Ļ�����+1
		if (flagForWifiBluetooth == 0) {
			flagForWifiBluetooth = 1;// ������ģʽ
			Toast.makeText(this, "�����Ѿ����ӣ�", Toast.LENGTH_SHORT).show();
		}
		int date[] = new int[10];
		for (int i = 0; i < 10; i++)// ��byte���޷�����ֵ����date
		{
			if (buffer[i] < 0) {
				date[i] = buffer[i] & 0xff;
			} else {
				date[i] = buffer[i];
			}
		}
		if (date[1] == 255) {
			index = 1;
		}
		if (buffer.length >= 9) {
			Log.i("AL", " " + date[0] + " " + date[1] + " " + date[2] + " "
					+ date[3] + " " + date[4] + " " + date[5] + " " + date[6]
					+ " " + date[7] + " ");
			setDoorStationFromBluetooth(date[index + 1]);
			this.temputerTextView
					.setText((date[index + 2] * 25.5 + date[index + 3] * 1.0 / 10)
							+ "��");// �����¶�
			this.wasiTextView.setText("CPU�¶�: " + date[index + 4] + "��");// CPU�¶�
			if (date[index + 6] == 1) {
				this.fanghuotishiTextView.setText("����״̬:��������Ŷ");
			} else {
				this.fanghuotishiTextView.setText("����״̬:����û��Ү");
			}

		}

	}

	private void setDoorStationFromBluetooth(int doorStation) {
		switch (doorStation) {
		case 0:// �Ź���
			sysStationTextView.setText("��״̬: ������");
			doorStationImageView.setImageResource(R.drawable.door_closed);
			break;
		case 1:// ���� �Ǿ���״̬
			sysStationTextView.setText("��״̬: �����ţ�����״̬δ��������ע�⡣");
			doorStationImageView
					.setImageResource(R.drawable.door_almose_closed);
			break;
		case 2:// �ſ���
			sysStationTextView.setText("��״̬: ������");
			doorStationImageView.setImageResource(R.drawable.door);
			break;
		case 3:// ���ڱ���

			break;
		case 4:// ���� ����״̬
			sysStationTextView.setText("��״̬: �����ţ�����״̬�ѿ�����");
			doorStationImageView
					.setImageResource(R.drawable.door_almose_closed);
			break;
		default:
			break;
		}
	}

	// ************�������˳�*************//
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳����˳�E-Shark���ܿ���ƽ̨",
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// *****************wifi********************//
	private void openWifi() {
		wifiManager.setWifiEnabled(true);
		// System.out.println("wifi state -----> "+wifiManager.getWifiState());
		Log.i("AL", "wifi state -----> " + wifiManager.getWifiState());
		Toast.makeText(this, "���ڴ�wifi����ǰwifi״̬Ϊ" + wifiManager.getWifiState(),
				Toast.LENGTH_SHORT).show();
	}

	private void closeWifi() {
		wifiManager.setWifiEnabled(false);
		Log.i("AL", "wifi state -----> " + wifiManager.getWifiState());
		Toast.makeText(this, "���ڹر�wifi����ǰwifi״̬Ϊ" + wifiManager.getWifiState(),
				Toast.LENGTH_SHORT).show();
	}

	private void checkWifi() {
		Log.i("AL", "wifi state -----> " + wifiManager.getWifiState());
		Toast.makeText(this, "��ǰwifi״̬Ϊ" + wifiManager.getWifiState(),
				Toast.LENGTH_SHORT).show();
	}

	// *******************����******************//

	// ******************�����ı�ĺ���***********************//
	public int getflagForWifiBluetooth() {
		return flagForWifiBluetooth;
	}

	public void setflagForWifiBluetooth(int flag) {
		flagForWifiBluetooth = flag;
	}

	// *********************TreeView*******************//
	private void initEvent() {
		mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
			@Override
			public void onClick(Node node, int position) {
				if (node.isLeaf()) {
					Toast.makeText(Main.this, node.getName(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mTree.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// DialogFragment
				final EditText et = new EditText(Main.this);
				new AlertDialog.Builder(Main.this)
						.setTitle("Add Node")
						.setView(et)
						.setPositiveButton(
								"Sure",
								new android.content.DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										if (TextUtils.isEmpty(et.getText()
												.toString()))
											return;
										mAdapter.addExtraNode(position, et
												.getText().toString());
									}
								}).setNegativeButton("Cancel", null).show();

				return true;
			}
		});
	}

	private void initDatas() {
		mDatas2 = new ArrayList<OrgBean>();
		OrgBean bean2 = new OrgBean(idOfTreeList, 0, "���");
		idOfTreeList++;
		mDatas2.add(bean2);
		bean2 = new OrgBean(idOfTreeList, 0, "��ȥ7��");
		idOfTreeList++;
		mDatas2.add(bean2);
		bean2 = new OrgBean(idOfTreeList, 0, "һ������");
		idOfTreeList++;
		mDatas2.add(bean2);
		bean2 = new OrgBean(idOfTreeList, 0, "��ǰ");
		idOfTreeList++;
		mDatas2.add(bean2);

	}

	// *************************************************//
	public void setFlagCarmeraIsOpened(boolean flag) {
		flagCarmeraIsOpened = flag;
	}

	public boolean isFlageCarmeraIsOpened() {
		return flagCarmeraIsOpened;
	}

	private Vector<ImageButton> getImageButList() {
		Vector<ImageButton> imageButVect = new Vector<ImageButton>();
		imageButVect.add(tab2_closeCarmeraButton);
		imageButVect.add(tab2_openCarmeraButton);
		imageButVect.add(openDoorButton);
		imageButVect.add(closeDoorButton);
		imageButVect.add(searchButton);
		imageButVect.add(openAlarmButton);
		imageButVect.add(closeAlarmButton);
		imageButVect.add(msgCloseDoorButton);
		imageButVect.add(msgOpenDoorButton);
		imageButVect.add(startCarmeraButton);
		return imageButVect;
	}

	private int[] getImageButIDArray() {
		int IdArray[] = { R.id.id_closeCarmeraButton,
				R.id.id_openCarmeraButton, R.id.id_openDoorView,
				R.id.id_closeDoorView, R.id.id_SearchImageButton,
				R.id.id_openAlarm, R.id.id_closeAlarm, R.id.id_msgCloseDoor,
				R.id.id_msgOpenDoor, R.id.id_carmeraButton };
		return IdArray;
	}

}
