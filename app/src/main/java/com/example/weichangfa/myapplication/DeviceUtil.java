package com.example.weichangfa.myapplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 
 * @author jzy
 * 
 */
public class DeviceUtil {

    private static float scale;
    private static int[] screenPx;
    private static int statusBarHeight;

    /**
     * 获得资源文件名称
     * 
     * @param cx
     * @param defType
     * @param file_name
     * @return
     */
    public static int getResIdFromFileName(Context cx, String defType, String file_name) {
        // TODO Auto-generated method stub
        Resources rs = cx.getResources();
        String packageName = getMyPackageName(cx);
        return rs.getIdentifier(file_name, defType, packageName);
    }

    /**
     * 获得当前程序包名
     * 
     * @param cx
     * @return
     */
    public static String getMyPackageName(Context cx) {
        try {
            return cx.getPackageManager().getPackageInfo(cx.getPackageName(), 0).packageName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
           
        }
        return null;
    }

    /**
     * 获取当前程序的版本号
     * 
     * @param cx
     * @return
     */
    public static String getVersionName(Context cx) {
        return getPackageInfo(cx).versionName;
    }

    /**
     * 获取当前程序的内部版本号
     * 
     * @param cx
     * @return
     */
    public static int getVersionCode(Context cx) {
        return getPackageInfo(cx).versionCode;
    }

    /**
     * 或得程序名称
     * 
     * @param cx
     * @return
     */
    public static String getAppName(Context cx) {
        PackageInfo pi = getPackageInfo(cx);
        PackageManager packageManager = cx.getPackageManager();
        return packageManager.getApplicationLabel(pi.applicationInfo).toString();
    }

    /**
     * 获得包信息
     * 
     * @param c
     * @return
     */
    private static PackageInfo getPackageInfo(Context c) {
        try {
            return c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (NameNotFoundException e) {
           
        }
        return null;
    }

    /**
     * 是否联网
     * 
     * @param cx
     * @return
     */
    public static boolean isConnectNet(Context cx) {
        ConnectivityManager cManager = (ConnectivityManager) cx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        return (info != null && info.isAvailable()) ? true : false;
    }

    // 判断渠道号
    public static String getChannelName(Context cx, String meta_date) {
        return getAppMetaDate(cx).get(meta_date).toString();
    }

    /**
     * 获取可用存储空间大小 若存在SD卡则返回SD卡剩余控件大小 否则返回手机内存剩余空间大小
     * 
     * @return
     */
    public static long getAvailableStorageSpace() {
        long externalSpace = getExternalStorageSpace();
        if (externalSpace == -1L) {
            return getInternalStorageSpace();
        }

        return externalSpace;
    }

    /**
     * 获取SD卡可用空间
     * 
     * @return availableSDCardSpace 可用空间(MB)。-1L:没有SD卡
     */
    public static long getExternalStorageSpace() {
        long availableSDCardSpace = -1L;
        // 存在SD卡
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSize = sf.getBlockSize();// 块大小,单位byte
            long availCount = sf.getAvailableBlocks();// 可用块数量
            // long blockCount = sf.getBlockCount();//块总数量
            // Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
            // Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");

            availableSDCardSpace = availCount * blockSize / 1024 / 1024;// 可用SD卡空间，单位MB
        }

        return availableSDCardSpace;
    }

    /**
     * 获取机器内部可用空间
     * 
     * @return availableSDCardSpace 可用空间(MB)。-1L:没有SD卡
     */
    public static long getInternalStorageSpace() {
        long availableInternalSpace = -1L;

        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = sf.getBlockSize();// 块大小,单位byte
        long availCount = sf.getAvailableBlocks();// 可用块数量
        // long blockCount = sf.getBlockCount();//块总数量
        // Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
        // Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");

        availableInternalSpace = availCount * blockSize / 1024;// 可用SD卡空间，单位KB

        return availableInternalSpace;
    }

    /**
     * 
     * @param cx
     * @return
     */
    public static Bundle getAppMetaDate(Context cx) {
        try {
            ApplicationInfo appInfo = cx.getPackageManager().getApplicationInfo(cx.getPackageName(),
                    PackageManager.GET_META_DATA);
            return appInfo.metaData;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
           
        }
        return null;
    }

    /**
     * 查询包名是否存在
     * 
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获得IMEI
     * 
     * @param cx
     * @return
     */
    public static String getImei(Context cx) {
        TelephonyManager tm = (TelephonyManager) cx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    // 获取网卡地址
    public static String getMacAddress(Context cx) {
        WifiManager wifi = (WifiManager) cx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    // 获取手机类型
    public static String getModel(Context cx) {
        return Build.MODEL;
    }

    public static void openWebBrower(Activity act, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        act.startActivity(i);
    }

    public static void openCallPhone(Activity act, String tel) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(tel));
        act.startActivity(i);
    }

    public static void openEmail(Activity act, String[] emailUrl, String emailTitle, String emailBody) {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("plain/text");

        // 设置邮件默认地址 参数必须是string[] 否则抛异常
        email.putExtra(android.content.Intent.EXTRA_EMAIL, emailUrl);
        // 设置邮件默认标题
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
        // 设置要默认发送的内容
        email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
        // 调用系统的邮件系统
        act.startActivity(Intent.createChooser(email, "请选择发送邮件的应用"));
    }

