package com.syriasoft.house_keeping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RECIEVER extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ReceivingService.class);
        context.startService(i);
    }
}
