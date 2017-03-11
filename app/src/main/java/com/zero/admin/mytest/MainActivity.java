package com.zero.admin.mytest;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private AFragment mAFragment;
    private BFragment mBFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAFragment = new AFragment();
        mBFragment = new BFragment();

        addFrame(mAFragment);
        View viewById = findViewById(R.id.tv1);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detachFrame(mAFragment);
            }
        });

        View viewById1 = findViewById(R.id.tv2);
        viewById1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFrame(mAFragment);
            }
        });




    }

    private void scan() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        Uri parse = Uri.parse("file://" + externalStorageDirectory);
        Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, parse);
        this.sendBroadcast(intent);
    }

    private void scanA(String fileName){
        String[] strings = {Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + fileName};
        MediaScannerConnection.scanFile(this, strings, null, null);
    }

    private void play() {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replaceFrame(Fragment f) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl, f);
        fragmentTransaction.commit();
    }

    private void addFrame(Fragment f) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl, f);
        fragmentTransaction.commit();
    }

    private void hideFrame(Fragment f) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.hide(f);
        fragmentTransaction.commit();
    }

    private void showFrame(Fragment f) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.show(f);
        fragmentTransaction.commit();
    }


    private void detachFrame(Fragment f) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.detach(f);
        fragmentTransaction.commit();
    }

    private void attachFrame(Fragment f) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.attach(f);
        fragmentTransaction.commit();
    }

    private void setVoice(String path2, int id) {
        ContentValues cv = new ContentValues();
        Uri newUri = null;
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path2);

        // 查询音乐文件在媒体库是否存在
        Cursor cursor = this.getContentResolver().query(uri, null,
                MediaStore.MediaColumns.DATA + "=?", new String[]{path2},
                null);

        Log.i("AAA", "path2=" + path2);
        Log.i("AAA", "cursor.getCount()=" + cursor.getCount());

        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            switch (id) {
                case AppConstant.RINGTONE:
                    cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                    cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                    cv.put(MediaStore.Audio.Media.IS_ALARM, false);
                    cv.put(MediaStore.Audio.Media.IS_MUSIC, false);
                    break;
                case AppConstant.NOTIFICATION:
                    cv.put(MediaStore.Audio.Media.IS_RINGTONE, false);
                    cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                    cv.put(MediaStore.Audio.Media.IS_ALARM, false);
                    cv.put(MediaStore.Audio.Media.IS_MUSIC, false);
                    break;
                case AppConstant.ALARM:
                    cv.put(MediaStore.Audio.Media.IS_RINGTONE, false);
                    cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                    cv.put(MediaStore.Audio.Media.IS_ALARM, true);
                    cv.put(MediaStore.Audio.Media.IS_MUSIC, false);
                    break;
                case AppConstant.ALL:
                    cv.put(MediaStore.Audio.Media.IS_RINGTONE, false);
                    cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                    cv.put(MediaStore.Audio.Media.IS_ALARM, false);
                    cv.put(MediaStore.Audio.Media.IS_MUSIC, true);
                    break;
                default:
                    break;
            }


            // 把需要设为铃声的歌曲更新铃声库
            getContentResolver().update(uri, cv, MediaStore.MediaColumns.DATA + "=?",
                    new String[]{path2});
            newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));

            Log.i("AAA", newUri.getPath());

            // 以下为关键代码：
            switch (id) {
                case AppConstant.RINGTONE:
                    RingtoneManager.setActualDefaultRingtoneUri(this,
                            RingtoneManager.TYPE_RINGTONE, newUri);
                    break;


                case AppConstant.NOTIFICATION:
                    RingtoneManager.setActualDefaultRingtoneUri(this,
                            RingtoneManager.TYPE_NOTIFICATION, newUri);
                    break;


                case AppConstant.ALARM:
                    RingtoneManager.setActualDefaultRingtoneUri(this,
                            RingtoneManager.TYPE_ALARM, newUri);
                    break;


                case AppConstant.ALL:
                    RingtoneManager.setActualDefaultRingtoneUri(this,
                            RingtoneManager.TYPE_ALL, newUri);
                    break;
                default:
                    break;


            }


            // 播放铃声
            Ringtone rt = RingtoneManager.getRingtone(this, newUri);
            rt.play();
        }
    }


    public class BatteryChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
                // 当前电池的电压
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                // 电池的健康状态
                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                    case BatteryManager.BATTERY_HEALTH_COLD:
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                }
                // 电池当前的电量, 它介于0和 EXTRA_SCALE之间
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                // 电池电量的最大值
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                // 当前手机使用的是哪里的电源
                int pluged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                switch (pluged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        // 电源是AC charger.[应该是指充电器]
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        // 电源是USB port
                        break;
                }
                // 当前状态
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        // 正在充电
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        // 正在放电
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        // 充满
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        // 没有充电
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        // 未知状态
                        break;
                }
                // 电池使用的技术。比如，对于锂电池是Li-ion
                String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                // 当前电池的温度
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            } else if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_LOW)) {
                // 表示当前电池电量低
            } else if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_OKAY)) {
                // 表示当前电池已经从电量低恢复为正常
            }
        }
    }
}
