package planetdave.me.cs4084project;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver{
    private String silenceAction;
    private String unSilenceAction;
    private Bundle bundle;
    private TimetableEntry entry;

    private static final int SILENCE_CODE = 9999;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String alarmAction = context.getString(R.string.timetable_alarm_set_action);
        silenceAction = context.getString(R.string.timetable_silence_action);
        unSilenceAction = context.getString(R.string.timetable_un_silence_action);

        bundle = intent.getExtras();
        entry = bundle.getParcelable(context.getString(R.string.timetable_entry_info_key));

        if(entry != null){
            if (action.equals(alarmAction)) {
                upcomingAlarmReceived(context, intent);
            } else if (action.equals(silenceAction)) {
                silenceAlarmReceived(context);
            } else if (action.equals(unSilenceAction)) {
                unSilenceAlarmReceived(context, intent);
            }
        }
    }

    private void upcomingAlarmReceived(Context context, Intent intent) {

        if (entry == null ){
            return;
        }
        //Toast.makeText(context, "alarm broadcast received", Toast.LENGTH_LONG).show();
        Intent resultIntent = new Intent(context, TimetableEntryInfoActivity.class);
        resultIntent.putExtras(bundle);
        PendingIntent notificationIntent = PendingIntent.getActivity(
                context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(entry.getModule() + " " + entry.getType() + " : " + entry.getRoom())
                .setContentText("starts in 15 minutes")
                .setSmallIcon(R.mipmap.ic_student_helper)
                .setAutoCancel(true)
                .setVibrate(new long[] {0, 1000, 200,1000 })
                .setLights(Color.MAGENTA, 500, 500)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(notificationIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());

        setSilenceAlarm(context);
    }

    private void setSilenceAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 15);
        //calendar.add(Calendar.MINUTE, 1);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        Intent newIntent = new Intent(context, AlarmReceiver.class);
        newIntent.putExtra(context.getString(R.string.timetable_entry_info_key), entry);
        newIntent.setAction(silenceAction);
        PendingIntent pIntent = PendingIntent.getBroadcast(context,
                SILENCE_CODE, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    private void silenceAlarmReceived(Context context) {

        Intent intent = new Intent(context, AlertDialogActivity.class);
        intent.putExtra(context.getString(R.string.alert_dialog_title_key),
                context.getString(R.string.alert_dialog_title));
        intent.putExtra(context.getString(R.string.alert_dialog_message_key),
                context.getString(R.string.alert_dialog_silence));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int previousWringerMode = audioManager.getRingerMode();
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        setUnSilenceAlarm(context, previousWringerMode);
    }

    private void setUnSilenceAlarm(Context context, int previousWringerMode) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, entry.getDuration() * 60 - 10);
       // calendar.add(Calendar.MINUTE, entry.getDuration());

        //calendar.add(Calendar.MINUTE, 1);

        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(unSilenceAction);
        intent.putExtra(context.getString(R.string.ringer_value_intent_key), previousWringerMode);
        intent.putExtra(context.getString(R.string.timetable_entry_info_key), entry);
        PendingIntent pIntent = PendingIntent.getBroadcast(context,
                SILENCE_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    private void unSilenceAlarmReceived(Context context, Intent intent) {

        Intent newIntent = new Intent(context, AlertDialogActivity.class);
        newIntent.putExtra(context.getString(R.string.alert_dialog_title_key),
                context.getString(R.string.alert_dialog_title));
        newIntent.putExtra(context.getString(R.string.alert_dialog_message_key),
                context.getString(R.string.alert_dialog_un_silence));
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(newIntent);

        int RINGER_MODE = intent.getIntExtra(context.getString(R.string.ringer_value_intent_key),
                AudioManager.RINGER_MODE_NORMAL);
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(RINGER_MODE);
    }

}
