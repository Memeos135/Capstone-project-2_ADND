package com.example.macros;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class MacrosWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.macros);

        views.setOnClickPendingIntent(R.id.protein, getPendingSelfIntent(context, "protein", 0, appWidgetId));
        views.setOnClickPendingIntent(R.id.carbs, getPendingSelfIntent(context, "carbs", 1, appWidgetId));
        views.setOnClickPendingIntent(R.id.fat, getPendingSelfIntent(context, "fat", 2, appWidgetId));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action, int rc, int appID) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(action);
        return PendingIntent.getService(context, rc, intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals("update")){
            getData(context);
        }else if(intent.getAction().equals("setBase")){
            getBaseData(context);
        }
    }

    public void getBaseData(final Context context){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userGoalMacros");

        final String[] proteinMax = new String[1];
        final String[] carbsMax = new String[1];
        final String[] fatMax = new String[1];

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getKey().equals("protein")){
                            proteinMax[0] = dataSnapshot1.getValue().toString();
                        }else if(dataSnapshot1.getKey().equals("carbs")){
                            carbsMax[0] = dataSnapshot1.getValue().toString();
                        }else if(dataSnapshot1.getKey().equals("fats")){
                            fatMax[0] = dataSnapshot1.getValue().toString();
                        }
                    }
                    int fours = (Integer.parseInt(proteinMax[0]) + Integer.parseInt(carbsMax[0]))*4;
                    int nines = Integer.parseInt(fatMax[0])*9;

                    int total = fours+nines;

                    updateMaxes(proteinMax[0], carbsMax[0], fatMax[0], String.valueOf(total), context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateMaxes(String protein, String carbs, String fat, String calorie, Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, MacrosWidget.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for(int widgetId : allWidgetIds){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.macros);
            remoteViews.setTextViewText(R.id.textView20, protein);
            remoteViews.setTextViewText(R.id.textView23, carbs);
            remoteViews.setTextViewText(R.id.textView26, fat);
            remoteViews.setTextViewText(R.id.textView31, calorie);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public void getData(final Context context){
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        final String[] proteinVal = new String[1];
        final String[] carbVal = new String[1];
        final String[] fatVal = new String[1];

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("currentMacrosProgress").child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(day));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getKey().equals("protein")){
                            proteinVal[0] = dataSnapshot1.getValue().toString();
                        }else if(dataSnapshot1.getKey().equals("carbs")){
                            carbVal[0] = dataSnapshot1.getValue().toString();
                        }else if(dataSnapshot1.getKey().equals("fat")){
                            fatVal[0] = dataSnapshot1.getValue().toString();
                        }
                    }

                    int fours = (Integer.parseInt(proteinVal[0]) + Integer.parseInt(carbVal[0]))*4;
                    int nines = Integer.parseInt(fatVal[0])*9;

                    int total = fours+nines;

                    updateTexts(proteinVal[0], carbVal[0], fatVal[0], context, String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateTexts(String protein, String carbs, String fat, Context context, String calorie){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, MacrosWidget.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for(int widgetId : allWidgetIds){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.macros);
            remoteViews.setTextViewText(R.id.textView21, protein);
            remoteViews.setTextViewText(R.id.textView24, carbs);
            remoteViews.setTextViewText(R.id.textView27, fat);
            remoteViews.setTextViewText(R.id.textView32, calorie);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}

