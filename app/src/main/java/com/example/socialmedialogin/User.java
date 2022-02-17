package com.example.socialmedialogin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
    private Uri profileImage;
    private String userName;
    private String email;

    protected User(Parcel in) {
        profileImage = in.readParcelable(Uri.class.getClassLoader());
        userName = in.readString();
        email = in.readString();
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

    public String getEmail() {
        return email;
    }

    public Uri getProfileImage() {
        return profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public User(Uri profileImage, String userName, String email) {
        this.profileImage = profileImage;
        this.userName = userName;
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(profileImage, flags);
        dest.writeString(userName);
        dest.writeString(email);
    }
}
