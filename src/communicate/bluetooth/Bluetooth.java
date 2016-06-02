package communicate.bluetooth;

import java.util.Iterator;
import java.util.Set;

import communicate.bluetooth.ConnectThread.IConnectThread;
import communicate.bluetooth.ConnectedThread.IConnectedThread;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Bluetooth implements IConnectedThread,IConnectThread{
	private BluetoothAdapter adapter ;//����������
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
			//Toast.makeText(mainActivity, "�������豸", Toast.LENGTH_SHORT).show();
			if(adapter.isEnabled())
			{
				//Toast.makeText(mainActivity, "�����Ѵ�", Toast.LENGTH_SHORT).show();

			}else {
				//Toast.makeText(mainActivity, "����δ��", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//����һ��intent���󣬸ö�����������һ��activity��ʾ������
				mainActivity.startActivity(intent);
			}
			Set<BluetoothDevice> devices = adapter.getBondedDevices();
			
			if(devices.size() > 0)
			{
				String devicesNameString = "��������豸��";
				String devicesMac = "��������豸MAC��ַ ��";
				for(Iterator iterator = devices.iterator();iterator.hasNext();)
				{
					BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
					devicesNameString = devicesNameString + bluetoothDevice.getName()+"   ";
					devicesMac = devicesMac + bluetoothDevice.getAddress()+";  ";
				}
				Toast.makeText(mainActivity, devicesNameString+devicesMac, Toast.LENGTH_SHORT).show();//��ʾ����Եĵ�ַ
				
			}
			
			
		}else {
			Toast.makeText(mainActivity, "û�������豸", Toast.LENGTH_SHORT).show();
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
			//�ο������÷�
//			String devicesNameString = "��������豸��";//\\
//			String devicesMac = "��������豸MAC��ַ ��";
//			for(Iterator iterator = devices.iterator();iterator.hasNext();)
//			{
//				BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
//				devicesNameString = devicesNameString + bluetoothDevice.getName()+"   ";
//				devicesMac = devicesMac + bluetoothDevice.getAddress()+";  ";
//			}
//			Toast.makeText(mainActivity, devicesNameString+devicesMac, Toast.LENGTH_SHORT).show();//��ʾ����Եĵ�ַ
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
		//��һ����ֵ�Դ����intent�� ��Ҫ����ָ���ɼ�״̬ʱ�� ���ó���300�뽫��300�����
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, seconds);
		mainActivity.startActivity(discoverableIntent);
	}
	public void connectDevice (BluetoothDevice bluetoothDevice,String MAC_BlueTooth)
	{
		Toast.makeText(mainActivity, bluetoothDevice.getName()+"="+bluetoothDevice.getAddress(), Toast.LENGTH_SHORT).show();
		if(bluetoothDevice.getAddress().equals(MAC_BlueTooth))
		{
			connectThread = new ConnectThread(bluetoothDevice, adapter,mainActivity);//��ʼ����
			connectThread.addIConnectThread(this);
			connectThread.start();
		}
		
		//Toast.makeText(mainActivity, "��ʼ����", Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
	public void getConnectThreadSocket(BluetoothSocket mmSocket) {//���ӳɹ��� ��ʼ��������
		// TODO Auto-generated method stub
		bluetoothSocket = mmSocket;
		 Log.i("AL", "�õ�socket");
		connectedThread = new ConnectedThread(bluetoothSocket,mainActivity);
		connectedThread.addIConnectedThread(this);
		connectedThread.start();//��ʼ�ܹ��������ݣ���������
	}
	@Override
	public void getConnectedThreadReceiveBuffer(byte[] buffer) {
		Log.i("AL", "�����Ѿ����䵽��Bluetooth");
		iBluetooth.getConnectedThreadReceiveBuffer(buffer);//�����յ������������洫
	}
	public void breakLink()//�Ͽ�����
	{
		connectThread.cancel();
	}
	public void sendMassage(byte[] bytes)//��������
	{
		Log.i("AL", "�����е���Bluetooth.sendMassage");
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
