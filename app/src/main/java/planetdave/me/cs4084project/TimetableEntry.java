package planetdave.me.cs4084project;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by David on 24/04/2017.
 *
 */

public class TimetableEntry implements Parcelable{

    private String id;
    private int startTime;
    private int day;
    private int duration;
    private String module;
    private String type;
    private String group;
    private String room;
    private int colour;

    TimetableEntry(){
        id = "";
        duration = 1;
    }

    TimetableEntry(@NonNull String id, int day, int startTime, int duration,
                   @NonNull String module, @NonNull  String type, @NonNull String group,
                   @NonNull String room) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.duration = duration;
        this.module = module;
        this.type = type;
        this.group = group;
        this.room = room;
    }

    protected TimetableEntry(Parcel in) {
        id = in.readString();
        startTime = in.readInt();
        day = in.readInt();
        duration = in.readInt();
        module =in.readString();
        type = in.readString();
        group = in.readString();
        room = in.readString();
        colour = in.readInt();
    }

    public static final Creator<TimetableEntry> CREATOR = new Creator<TimetableEntry>() {
        @Override
        public TimetableEntry createFromParcel(Parcel in) {
            return new TimetableEntry(in);
        }

        @Override
        public TimetableEntry[] newArray(int size) {
            return new TimetableEntry[size];
        }
    };

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

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

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(startTime);
        dest.writeInt(day);
        dest.writeInt(duration);
        dest.writeString(module);
        dest.writeString(type);
        dest.writeString(group);
        dest.writeString(room);
        dest.writeInt(colour);
    }
}
