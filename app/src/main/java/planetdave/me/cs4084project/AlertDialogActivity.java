package planetdave.me.cs4084project;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlertDialogActivity extends AppCompatActivity {
    AlertDialog.Builder dialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getBooleanExtra(getString(R.string.app_exit_key), false)){
            finish();
            System.out.println("should finish here");
            return;
        }
        setContentView(R.layout.activity_alert_dialog);

        dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setTitle(getIntent().getStringExtra(getString(R.string.alert_dialog_title_key)));
        dialog.setMessage(getIntent().getStringExtra(getString(R.string.alert_dialog_message_key)));
        dialog.setNeutralButton("Thanks Dave!",null);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onBackPressed();
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(null, LoginActivity.class));
        Intent a = new Intent(this.getApplicationContext(), AlertDialogActivity.class);
        finish();
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        a.putExtra(getString(R.string.app_exit_key), true);
        startActivity(a);
    }
}
