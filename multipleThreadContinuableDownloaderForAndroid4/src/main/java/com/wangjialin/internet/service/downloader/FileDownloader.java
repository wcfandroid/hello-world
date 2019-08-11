package com.wangjialin.internet.service.downloader;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

import com.wangjialin.internet.service.FileService;

public class FileDownloader {
	private static final String TAG = "FileDownloader";	//���ñ�ǩ������Logcat��־��¼
	private static final int RESPONSEOK = 200;	//��Ӧ��Ϊ200�������ʳɹ�
	private Context context;	//Ӧ�ó���������Ķ���
	private FileService fileService;	//��ȡ�������ݿ��ҵ��Bean
	private boolean exited;	//ֹͣ���ر�־
	private int downloadedSize = 0;	//�������ļ�����
	private int fileSize = 0;	//ԭʼ�ļ�����
	private DownloadThread[] threads;	//�����߳������������̳߳�
	private File saveFile;	//���ݱ��浽�ı����ļ�
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();	//������߳����صĳ���
	private int block;	//ÿ���߳����صĳ���
	private String downloadUrl;	//����·��
	
	/**
	 * ��ȡ�߳���
	 */
	public int getThreadSize() {
		return threads.length;	//�������鳤�ȷ����߳���
	}
	
	/**
	 * �˳�����
	 */
	public void exit(){
		this.exited = true;	//�����˳���־Ϊtrue
	}
	public boolean getExited(){
		return this.exited;
	}
	/**
	 * ��ȡ�ļ���С
	 * @return
	 */
	public int getFileSize() {
		return fileSize;	//�����Ա�����л�ȡ�����ļ��Ĵ�С
	}
	
	/**
	 * �ۼ������ش�С
	 * @param size
	 */
	protected synchronized void append(int size) {	//ʹ��ͬ���ؼ��ֽ��������������
		downloadedSize += size;	//��ʵʱ���صĳ��ȼ��뵽�����س�����
	}
	
