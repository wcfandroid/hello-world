package com.wangjialin.internet.multipleThreadContinuableDownloaderForAndroid4;

import java.io.File;

import com.wangjialin.internet.service.downloader.DownloadProgressListener;
import com.wangjialin.internet.service.downloader.FileDownloader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MultipleThreadContinuableDownloaderForAndroid4Activity extends
		Activity {
	private static final int PROCESSING = 1;// 正在下载实时数据传输Message标志
	private static final int FAILURE = -1;// 下载失败时的Message标志
	private EditText pathText;// 下载输入文本框
	private TextView resultView;// 实现进度显示百分比文本框
	private Button downloadButton;// 下载按钮，可以触发下载事件
	private Button stopbutton;// 停止按钮，可以停止下载
	private ProgressBar progressBar;// 下载进度条，实时图形化的显示进度信息
	// handler对象的作用是用于往创建Handler对象所在的线程所绑定的消息队列发送信息并处理信息
	private Handler handler = new UIHandler();

	private final class UIHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case PROCESSING:// 下载时
				int size = msg.getData().getInt("size");// 从消息中获取已经下载的数据长度
				progressBar.setProgress(size);// 设置进度条的进度
				float num = (float) progressBar.getProgress()
						/ (float) progressBar.getMax();// 计算已经下载的百分比，此处需要转换为浮点数计算
				int result = (int) (num * 100);// 把获取的浮点数计算结构专访为整数
				resultView.setText(result + "%");// 把下载的百分比显示在界面显示控件上
				if (progressBar.getProgress() == progressBar.getMax()) {
					Toast.makeText(getApplicationContext(), R.string.success,
							Toast.LENGTH_LONG).show();
					// 使用Toast技术，提示用户下载完成
				}
				break;
			case FAILURE:// 下载失败
				Toast.makeText(getApplicationContext(), R.string.error,
						Toast.LENGTH_LONG).show();// 提示用户下载失败

			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 应用程序启动时会首先调用而且在应用程序整个生命周期中只会调用一次，适合于初始化工作
		super.onCreate(savedInstanceState);// 使用父类的onCreate用做屏幕主界面的底层和基本绘制工作
		setContentView(R.layout.main);// 根据XML界面文件设置我们的主界面
		pathText = (EditText) this.findViewById(R.id.path);// 获取下载URL的文本输入对象
		resultView = (TextView) this.findViewById(R.id.resultView);// 获取显示下载百分比文本控制对象
		downloadButton = (Button) this.findViewById(R.id.downloadbutton);// 获取下载按钮对象
		stopbutton = (Button) this.findViewById(R.id.stopbutton);// 获取停止下载按钮对象
		progressBar = (ProgressBar) this.findViewById(R.id.progressBar);// 获取进度条对象
		ButtonClickListener listener = new ButtonClickListener();// 声明并定义按钮监听对象
		downloadButton.setOnClickListener(listener);
		stopbutton.setOnClickListener(listener);
	}

	/**
	 * 按钮监听器实现类
	 * 
	 * @zhangxiaobo
	 */
	private final class ButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 该方法在注册了该按钮监听器的对象被单击时会自动调用，用力响应单击事件
			switch (v.getId()) {
			case R.id.downloadbutton:// 获取点击对象的id
				String path = pathText.getText().toString();// 获取下载路径
				Toast.makeText(getApplicationContext(), path, 1).show();
				if (Environment.getExternalStorageState().endsWith(
						Environment.MEDIA_MOUNTED)) {
					// 获取SDCard是否存在，当SDCard存在时
					Environment.getExternalStorageDirectory();// 获取SDCard根目录文件、
					File saveDir = Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
					getExternalFilesDir(Environment.DIRECTORY_MOVIES);
					download(path, saveDir);
					Toast.makeText(getApplicationContext(), saveDir.toString(), 1).show();
				} else {
					// 当SDCard不存在时
					Toast.makeText(getApplicationContext(),
							R.string.sdcarderror, Toast.LENGTH_LONG).show();// 提示用户SDCard不存在
				}
				downloadButton.setEnabled(false);
				stopbutton.setEnabled(true);
				break;
			case R.id.stopbutton:
				exit();// 停止下载
				downloadButton.setEnabled(true);
				stopbutton.setEnabled(false);
				break;
			}
		}

	}

	// 由于用户的输入事件（点击button，触摸屏幕...）是由主线程负责处理的
	// 如果主线程处于工作状态
	// 此时用户产生的输入时间如果没能在5秒内得到处理，系统就会报应用无响应的错误
	// 所以在主线程里不能执行一件比较耗时的工作，否则会因主线程阻塞而无法处理用户的输入事件
	// 导致“应用无响应”错误的出现，耗时的工作应该在子线程里执行
	private DownloadTask task;// 声明下载执行者

	/**
	 * 退出下载
	 */
	public void exit() {
		if (task != null)
			task.exit();
	}

	/**
	 * 下载资源，声明下载执行者并开辟线程开始下载
	 * 
	 * @param path
	 *            下载的路径
	 * @param saveDir
	 *            保存文件
	 */
	private void download(String path, File saveDir) {
		task = new DownloadTask(path, saveDir);// 实例化下载业务
		new Thread(task).start();
	}

	/**
	 * UI控制画面的重绘（更新）是由主线程负责处理的，如果在子线程中更新UI控件值，更新后值不会重绘到屏幕上
	 * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
	 */
	private final class DownloadTask implements Runnable {
		private String path;// 下载路径
		private File saveDir;// 下载到保存到的文件
		private FileDownloader loader;// 文件下载器（下载线程的容器）

		/**
		 * 构造方法，实现变量的初始化
		 * 
		 * @param path
		 *            下载路径
		 * @param saveDir
		 *            下载要保存到的文件
		 */
		public DownloadTask(String path, File saveDir) {
			this.path = path;
			this.saveDir = saveDir;
		}

		/**
		 * 退出下载
		 */
		public void exit() {
			if (loader != null)
				loader.exit();// 如果下载器存在的话则退出下载
		}

		DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
			/**
			 * 下载的文件长度会不断地被传入该回调方法
			 */
			public void onDownloadSize(int size) {
				Message msg = new Message();
				msg.what = PROCESSING;
				msg.getData().putInt("size", size);
				handler.sendMessage(msg);
			}
		};

		public void run() {
			// TODO Auto-generated method stub
			try {
				loader = new FileDownloader(getApplicationContext(), path,
						saveDir, 3);
				progressBar.setMax(loader.getFileSize());// 设置进度条的最大刻度
				loader.download(downloadProgressListener);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				handler.sendMessage(handler.obtainMessage(FAILURE));

			}
		}

	}
}
