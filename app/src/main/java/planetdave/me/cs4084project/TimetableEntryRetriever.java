package planetdave.me.cs4084project;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dave on 29/04/2017.
 *
 */

final class TimetableEntryRetriever {

    private static int currentColour = 0;
    static int NUM_DAYS = 5;
    static Map<String, Integer> colours = new HashMap<>();


    @SuppressWarnings("unchecked")
    static List<TimetableEntry>[] getTimetableEntries(Context context) {

        SharedPreferences sPrefs = context.getSharedPreferences(
                context.getString(R.string.shared_preferences),
                MODE_PRIVATE);
        DatabaseHelper db = new DatabaseHelper(context.getApplicationContext());

        String currentUserKey = context.getString(R.string.current_user_key);
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
                int color = getModuleColour(module, context);
                while(startTime < start){
                    //entries[day].add(new TimetableEntry());
                    entries[day].add(null);
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
        db.close();
        currentColour = 0;
        return entries;
    }


    static int getModuleColour(String module, Context context){
        if(!colours.containsKey(module)){
            colours.put(module, context.getResources().getIdentifier(
                    ("tt_background_colour_" + currentColour),
                    "color",
                    context.getPackageName()
            ));
            currentColour++;
        }
        return colours.get(module);
    }
}
