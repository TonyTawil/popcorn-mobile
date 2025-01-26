package com.example.popcorn.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    private String name;
    private String role;
    private String profile_path;

    public Person(String name, String role, String profile_path) {
        this.name = name;
        this.role = role;
        this.profile_path = profile_path;
    }

    protected Person(Parcel in) {
        name = in.readString();
        role = in.readString();
        profile_path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(role);
        dest.writeString(profile_path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getImageUrl() {
        return profile_path;
    }
}