	/**
	 * ����ָ���߳�������ص�λ��
	 * @param threadId �߳�id
	 * @param pos ������ص�λ��
	 */
	protected synchronized void update(int threadId, int pos) {
		this.data.put(threadId, pos);	//���ƶ��߳�ID���̸߳������µ����س��ȣ���ǰ��ֵ�ᱻ���ǵ�
		this.fileService.update(this.downloadUrl, threadId, pos);	//�������ݿ���ָ���̵߳����س���
	}
	/**
	 * �����ļ�������
	 * @param downloadUrl ����·��
	 * @param fileSaveDir �ļ�����Ŀ¼
	 * @param threadNum �����߳���
	 */
	public FileDownloader(Context context, String downloadUrl, File fileSaveDir, int threadNum) {
		try {
			this.context = context;	//�������Ķ���ֵ
			this.downloadUrl = downloadUrl;	//�����ص�·����ֵ
			fileService = new FileService(this.context);	//ʵ�������ݲ���ҵ��Bean���˴���Ҫʹ��Context����Ϊ�˴������ݿ���Ӧ�ó���˽��
			URL url = new URL(this.downloadUrl);	//��������·��ʵ����URL
			if(!fileSaveDir.exists()) fileSaveDir.mkdirs();	//���ָ�����ļ������ڣ��򴴽�Ŀ¼���˴����Դ������Ŀ¼
			this.threads = new DownloadThread[threadNum];	//�������ص��߳������������̳߳�				
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();	//����һ��Զ�����Ӿ������ʱ��δ��������
			conn.setConnectTimeout(5*1000);	//�������ӳ�ʱʱ��Ϊ5��
			conn.setRequestMethod("GET");	//��������ʽΪGET
			conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");	//���ÿͻ��˿��Խ��ܵ�ý������
			conn.setRequestProperty("Accept-Language", "zh-CN");	//���ÿͻ�������
			conn.setRequestProperty("Referer", downloadUrl); 	//�����������Դҳ�棬���ڷ���˽�����Դͳ��
			conn.setRequestProperty("Charset", "UTF-8");	//���ÿͻ��˱���
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");	//�����û�����
			conn.setRequestProperty("Connection", "Keep-Alive");	//����Connection�ķ�ʽ
			conn.connect();	//��Զ����Դ�������������ӣ������޷��ص�������
			printResponseHeader(conn);	//��Ӧ���ص�HTTPͷ�ֶμ���
			if (conn.getResponseCode()==RESPONSEOK) {	//�˴��������򿪷���������ȡ���ص�״̬�룬���ڼ���Ƿ�����ɹ�����������Ϊ200ʱִ������Ĵ���
				this.fileSize = conn.getContentLength();//������Ӧ��ȡ�ļ���С
				if (this.fileSize <= 0) throw new RuntimeException("Unkown file size ");	//���ļ���СΪС�ڵ�����ʱ�׳�����ʱ�쳣
						
				String filename = getFileName(conn);//��ȡ�ļ�����	
				this.saveFile = new File(fileSaveDir, filename);//�����ļ�����Ŀ¼���ļ������������ļ�
				Map<Integer, Integer> logdata = fileService.getData(downloadUrl);//��ȡ���ؼ�¼
				
				if(logdata.size()>0){//����������ؼ�¼
					for(Map.Entry<Integer, Integer> entry : logdata.entrySet())	//���������е�����
						data.put(entry.getKey(), entry.getValue());//�Ѹ����߳��Ѿ����ص����ݳ��ȷ���data��
				}
				
				if(this.data.size()==this.threads.length){//����Ѿ����ص����ݵ��߳������������õ��߳�����ͬʱ����������߳��Ѿ����ص������ܳ���
					for (int i = 0; i < this.threads.length; i++) {	//����ÿ���߳��Ѿ����ص�����
						this.downloadedSize += this.data.get(i+1);	//�����Ѿ����ص�����֮��
					}
					print("�Ѿ����صĳ���"+ this.downloadedSize + "���ֽ�");	//��ӡ���Ѿ����ص������ܺ�
				}

				this.block = (this.fileSize % this.threads.length)==0? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;	//����ÿ���߳����ص����ݳ���
			}else{
				print("��������Ӧ����:" + conn.getResponseCode() + conn.getResponseMessage());	//��ӡ����
				throw new RuntimeException("server response error ");	//�׳�����ʱ�����������쳣
			}
		} catch (Exception e) {
			print(e.toString());	//��ӡ����
			throw new RuntimeException("Can't connection this url");	//�׳�����ʱ�޷����ӵ��쳣
		}
	}
	/**
	 * ��ȡ�ļ���
	 */
	private String getFileName(HttpURLConnection conn) {
		String filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/') + 1);	//������·�����ַ����л�ȡ�ļ�����
		
		if(filename==null || "".equals(filename.trim())){//�����ȡ�����ļ�����
			for (int i = 0;; i++) {	//����ѭ������
				String mine = conn.getHeaderField(i);	//�ӷ��ص����л�ȡ�ض�������ͷ�ֶ�ֵ
				if (mine == null) break;	//����������˷���ͷĩβ���˳�ѭ��
				if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){	//��ȡcontent-disposition����ͷ�ֶΣ�������ܻ�����ļ���
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());	//ʹ��������ʽ��ѯ�ļ���
					if(m.find()) return m.group(1);	//����з��������������ַ���
				}
			}
			filename = UUID.randomUUID()+ ".tmp";//�������ϵı�ʶ����(ÿ����������Ψһ�ı�ʶ��)�Լ� CPU ʱ�ӵ�Ψһ�������ɵĵ�һ�� 16 �ֽڵĶ�������Ϊ�ļ���
		}
		return filename;
	}
	
	/**
	 *  ��ʼ�����ļ�
	 * @param listener �������������ı仯,�������Ҫ�˽�ʵʱ���ص�����,��������Ϊnull
	 * @return �������ļ���С
	 * @throws Exception
	 */
	public int download(DownloadProgressListener listener) throws Exception{	//�������أ����׳��쳣�������ߣ�������쳣�Ļ�
		try {
			RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rwd");	//The file is opened for reading and writing. Every change of the file's content must be written synchronously to the target device.
			if(this.fileSize>0) randOut.setLength(this.fileSize);	//�����ļ��Ĵ�С
			randOut.close();	//�رո��ļ���ʹ������Ч
			URL url = new URL(this.downloadUrl);	//A URL instance specifies the location of a resource on the internet as specified by RFC 1738
			if(this.data.size() != this.threads.length){	//���ԭ��δ�����ػ���ԭ�ȵ������߳��������ڵ��߳�����һ��
				this.data.clear();	//Removes all elements from this Map, leaving it empty.
				for (int i = 0; i < this.threads.length; i++) {	//�����̳߳�
					this.data.put(i+1, 0);//��ʼ��ÿ���߳��Ѿ����ص����ݳ���Ϊ0
				}
				this.downloadedSize = 0;	//�����Ѿ����صĳ���Ϊ0
			}
			for (int i = 0; i < this.threads.length; i++) {//�����߳̽�������
				int downloadedLength = this.data.get(i+1);	//ͨ���ض����߳�ID��ȡ���߳��Ѿ����ص����ݳ���
				if(downloadedLength < this.block && this.downloadedSize < this.fileSize){//�ж��߳��Ƿ��Ѿ��������,�����������	
					this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.data.get(i+1), i+1);	//��ʼ���ض�id���߳�
					this.threads[i].setPriority(7);	//�����̵߳����ȼ���Thread.NORM_PRIORITY = 5 Thread.MIN_PRIORITY = 1 Thread.MAX_PRIORITY = 10
					this.threads[i].start();	//�����߳�
				}else{
					this.threads[i] = null;	//�������߳��Ѿ������������
				}
			}
			fileService.delete(this.downloadUrl);	//����������ؼ�¼��ɾ�����ǣ�Ȼ���������
			fileService.save(this.downloadUrl, this.data);	//���Ѿ����ص�ʵʱ����д�����ݿ�
			boolean notFinished = true;//����δ���
			while (notFinished) {// ѭ���ж������߳��Ƿ��������
				Thread.sleep(900);
				notFinished = false;//�ٶ�ȫ���߳��������
				for (int i = 0; i < this.threads.length; i++){
					if (this.threads[i] != null && !this.threads[i].isFinished()) {//��������߳�δ�������
						notFinished = true;//���ñ�־Ϊ����û�����
						if(this.threads[i].getDownloadedLength() == -1){//�������ʧ��,���������Ѿ����ص����ݳ��ȵĻ���������
							this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.data.get(i+1), i+1);	//���¿��������߳�
							this.threads[i].setPriority(7);	//�������ص����ȼ�
							this.threads[i].start();	//��ʼ�����߳�
						}
					}
				}				
				if(listener!=null) listener.onDownloadSize(this.downloadedSize);//֪ͨĿǰ�Ѿ�������ɵ����ݳ���
			}
			if(downloadedSize == this.fileSize) fileService.delete(this.downloadUrl);//�������ɾ����¼
		} catch (Exception e) {
			print(e.toString());	//��ӡ����
			throw new Exception("File downloads error");	//�׳��ļ������쳣
		}
		return this.downloadedSize;
	}
	/**
	 * ��ȡHttp��Ӧͷ�ֶ�
	 * @param http	HttpURLConnection����
	 * @return	����ͷ�ֶε�LinkedHashMap
	 */
	public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();	//ʹ��LinkedHashMap��֤д��ͱ�����ʱ���˳����ͬ�����������ֵ����
		for (int i = 0;; i++) {	//�˴�Ϊ����ѭ������Ϊ��֪��ͷ�ֶε�����
			String fieldValue = http.getHeaderField(i);	//getHeaderField(int n)���ڷ��� ��n��ͷ�ֶε�ֵ��

			if (fieldValue == null) break;	//�����i���ֶ�û��ֵ�ˣ������ͷ�ֶβ����Ѿ�ѭ����ϣ��˴�ʹ��Break�˳�ѭ��
			header.put(http.getHeaderFieldKey(i), fieldValue);	//getHeaderFieldKey(int n)���ڷ��� ��n��ͷ�ֶεļ���
		}
		return header;
	}
	/**
	 * ��ӡHttpͷ�ֶ�
	 * @param http HttpURLConnection����
	 */
	public static void printResponseHeader(HttpURLConnection http){
		Map<String, String> header = getHttpResponseHeader(http);	//��ȡHttp��Ӧͷ�ֶ�
		for(Map.Entry<String, String> entry : header.entrySet()){	//ʹ��For-Eachѭ���ķ�ʽ������ȡ��ͷ�ֶε�ֵ����ʱ������ѭ��������˳����ͬ
			String key = entry.getKey()!=null ? entry.getKey()+ ":" : "";	//���м���ʱ�����ȡ�������û����Ϊ���ַ���
			print(key+ entry.getValue());	//��Ӧ����ֵ�����
		}
	}
	
	/**
	 * ��ӡ��Ϣ
	 * @param msg	��Ϣ�ַ���
	 */
	private static void print(String msg){
		Log.i(TAG, msg);	//ʹ��LogCat��Information��ʽ��ӡ��Ϣ
	}
}
