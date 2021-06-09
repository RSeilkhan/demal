package com.rakhatali.demal.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rakhatali.demal.services.PlayerService;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, PlayerService.class);
        if(actionName != null){
            serviceIntent.putExtra("ActionName", intent.getAction());
            context.startService(serviceIntent);

        }
    }
}
