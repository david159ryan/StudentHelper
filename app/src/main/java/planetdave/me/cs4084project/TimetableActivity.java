package planetdave.me.cs4084project;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TimetableActivity extends AppCompatActivity {


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

        ListView dayLists[] = new ListView[5];
        for(int i = 0; i < dayLists.length; i++){
            dayLists[i] = (ListView)findViewById(R.id.timetable_lv_monday + i);
            dayLists[i].setAdapter(new ArrayAdapter<>(this,
                    R.layout.layout_timetable_entry,
                    R.id.tt_entry_line1,
                    items[i]));
            //dayLists[i]
        }

    }

    private class DayListAdapter extends ArrayAdapter<String>{

        private String items[];
        private Context context;

        public DayListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String[] objects) {
            super(context, resource, objects);
        }

        //TODO create a class for a timetable entry and get this thing to use it properly
        @NonNull
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_timetable_entry, parent, false);
            }
            return convertView;
        }
    }
}
