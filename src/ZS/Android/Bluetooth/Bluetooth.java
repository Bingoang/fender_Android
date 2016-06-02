package ZS.Android.Bluetooth;

import java.util.Iterator;
import java.util.Set;

import ZS.Android.Bluetooth.ConnectThread.IConnectThread;
import ZS.Android.Bluetooth.ConnectedThread.IConnectedThread;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Bluetooth implements IConnectedThread,IConnectThread{
	private BluetoothAdapter adapter ;//蓝牙适配器
	private ConnectedThread connectedThread;
	private ConnectThread connectThread;
	private Activity mainActivity;
	private BluetoothSocket bluetoothSocket;
//	private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
	public Bluetooth(Activity mainActivity,BluetoothAdapter adapter)
	{
		this.adapter = adapter;
		this.mainActivity = mainActivity;
	}
	
	
	public void openBluetooth ()
	{
		
		if(adapter!=null)
		{
			//Toast.makeText(mainActivity, "有蓝牙设备", Toast.LENGTH_SHORT).show();
			if(adapter.isEnabled())
			{
				//Toast.makeText(mainActivity, "蓝牙已打开", Toast.LENGTH_SHORT).show();

			}else {
				//Toast.makeText(mainActivity, "蓝牙未打开", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//创建一个intent对象，该对象用于启动一个activity提示打开蓝牙
				mainActivity.startActivity(intent);
			}
			Set<BluetoothDevice> devices = adapter.getBondedDevices();
			
			if(devices.size() > 0)
			{
				String devicesNameString = "现以配对设备：";
				String devicesMac = "现以配对设备MAC地址 ：";
				for(Iterator iterator = devices.iterator();iterator.hasNext();)
				{
					BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
					devicesNameString = devicesNameString + bluetoothDevice.getName()+"   ";
					devicesMac = devicesMac + bluetoothDevice.getAddress()+";  ";
				}
				Toast.makeText(mainActivity, devicesNameString+devicesMac, Toast.LENGTH_SHORT).show();//显示已配对的地址
				
			}
			
			
		}else {
			Toast.makeText(mainActivity, "没有蓝牙设备", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void closeBluetooth()
	{
		adapter.disable();
	}
	public boolean startDiscovery()
	{
		return adapter.startDiscovery();
	}
	public void cancelDiscovery()
	{
		adapter.cancelDiscovery();
	}
	public Set<BluetoothDevice> getMatchedDevice()
	{
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		if(devices.size() > 0)
		{
			//参考迭代用法
//			String devicesNameString = "现以配对设备：";//\\
//			String devicesMac = "现以配对设备MAC地址 ：";
//			for(Iterator iterator = devices.iterator();iterator.hasNext();)
//			{
//				BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
//				devicesNameString = devicesNameString + bluetoothDevice.getName()+"   ";
//				devicesMac = devicesMac + bluetoothDevice.getAddress()+";  ";
//			}
//			Toast.makeText(mainActivity, devicesNameString+devicesMac, Toast.LENGTH_SHORT).show();//显示已配对的地址
			return devices;
		}
		else 
		{
			return null;
		}
	}
	public void setDiscoverableSeconds(int seconds)
	{
		
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		//将一个键值对存放入intent中 主要用于指定可见状态时间 设置超过300秒将以300秒计算
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, seconds);
		mainActivity.startActivity(discoverableIntent);
	}
	public void connectDevice (BluetoothDevice bluetoothDevice,String MAC_BlueTooth)
	{
		Toast.makeText(mainActivity, bluetoothDevice.getName()+"="+bluetoothDevice.getAddress(), Toast.LENGTH_SHORT).show();
		if(bluetoothDevice.getAddress().equals(MAC_BlueTooth))
		{
			connectThread = new ConnectThread(bluetoothDevice, adapter,mainActivity);//开始链接
			connectThread.addIConnectThread(this);
			connectThread.start();
		}
		
		//Toast.makeText(mainActivity, "开始链接", Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
	public void getConnectThreadSocket(BluetoothSocket mmSocket) {//链接成功后 开始接收数据
		// TODO Auto-generated method stub
		bluetoothSocket = mmSocket;
		 Log.i("AL", "得到socket");
		connectedThread = new ConnectedThread(bluetoothSocket,mainActivity);
		connectedThread.addIConnectedThread(this);
		connectedThread.start();//开始能够接收数据，发送数据
	}
	@Override
	public void getConnectedThreadReceiveBuffer(byte[] buffer) {
		Log.i("AL", "数据已经传输到了Bluetooth");
		iBluetooth.getConnectedThreadReceiveBuffer(buffer);//将接收到的数据往上面传
	}
	public void breakLink()//断开连接
	{
		connectThread.cancel();
	}
	public void sendMassage(byte[] bytes)//发送数据
	{
		Log.i("AL", "我运行到了Bluetooth.sendMassage");
		connectedThread.write(bytes);
	}
	 private IBluetooth iBluetooth ;
		public void addIBluetooth(IBluetooth iBluetooth)
		{
			this.iBluetooth = iBluetooth;
		}
	    public interface IBluetooth
	    {
	    	void getConnectedThreadReceiveBuffer(byte[] buffer); 
	    }
}
