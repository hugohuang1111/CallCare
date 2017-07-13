package me.hugo.app.callcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.hugo.app.callcare.service.CallService;

/**
 * Created by hugo on 13/07/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // 开启应用
//            Intent sintent = context.getPackageManager().getLaunchIntentForPackage( "me.hugo.app.callcare" );
//            context.startActivity( sintent );

            // 开启服务代码
            context.startService(new Intent(context, CallService.class));
        }
    }

}
