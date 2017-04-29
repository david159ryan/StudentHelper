package planetdave.me.cs4084project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;

/**
 * Created by David on 24/04/2017.
 *
 */

public class TimetableEntry implements Parcelable{

    private AlarmManager alarmManager = null;
    private PendingIntent pIntent = null;
    private boolean shouldActivateAlarm = true;

    private String id;
    private int startTime;
    private int day;
    private int duration;
    private String module;
    private String type;
    private String group;
    private String room;
    private int colour;

    TimetableEntry(){
        id = "";
        duration = 1;
    }

    TimetableEntry(@NonNull String id, int day, int startTime, int duration,
                   @NonNull String module, @NonNull  String type, @NonNull String group,
                   @NonNull String room, int color) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.duration = duration;
        this.module = module;
        this.type = type;
        this.group = group;
        this.room = room;
        this.colour = color;
    }

    protected TimetableEntry(Parcel in) {
        id          = in.readString();
        startTime   = in.readInt();
        day         = in.readInt();
        duration    = in.readInt();
        module      = in.readString();
        type        = in.readString();
        group       = in.readString();
        room        = in.readString();
        colour      = in.readInt();
    }

    public static final Creator<TimetableEntry> CREATOR = new Creator<TimetableEntry>() {
        @Override
        public TimetableEntry createFromParcel(Parcel in) {
            return new TimetableEntry(in);
        }

        @Override
        public TimetableEntry[] newArray(int size) {
            return new TimetableEntry[size];
        }
    };

    public int getDay() {
        return day;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }
    @NonNull
    public String getModule() {
        return module;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public String getGroup() {
        return group;
    }

    @NonNull
    public String getRoom() {
        return room;
    }

    public int getColour() {
        return colour;
    }


    public boolean shouldActivateAlarm(){
        return shouldActivateAlarm;
    }

    public void setAlarmEnabled(boolean enabled){
        shouldActivateAlarm = enabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(startTime);
        dest.writeInt(day);
        dest.writeInt(duration);
        dest.writeString(module);
        dest.writeString(type);
        dest.writeString(group);
        dest.writeString(room);
        dest.writeInt(colour);
    }

    public void setAlarm(Context context) {
        if(alarmManager == null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY + day);
            calendar.set(Calendar.HOUR_OF_DAY, startTime - 1);
            calendar.set(Calendar.MINUTE, 45);

            if(calendar.getTimeInMillis() < System.currentTimeMillis() - AlarmManager.INTERVAL_HOUR){
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(
                    Context.ALARM_SERVICE
            );

            System.out.println(calendar.getTime().toString());

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(context.getString(R.string.timetable_entry_info_key), this);
            intent.setAction(context.getString(R.string.timetable_alarm_set_action));
            intent.addCategory(context.getString(R.string.alarm_category));
            pIntent = PendingIntent.getBroadcast(
                    context, 0, intent, 0
            );
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pIntent);

        }
    }

    public void cancelAlarm(){
        if(alarmManager != null){
            alarmManager.cancel(pIntent);
        }
    }
}
