package communicate.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnectedThread extends Thread{
	private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
//    private Looper looper = Looper.getMainLooper();//得到主线程的Looper  用接口代替 这种方法不用
//    private Handler mHandler = new Handler(looper);
    public ConnectedThread(BluetoothSocket socket,Activity mainActivity) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // 缓存 4K
        int bytes; // bytes returned from read()
        Log.i("AL", "进入ConnectedThread进程");
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
            	Log.i("AL", "读取数据");
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI Activity
                Log.i("AL", "向主机发送："+buffer[0]);
                icConnectedThread.getConnectedThreadReceiveBuffer(buffer);
//                mHandler.obtainMessage(1, bytes, -1, buffer[0])
//                        .sendToTarget();
            } catch (IOException e) {
            	Log.v("AL", "意外退出蓝牙接收");
                break;
            }
        }
    }

    /* Call this from the main Activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
        	Log.i("AL", "我运行到了ConnectedThread.sendMassage");
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main Activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
    private IConnectedThread icConnectedThread;
    public void addIConnectedThread(IConnectedThread icConnectedThread) {
		this.icConnectedThread = icConnectedThread;
	}
    public interface IConnectedThread 
    {
    	void getConnectedThreadReceiveBuffer(byte[] buffer);
    }
    
}

