package com.suchbeacon.android;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by david on 2/15/14.
 */
public class InfoControlWidget extends AppWidgetProvider {

    public static final String STOP_START_BUTTON = "SERVICE_STOP_START_BUTTON";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.eyebeacon_appwidget);
            Intent intent = new Intent(STOP_START_BUTTON);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.serviceToggle, pendingIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (STOP_START_BUTTON.equals(intent.getAction())) {
            PendingIntent stopBeaconService = Util.getStopServicePendingIntent(context);
            try {
                stopBeaconService.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }

    }
}
