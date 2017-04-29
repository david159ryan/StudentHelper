package planetdave.me.cs4084project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class TimetableEntryInfoActivity extends AppCompatActivity {

    DatabaseHelper db;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_entry_info);

        if (savedInstanceState != null){
            return;
        }
        db = new DatabaseHelper(getApplicationContext());
        Bundle b = this.getIntent().getExtras();
        final TimetableEntry t = b.getParcelable(getString(R.string.timetable_entry_info_key));


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
        Room r = new Room(t.getRoom(), db);

        LinearLayout layout = (LinearLayout)getWindow().getDecorView()
                .findViewById(R.id.te_info_layout);
        layout.setBackgroundColor(getResources().getColor(t.getColour()));
        //shape.setColor(t.getColour());
        //getWindow().getDecorView().setBackground(shape);

        TextView day = (TextView)findViewById(R.id.te_textview_day);
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

        day.setText(getString(R.string.tt_mon_long + t.getDay()));
        code.setText(t.getModule());
        title.setText(moduleTitle);
        type.setText(t.getType());
        group.setText(t.getGroup());
        building.setText(r.getBuilding());
        buildingCode.setText("(" + r.getBuildingCode() + ")");
        floor.setText(r.getFloor());
        room.setText(r.getRoom());

        mCursor.close();
    }

    @Override
    public void onBackPressed() {
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
        if(this.isTaskRoot()){
            //startActivity(new Intent(null, LoginActivity.class));
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
        }else{
            super.onBackPressed();
        }
    }
}
