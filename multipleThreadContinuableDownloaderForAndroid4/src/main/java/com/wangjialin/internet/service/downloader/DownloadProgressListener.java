package com.wangjialin.internet.service.downloader;

/**
 * ���ؽ��ȼ�����
 * @author Wang Jialin
 *
 */
public interface DownloadProgressListener {
	/**
	 * ���ؽ��ȼ������� ��ȡ�ʹ������ص����ݵĴ�С
	 * @param size ���ݴ�С
	 */
	public void onDownloadSize(int size);
}
