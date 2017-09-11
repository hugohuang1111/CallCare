package me.hugo.app.callcare;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import me.hugo.app.callcare.service.CallService;
import me.hugo.app.callcare.util.ContectUtil;

public class MainActivity extends AppCompatActivity {

    CallService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startCallService();

        Button btn = (Button)findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.muteVolume();
            }
        });

        btn = (Button)findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.resumeVolume();
            }
        });

        Intent intent = new Intent(MainActivity.this, CallService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                CallService.CallBinder binder = (CallService.CallBinder)iBinder;
                mService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mService = null;
            }
        }, Context.BIND_AUTO_CREATE);

        //ContectUtil.inContectList(this, "18702819799");

        Log.d("HHH", "CallCare Init");
    }

    private void startCallService() {
        Intent callServiceIntent = new Intent(MainActivity.this, CallService.class);
        startService(callServiceIntent);
    }
}
