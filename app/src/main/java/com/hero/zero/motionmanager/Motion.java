package com.hero.zero.motionmanager;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shadk on 06.12.2015.
 */
public class Motion implements Parcelable {
    private String mName = null;
    private ArrayList<Point> mPoints = null;
    private int mCursorPosition = -1;

    public Motion() {
        mPoints = new ArrayList <Point> ();
    }

    public Motion(Parcel in) {
        mPoints = new ArrayList <Point> ();
        Object [] pointsObj = in.readParcelableArray(Point.class.getClassLoader());
        for (int i = 0; i < pointsObj.length; ++i) {
            mPoints.add((Point)pointsObj[i]);
        }
    }

    public int getPosition() {
        return mCursorPosition;
    }
    public void addPoint(float x, float y, float z) {
        mPoints.add(new Point(x, y ,z));
    }

    public void setName(String name) {
        mName = new String(name);
    }

    public boolean begin() {
        if (mPoints.size() > 0) {
            mCursorPosition = 0;
            return true;
        } else {
            mCursorPosition = -1;
            return false;
        }
    }

    public void clear() {
        mPoints.clear();
        mCursorPosition = -1;
    }

    public int size() {
        return mPoints.size();
    }

    public boolean next() {
        if (mPoints.size() != 0 && mCursorPosition < mPoints.size() - 1) {
            mCursorPosition++;
            return true;
        } else return false;
    }

    public float getXCord() throws Exception {
        if (mCursorPosition != -1) {
            return (mPoints.get(mCursorPosition)).x;
        } else throw new Exception("Motion cords array is empty");
    }

    protected ArrayList getPointsArray() {
        return mPoints;
    }

    public float getYCord() throws Exception {
        if (mCursorPosition != -1) {
            return (mPoints.get(mCursorPosition)).y;
        } else throw new Exception("Motion cords array is empty");
    }

    public float getZCord() throws Exception {
        if (mCursorPosition != -1) {
            return (mPoints.get(mCursorPosition)).z;
        } else throw new Exception("Motion cords array is empty");
    }

    @Override
    public String toString() {
        return (mName == null) ? "No name" : mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Object [] pointsObj = mPoints.toArray();
        Point [] points = new Point [pointsObj.length];
        for (int i = 0; i < pointsObj.length; ++i) {
            points[i] = (Point) pointsObj[i];
        }

        dest.writeParcelableArray(points , flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Motion createFromParcel(Parcel in) {
            return new Motion(in);
        }

        public Motion[] newArray(int size) {
            return new Motion[size];
        }
    };


}

class Point implements Parcelable{
    public float x,y,z;

    Point(float x, float y, float z) {
        setup(x, y, z);
    }

    private void setup(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Point(Parcel in) {
        float[] data = new float[3];
        in.readFloatArray(data);
        setup(data[0], data[1], data[2]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(new float[] {this.x, this.y, this.z} );
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        public Point[] newArray(int size) {
            return new Point[size];
        }
    };
}
