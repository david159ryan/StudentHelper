package planetdave.me.cs4084project;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ModuleInfoActivity extends AppCompatActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_info);

        String module = getIntent().getStringExtra(getString(R.string.module_info_code_key));
        int colour = getIntent().getIntExtra(getString(R.string.module_info_colour_key),0);

        Toolbar toolbar = (Toolbar)findViewById(R.id.mi_toolbar);
        TextView title = (TextView)findViewById(R.id.mi_textview_title);
        TextView description = (TextView)findViewById(R.id.mi_textview_description);
        TextView lecturer = (TextView)findViewById(R.id.mi_textview_lecturer);

        ModuleInfo moduleInfo = new ModuleInfo(module, getApplicationContext());
        toolbar.setBackgroundColor(getResources().getColor(colour));
        toolbar.setTitle(moduleInfo.getCode());
        title.setText(moduleInfo.getTitle());
        description.setText(moduleInfo.getDescription());
        lecturer.setText(moduleInfo.getLecturer());
    }

    @Override
    public void onBackPressed() {
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
        finish();
    }
}