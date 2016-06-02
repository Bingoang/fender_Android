package ZS.Android.Bluetooth;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

public class ConnectThread extends Thread{
	private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private Activity mainActivity;
    private BluetoothAdapter mAdapter;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//更改这里可以不同的链接
    public ConnectThread(BluetoothDevice device,BluetoothAdapter mAdapter,Activity mainActivity) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
    	this.mainActivity = mainActivity;
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.mAdapter=mAdapter;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
       mAdapter.cancelDiscovery();
     //  Toast.makeText(mainActivity, "开始链接，取消扫描", Toast.LENGTH_SHORT).show();
        Log.v("al", "取消扫描");
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
        	Log.v("al", "开始链接");
            mmSocket.connect();
        //    Toast.makeText(activity, "链接成功", Toast.LENGTH_SHORT).show();
            Log.v("al", "链接成功");
           // Toast.makeText(mainActivity, "链接成功", Toast.LENGTH_SHORT).show();
            iConnectThread.getConnectThreadSocket(mmSocket);
           
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
                Log.v("al", "链接失败");
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
}
    
    
    private IConnectThread iConnectThread ;
	public void addIConnectThread(IConnectThread icConnectThread)
	{
		this.iConnectThread = icConnectThread;
	}
    public interface IConnectThread
    {
    	void getConnectThreadSocket(BluetoothSocket mmSocket);
    	
    }

    
}
