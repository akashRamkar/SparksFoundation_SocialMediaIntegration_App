package com.example.socialmedialogin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private Uri profileImage;
    private String userName;

    public User(Uri profileImage, String userName) {
        this.profileImage = profileImage;
        this.userName = userName;
    }

    protected User(Parcel in) {
        profileImage = in.readParcelable(Uri.class.getClassLoader());
        userName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public Uri getProfileImage() {
        return profileImage;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(profileImage, i);
        parcel.writeString(userName);
    }
}
