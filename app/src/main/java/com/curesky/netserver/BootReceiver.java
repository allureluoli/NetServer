package com.curesky.netserver; // 确保包名与 manifest 一致

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 开机后启动 MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 必须加！
            context.startActivity(launchIntent);

            // 或者启动 Service（如有需要）
            // Intent serviceIntent = new Intent(context, MyBackgroundService.class);
            // context.startService(serviceIntent);
        }
    }
}
