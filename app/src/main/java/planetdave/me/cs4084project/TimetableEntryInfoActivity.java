package planetdave.me.cs4084project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Retrieves and displays the information for a module
 */
public class TimetableEntryInfoActivity extends AppCompatActivity {

    DatabaseHelper db;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String days[] = getResources().getStringArray(R.array.days_long);
        /* if this activity is launched by a notification while the app isn't running,
         * this activity will relaunch itself with a clear activity stack and then immediately
         * finish
         */
        if(getIntent().getBooleanExtra(getString(R.string.app_exit_key), false)){
            finish();
            return;
        }
        setContentView(R.layout.activity_timetable_entry_info);


        db = new DatabaseHelper(getApplicationContext());
        Bundle b = this.getIntent().getExtras();

        /* retrieve timetable entry to display */
        final TimetableEntry t = b.getParcelable(getString(R.string.timetable_entry_info_key));

        /* disaster! */
        if(t == null){
            finish();
            return;
        }

        /* retrieves the module information */
        Cursor mCursor = db.getReadableDatabase().rawQuery("SELECT * FROM " +
                        getString(R.string.db_table_module_details) +
                        " WHERE " + getString(R.string.db_module_id) + " = '" +
                        t.getModule() + "';",
                null);

        mCursor.moveToFirst();
        String moduleTitle = mCursor.getString(
                mCursor.getColumnIndex(
                        getString(R.string.db_module_title)
                )
        );


        Room r = new Room(t.getRoom(), this);

        LinearLayout layout = (LinearLayout)getWindow().getDecorView()
                .findViewById(R.id.te_info_layout);
        layout.setBackgroundColor(ContextCompat.getColor(this, t.getColour()));

        TextView day = (TextView)findViewById(R.id.te_textview_day);
        TextView time = (TextView)findViewById(R.id.te_textview_time);
        TextView code = (TextView)findViewById(R.id.te_textview_code);
        TextView title = (TextView)findViewById(R.id.te_textview_title);
        TextView type = (TextView)findViewById(R.id.te_textview_type);
        TextView group = (TextView)findViewById(R.id.te_textview_group);
        TextView building = (TextView)findViewById(R.id.te_textview_building);
        TextView buildingCode = (TextView)findViewById(R.id.te_textview_building_code);
        TextView floor = (TextView)findViewById(R.id.te_textview_floor_num);
        TextView room = (TextView)findViewById(R.id.te_textview_room_num);

        Button details = (Button)findViewById(R.id.te_button_details);

        String colorName = getResources().getResourceName(t.getColour());
        int buttonColor = getResources().getIdentifier(colorName + "_light", "color",
                getPackageName());
        details.setBackgroundColor(buttonColor);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimetableEntryInfoActivity.this,
                        ModuleInfoActivity.class);
                intent.putExtra(getString(R.string.module_info_code_key), t.getModule());
                intent.putExtra(getString(R.string.module_info_colour_key), t.getColour());
                startActivity(intent);
            }
        });

        String timeString = t.getStartTime() + ":00";

        day.setText(days[t.getDay()]);
        time.setText(timeString);
        code.setText(t.getModule());
        title.setText(moduleTitle);
        type.setText(t.getType());
        group.setText(t.getGroup());
        building.setText(r.getBuilding());
        buildingCode.setText("(" + r.getBuildingCode() + ")");
        floor.setText(r.getFloor());
        room.setText(r.getRoom());

        db.close();
        mCursor.close();
    }

    @Override
    public void onBackPressed() {
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
        if(this.isTaskRoot()){
            //startActivity(new Intent(null, LoginActivity.class));
            Intent a = new Intent(this.getApplicationContext(), TimetableEntryInfoActivity.class);
            finish();
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
            a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.putExtra(getString(R.string.app_exit_key), true);
            startActivity(a);
        }else{
            super.onBackPressed();
        }
    }
}
