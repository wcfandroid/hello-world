package com.wangjialin.internet.service.downloader;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * �����̣߳����ݾ������ص�ַ�����ֵ����ļ������ؿ�Ĵ�С���Ѿ����ص����ݴ�С����Ϣ��������
 * @author Wang Jialin
 *
 */
public class DownloadThread extends Thread {
	private static final String TAG = "DownloadThread";	//����TAG���������ӵĴ�ӡ���
	private File saveFile;	//���ص����ݱ��浽���ļ�
	private URL downUrl;	//���ص�URL
	private int block;	//ÿ���߳����صĴ�С
	private int threadId = -1;	//��ʼ���߳�id����
	private int downloadedLength;	//���߳��Ѿ����ص����ݳ���
	private boolean finished = false;	//���߳��Ƿ�������صı�־
	private FileDownloader downloader;	//�ļ�������
	
	public DownloadThread(FileDownloader downloader, URL downUrl, File saveFile, int block, int downloadedLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downloadedLength = downloadedLength;
	}
	
	@Override
	public void run() {
		if(downloadedLength < block){//δ�������
			try {
				HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();	//����HttpURLConnection����
				http.setConnectTimeout(5 * 1000);	//�������ӳ�ʱʱ��Ϊ5����
				http.setRequestMethod("GET");	//��������ķ���ΪGET
				http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");	//���ÿͻ��˿��Խ��ܵķ�����������
				http.setRequestProperty("Accept-Language", "zh-CN");	//���ÿͻ���ʹ�õ�����������
				http.setRequestProperty("Referer", downUrl.toString()); 	//�����������Դ�����ڶԷ�����Դ����ͳ��
				http.setRequestProperty("Charset", "UTF-8");	//����ͨ�ű���ΪUTF-8
				int startPos = block * (threadId - 1) + downloadedLength;//��ʼλ��
				int endPos = block * threadId -1;//����λ��
				http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);//���û�ȡʵ�����ݵķ�Χ,���������ʵ�����ݵĴ�С���Զ�����ʵ�ʵ����ݴ�С
				http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");	//�ͻ����û�����
				http.setRequestProperty("Connection", "Keep-Alive");	//ʹ�ó�����
				
				InputStream inStream = http.getInputStream();	//��ȡԶ�����ӵ�������
				byte[] buffer = new byte[1024];	//���ñ������ݻ���Ĵ�СΪ1M
				int offset = 0;	//����ÿ�ζ�ȡ��������
				print("Thread " + this.threadId + " starts to download from position "+ startPos);	//��ӡ���߳̿�ʼ���ص�λ��
				RandomAccessFile threadFile = new RandomAccessFile(this.saveFile, "rwd");	//If the file does not already exist then an attempt will be made to create it and it require that every update to the file's content be written synchronously to the underlying storage device. 
				threadFile.seek(startPos);	//�ļ�ָ��ָ��ʼ���ص�λ��
				while (!downloader.getExited() && (offset = inStream.read(buffer, 0, 1024)) != -1) {	//���û�û��Ҫ��ֹͣ���أ�ͬʱû�е����������ݵ�ĩβʱ���һֱѭ����ȡ����
					threadFile.write(buffer, 0, offset);	//ֱ�Ӱ�����д���ļ���
					downloadedLength += offset;	//�������ص��Ѿ�д���ļ��е����ݼ��뵽���س�����
					downloader.update(this.threadId, downloadedLength);	//�Ѹ��߳��Ѿ����ص����ݳ��ȸ��µ����ݿ���ڴ��ϣ����
					downloader.append(offset);	//�������ص����ݳ��ȼ��뵽�Ѿ����ص������ܳ�����
				}//���߳�����������ϻ������ر��û�ֹͣ
				threadFile.close();	//Closes this random access file stream and releases any system resources associated with the stream.
				inStream.close();	//Concrete implementations of this class should free any resources during close
				if(downloader.getExited())
				{
					print("Thread " + this.threadId + " has been paused");
				}
				else
				{
					print("Thread " + this.threadId + " download finish");
				}
				
				this.finished = true;	//������ɱ�־Ϊtrue��������������ɻ����û������ж�����
			} catch (Exception e) {	//�����쳣
				this.downloadedLength = -1;	//���ø��߳��Ѿ����صĳ���Ϊ-1
				print("Thread "+ this.threadId+ ":"+ e);	//��ӡ���쳣��Ϣ
			}
		}
	}
	/**
	 * ��ӡ��Ϣ
	 * @param msg	��Ϣ
	 */
	private static void print(String msg){
		Log.i(TAG, msg);	//ʹ��Logcat��Information��ʽ��ӡ��Ϣ
	}
	
	/**
	 * �����Ƿ����
	 * @return
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * �Ѿ����ص����ݴ�С
	 * @return �������ֵΪ-1,��������ʧ��
	 */
	public long getDownloadedLength() {
		return downloadedLength;
	}
}


