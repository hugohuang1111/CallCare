package me.hugo.app.callcare.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by hugo on 12/07/2017.
 */

public class CallService extends Service {

    private IBinder binder;
    private AudioManager mAudioMgr;
    private TelephonyManager mTelMgr;
    private MyPhoneStateListener mListener;
    private int curVolume;
    private boolean neeResume;
    private boolean callBegin;

    public class CallBinder extends Binder {
        public CallService getService() {
            return CallService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        binder = new CallBinder();
        mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mTelMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new MyPhoneStateListener();
        mTelMgr.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

//        mListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "18708111000");
//        mListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "01053519777");
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: {
                    callBegin = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (isBlack(incomingNumber)) {
                                neeResume = true;
                                if (callBegin) {
                                    muteVolume();
                                }
                            }
                        }
                    }).start();
                    break;
                }
                case TelephonyManager.CALL_STATE_OFFHOOK:
                default:
                {
                    callBegin = false;
                    if (neeResume) {
                        neeResume = false;
                        resumeVolume();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        mTelMgr.listen(mListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    private boolean isBlack(String phoneNumber) {
        String page = httpGet("http://www.so.com/s", "q=" + phoneNumber);
        return page.contains("class=\"mohe-tips\"");
    }

    public void muteVolume() {
        curVolume = mAudioMgr.getStreamVolume(AudioManager.STREAM_RING);
        mAudioMgr.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    public void resumeVolume() {
        mAudioMgr.setStreamVolume(AudioManager.STREAM_RING, curVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    private String httpGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

}
