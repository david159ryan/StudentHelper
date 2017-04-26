package planetdave.me.cs4084project;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;

public class TimetableEntryInfoActivity extends AppCompatActivity {

    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_entry_info);

        db = new DatabaseHelper(getApplicationContext());
        Bundle b = this.getIntent().getExtras();
        TimetableEntry t = b.getParcelable(getString(R.string.timetable_entry_info_key));


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

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(this, null);
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
}
