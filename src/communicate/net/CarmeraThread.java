package communicate.net;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import activitis.Main;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class CarmeraThread extends Thread{
	
	Main main = null;
	private Bitmap bmp = null;
	private Handler handler;
	private String msg = null;
	private Socket ClientSocket;
	public CarmeraThread(Handler handler, Main main) {
		this.handler = handler;
		this.main = main;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(!main.isFlageCarmeraIsOpened())
		{
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(main, "����ͷδ���������ȴ�����ͷ", Toast.LENGTH_SHORT).show();
				}
			});
			return;
		}
		if(link())//������ӳɹ� ��ô�͸�����Ϣ
		 {
			 handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(main, "��Ƶ���ӳɹ�", Toast.LENGTH_SHORT).show();
				}
			});
			 //������ӳɹ��� ��״̬��Ϊ2
			//main.setflagForWifiBluetooth(2);//��Ϊwifiģʽ
			try {
				initialSocket();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		 }
		 else {
			 handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(main, "���ӳ�ʱ��������������", Toast.LENGTH_SHORT).show();
						main.setflagForWifiBluetooth(0);//���Ĭ��״̬
					}
				});
		}
	}
	private boolean link()
	{
		try {
			ClientSocket = new Socket(NetThread.IP, 8089);
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	 private void initialSocket() throws IOException {
		// TODO Auto-generated method stub 
		 send("to computer&phone get carmera");
	     receiveMsg(ClientSocket);//�õ����������ʼ���������Ϣ
	}
	 private void receiveMsg(Socket socket) throws IOException{  
		 	
//	        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));  
//	       
//	        msg=reader.readLine();  
		
			 
		 
			 	Log.i("AL", "������պ���");
		        DataInputStream dataInput = new DataInputStream(socket.getInputStream()); 
	            int size = dataInput.readInt();    
	            Log.i("AL", "�յ����ݣ�size��"+size);
	            byte[] data = new byte[size];    
	            int len = 0;    
	            int l=0;
	            while (len < size) {  
	            	if(l<50)
	            	{
	            		Log.i("AL", "ѭ��"+len);
	            		l++;
	            	}
	                len += dataInput.read(data, len, size - len); 
	            }    
	            Log.i("AL", "ѭ�����");
	            ByteArrayOutputStream outPut = new ByteArrayOutputStream();    
	            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);    
	            bmp.compress(CompressFormat.PNG, 100, outPut);    
	            
		        handler.post(new Runnable() {
					@Override
					public void run() {
						//�õ�������������
						Toast.makeText(main, "�յ�ͼƬ", Toast.LENGTH_LONG).show();
						 main.carmeraImageView.setImageBitmap(bmp);
					}
				});
		
		        socket.close(); 
	        
		 
	    }
		public void send(String msg) throws IOException
		 {
			 BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(ClientSocket.getOutputStream()));  
		     writer.write((msg.replaceAll("\n", "")+"\n"));  
		     writer.flush();  
		 }

}