    /**
     * 
     * @param mask
     * @param color
     * @return
     */
    public static Bitmap maskBitmapColor(Bitmap mask, int color) {
        int w = mask.getWidth();
        int h = mask.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 拷贝一份原图
                                                                       // png8画质
        Canvas canvas = new Canvas(newBitmap);// 将拷贝作为画布
        Paint paint = new Paint();// 画笔
        paint.setColor(color | 0xff000000);// 设置颜色并将透明色变成不透明 0xff000000黑色
        canvas.drawRect(new Rect(0, 0, w, h), paint);// 画个同样大小的矩形
        Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 抗锯齿
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));// 设置图层交集模式
        canvas.drawBitmap(mask, 0, 0, xferPaint);// 用设置好的画笔再画出原图
        return newBitmap;// 返回画布上的图
    }

    /**
     * 获取状态栏高度
     * 
     * @param act
     * @return
     */
    public static int getStatusBarHeight(Activity act) {
        if (statusBarHeight == 0) {
            Rect frame = new Rect();
            act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;
        }

        return statusBarHeight;
    }

    // 单位转换
    public static final int dip2px(Context context, float dipValue) {
        if (scale == 0)
            scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // 单位转换
    public static final int px2dip(Context context, float pxValue) {
        if (scale == 0)
            scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 单位转换
    public static final float getDensity(Context context) {
        if (scale == 0)
            scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    // 获取屏幕高度和宽度
    public static int[] getScreenPx(Context context) {
        if (screenPx == null) {
            DisplayMetrics metric = new DisplayMetrics();
            WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mgr.getDefaultDisplay().getMetrics(metric);
            screenPx = new int[] { metric.widthPixels, metric.heightPixels };
        }

        return screenPx;
    }

    /**
     * 获得view当前位置
     * 
     * @param act
     * @param v
     * @return
     */
    public static int[] getViewLocation(Activity act, View v) {
        int[] location = new int[2];
        if (v != null)
            v.getLocationOnScreen(location);
        location[1] -= getStatusBarHeight(act);
        return location;
    }

    /**
     * 获得View截图
     * 
     * @param view
     * @return
     */
    public static Bitmap getShotScreenByView(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bimtap = view.getDrawingCache();
        return bimtap;
    }

    public final static Bitmap loadImageFromUrl(String imgUrl) {
        HttpURLConnection httpurlconnection = null;
        InputStream inputStream = null;
        try {
            // // 网络连接
            URL url = new URL(imgUrl);
            httpurlconnection = (HttpURLConnection) url.openConnection();
            inputStream = httpurlconnection.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
           
        } finally {
            // 关闭流和连接
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                   
                }
            if (httpurlconnection != null)
                httpurlconnection.disconnect();
        }
        return null;
    }

    /**
     * 隐藏键盘
     * 
     * @param context
     * @param view
     */
    public static void hideIMM(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     * 
     * @param context
     * @param view
     */
    public static void showIMM(Context context, View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 获得键盘状态(显示、未显示)
     * 
     * @param context
     * @return
     */
    public static boolean getIMMStatus(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        return isOpen;
    }

    public static void sendMail(Context ctx, String[] address, String subject, String content) {
        // 建立Intent 对象
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        // 设置文本格式
        emailIntent.setType("plain/text");
        // 设置对方邮件地址
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, address);
        // 设置标题内容
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        // 设置邮件文本内容
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        // 启动一个新的ACTIVITY,"Sending mail..."是在启动这个ACTIVITY的等待时间时所显示的文字
        ctx.startActivity(Intent.createChooser(emailIntent, "Sending mail..."));
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * 
     * @param pxValue
     * @param scale
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(float pxValue, float scale) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * 
     * @param dipValue
     * @param scale
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(float dipValue, float scale) {
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    public static final int SDK_VERSION_1_5 = 3;

    public static final int SDK_VERSION_1_6 = 4;

    public static final int SDK_VERSION_2_0 = 5;

    public static final int SDK_VERSION_2_0_1 = 6;

    public static final int SDK_VERSION_2_1 = 7;

    public static final int SDK_VERSION_2_2 = 8;

    public static final int SDK_VERSION_2_3 = 9;

    public static final int SDK_VERSION_2_3_3 = 10;

    public static final int SDK_VERSION_3 = 11;

    public static final int SDK_VERSION_4_0_1 = 14;

    public static final int SDK_VERSION_4_0_3 = 15;

    public static final int SDK_VERSION_4_1_0 = 16;

    public static final int SDK_VERSION_4_2_0 = 17;

    /**
     * 获得设备型号
     * 
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获得国际移动设备身份码
     * 
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 获得国际移动用户识别码
     * 
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }



    /**
     * 
     * @param width
     *            图像的宽度
     * @param height
     *            图像的高度
     * @param image
     *            源图片
     * @param outerRadiusRat
     *            圆角的大小
     * @return 圆角图片
     */
    public static Bitmap createFramedPhoto(int width, int height, Bitmap image, float outerRadiusRat) {
        // 根据源文件新建一个 darwable对象
        Drawable imageDrawable = new BitmapDrawable(image);

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, width, height);

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

        // 将源图片绘制到这个圆角矩形上
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, width, height);
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();

        return output;
    }

    public static void copyToClipboard(Context context, String str) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(str);
    }

    /**
     * 友盟统计设备信息
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
