package com.wangjialin.internet.service;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * ҵ��Bean��ʵ�ֶ����ݵĲ���
 * @author Wang Jialin
 *
 */
public class FileService {
	private DBOpenHelper openHelper;	//�������ݿ������

	public FileService(Context context) {
		openHelper = new DBOpenHelper(context);	//���������Ķ���ʵ�������ݿ������
	}
	/**
	 * ��ȡ�ض�URI��ÿ���߳��Ѿ����ص��ļ�����
	 * @param path
	 * @return
	 */
	public Map<Integer, Integer> getData(String path){
		SQLiteDatabase db = openHelper.getReadableDatabase();	//��ȡ�ɶ������ݿ�����һ��������ڸò������ڲ�ʵ�����䷵�ص���ʵ�ǿ�д�����ݿ���
		Cursor cursor = db.rawQuery("select threadid, downlength from filedownlog where downpath=?", new String[]{path});	//��������·����ѯ�����߳��������ݣ����ص�Cursorָ���һ����¼֮ǰ
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();	//����һ����ϣ�����ڴ��ÿ���̵߳��Ѿ����ص��ļ�����
		while(cursor.moveToNext()){	//�ӵ�һ����¼��ʼ��ʼ����Cursor����
			data.put(cursor.getInt(0), cursor.getInt(1));	//���߳�id�͸��߳������صĳ������ý�data��ϣ����
			data.put(cursor.getInt(cursor.getColumnIndexOrThrow("threadid")), cursor.getInt(cursor.getColumnIndexOrThrow("downlength")));
		}
		cursor.close();	//�ر�cursor���ͷ���Դ
		db.close();	//�ر����ݿ�
		return data;	//���ػ�õ�ÿ���̺߳�ÿ���̵߳����س���
	}
	/**
	 * ����ÿ���߳��Ѿ����ص��ļ�����
	 * @param path	���ص�·��
	 * @param map ���ڵ�id���Ѿ����صĳ��ȵļ���
	 */
	public void save(String path,  Map<Integer, Integer> map){
		SQLiteDatabase db = openHelper.getWritableDatabase();	//��ȡ��д�����ݿ���
		db.beginTransaction();	//��ʼ������Ϊ�˴�Ҫ�����������
		try{
			for(Map.Entry<Integer, Integer> entry : map.entrySet()){	//����For-Each�ķ�ʽ�������ݼ���
				db.execSQL("insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
						new Object[]{path, entry.getKey(), entry.getValue()});	//�����ض�����·���ض��߳�ID�Ѿ����ص�����
			}
			db.setTransactionSuccessful();	//��������ִ�еı�־Ϊ�ɹ�
		}finally{	//�˲��ֵĴ���϶��Ǳ�ִ�еģ������ɱ��������Ļ�
			db.endTransaction();	//����һ������������������˳ɹ���־�����ύ���񣬷���������
		}
		db.close();	//�ر����ݿ⣬�ͷ������Դ
	}
	/**
	 * ʵʱ����ÿ���߳��Ѿ����ص��ļ�����
	 * @param path
	 * @param map
	 */
	public void update(String path, int threadId, int pos){
		SQLiteDatabase db = openHelper.getWritableDatabase();	//��ȡ��д�����ݿ���
		db.execSQL("update filedownlog set downlength=? where downpath=? and threadid=?",
				new Object[]{pos, path, threadId});	//�����ض�����·�����ض��߳��Ѿ����ص��ļ�����
		db.close();	//�ر����ݿ⣬�ͷ���ص���Դ
	}
	/**
	 * ���ļ�������ɺ�ɾ����Ӧ�����ؼ�¼
	 * @param path
	 */
	public void delete(String path){
		SQLiteDatabase db = openHelper.getWritableDatabase();	//��ȡ��д�����ݿ���
		db.execSQL("delete from filedownlog where downpath=?", new Object[]{path});	//ɾ���ض�����·���������̼߳�¼
		db.close();	//�ر����ݿ⣬�ͷ���Դ
	}
	
}