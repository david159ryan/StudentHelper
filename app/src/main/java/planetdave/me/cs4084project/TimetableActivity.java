package planetdave.me.cs4084project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    private static final int NUM_DAYS = 5;
    private SharedPreferences sPrefs;
    private DatabaseHelper db;
    private int currentColour = 0;
    private Map<String, Integer> colours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        colours = new HashMap<>();

        //getScreenDimensions();
        sPrefs = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE);
        db = new DatabaseHelper(getApplicationContext());

        List<TimetableEntry>[] entries = getTimetableEntries();

        ListView dayLists[] = new ListView[5];
        for(int i = 0; i < dayLists.length; i++){
            dayLists[i] = (ListView)findViewById(R.id.timetable_lv_monday + i);
            dayLists[i].setAdapter(new DayListAdapter(
                    this,
                    entries[i]
            ));
            dayLists[i].setOnItemClickListener(this);
        }

    }
/*
    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;
    }
*/
    @SuppressWarnings("unchecked")
    private List<TimetableEntry>[] getTimetableEntries() {
        String currentUserKey = getString(R.string.current_user_key);
        String id = sPrefs.getString(currentUserKey, "");

        ArrayList<TimetableEntry>[] entries = new ArrayList[NUM_DAYS];
        for(int i = 0 ; i < NUM_DAYS; i ++){
            entries[i] = new ArrayList<>();
        }

        for(int i = 0; i < NUM_DAYS; i ++){
            Cursor c = db.getReadableDatabase().rawQuery("SELECT * FROM timetable " +
                            "WHERE _id = '"+id+"' AND _day = "+ i +" " +
                            "ORDER BY _day, _start ASC", null);
            int startTime = 9;

            while(c.moveToNext()){

                int day = Integer.parseInt(c.getString(c.getColumnIndex("_day")));
                int start = Integer.parseInt(c.getString(c.getColumnIndex("_start")));
                int end = Integer.parseInt(c.getString(c.getColumnIndex("_end")));
                int duration = end - start;
                String module = c.getString(c.getColumnIndex("_module"));
                String type = c.getString(c.getColumnIndex("_type"));
                String group = c.getString(c.getColumnIndex("_group"));
                String room = c.getString(c.getColumnIndex("_room"));
                int color = getModuleColour(module);
                while(startTime < start){
                    entries[day].add(new TimetableEntry());
                    startTime++;
                }
                entries[day].add(new TimetableEntry(
                        id, day, start, duration, module, type, group, room, color
                ));
                startTime += duration;
                System.out.println();
            }
            c.close();
        }


        return entries;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TimetableEntry t = (TimetableEntry)parent.getItemAtPosition(position);

        Intent intent = new Intent(this, TimetableEntryInfoActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(getString(R.string.timetable_entry_info_key), t);
        intent.putExtras(b);
        startActivity(intent);
    }

    public int getModuleColour(String module){
        if(!colours.containsKey(module)){
            colours.put(module, getResources().getIdentifier(
                    ("tt_background_colour_" + currentColour),
                    "color",
                    getPackageName()
            ));
            currentColour++;
        }
        return colours.get(module);
    }

    private class DayListAdapter extends ArrayAdapter<TimetableEntry>{

        private Context context;

        DayListAdapter(@NonNull Context context, @NonNull List<TimetableEntry> objects) {
            super(context, R.layout.layout_timetable_entry, R.id.menu_selection, objects);
            this.context = context;
        }



        //TODO create a class for a timetable entry and get this thing to use it properly
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EntryHolder holder;
            System.out.println("In getView");
            View row = convertView;


            if (row == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.layout_timetable_entry, parent, false);

                holder = new EntryHolder();
                holder.module = (TextView)row.findViewById(R.id.tt_entry_module);
                holder.type = (TextView)row.findViewById(R.id.tt_entry_type);
                holder.group = (TextView)row.findViewById(R.id.tt_entry_group);
                holder.room = (TextView)row.findViewById(R.id.tt_entry_room);

            }else{
                holder = (EntryHolder)convertView.getTag();
            }

            TimetableEntry item = getItem(position);
            int duration = item.getDuration();
            int minHeight = getMinHeight();
            row.setMinimumHeight(minHeight * duration);

            if(item.getId().equals("")){
                row.setVisibility(View.GONE);
            }else{
                holder.module.setText(item.getModule());
                holder.type.setText(item.getType());
                holder.group.setText(item.getGroup());
                holder.room.setText(item.getRoom());

                GradientDrawable shape = (GradientDrawable)row.getBackground();
                shape.setColor(getResources().getColor(item.getColour()));
                row.setBackground(shape);
            }

            row.setTag(holder);
            return row;
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
