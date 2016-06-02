package communicate.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import com.example.fender.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class MyServiceView extends SurfaceView implements Callback, Runnable {
	// ����
	private Calendar calendar;
	private boolean flag = false;// �����Ƿ�����Ƶ
	private boolean flag_noce = false;// ������������һ��ͼƬ �����Ƚ��ٵ�ʱ����
	private SurfaceHolder sfh;
	private Canvas canvas;
	private String imageURL = "http://192.168.8.1:8083/?action=snapshot"; // ��Ƶ��ַ
	URL videoURL;
	HttpURLConnection conn;
	private Bitmap mBitmap;
	Bitmap bitmap;
	private Bitmap bmp;// ��ȡ��Դ�ļ��е�ͼ��
	private Paint paint;
	InputStream inputStream;
	private static int mScreenWidth;
	private static int mScreenHeight;
	float myEyesDistence;
	int numberOfFaceDetected;
	Paint myPaint = new Paint();
	private DrawVideo drawVideo;

	public MyServiceView(Context context, AttributeSet attrs)
			throws InterruptedException {
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
		BitmapDrawable bmpDraw = new BitmapDrawable(is);
		bmp = bmpDraw.getBitmap();
	}

	/**
	 * ��ʼ�����������Ļ����ֵ
	 */
	private void ScreenValue() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels / 2;
	}

	class DrawVideo extends Thread {
		private boolean theadSuspend;

		public void suspendThread() {
			theadSuspend = true;
		}

		public void resumeThread() {
			synchronized (this) {
				theadSuspend = false;
				notify();
			}
		}

		public DrawVideo() {

		}

		public void run() {
			while (true) {// �ӷ���������ȡͼƬ��ʾ
				try {
					if (flag) {
						getView();
					} else {
						if (flag_noce)// ��ͼһ��
						{
							getView();
							flag_noce = false;
						} else {
							this.sleep(500);
//							drawInitialImage();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}

		// �ӷ�������ȡһ��ͼƬ
		private void getView() throws IOException {
			videoURL = new URL(imageURL);
			conn = (HttpURLConnection) videoURL.openConnection();
			conn.connect();
			inputStream = conn.getInputStream(); // ��ȡ��
			BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();// ����ԭͼ����ֵ
			BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeStream(inputStream, null,
					BitmapFactoryOptionsbfo);// �ӻ�ȡ�����й�����BMPͼ��
			mBitmap = Bitmap.createScaledBitmap(bitmap, mScreenWidth,
					mScreenHeight, true);
			canvas = sfh.lockCanvas(); // ��������
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(mBitmap, 0, 0, null);// ��BMPͼ���ڻ�����
			drawText();
			sfh.unlockCanvasAndPost(canvas);// ����һ��ͼ�񣬽�������
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawVideo = new DrawVideo();
		drawVideo.start();
	}

	public void start() throws InterruptedException {
//			this.notify();
			flag = true;
	}

	public void suspend() throws InterruptedException {
//			this.wait();
			flag = false;
	}

	@Override
	public void run() {
		while (true) {
			try {
				videoURL = new URL(imageURL);
				conn = (HttpURLConnection) videoURL.openConnection();
				conn.connect();
				inputStream = conn.getInputStream(); // ��ȡ��
				bitmap = BitmapFactory.decodeStream(inputStream);// �ӻ�ȡ�����й�����BMPͼ��
				mBitmap = Bitmap.createScaledBitmap(bitmap, mScreenWidth,
						mScreenHeight, true);
				canvas = sfh.lockCanvas();
				canvas.drawColor(Color.WHITE);
				canvas.drawBitmap(mBitmap, 0, 0, null);// ��BMPͼ���ڻ�����
				sfh.unlockCanvasAndPost(canvas);// ����һ��ͼ�񣬽�������
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.disconnect();
			}
		}
	}

	private void drawInitialImage() {

		canvas = sfh.lockCanvas();
		canvas.drawPosText("��Ƶ����Ѿ�ֹͣ", new float[] { 10 * 4,
				80 * 3, // ��һ����ĸ������10,10
				20 * 4,
				80 * 3, // �ڶ�����ĸ������20,20
				30 * 4,
				80 * 3, // ....
				40 * 4, 80 * 3, 50 * 4, 80 * 3, 60 * 4, 80 * 3, 70 * 4, 80 * 3,
				80 * 4, 80 * 3

		}, paint);

		canvas.drawBitmap(bmp, 20, 80, null);// ��BMPͼ���ڻ�����
		sfh.unlockCanvasAndPost(canvas);// ����һ��ͼ�񣬽�������
	}

	private void drawText() {
		Path path = new Path(); // ����һ��·��
		canvas.drawTextOnPath(getTime(), path, 20, 60, paint);
	}

	public void drowblack() {

	}

	/**
	 * ���淽�� ����ɹ����� true
	 * 
	 * @return boolean
	 * @throws InterruptedException
	 */
	public boolean saveBitmap() throws InterruptedException {
		if(!flag)
		{
			return false;
		}
		Bitmap bmpBitmap = mBitmap;
		Calendar calendar = Calendar.getInstance();
		String fileName = "E-Shark���" + calendar.getTime().getTime();
		File f = new File("/sdcard/"+"E-Shark", fileName + ".jpg");
		File f_dir = new File("/sdcard/"+"E-Shark");
		if (!f_dir.exists()&&!f_dir.isDirectory())
		{
			if(f_dir.mkdirs())
			{
				Log.i("AL","�Ѿ�����·��"+Environment.getExternalStorageDirectory().getPath()+"E-Shark");
			}else
			{
				Log.i("AL","����·��fails"+Environment.getExternalStorageDirectory().getPath()+"E-Shark");
			}
		}
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bmpBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			Log.i("AL", "FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("AL", "IOException");
			e.printStackTrace();
		}
		return false;
	}

	private String getTime() {
		String str = "E-Shark���";
		calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int mouth = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int date = calendar.get(Calendar.DAY_OF_WEEK);
		int sec = calendar.get(Calendar.SECOND);
		str = str + " " + mouth + "��" + day + "��";
		if (hour >= 12) {
			str = str + "PM" + " ";
		} else {
			str = str + "AM" + " ";
		}
		String hourString;
		String minuteString;
		String secString;
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
		str = str + "" + hourString + ":" + minuteString + ":" + secString;
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
		str = str + xinqiString;
		return str;
	}

}
