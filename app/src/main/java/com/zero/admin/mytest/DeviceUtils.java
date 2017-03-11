package com.zero.admin.mytest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * @author zero
 * @Describe 设备工具集
 * @time 2016-7-2下午5:02:32
 */
public class DeviceUtils {
    // 移动
    private static final int CHINA_MOBILE       = 1;
    // 联通
    private static final int UNICOM             = 2;
    // 电信
    private static final int TELECOMMUNICATIONS = 3;
    // 失败
    private static final int ERROR              = 0;

    /**
     * 获取屏幕分辨率
     * ldpi 120dpi
     * mdpi 160dpi
     * hdpi 240dpi
     * xhdpi 320dpi
     * @param activity
     * @return
     */
    public static int getDisplayDensity(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.densityDpi;
    }


    /**
     * 手机唯一标识
     *
     * @param context 上下文
     */
    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = ""
                + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32)
                | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    /**
     * 手机MAC地址
     *
     * @param context 上下文
     */
    public static String getMacAddressInfo(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取TelephonyManager对象
     *
     * @param context 上下文
     */
    public static TelephonyManager getTelphoneManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取设备ID
     *
     * @param context 上下文
     */
    public static String getDeviceID(Context context) {
        return getTelphoneManager(context).getDeviceId();
    }

    /**
     * IMSI号
     *
     * @param context
     */
    public static String getImis(Context context) {
        return getTelphoneManager(context).getSubscriberId();
    }

    /**
     * 厂商信息
     */
    public static String getProductInfo() {
        return android.os.Build.MODEL;
    }

    /**
     * release版本
     */
    public static String getReleaseVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * SDK_INT 版本
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 手机号码
     *
     * @param context 上下文
     */
    public static String getPhoneNum(Context context) {
        return getTelphoneManager(context).getLine1Number();
    }

    /**
     * 获取使用wifi网络情况的IP<BR>
     * 需要配置如下权限 <BR>
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/><BR>
     * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/><BR>
     * <uses-permission android:name="android.permission.WAKE_LOCK"/><BR>
     */
    public static String getWiFiIP(Context context) {
        String ip = null;
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress != 0) {
            ip = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "."
                    + ((ipAddress >> 16) & 0xFF) + "." + (ipAddress >> 24 & 0xFF);
        }
        return ip;
    }

    /**
     * 获取个人网络的IP<BR>
     * 需要如下权限<BR>
     * <uses-permission android:name="android.permission.INTERNET"/><BR>
     *
     * @param context
     * @return
     */
    public static String getMobileIP(Context context) {
        String ip = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return ip;
    }

    /**
     * 当前运营商
     *
     * @param context 上下文
     * @return 返回0 表示失败 1表示为中国移动 2为中国联通 3为中国电信
     */
    public static int getProviderName(Context context) {
        String IMSI = getImis(context);
        if (IMSI == null) {
            return ERROR;
        }
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            return CHINA_MOBILE;
        } else if (IMSI.startsWith("46001")) {
            return UNICOM;
        } else if (IMSI.startsWith("46003")) {
            return TELECOMMUNICATIONS;
        }
        return ERROR;
    }

    /**
     * 手机CPU名字
     */
    public static String getCpuName() {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            // 读取文件CPU信息
            fileReader = new FileReader("/pro/cpuinfo");
            bufferedReader = new BufferedReader(fileReader);
            String string = bufferedReader.readLine();
            String[] strings = string.split(":\\s+", 2);
            return strings[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 检查程序是否运行
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static boolean isAppRunning(Context context, String packageName) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName)
                    && info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 是否在最前面
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static boolean isTopActivity(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            System.out.println("---------------包名-----------"
                    + tasksInfo.get(0).topActivity.getPackageName());
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
