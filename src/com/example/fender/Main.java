package com.example.fender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.example.fender.R.id;

import sys.util.PhoneUtil;
import treeview.adapter.SimpleTreeListViewAdapter;
import treeview.bean.FileBean;
import treeview.bean.OrgBean;
import treeview.utils.Node;
import treeview.utils.adapter.TreeListViewAdapter.OnTreeNodeClickListener;
import ZS.Android.Bluetooth.Bluetooth;
import ZS.Android.Bluetooth.Bluetooth.IBluetooth;
import ZS.Android.Net.CarmeraThread;
import ZS.Android.Net.MyServiceView;
import ZS.Android.Net.NetThread;
import android.R.integer;
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
import android.os.Handler;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import donghua.ZS_RadioButton;

public class Main extends Activity implements OnClickListener,IBluetooth{
	
	//*******************TreeView************************/
	public ListView mTree;
	public SimpleTreeListViewAdapter<OrgBean> mAdapter;
	private List<FileBean> mDatas;
	public List<OrgBean> mDatas2;
	public int idOfTreeList= 1;
	//*************wifi/bluetooth标志位  0:未开启任何状态 1：蓝牙状态 2：wifi状态 3 : 网络正在连接 4 共存状态
	private static byte WIFIOPENDOOR = 0x01;//发送给下位机的 开门
	private static byte WIFICLOSEDOOR = 0x02;//关门
	private static byte WIFIOPENALARM = 0x03;//开警报
	private static byte WIFICLOSEALARM = 0x04;//关警报
	private int flagForWifiBluetooth=0;
	private boolean flagCarmeraIsOpened = false;//电脑端视频是否已经打开 打开为true
	//*********wifi***********//
	private WifiManager wifiManager = null;
	private NetThread netThread = null;//用于连接服务器
	//**********************//
	private long mExitTime ; //退出时间
	//按钮动画
	private ZS_RadioButton zs_RadioButton=new ZS_RadioButton();
	private int[] res ={R.id.a_imageView,R.id.b_imageView,R.id.c_imageView,
			R.id.d_imageView,R.id.e_imageView,R.id.f_imageView,
			R.id.g_imageView};
	private List<ImageView> imageViewList = new ArrayList<ImageView>();
	//***************蓝牙**********************//
	private int flag_bluetooth_count = 0;//用来计数是否关闭蓝牙 每次蓝牙接到数据把他置位0 如果自加到10说明蓝牙断开了，那么断开蓝牙
	private byte[] buffer;//用来接收蓝牙传来的数据
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();//蓝牙适配器
	private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();//
	private BluetoothSocket bluetoothSocket;
//	private static byte[] SENDMASSAGE = {0x0D,0x0A,0x0A,0x0D};
	private static byte[] BLUEOPENDOOR = {0x01};//开门
	private static byte[] BLUECLOSEDOOR = {0x02};//关门
	private static byte[] BLUECLOSEALARM = {0x03};//关报警
	private static byte[] BLUEOPENALARM = {0x04};//开报警
	//private static String MAC_BLUETOOTH="20:15:03:27:06:72";//要连接的蓝牙的设备物理地址
	private static String MAC_BLUETOOTH="00:0E:EA:CA:01:98";//要连接的蓝牙的设备物理地址
	private Bluetooth bluetooth = new Bluetooth(this, adapter);
	
	//*******************************************//
	//用来更新时间的handler
	private Handler handler = new Handler();
	private View view1;
	private View view2;
	private View view3;
	private List<View>viewList;
	private ViewPager pager;
	private PagerTabStrip tabStrip;
	private List<String>titleList;
	//**********界面控件**************//
	private TextView titleTextView;
	private TextView mouthTextView;
	private TextView dayTextView;
	private TextView timeTextView;
	private TextView amPmTextView;
	public ImageView doorStationImageView;
	public TextView wasiTextView;//CPU温度
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
	//*************存储时间****************//
	private int hour;
	private int minute;
	private int day;
	private int mouth;
	private int date;
	private int sec;
	String hourString="";
	String minuteString="";
	String secString="";
	private Calendar calendar;
	private TimeThread timeThread;
	@Override
	protected void onDestroy() {
		 
		exitMe();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialAdapter();//初始化viewPager
        setButtonEffect();//设置按钮特效
        initialTextView();//初始化时间
        refreshTime();//刷新系统时间
        timeThread.start(); //打开时间进程
        initialDonghua();//绑定动画按钮
        //***************蓝牙******************//
    	//创建intentFliter对象,将其action指定为BluetoothDevice.ACTION_FOUND
    	IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	bluetooth.addIBluetooth(this);
        registerReceiver(bluetoothReceiver, intentFilter);
        //***************wifi*********************//
        initialWifi();
        //****************TreeView*******************//
        initialTreeView();
//		initEvent();//初始化TreeView的点击事件 可以不加
        //
		//***************************//
        
    }
    
