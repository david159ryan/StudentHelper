package planetdave.me.cs4084project;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlertDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setTitle(getIntent().getStringExtra(getString(R.string.alert_dialog_title_key)));
        dialog.setMessage(getIntent().getStringExtra(getString(R.string.alert_dialog_title_key)));
        dialog.setNeutralButton("Thanks Dave!",null);
        final AlertDialog alert = dialog.create();
        alert.show();
        finish();
    }
}
