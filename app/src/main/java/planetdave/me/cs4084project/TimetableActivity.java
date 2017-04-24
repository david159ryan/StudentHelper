package planetdave.me.cs4084project;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TimetableActivity extends AppCompatActivity {

    private static final int NUM_DAYS = 5;
    private SharedPreferences sPrefs;
    private DatabaseHelper db;
    private List<TimetableEntry> entries[];
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private static final String[][] items= {
            {"lorem", "ipsum", "", "dolor", "", "lorem", "ipsum", "", "dolor"},
            {"lorem", "ipsum", "", "dolor", "", "lorem", "ipsum", "", "dolor"},
            {"lorem", "ipsum", "", "dolor", "", "lorem", "ipsum", "", "dolor"},
            {"lorem", "ipsum", "", "dolor", "", "lorem", "ipsum", "", "dolor"},
            {"lorem", "ipsum", "", "dolor", "", "lorem", "ipsum", "", "dolor"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        getScreenDimensions();
        sPrefs = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE);
        db = new DatabaseHelper(getApplicationContext());

        entries = getTimetableEntries();

        ListView dayLists[] = new ListView[5];
        for(int i = 0; i < dayLists.length; i++){
            dayLists[i] = (ListView)findViewById(R.id.timetable_lv_monday + i);
            /*
            dayLists[i].setAdapter(new ArrayAdapter<>(this,
                    R.layout.layout_timetable_entry,
                    R.id.tt_entry_line1,
                    entries[i]));
            //dayLists[i]
            */
            dayLists[i].setAdapter(new DayListAdapter(
                    this,
                    entries[i]
            ));
        }

    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;
    }

    @SuppressWarnings("unchecked")
    private List<TimetableEntry>[] getTimetableEntries() {
        String currentUserKey = getString(R.string.current_user_key);
        String id = sPrefs.getString(currentUserKey, "");
        Cursor c = db.getReadableDatabase().rawQuery("SELECT * FROM timetable", null);

        System.out.println("SELECT * FROM timetable");

        ArrayList<TimetableEntry>[] entries = new ArrayList[NUM_DAYS];
        for(int i = 0 ; i < NUM_DAYS; i ++){
            entries[i] = new ArrayList<>();
        }

        while(c.moveToNext()){
            int day = Integer.parseInt(c.getString(c.getColumnIndex("_day")));
            int start = Integer.parseInt(c.getString(c.getColumnIndex("_start")));
            int end = Integer.parseInt(c.getString(c.getColumnIndex("_end")));
            int duration = end - start;
            String module = c.getString(c.getColumnIndex("_module"));
            String type = c.getString(c.getColumnIndex("_type"));
            String group = c.getString(c.getColumnIndex("_group"));
            String room = c.getString(c.getColumnIndex("_room"));

            entries[day].add(new TimetableEntry(
                id, start, duration, module, type, group, room
            ));

            System.out.println();
        }
        c.close();
        return entries;
    }

    private class DayListAdapter extends ArrayAdapter<TimetableEntry>{

        private Context context;
        int nextItemY = 0;

        DayListAdapter(@NonNull Context context, @NonNull List<TimetableEntry> objects) {
            super(context, R.layout.layout_timetable_entry, R.id.menu_selection, objects);
            this.context = context;
            System.out.println("in daylistadapter constructor");
            System.out.println("item count: " + getCount());
        }

        //TODO create a class for a timetable entry and get this thing to use it properly
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EntryHolder holder = null;
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

            int duration = getItem(position).getDuration();

            holder.module.setText(getItem(position).getModule());
            holder.type.setText(getItem(position).getType());
            holder.group.setText(getItem(position).getGroup());
            holder.room.setText(getItem(position).getRoom());

            int minHeight = getMinHeight(duration);
            row.setMinimumHeight(minHeight);
            row.setY((getItem(position).getStartTime() - 9) * minHeight - nextItemY);
            nextItemY += minHeight;
            System.out.println((getItem(position).getStartTime() - 9) * minHeight);
            row.setTag(holder);
            return row;
        }

        private int getMinHeight(int duration) {
            int frameHeight = (int)getResources().getDimension(R.dimen.tt_frame_height);
            int containerHeight = findViewById(R.id.tt_scroll_view).getHeight();
            int height = (containerHeight - frameHeight)*duration/9;
            return height;
        }

    }
    private static class EntryHolder{
        TextView module;
        TextView type;
        TextView group;
        TextView room;
    }
}
