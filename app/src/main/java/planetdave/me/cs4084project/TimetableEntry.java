package planetdave.me.cs4084project;

import android.support.annotation.NonNull;

import java.sql.Time;

/**
 * Created by David on 24/04/2017.
 */

public class TimetableEntry {

    private String id;
    private int startTime;
    private int duration;
    private String module;
    private String type;
    private String group;
    private String room;

    @NonNull
    public String getId() {
        return id;
    }

    public int getStartTime() {
        return startTime;
    }
    public int getDuration() {
        return duration;
    }
    @NonNull
    public String getModule() {
        return module;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public String getGroup() {
        return group;
    }

    @NonNull
    public String getRoom() {
        return room;
    }

    TimetableEntry(){
        id = "";
        duration = 1;
    }

    TimetableEntry(@NonNull String id, int startTime, int duration,
                          @NonNull String module, @NonNull  String type, @NonNull String group,
                          @NonNull String room) {
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
        this.module = module;
        this.type = type;
        this.group = group;
        this.room = room;
    }
}