	@Override
	public void onClick(View arg0) {
				switch (arg0.getId()) {
				//*******************右边的按钮*****************//
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
					//*******************tab1按钮*****************//
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
				//*******************tab3**********************//
				case R.id.id_SearchImageButton:
					searchClick();
					break;	
				//*****************tab2*************************//
				case R.id.id_carmeraButton:
					try {
						startCarmeraClick();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case R.id.id_closeCarmeraButton:
					closeCarmeraClick();
					break;
				case R.id.id_openCarmeraButton:
					openCarmeraClick();
					break;
				default:
					break;
				}
	}
  
	private void openAlarmClick() {
		 
		
		if(flagForWifiBluetooth==2)//wifi模式
    	{
	    	try {
				netThread.send("to computer&开始报警");
				netThread.send("to RAM&"+WIFIOPENALARM);
			} catch (IOException e) {
				 
				e.printStackTrace();
			}
    	}else if(flagForWifiBluetooth==1)//蓝牙模式
    	{
    		if(adapter.isEnabled())
    		{
    			bluetooth.sendMassage(BLUEOPENALARM);
    		}	
    	}else {
    		Toast.makeText(this, "请打开蓝牙或者网络模式", Toast.LENGTH_SHORT).show();
		}
	}
	private void closeAlarmClick() {
		 
		if(flagForWifiBluetooth==2)//wifi模式
    	{
	    	try {
				netThread.send("to computer&停止报警");
				netThread.send("to RAM&"+WIFICLOSEALARM);//发送给下位机 停止报警
			} catch (IOException e) {
				 
				e.printStackTrace();
			}
    	}else if(flagForWifiBluetooth==1)//蓝牙模式
    	{
    		if(adapter.isEnabled())
    		{
    			bluetooth.sendMassage(BLUECLOSEALARM);
    		}	
    	}else {
    		Toast.makeText(this, "请打开蓝牙或者网络模式", Toast.LENGTH_SHORT).show();
		}
	}
	private void openCarmeraClick() {
		 myServiceView.start();
		 Log.i("AL", "打开视频！！");
//    	if(flagForWifiBluetooth==2)//wifi模式
//    	{
//	    	try {
//				netThread.send("to computer&打开视频");
//			} catch (IOException e) {
//			
//				e.printStackTrace();
//			}
//    	}else if(flagForWifiBluetooth==1)//蓝牙模式
//    	{
//    		if(adapter.isEnabled())
//    		{
//    			bluetooth.sendMassage(BLUECLOSEDOOR);
//    		}	
//    	}else {
//    		Toast.makeText(this, "请打开蓝牙或者网络模式", Toast.LENGTH_SHORT).show();
//		}
	}

	private void closeCarmeraClick() {
		 
		 myServiceView.suspend();
//    	if(flagForWifiBluetooth==2)//wifi模式
//    	{
//	    	try {
//				netThread.send("to computer&关闭视频");
//			} catch (IOException e) {
//				
//				e.printStackTrace();
//			}
//    	}else if(flagForWifiBluetooth==1)//蓝牙模式
//    	{
//    		if(adapter.isEnabled())
//    		{
//    			bluetooth.sendMassage(BLUECLOSEDOOR);
//    		}	
//    	}else {
//    		Toast.makeText(this, "请打开蓝牙或者网络模式", Toast.LENGTH_SHORT).show();
//		}
	}

	/**
	 * 截图
	 * @throws InterruptedException 
	 */
	private void startCarmeraClick() throws InterruptedException {
		 
		
//		new CarmeraThread(handler, Main.this).start();
		if(myServiceView.saveBitmap())
		{
			Toast.makeText(this, "图片保存成功，图片位置：SD卡/DCIM/EShark", Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
		}
	}


	
	private void searchClick() {
		 
    	if(flagForWifiBluetooth==2)//wifi模式
    	{
	    	try {
				netThread.send("Phone get all records");
			} catch (IOException e) {
				 
				e.printStackTrace();
			}
    	}else {
    		Toast.makeText(this, "请打开网络模式", Toast.LENGTH_SHORT).show();
		}
    	
	}
	private void closeDoorClick() {
		 
    	if(flagForWifiBluetooth==2)//wifi模式
    	{
	    	try {
				netThread.send("to RAM&"+WIFICLOSEDOOR);
			} catch (IOException e) {
				 
				e.printStackTrace();
			}
    	}else if(flagForWifiBluetooth==1)//蓝牙模式
    	{
    		if(adapter.isEnabled())
    		{
    			bluetooth.sendMassage(BLUECLOSEDOOR);
    		}	
    	}else {
    		Toast.makeText(this, "请打开蓝牙或者网络模式", Toast.LENGTH_SHORT).show();
		}
	}

	private void openDoorClick() {
		 
    	if(flagForWifiBluetooth==2)//wifi模式
    	{
	    	try {
				netThread.send("to RAM&"+WIFIOPENDOOR);//wifi模式下发给RAM的数据
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}else if(flagForWifiBluetooth==1)//蓝牙模式
    	{
    		if(adapter.isEnabled())
    		{
    			bluetooth.sendMassage(BLUEOPENDOOR);
    		}	
    	}else {
    		Toast.makeText(this, "请打开蓝牙或者网络模式", Toast.LENGTH_SHORT).show();
		}
	}

	private void bluetoothClick() {
		 
		if(flagForWifiBluetooth != 1)
		{
			if(!adapter.isEnabled())//打开蓝牙
			{
				Toast.makeText(this, "请打开蓝牙", Toast.LENGTH_SHORT).show();//先打开蓝牙！
				bluetooth.openBluetooth();
			}else {
//				flagForWifiBluetooth=1;//打开蓝牙模式
				Toast.makeText(this, "正在连接。。。", Toast.LENGTH_SHORT).show();//先打开蓝牙！
				bluetooth.startDiscovery();
			}
		}else {
			flagForWifiBluetooth = 0;//挂起状态
			bluetooth.breakLink();
		}
	}

	/**
     * 用于连接网络
     */
    private void netClick() {
    	if(flagForWifiBluetooth == 3)//如果正在连接
    	{
    		Toast.makeText(this, "别催我咯，正在拼命连接中~",Toast.LENGTH_SHORT).show();
    	}else if(flagForWifiBluetooth!=2)//如果没打开WIFI模式
    	{
    		flagForWifiBluetooth = 3;
    		netThread = new NetThread(handler,this);//连接网络
			netThread.start();
    	}else {
    		{
    			netThread.interrupt();
    			flagForWifiBluetooth=0;//默认状态
    		}
		}
	}
    /**
     * 初始化TreeView
     */
    private void initialTreeView() {
		
    	 mTree = (ListView) view3.findViewById(R.id.id_listview);
 		initDatas();
 		try
 		{
 			mAdapter = new SimpleTreeListViewAdapter<OrgBean>(mTree, this,
 					mDatas2, 0);
 			mTree.setAdapter(mAdapter);
 		} catch (IllegalAccessException e)
 		{
 			e.printStackTrace();
 		}
	}
    
    /**
     * 初始化动画按钮
     */
	private void initialDonghua() {
		
    	  for(int i=0;i<res.length;i++)//绑定动画按钮
          {
          	ImageView imageView = (ImageView) findViewById(res[i]);
          	imageView.setOnClickListener(this);
          	imageViewList.add(imageView);
          }
          zs_RadioButton.addListView(imageViewList);
          zs_RadioButton.start();//在打开的时候先展开一次按钮
	}
	/**
     * 初始化WIFI
     */
    private void initialWifi() {
    	 wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	}

	/**
     * 设置按钮特效
     */
    private void setButtonEffect() {
		 
    	openDoorButton =  (ImageButton) view1.findViewById(R.id.id_openDoorView);
    	closeDoorButton =  (ImageButton) view1.findViewById(R.id.id_closeDoorView);
    	searchButton = (ImageButton) view3.findViewById(R.id.id_SearchImageButton);
    	openAlarmButton = (ImageButton) view1.findViewById(R.id.id_openAlarm);
    	closeAlarmButton = (ImageButton) view1.findViewById(R.id.id_closeAlarm);
    	msgCloseDoorButton = (ImageButton) view1.findViewById(R.id.id_msgCloseDoor);
    	msgOpenDoorButton= (ImageButton) view1.findViewById(R.id.id_msgOpenDoor);
    	startCarmeraButton = (ImageButton) view2.findViewById(R.id.id_carmeraButton);
//    	carmeraImageView = (ImageView) view2.findViewById(R.id.id_carmera);
    	myServiceView = (MyServiceView) view2.findViewById(R.id.id_myCarmera);//用来控制摄像头
    	tab2_closeCarmeraButton = (ImageButton) view2.findViewById(R.id.id_closeCarmeraButton);
    	tab2_openCarmeraButton = (ImageButton) view2.findViewById(R.id.id_openCarmeraButton);
    	tab2_closeCarmeraButton.setOnClickListener(this);
    	tab2_openCarmeraButton.setOnClickListener(this);
    	openDoorButton.setOnClickListener(this);
    	closeDoorButton.setOnClickListener(this);
    	searchButton.setOnClickListener(this);
    	openAlarmButton.setOnClickListener(this);
    	closeAlarmButton.setOnClickListener(this);
    	msgCloseDoorButton.setOnClickListener(this);
    	msgOpenDoorButton.setOnClickListener(this);
    	startCarmeraButton.setOnClickListener(this);
		//**************************开门按钮效果***************************//
		openDoorButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					Log.i("AL", "进入按下函数");
					openDoorButton.setBackgroundResource(R.drawable.touchdown_bg);	
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					Log.i("AL", "进入放开函数");
					openDoorButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
		//**************************关门按钮效果***************************//
		 closeDoorButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					 closeDoorButton.setBackgroundResource(R.drawable.touchdown_bg);
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					 closeDoorButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
		//**************************搜索按钮效果***************************//
		 searchButton.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_DOWN)
					{
						//按下事件
						 searchButton.setBackgroundResource(R.drawable.touchdown_bg);
					}
					if(event.getAction()==MotionEvent.ACTION_UP)
					{
						//放开事件
						 searchButton.setBackgroundResource(R.drawable.touchup_bg);
					}
					return false;
				}
			});
		//**************************短信关门按钮效果***************************//
		 msgCloseDoorButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					msgCloseDoorButton.setBackgroundResource(R.drawable.touchdown_bg);
					
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					msgCloseDoorButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
		//**************************短信开门按钮效果***************************//
		 msgOpenDoorButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					msgOpenDoorButton.setBackgroundResource(R.drawable.touchdown_bg);
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					msgOpenDoorButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
		//**************************开报警按钮效果***************************//
		 openAlarmButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					openAlarmButton.setBackgroundResource(R.drawable.touchdown_bg);
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					openAlarmButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
		//**************************关报警按钮效果***************************//
		 closeAlarmButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					closeAlarmButton.setBackgroundResource(R.drawable.touchdown_bg);
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					closeAlarmButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
		//**************************打开视频钮效果***************************//
		 tab2_openCarmeraButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					tab2_openCarmeraButton.setBackgroundResource(R.drawable.touchdown_bg);
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					tab2_openCarmeraButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
		//**************************关闭视频钮效果***************************//
		 tab2_closeCarmeraButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					tab2_closeCarmeraButton.setBackgroundResource(R.drawable.touchdown_bg);
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					tab2_closeCarmeraButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
			//**************************截图按钮效果***************************//
		 startCarmeraButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					startCarmeraButton.setBackgroundResource(R.drawable.touchdown_bg);
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					startCarmeraButton.setBackgroundResource(R.drawable.touchup_bg);
				}
				return false;
			}
		});
		 
	}
    
	/**
     * 初始化viewpager
     * 
     */
    private void initialAdapter()
    {
    	viewList = new ArrayList<View>();
    	titleList = new ArrayList<String>();
    	tabStrip = (PagerTabStrip) findViewById(R.id.tab);
        view1=View.inflate(this, R.layout.tab1, null);
        view2=View.inflate(this, R.layout.tab2, null);
        view3=View.inflate(this, R.layout.tab3, null);
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        titleList.add("主系统");
        titleList.add("监控");
        titleList.add("记录查询");
        tabStrip.setTextColor(Color.WHITE);
        //初始化viewpager
        pager = (ViewPager) findViewById(R.id.pager_main);
        //创建适配器
        MyPagerAdapterTitle adapter = new MyPagerAdapterTitle(viewList,titleList);
        //Viewpager 加载适配器
        pager.setAdapter(adapter);
    }
    /**
     * 初始化时间
     * 并且初始化其他textview
     */
    private void initialTextView()
    {
    	mouthTextView = (TextView) view1.findViewById(R.id.mouthText);
    	dayTextView = (TextView) view1.findViewById(R.id.dayText);
    	timeTextView = (TextView) view1.findViewById(R.id.timeText);
    	amPmTextView = (TextView) view1.findViewById(R.id.amPmText);
    	titleTextView=(TextView) findViewById(R.id.id_Titile);
    	doorStationImageView = (ImageView) view1.findViewById(R.id.id_doorStationImageView);
    	temputerTextView = (TextView) view1.findViewById(R.id.id_temputerTextView);
    	wasiTextView = (TextView) view1.findViewById(R.id.id_wasiTextview);
    	sysStationTextView = (TextView) view1.findViewById(R.id.id_sysSationTextView);
    	fanghuotishiTextView = (TextView) view1.findViewById(R.id.id_fanghuoTextView);
    	zhongheTextView = (TextView) view1.findViewById(R.id.id_zhongheTextView);
    	timeThread= new TimeThread();
    }
    /**
     * 刷新时间
     */
    private void refreshTime()
    {
    	
    	calendar=Calendar.getInstance();
    	hour=calendar.get(Calendar.HOUR_OF_DAY);
    	if(hour>=12)
    	{
    		amPmTextView.setText("PM");
    	}else {
    		amPmTextView.setText("AM");
		}
    	hour=calendar.get(Calendar.HOUR);
    	minute=calendar.get(Calendar.MINUTE);
    	mouth=calendar.get(Calendar.MONTH)+1;
    	day=calendar.get(Calendar.DAY_OF_MONTH);
    	date=calendar.get(Calendar.DAY_OF_WEEK);
    	sec = calendar.get(Calendar.SECOND);
    	mouthTextView.setText(""+mouth+"月"+day+"日");

    	if(hour<10)
    	{
    		hourString="0"+hour;
    	}else {
    		hourString=""+hour;
		}
    	if(minute<10)
    	{
    		minuteString="0"+minute;
    	}else {
    		minuteString=""+minute;
		}
    	if(sec<10)
    	{
    		secString="0"+sec;
    	}else {
    		secString=""+sec;
		}
    	timeTextView.setText(""+hourString+":"+minuteString+":"+secString);
    	String xinqiString="";
    	switch (date) {
		case 1:
			xinqiString="Sunday";
			break;
		case 2:
			xinqiString="Monday";
			break;
		case 3:
			xinqiString="Tuesday";
			break;
		case 4:
			xinqiString="Wednesday";
			break;
		case 5:
			xinqiString="Thursday";
			break;
		case 6:
			xinqiString="Friday";
			break;
		case 7:
			xinqiString="Saturday";
			break;
		default:
			break;
		}
    	dayTextView.setText(xinqiString);
    }
    
    /**
     * 刷新时间用的线程
     */
    private class TimeThread extends Thread
    {
    	int index=1;
    	Vector<String> titleStrings = new Vector<String>();
    	public TimeThread() {
			
    		super();
    		titleStrings.add("E-Shark智能家居（连接中.）");
    		titleStrings.add("E-Shark智能家居（连接中..）");
    		titleStrings.add("E-Shark智能家居（连接中...）");
    		titleStrings.add("E-Shark智能家居（连接中....）");
		}
    	@Override
    	public void run() {
    		while(true)
    		{
    		 
    		handler.post(new Runnable() {
				@Override
				public void run() {
					
					refreshTime();
					//Log.i("AL", "我现在在时间线程中");
					//*************用于刷新蓝牙按钮状态***********//
					switch (flagForWifiBluetooth) {
					case 0:
						titleTextView.setText("E-Shark智能家居");
						imageViewList.get(1).setImageResource(R.drawable.b);
						imageViewList.get(2).setImageResource(R.drawable.wifi);
						break;
					case 1:
						titleTextView.setText("E-Shark智能家居（蓝牙模式）");
						imageViewList.get(1).setImageResource(R.drawable.b_);
						imageViewList.get(2).setImageResource(R.drawable.wifi);
						if(!adapter.isEnabled())//如果蓝牙模式打开但是蓝牙却没开,这时设回默认模式
						{
							flagForWifiBluetooth=0;
							bluetooth.breakLink();//断开蓝蓝牙设备
						}
						break;
					case 2:
						titleTextView.setText("E-Shark智能家居（WIFI模式）");
						imageViewList.get(1).setImageResource(R.drawable.b);
						imageViewList.get(2).setImageResource(R.drawable.wificlose);
						break;
					case 3:
						titleTextViewWifiChange();
						imageViewList.get(1).setImageResource(R.drawable.b);
						imageViewList.get(2).setImageResource(R.drawable.wifi);
						break;
					default:
						break;
					}
					flag_bluetooth_count++;
					if(flag_bluetooth_count>10)//5秒没接收到数据 说明蓝牙已经断开 那么断开蓝牙
					{
						flag_bluetooth_count=11; 
						if(flagForWifiBluetooth==1)
						{
							flagForWifiBluetooth=0;//关闭蓝牙模式
							Toast.makeText(Main.this, "请注意，蓝牙已断开！", Toast.LENGTH_SHORT).show();
						}
						
					}
					
//					if(flagForWifiBluetooth == 1)
//					{
//						imageViewList.get(1).setImageResource(R.drawable.b_);
//						titleTextView.setText("E-Shark智能家居（蓝牙模式）");
//						if(!adapter.isEnabled())//如果蓝牙模式打开但是蓝牙却没开,这时设回默认模式
//						{
//							flagForWifiBluetooth=0;
//						}
//					}else {
//						{
//							imageViewList.get(1).setImageResource(R.drawable.b);
//						}
//					}
//					//*************用于刷新wifi按钮状态***********
//					if(flagForWifiBluetooth!=2)
//					{
//						imageViewList.get(2).setImageResource(R.drawable.wifi);
//						
//					}else if(flagForWifiBluetooth==2){
//						{
//							imageViewList.get(2).setImageResource(R.drawable.wificlose);
//							titleTextView.setText("E-Shark智能家居（WIFI模式）");
//						}
//					}
					//****************************************//
				}

				private void titleTextViewWifiChange() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							 
							titleTextView.setText(titleStrings.get(index));
							if(index == 3)
							{
								index = 0;
							}else {
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
	 * 进入setting界面
	 */
	private void setting() {
		Intent intent = new Intent();
		intent.setClass(Main.this, SettingActivity.class);
		startActivity(intent);
	}
	/**
	 * 用于选择时间和日期
	 */
	private void datapicker() {
		new DatePickerDialog(this, 3,new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
				
			}
		}, calendar.get(Calendar.YEAR),  calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
       new TimePickerDialog(this, 3, new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker arg0, int arg1, int arg2) {		
			}
		}, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true).show();
	}
	/**
	 * 关于我们
	 */
	private void aboutUs() {
		 new AlertDialog.Builder(Main.this).setTitle("关于E-Shark")//设置对话框标题
		 .setMessage("E-Shark团队成员：叶健，郭新兴，马敬川，李轩昂，赵思晨。联系方式：yejianshanghai@163.com")//设置显示的内容
		 .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
			 @Override
			 public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
			 }
		 }).show();//在按键响应事件中显示此对话框
	}
	/**
	 * 退出系统能够提示
	 */
	private void exitMe()
	{
			 new AlertDialog.Builder(Main.this).setTitle("E-Shark提醒您")//设置对话框标题
			 .setMessage("请确认所有数据都保存后再退出系统！")//设置显示的内容
			 .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
				 @Override
				 public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
					 finish();
				 }
			 }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
				 @Override
				 public void onClick(DialogInterface dialog, int which) {//响应事件
					 Log.i("alertdialog"," 请保存数据！");
				 }
			 }).show();//在按键响应事件中显示此对话框		 
	}
    //********蓝牙**************//
	//显示检测到的蓝牙设备
	private class BluetoothReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context contex, Intent intent) {
			
			BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			bluetooth.connectDevice(device,MAC_BLUETOOTH);
			
		}
	}
	//接受到的蓝牙发来的数据
	@Override
	public void getConnectedThreadReceiveBuffer(byte[] buffer) {
		flag_bluetooth_count=0;//蓝牙检测是否断开 置零
		 this.buffer=buffer;
		 handler.post(new Runnable() {
			@Override
			public void run() {
				setStationByBluetooth(Main.this.buffer);
//				Toast.makeText(Main.this, new String(Main.this.buffer), Toast.LENGTH_SHORT).show();
//				temputerTextView.setText(new String(Main.this.buffer));
			}
		});
			
	}
	
	/**
	 * 用于解析蓝牙传来的数据并显示在主界面上的函数
	 * 蓝牙接收
	 * @param buffer
	 */
	private void setStationByBluetooth(byte[] buffer)
	{
		//用于修正BUG的
		int index=0;//因为接收到的数据第一位经常丢失 二第一二位为包头 为ff ff 所以 当
					//包头只有一位的时候 就直接显示 有两位 就在原来的基础上+1
		if(flagForWifiBluetooth ==0 )
		{
			flagForWifiBluetooth=1;//打开蓝牙模式
			Toast.makeText(this, "蓝牙已经连接！", Toast.LENGTH_SHORT).show();
		}
		int date[] = new int[10] ;
		for(int i =0;i<10;i++)//将byte的无符号数值赋给date
		{
			if(buffer[i]<0)
			{
				date[i] = buffer[i]&0xff;
			}else {
				date[i] = buffer[i];
 			}
		}
		if(date[1]==255)
		{
			index = 1;
		}
		if(buffer.length>=9)
		{
			Log.i("AL", " "+date[0]+" "+date[1]+" "+date[2]+" "+date[3]+" "+date[4]+" "+date[5]+" "+date[6]+" "+date[7]+" ");
			setDoorStationFromBluetooth(date[index+1]);
			this.temputerTextView.setText((date[index+2]*25.5+date[index+3]*1.0/10)+"°");//设置温度	
			this.wasiTextView.setText("CPU温度: "+date[index+4]+"°");//CPU温度
			if(date[index+6]==1)
			{
				this.fanghuotishiTextView.setText("门外状态:门外有人哦");
			}else {
				this.fanghuotishiTextView.setText("门外状态:门外没人耶");
			}
			
		}
		
	}
	private void setDoorStationFromBluetooth(int doorStation)
	{
		switch (doorStation) {
		case 0://门关着
			sysStationTextView.setText("门状态: 关着呢");
			doorStationImageView.setImageResource(R.drawable.door_closed);
			break;
		case 1://虚掩 非警戒状态
			sysStationTextView.setText("门状态: 虚掩着，警戒状态未开启，请注意。");
			doorStationImageView.setImageResource(R.drawable.door_almose_closed);
			break;
		case 2://门开着 
			sysStationTextView.setText("门状态: 开着呢");
			doorStationImageView.setImageResource(R.drawable.door);
			break;
		case 3://正在报警
			
			break;
		case 4://虚掩 警戒状态
			sysStationTextView.setText("门状态: 虚掩着，警戒状态已开启！");
			doorStationImageView.setImageResource(R.drawable.door_almose_closed);
			break;
		default:
			break;
		}
	}
	//************按两次退出*************//
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
	        if ((System.currentTimeMillis() - mExitTime) > 2000 ) {    
	            Toast.makeText(this, "再按一次退出，退出E-Shark智能控制平台", Toast.LENGTH_SHORT).show();
	            mExitTime = System.currentTimeMillis();
	        }else{
	            finish();
	        }
	        return true;
	    }
	     return super.onKeyDown(keyCode, event);
	}
	//*****************wifi********************//
	 private void openWifi()
	    {
	    	wifiManager.setWifiEnabled(true);
//	    	System.out.println("wifi state -----> "+wifiManager.getWifiState());
	    	Log.i("AL", "wifi state -----> "+wifiManager.getWifiState());
	    	Toast.makeText(this, "正在打开wifi，当前wifi状态为"+wifiManager.getWifiState(), Toast.LENGTH_SHORT).show();
	    }
	    private void closeWifi()
	    {
	    	wifiManager.setWifiEnabled(false);
	    	Log.i("AL", "wifi state -----> "+wifiManager.getWifiState());
	    	Toast.makeText(this, "正在关闭wifi，当前wifi状态为"+wifiManager.getWifiState(), Toast.LENGTH_SHORT).show();
	    }
	    private void checkWifi()
	    {
	    	Log.i("AL", "wifi state -----> "+wifiManager.getWifiState());
	    	Toast.makeText(this, "当前wifi状态为"+wifiManager.getWifiState(), Toast.LENGTH_SHORT).show();
	    }
	//*******************联网******************//

   //******************变量改变的函数***********************//
	    public int getflagForWifiBluetooth()
	    {
	    	return flagForWifiBluetooth;
	    }
	    public void setflagForWifiBluetooth(int flag)
	    {
	    	flagForWifiBluetooth=flag;
	    }
	    
  //*********************TreeView*******************//
	    private void initEvent()
		{
	    	mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener()
			{
				@Override
				public void onClick(Node node, int position)
				{
					if (node.isLeaf())
					{
						Toast.makeText(Main.this, node.getName(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			mTree.setOnItemLongClickListener(new OnItemLongClickListener()
			{

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						final int position, long id)
				{
					// DialogFragment
					final EditText et = new EditText(Main.this);
					new AlertDialog.Builder(Main.this).setTitle("Add Node")
							.setView(et)
							.setPositiveButton("Sure", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{

									if (TextUtils.isEmpty(et.getText().toString()))
										return;
									mAdapter.addExtraNode(position, et.getText()
											.toString());
								}
							}).setNegativeButton("Cancel", null).show();

					return true;
				}
			});
		}

		private void initDatas()
		{
//			mDatas = new ArrayList<FileBean>();
//			FileBean bean = new FileBean(1, 0, "最近");//id pid text
//			mDatas.add(bean);
//			bean = new FileBean(2, 0, "一星期内");
//			mDatas.add(bean);
//			bean = new FileBean(3, 0, "本月");
//			mDatas.add(bean);
//			bean = new FileBean(4, 1, "AL1-1");
//			mDatas.add(bean);
//			bean = new FileBean(5, 1, "AL1-2");
//			mDatas.add(bean);
//			bean = new FileBean(6, 5, "AL1-2-1");
//			mDatas.add(bean);
//			bean = new FileBean(7, 3, "AL3-1");
//			mDatas.add(bean);
//			bean = new FileBean(8, 3, "AL3-2");
//			mDatas.add(bean);
			// initDatas
			mDatas2 = new ArrayList<OrgBean>();
			OrgBean bean2 = new OrgBean(idOfTreeList, 0, "最近");
			idOfTreeList++;
			mDatas2.add(bean2);
			bean2 = new OrgBean(idOfTreeList, 0, "过去7天");
			idOfTreeList++;
			mDatas2.add(bean2);
			bean2 = new OrgBean(idOfTreeList, 0, "一个月内");
			idOfTreeList++;
			mDatas2.add(bean2);
			bean2 = new OrgBean(idOfTreeList, 0, "往前");
			idOfTreeList++;
			mDatas2.add(bean2);

		}
//*************************************************//
		public void setFlagCarmeraIsOpened(boolean flag)
		{
			flagCarmeraIsOpened = flag;
		}
		public boolean isFlageCarmeraIsOpened()
		{
			return flagCarmeraIsOpened;
		}
}
