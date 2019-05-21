package com.example.macros;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class WidgetIntentService extends IntentService {

    Context context;

    public WidgetIntentService() {
        super("Widget Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = this;

        if("protein".equals(intent.getAction())){

            startActivity(new Intent(context, MacrosActivity.class)
            .putExtra("focus", "protein").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }else if("carbs".equals(intent.getAction())){

            startActivity(new Intent(context, MacrosActivity.class)
            .putExtra("focus", "carbs").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }else{

            startActivity(new Intent(context, MacrosActivity.class)
            .putExtra("focus", "fat").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }
}
