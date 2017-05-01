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
        String userKey = context.getString(R.string.current_user_key);
        String noUser = context.getString(R.string.no_current_user);
        String prefKey = context.getString(R.string.shared_preferences);

        /* only set alarms if a user is logged in */
        if(!context.getSharedPreferences(prefKey, Context.MODE_PRIVATE)
                .getString(userKey, noUser).equals(noUser)){
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
}

