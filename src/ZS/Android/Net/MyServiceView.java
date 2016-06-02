package ZS.Android.Net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import com.example.fender.R;

import android.R.bool;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class MyServiceView extends SurfaceView implements Callback, Runnable {
	//声明
	private Calendar calendar;
	private boolean flag = false;//控制是否开启视频 
	private boolean flag_noce = false;//控制用来接受一张图片 流量比较少的时候用
	private SurfaceHolder sfh;
	private Canvas canvas;
	private String imageURL = "http://192.168.8.1:8083/?action=snapshot";		//视频地址
	URL videoURL;
	HttpURLConnection conn;
	private Bitmap mBitmap;
	Bitmap bitmap;
	private Bitmap bmp;//读取资源文件中的图像
	private Paint paint;
	InputStream inputStream;
	private static int mScreenWidth; 
    private static int mScreenHeight;
    float myEyesDistence;
    int numberOfFaceDetected;
    Paint myPaint = new Paint();	
    private DrawVideo drawVideo;
	public MyServiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ScreenValue();
		paint = new Paint();
		paint.setAntiAlias(true);
		myPaint.setColor(Color.GREEN);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(3);
		sfh = this.getHolder();
  	  	sfh.addCallback(this);
  	  	this.setKeepScreenOn(true);
  	  	setFocusable(true);
  	  	this.getWidth();
  	  	this.getHeight();
  	  	
  	  	paint.setTextSize(30);
	  	Resources r = this.getContext().getResources();
	  	InputStream is = r.openRawResource(R.drawable.tab2_carmera);
	  	BitmapDrawable  bmpDraw = new BitmapDrawable(is);
	  	bmp = bmpDraw.getBitmap();
//	  	drawInitialImage();//初始化的图片
	}
	
	/**
	 * 初始化——获得屏幕像素值
	 */
	private void ScreenValue() {
		DisplayMetrics dm = new  DisplayMetrics();     
		dm = getResources().getDisplayMetrics();
	    mScreenWidth = dm.widthPixels; 
	    mScreenHeight = dm.heightPixels/2;		
	}

	class DrawVideo extends Thread{
		 private boolean theadSuspend;

		    public void suspendThread() {
		    	theadSuspend = true;
		    }

		    public void resumeThread() {
		            synchronized(this) {
		            	theadSuspend = false;
		                    notify();
		            }
		    }
		public DrawVideo(){
			
		}
		
		public void run() {
			while(true){//从服务器不断取图片显示
				try {
				
					if(flag)
					{	
						 jietu();
						 
					}else {
						if(flag_noce)//截图一张
						{
							 jietu();
							 
							 flag_noce=false;
						}else {
							this.sleep(500);
							drawInitialImage();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}finally{
					if(conn!=null)
					{
						conn.disconnect();
					}
						
				}
			}
		}
		
		//从服务器获取一张图片
		private void jietu() throws IOException
		{
			videoURL = new URL(imageURL);
			conn = (HttpURLConnection) videoURL.openConnection();
			conn.connect();
			inputStream = conn.getInputStream();		//	获取流
			BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();// 配置原图缩放值
			BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeStream(inputStream,null,BitmapFactoryOptionsbfo);//从获取的流中构建出BMP图像
			mBitmap = Bitmap.createScaledBitmap(bitmap, mScreenWidth, mScreenHeight, true);
			canvas = sfh.lockCanvas();   //锁定画布
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(mBitmap, 0,0, null);//把BMP图像画在画布上
//			canvas.drawText(getTime(), 0, 0, paint);
			drawText();
			sfh.unlockCanvasAndPost(canvas);//画完一副图像，解锁画布
		}
	}	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawVideo =  new DrawVideo();
		drawVideo.start();
//		drawVideo.suspendThread();

	}	
	public void start() {
		flag=true;
//		if(drawVideo==null)
//		{
//			drawVideo =  new DrawVideo();
//			drawVideo.start();
//		}
		
	}
	public void suspend() {
//		if(drawVideo!=null)
//		{
////			drawVideo.stop();
//			drawVideo=null;
//		}
		flag = false;
	}

	@Override
	public void run() {
		while(true){
			try {
				videoURL = new URL(imageURL);
				conn = (HttpURLConnection) videoURL.openConnection();
				conn.connect();
	         	inputStream = conn.getInputStream(); //获取流
	         	bitmap = BitmapFactory.decodeStream(inputStream);//从获取的流中构建出BMP图像
	         	mBitmap = Bitmap.createScaledBitmap(bitmap, mScreenWidth, mScreenHeight, true);
	            canvas = sfh.lockCanvas();
	            canvas.drawColor(Color.WHITE);
	            canvas.drawBitmap(mBitmap, 0,0, null);//把BMP图像画在画布上
//	            canvas.drawText(getTime(), 0, 0, paint);
	            
	            sfh.unlockCanvasAndPost(canvas);//画完一副图像，解锁画布 
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				conn.disconnect();
			}
		}			
	}
	private void drawInitialImage()
	{
		
		canvas = sfh.lockCanvas();
//		 canvas.drawText("视频已经停止", 0, 40, 0, 30, null);
		  canvas.drawPosText("视频监控已经停止", new float[]{   
		            10*4,80*3, //第一个字母在坐标10,10   
		            20*4,80*3, //第二个字母在坐标20,20   
		            30*4,80*3, //....   
		            40*4,80*3,   
		            50*4,80*3,   
		            60*4,80*3,   
		            70*4,80*3,   
		            80*4,80*3   
		             
		    }, paint);  
		 
         canvas.drawBitmap(bmp, 20,80, null);//把BMP图像画在画布上
         sfh.unlockCanvasAndPost(canvas);//画完一副图像，解锁画布 
	}
	private void drawText()
	{
		 Path path = new Path(); //定义一条路径   
//         path.moveTo(0, 0); //移动到 坐标10,10   
//         path.lineTo(50, 60);   
//         path.lineTo(200,80);   
//         path.lineTo(10, 10);                                                                                                                    
//       canvas.drawPath(path, paint);   
         canvas.drawTextOnPath(getTime(), path, 20, 60, paint);   
	}
    public void drowblack(){
    	
    }
    /** 保存方法
     * 保存成功返回 true 
     * @return boolean
     * @throws InterruptedException 
     */
	 public boolean saveBitmap() throws InterruptedException {
     
	 if(mBitmap == null||flag==false)//如果还没有截图 则返回false
	 {
		 flag_noce=true;
	 }

//	 if(mBitmap == null)
//	 {
//		 return false;
//	 }
	 
      Bitmap bmpBitmap=mBitmap;
      Calendar calendar=Calendar.getInstance();
      String fileName = "E-Shark监控"+calendar.getTime().getTime();
	  File f = new File("/sdcard/EShark", fileName+".jpg");
	  if (f.exists()) {
	   f.delete();
	  }
	  try {
	   FileOutputStream out = new FileOutputStream(f);
	   bmpBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
	   out.flush();
	   out.close();
	   return true;
//	   Log.i(TAG, "已经保存");
	  } catch (FileNotFoundException e) {
	   // TODO Auto-generated catch block
		 Log.i("AL", "FileNotFoundException");
	   e.printStackTrace();
	   
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
		  Log.i("AL", "IOException");
	   e.printStackTrace();
	  }
	  return false;
	 }
	 
	 private String getTime()
	    {
	    	String str = "E-Shark监控";
//	    	int year=calendar.get(Calendar.YEAR);
//	    	str=str+""+year+"年";
	    	calendar=Calendar.getInstance();
	    	int hour=calendar.get(Calendar.HOUR_OF_DAY);
	    	hour=calendar.get(Calendar.HOUR);
	    	int minute=calendar.get(Calendar.MINUTE);
	    	int mouth=calendar.get(Calendar.MONTH)+1;
	    	int day=calendar.get(Calendar.DAY_OF_MONTH);
	    	int date=calendar.get(Calendar.DAY_OF_WEEK);
	    	int sec = calendar.get(Calendar.SECOND);
	    	str = str+" "+mouth+"月"+day+"日";
	    	if(hour>=12)
	    	{
	    		str=str+"PM"+" ";
//	    		amPmTextView.setText("PM");
	    	}else {
	    		str=str+"AM"+" ";
			}
	    	String hourString;
	    	String minuteString;
	    	String secString;
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
//	    	timeTextView.setText(""+hourString+":"+minuteString+":"+secString);
	    	str = str+""+hourString+":"+minuteString+":"+secString;
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
//	    	dayTextView.setText(xinqiString);
	    	str = str + xinqiString;
	    	return str;
	    }
	 
}
