package com.rao.multiaccountcredentials.Models;



import android.os.Parcel;
import android.os.Parcelable;

public class Field implements Parcelable {
    private String name;        // Field name
    private boolean isOptional; // Is the field optional?
    private int inputType;      // Input type for the field (e.g., text, email)

    public Field(String name, boolean isOptional, int inputType) {
        this.name = name;
        this.isOptional = isOptional;
        this.inputType = inputType;
    }

    // Getters
    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public int getInputType() {
        return inputType;
    }

    // Parcelable implementation
    protected Field(Parcel in) {
        name = in.readString();
        isOptional = in.readByte() != 0;
        inputType = in.readInt();
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (isOptional ? 1 : 0));
        dest.writeInt(inputType);
    }
}
