package planetdave.me.cs4084project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by Dave on 29/04/2017.
 *
 */


public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        List<TimetableEntry> entries[] = TimetableEntryRetriever.getTimetableEntries(context);
        for(List<TimetableEntry> day : entries){
            for(TimetableEntry entry : day){
                if(entry != null) {
                    entry.setAlarm(context);
                }
            }
        }
    }
}

