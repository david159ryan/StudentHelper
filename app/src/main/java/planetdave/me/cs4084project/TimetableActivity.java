package planetdave.me.cs4084project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class TimetableActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    //TODO remove this
    TimetableEntry tEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        SharedPreferences sPrefs = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE);

        List<TimetableEntry>[] entries = TimetableEntryRetriever.getTimetableEntries(this);

        ListView dayLists[] = new ListView[5];
        for(int i = 0; i < dayLists.length; i++){
            dayLists[i] = (ListView)findViewById(R.id.timetable_lv_monday + i);
            dayLists[i].setAdapter(new DayListAdapter(
                    this,
                    entries[i]
            ));
            dayLists[i].setOnItemClickListener(this);
        }
        if(!sPrefs.getBoolean(getString(R.string.alarms_set_key), false)){
            setAlarms(entries);
            sPrefs.edit().putBoolean(getString(R.string.alarms_set_key), true).apply();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
        TimetableEntry t = (TimetableEntry)parent.getItemAtPosition(position);

        Intent intent = new Intent(this, TimetableEntryInfoActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(getString(R.string.timetable_entry_info_key), t);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void setAlarms(List<TimetableEntry>[] entries) {
        Context context = TimetableActivity.this;
        for (List<TimetableEntry> entry : entries) {
            for (TimetableEntry e : entry) {
                if(e != null){
                    e.setAlarm(context);
                }
            }
        }
        //TODO testing
       // setTestAlarm();
        //TODO end testing
        Toast.makeText(this, "Alarms set", Toast.LENGTH_LONG).show();
    }

    //TODO remove this
    private void setTestAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);

        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(
                Context.ALARM_SERVICE
        );

        tEntry = new TimetableEntry(
                "0867284", 0, 4, 1, "CS4084", "LEC", "NA", "CSG001",
                TimetableEntryRetriever.getModuleColour("CS4084", this)
        );
        tEntry.setAlarm(this);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(getString(R.string.timetable_entry_info_key), tEntry);

        intent.setAction(getString(R.string.timetable_alarm_set_action));
        intent.addCategory(getString(R.string.alarm_category));
        PendingIntent pIntent = PendingIntent.getBroadcast(
                this, 0, intent, 0
        );
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    private class DayListAdapter extends ArrayAdapter<TimetableEntry>{

        private Context context;

        DayListAdapter(@NonNull Context context, @NonNull List<TimetableEntry> objects) {
            super(context, R.layout.layout_timetable_entry, R.id.menu_selection, objects);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            EntryHolder holder;


            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_timetable_entry, parent, false);

                holder = new EntryHolder();
                holder.module = (TextView)convertView.findViewById(R.id.tt_entry_module);
                holder.type = (TextView)convertView.findViewById(R.id.tt_entry_type);
                holder.group = (TextView)convertView.findViewById(R.id.tt_entry_group);
                holder.room = (TextView)convertView.findViewById(R.id.tt_entry_room);
                TimetableEntry item = getItem(position);
                int minHeight = getMinHeight();

                if(item == null){
                    convertView.setMinimumHeight(minHeight);
                    convertView.setVisibility(View.GONE);
                }else{
                    int duration = item.getDuration();
                    convertView.setMinimumHeight(minHeight * duration);


                    holder.module.setText(item.getModule());
                    holder.type.setText(item.getType());
                    holder.group.setText(item.getGroup());
                    holder.room.setText(item.getRoom());

                    GradientDrawable shape = (GradientDrawable)convertView.getBackground();
                    shape.setColor(ContextCompat.getColor(context, item.getColour()));
                    convertView.setBackground(shape);
                }
            }else{
                holder = (EntryHolder)convertView.getTag();
            }

            convertView.setTag(holder);
            return convertView;
        }

        private int getMinHeight() {
            int frameHeight = (int)getResources().getDimension(R.dimen.tt_frame_height);
            double containerHeight = findViewById(R.id.tt_scroll_view).getHeight();
            return (int)Math.ceil((containerHeight - frameHeight)/9);
        }

    }

    private static class EntryHolder{
        TextView module;
        TextView type;
        TextView group;
        TextView room;
    }

}
