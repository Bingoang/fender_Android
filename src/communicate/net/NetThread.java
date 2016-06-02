package communicate.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import treeview.adapter.SimpleTreeListViewAdapter;
import treeview.bean.OrgBean;

import com.example.fender.R;

import activitis.Main;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
/**
 * 用于创建�?个连接服务器的线程，
 * 可以接受信息和发送信息，
 * 传入�?个Handler可以用于更新状�?�，
 * @author AL
 *
 */
public class NetThread extends Thread
{
	public static final String IP= "192.168.8.199";
	Main main = null;
	private Handler handler;
	private String msg = null;
//	private DatagramSocket UDPsocket;
	//private Vector<Vector<String>> recordS;
	public NetThread(Handler handler,Main main) {
		this.handler = handler;
		this.main = main;
	}
	private Socket ClientSocket;
	 @Override
	public void run() {
		// TODO Auto-generated method stub
		
		 if(link())//如果连接成功 那么就更新信�?
		 {
			 handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(main, "连接成功", Toast.LENGTH_SHORT).show();
				}
			});
			 //如果连接成功�? 将状态设�?2
			main.setflagForWifiBluetooth(2);//设为wifi模式
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
						Toast.makeText(main, "连接超时，请�?查网络连�?", Toast.LENGTH_SHORT).show();
						main.setflagForWifiBluetooth(0);//设回默认状�??
					}
				});
		}
		 
		
	}
	 private void initialSocket() throws IOException {
		// TODO Auto-generated method stub 
		 send("phone is connected");
	     receiveMsg(ClientSocket);//得到输出流并�?始监控网络信�?
	}
	public void send(String msg) throws IOException
	 {
		 BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(ClientSocket.getOutputStream()));  
	     writer.write((msg.replaceAll("\n", ""))+"\n");  
	     writer.flush();  
	 }

	private boolean link()
	{
		try {
			ClientSocket = new Socket(IP, 8089);
//			UDPsocket=new DatagramSocket(8089);
			
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	 private void receiveMsg(Socket socket) throws IOException{  
	        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));  
	        while (true)
	        {
        	byte[] data =new byte[1024];//创建字节数组，指定接收的数据包的大小
    		DatagramPacket packet=new DatagramPacket(data, data.length);
    		//3.接收客户端发送的数据
//    		System.out.println("****服务器端已经启动，等待客户端发�?�数�?");
    		Log.i("AL", "****手机监听UDP数据已经启动，等待客户端发�?�数�?");
//    		UDPsocket.receive(packet);//此方法在接收到数据报之前会一直阻�?
//    		//4.读取数据
//    		msg=new String(data, 0, packet.getLength());	
	        msg=reader.readLine();  
	        Log.i("AL", "--->"+msg);
	        handler.post(new Runnable() {
				@Override
				public void run() {
					//得到的数据在这里
//					Toast.makeText(main, msg, Toast.LENGTH_LONG).show();
					if(!setList(msg))
					{
						setStation(msg);
					}
					
				}
			});
	        
	        
	        }
	    }
		
		
	
		private void setStation(String mes) {
			
			char[] mesg = mes.toCharArray();
			String mesgStr = null;
			for(int i=0;i<mesg.length;i++)
			{
				switch (mesg[i]) {
				case 's'://门状�?
					if((i+1)<=mesg.length)//判断是否会越�?
					{
						mesgStr  = mesg[i+1]+"";
						System.out.println("门状�?:"+mesgStr);
						setDoorStation(mesgStr);
					}
					break;
				case 'T'://CPU温度
					if(i+2<=mesg.length)
					{
						mesgStr = mesg[i+1]+""+mesg[i+2]+"";
						System.out.println("CPU温度�?"+mesgStr);
						setCPUtemper(mesgStr);
					}
					break;
				case 't'://室温
					if(i+4<=mesg.length)
					{
						mesgStr = mesg[i+1]+""+mesg[i+2]+""+mesg[i+3]+""+mesg[i+4]+"";
						System.out.println("室温�?"+mesgStr);
						setTemper(mesgStr);
					}
					break;
				case 'o'://操作
					if((i+2)<=mesg.length)//判断是否会越�?
					{
						mesgStr = mesg[i+1]+ (mesg[i+2]+"");
						System.out.println("操作编号:"+mesgStr);
					}
				case 'h'://操作
					if((i+2)<=mesg.length)//判断是否会越�?
					{
						mesgStr = mesg[i+1]+"";
						if(Integer.valueOf(mesgStr)==0)
						{
							setHaveManOutSide(false);//门外没人
						}else {
							setHaveManOutSide(true);//门外有人
						}
						System.out.println("操作编号:"+mesgStr);
					}
					break;
				default:
					break;
				}
			}	
		}
		

		private void setTemper(String mesgStr) {
			main.temputerTextView.setText(Integer.valueOf(mesgStr)*1.0/10+"°");
			
		}
		private void setHaveManOutSide(boolean HaveMan)
		{
			if(HaveMan)
				{
					main.fanghuotishiTextView.setText("门口状�?�：门外有人！！");
				}else {
					main.fanghuotishiTextView.setText("门口状�?�：门外无人�?");
				}
		}
		private void setCPUtemper(String mesgStr) {
			
			main.wasiTextView.setText("CPU温度:"+mesgStr);
			Log.i("AL","CPU温度:"+ mesgStr);
		}
		private void setDoorStation(String mesgStr) {
			Log.i("AL", Integer.valueOf(mesgStr)+"");	
			switch (Integer.valueOf(mesgStr)) {
			case 0://门关�?
				main.sysStationTextView.setText("门状�?: 关着�?");
				main.doorStationImageView.setImageResource(R.drawable.door_closed);
				break;
			case 1://虚掩 非警戒状�?
				main.sysStationTextView.setText("门状�?: 虚掩�?，警戒状态未�?启，请注意�??");
				main.doorStationImageView.setImageResource(R.drawable.door_almose_closed);
				break;
			case 2://门开�? 
				main.sysStationTextView.setText("门状�?: �?�?�?");
				main.doorStationImageView.setImageResource(R.drawable.door);
				break;
			case 3://正在报警
				
				break;
			case 4://虚掩 警戒状�??
				main.sysStationTextView.setText("门状�?: 虚掩�?，警戒状态已�?启！");
				main.doorStationImageView.setImageResource(R.drawable.door_almose_closed);
				break;

			default:
				break;
			}
		}
		private boolean setList(String msg) {
			//将得到的String进行分解
			Vector<String[]> records=new Vector<String[]>();
			String[] recordsTemp ;
			records=new Vector<String[]>();//�?0
			recordsTemp = msg.split("@");
			for(int i=0;i<recordsTemp.length;i++)
			{
				records.add(recordsTemp[i].split("&"));
			}
			Log.i("AL", "NetThread SetList 附�?�完�?");
			String[] str = records.get(0);
			if(!str[0].equals("Records"))//如果不是用于记录的信息则�?�?
			{
				return false;
			}
			main.mDatas2 = initDatas();
			for(String[] strs :records)
			{
				if(strs[0] =="" || strs==null||!strs[0].equals("Records"))
				{
					Log.i("AL", "NetThread SetList 条件不满足�?��??�?");
					Log.i("AL", strs[0]);
					return false;
				}
				int year = Integer.parseInt(strs[2].substring(0, 4));
				int month = Integer.parseInt(strs[2].substring(4, 6));
				int day = Integer.parseInt(strs[2].substring(6, 8));
				int hour = Integer.parseInt(strs[2].substring(8,10));
				Calendar calendar=Calendar.getInstance();
				int yearNow = calendar.get(Calendar.YEAR);
				int monthNow = calendar.get(Calendar.MONTH)+1;
				int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
				int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
				// 1�?�? 2�?星期 3�?个月 4�?�?
				int time = (year - 2000)*365 + month*30+day;
				int timeNow = (yearNow -2000)*365 +monthNow*30 + dayNow;
				if(timeNow - time <=2)
				{
					OrgBean bean2 = new OrgBean(main.idOfTreeList, 1 , strs[1]);
					main.idOfTreeList++;
					main.mDatas2.add(bean2);
					Log.i("AL", "NetThread SetList �?近项加入");
					Log.i("AL", "year: "+ year+" month: "+ month +" day: "+ day+" hour: "+ hour+" time"+time);
					Log.i("AL", "yearNow: "+ yearNow+" monthNow: "+ monthNow +" dayNow: "+ dayNow+" hourNow: "+ hourNow+" timeNow"+timeNow);
					
				}else if(timeNow - time <= 7)
				{
					OrgBean bean2 = new OrgBean(main.idOfTreeList, 2 , strs[1]);
					main.idOfTreeList++;
					main.mDatas2.add(bean2);
					Log.i("AL", "NetThread SetList 星期项加�?");
				}
				else if(timeNow - time <= 30)
				{
					OrgBean bean2 = new OrgBean(main.idOfTreeList, 3 , strs[1]);
					main.idOfTreeList++;
					main.mDatas2.add(bean2);
					Log.i("AL", "NetThread SetList 月项加入");
				}
				else 
				{
					OrgBean bean2 = new OrgBean(main.idOfTreeList, 4 , strs[1]);
					main.idOfTreeList++;
					main.mDatas2.add(bean2);
					Log.i("AL", "NetThread SetList �?前项加入");
				}
				
				
			}
			try
			{
				main.mAdapter = new SimpleTreeListViewAdapter<OrgBean>(main.mTree, main,
						main.mDatas2, 0);
				main.mTree.setAdapter(main.mAdapter);
				
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return true;
			
		}
		private List<OrgBean> initDatas()
		{
			List<OrgBean> mDatas2 =new ArrayList<OrgBean>();
			main.idOfTreeList = 1;
			// initDatas
			OrgBean bean2 = new OrgBean(main.idOfTreeList, 0, "�?�?");
			main.idOfTreeList++;
			mDatas2.add(bean2);
			bean2 = new OrgBean(main.idOfTreeList, 0, "过去7�?");
			main.idOfTreeList++;
			mDatas2.add(bean2);
			bean2 = new OrgBean(main.idOfTreeList, 0, "�?个月�?");
			main.idOfTreeList++;
			mDatas2.add(bean2);
			bean2 = new OrgBean(main.idOfTreeList, 0, "�?�?");
			main.idOfTreeList++;
			mDatas2.add(bean2);
			return mDatas2;
		}
		
}