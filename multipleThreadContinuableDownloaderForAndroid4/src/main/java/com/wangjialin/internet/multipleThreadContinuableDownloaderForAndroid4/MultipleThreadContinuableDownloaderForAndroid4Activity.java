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
	private static final int PROCESSING = 1;// ��������ʵʱ���ݴ���Message��־
	private static final int FAILURE = -1;// ����ʧ��ʱ��Message��־
	private EditText pathText;// ���������ı���
	private TextView resultView;// ʵ�ֽ�����ʾ�ٷֱ��ı���
	private Button downloadButton;// ���ذ�ť�����Դ��������¼�
	private Button stopbutton;// ֹͣ��ť������ֹͣ����
	private ProgressBar progressBar;// ���ؽ�������ʵʱͼ�λ�����ʾ������Ϣ
	// handler���������������������Handler�������ڵ��߳����󶨵���Ϣ���з�����Ϣ��������Ϣ
	private Handler handler = new UIHandler();

	private final class UIHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case PROCESSING:// ����ʱ
				int size = msg.getData().getInt("size");// ����Ϣ�л�ȡ�Ѿ����ص����ݳ���
				progressBar.setProgress(size);// ���ý������Ľ���
				float num = (float) progressBar.getProgress()
						/ (float) progressBar.getMax();// �����Ѿ����صİٷֱȣ��˴���Ҫת��Ϊ����������
				int result = (int) (num * 100);// �ѻ�ȡ�ĸ���������ṹר��Ϊ����
				resultView.setText(result + "%");// �����صİٷֱ���ʾ�ڽ�����ʾ�ؼ���
				if (progressBar.getProgress() == progressBar.getMax()) {
					Toast.makeText(getApplicationContext(), R.string.success,
							Toast.LENGTH_LONG).show();
					// ʹ��Toast��������ʾ�û��������
				}
				break;
			case FAILURE:// ����ʧ��
				Toast.makeText(getApplicationContext(), R.string.error,
						Toast.LENGTH_LONG).show();// ��ʾ�û�����ʧ��

			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// Ӧ�ó�������ʱ�����ȵ��ö�����Ӧ�ó�����������������ֻ�����һ�Σ��ʺ��ڳ�ʼ������
		super.onCreate(savedInstanceState);// ʹ�ø����onCreate������Ļ������ĵײ�ͻ������ƹ���
		setContentView(R.layout.main);// ����XML�����ļ��������ǵ�������
		pathText = (EditText) this.findViewById(R.id.path);// ��ȡ����URL���ı��������
		resultView = (TextView) this.findViewById(R.id.resultView);// ��ȡ��ʾ���ذٷֱ��ı����ƶ���
		downloadButton = (Button) this.findViewById(R.id.downloadbutton);// ��ȡ���ذ�ť����
		stopbutton = (Button) this.findViewById(R.id.stopbutton);// ��ȡֹͣ���ذ�ť����
		progressBar = (ProgressBar) this.findViewById(R.id.progressBar);// ��ȡ����������
		ButtonClickListener listener = new ButtonClickListener();// ���������尴ť��������
		downloadButton.setOnClickListener(listener);
		stopbutton.setOnClickListener(listener);
	}

	/**
	 * ��ť������ʵ����
	 * 
	 * @zhangxiaobo
	 */
	private final class ButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// �÷�����ע���˸ð�ť�������Ķ��󱻵���ʱ���Զ����ã�������Ӧ�����¼�
			switch (v.getId()) {
			case R.id.downloadbutton:// ��ȡ��������id
				String path = pathText.getText().toString();// ��ȡ����·��
				Toast.makeText(getApplicationContext(), path, 1).show();
				if (Environment.getExternalStorageState().endsWith(
						Environment.MEDIA_MOUNTED)) {
					// ��ȡSDCard�Ƿ���ڣ���SDCard����ʱ
					Environment.getExternalStorageDirectory();// ��ȡSDCard��Ŀ¼�ļ���
					File saveDir = Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
					getExternalFilesDir(Environment.DIRECTORY_MOVIES);
					download(path, saveDir);
					Toast.makeText(getApplicationContext(), saveDir.toString(), 1).show();
				} else {
					// ��SDCard������ʱ
					Toast.makeText(getApplicationContext(),
							R.string.sdcarderror, Toast.LENGTH_LONG).show();// ��ʾ�û�SDCard������
				}
				downloadButton.setEnabled(false);
				stopbutton.setEnabled(true);
				break;
			case R.id.stopbutton:
				exit();// ֹͣ����
				downloadButton.setEnabled(true);
				stopbutton.setEnabled(false);
				break;
			}
		}

	}

	// �����û��������¼������button��������Ļ...���������̸߳������
	// ������̴߳��ڹ���״̬
	// ��ʱ�û�����������ʱ�����û����5���ڵõ�����ϵͳ�ͻᱨӦ������Ӧ�Ĵ���
	// ���������߳��ﲻ��ִ��һ���ȽϺ�ʱ�Ĺ���������������߳��������޷������û��������¼�
	// ���¡�Ӧ������Ӧ������ĳ��֣���ʱ�Ĺ���Ӧ�������߳���ִ��
	private DownloadTask task;// ��������ִ����

	/**
	 * �˳�����
	 */
	public void exit() {
		if (task != null)
			task.exit();
	}

	/**
	 * ������Դ����������ִ���߲������߳̿�ʼ����
	 * 
	 * @param path
	 *            ���ص�·��
	 * @param saveDir
	 *            �����ļ�
	 */
	private void download(String path, File saveDir) {
		task = new DownloadTask(path, saveDir);// ʵ��������ҵ��
		new Thread(task).start();
	}

	/**
	 * UI���ƻ�����ػ棨���£��������̸߳�����ģ���������߳��и���UI�ؼ�ֵ�����º�ֵ�����ػ浽��Ļ��
	 * һ��Ҫ�����߳������UI�ؼ���ֵ��������������Ļ����ʾ���������������߳��и���UI�ؼ���ֵ
	 */
	private final class DownloadTask implements Runnable {
		private String path;// ����·��
		private File saveDir;// ���ص����浽���ļ�
		private FileDownloader loader;// �ļ��������������̵߳�������

		/**
		 * ���췽����ʵ�ֱ����ĳ�ʼ��
		 * 
		 * @param path
		 *            ����·��
		 * @param saveDir
		 *            ����Ҫ���浽���ļ�
		 */
		public DownloadTask(String path, File saveDir) {
			this.path = path;
			this.saveDir = saveDir;
		}

		/**
		 * �˳�����
		 */
		public void exit() {
			if (loader != null)
				loader.exit();// ������������ڵĻ����˳�����
		}

		DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
			/**
			 * ���ص��ļ����Ȼ᲻�ϵر�����ûص�����
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
				progressBar.setMax(loader.getFileSize());// ���ý����������̶�
				loader.download(downloadProgressListener);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				handler.sendMessage(handler.obtainMessage(FAILURE));

			}
		}

	}
}
