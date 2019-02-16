package sallycs.com.pomoapp;

import android.os.Parcel;
import android.os.Parcelable;

//Creates the Task class that allows for creation of Task Object

public class Task implements Parcelable {
    private String name;
    private String description;
    private int time;

    public Task(String head) {
        this.name = head;
    }
    public Task(String head, String description) {
        this.name = head;
        this.description = description;
    }

    public Task(String head, String description, int length){
        this.name = head;
        this.description = description;
        this.time = length;
    }

    public Task(String head, int length){
        this.name = head;
        this.time = length;
    }

    protected Task(Parcel in) {
        name = in.readString();
        description = in.readString();
        time = in.readInt();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String n){
        this.name = n;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getTime(){return Integer.toString(time);}

    public void setTime(int n){
        this.time = n;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeInt(time);
    }

}
