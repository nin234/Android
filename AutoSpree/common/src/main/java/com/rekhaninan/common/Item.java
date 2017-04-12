package com.rekhaninan.common;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by nin234 on 9/4/16.
 */
public class Item implements Parcelable  {

    private final String TAG = "Item";
    private  String name;
    private String make;
    private String model;
    private String color;
    private int year;
    private double price;
    private int miles;
    private String street;
    private String city;
    private String state;
    private String zip;
    private double latitude;
    private double longitude;
    private String notes;

    private String album_name;
    private double area;
    private double beds;
    private double baths;

    private String picurl;
    private boolean hidden;
    private int date;
    private int current;
    private int rowno;
    private String item;
    private boolean selected;
    private long share_id;
    private String nickname;
    private int phoneno;
    private String email;
    private String share_name;
    private int start_month;
    private int end_month;
    private int inventory;

    public int getStart_month() {
        return start_month;
    }

    public void setStart_month(int start_month) {
        this.start_month = start_month;
    }

    public int getEnd_month() {
        return end_month;
    }

    public void setEnd_month(int end_month) {
        this.end_month = end_month;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public String getShare_name() {
        return share_name;
    }

    public void setShare_name(String share_name) {
        this.share_name = share_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(int phoneno) {
        this.phoneno = phoneno;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getShare_id() {
        return share_id;
    }

    public void setShare_id(long share_id) {
        this.share_id = share_id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getRowno() {
        return rowno;
    }

    public void setRowno(int rowno) {
        this.rowno = rowno;
    }


    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }



    public Item()
    {
        share_id =0;
        start_month = 0;
        end_month = 11;
        inventory = 10;
    }

    public Item (Parcel source)
    {
        Log.d(TAG, "Creating  item from Parcel");
        name = source.readString();
        make = source.readString();
        model = source.readString();
        color = source.readString();
        year = source.readInt();
        price = source.readDouble();
        miles = source.readInt();
        street = source.readString();
        city = source.readString();
        state = source.readString();
        zip = source.readString();
        notes = source.readString();
        album_name = source.readString();
        latitude = source.readDouble();
        longitude = source.readDouble();
        area = source.readDouble();
        beds = source.readDouble();
        baths = source.readDouble();
        picurl = source.readString();
        hidden = source.readByte() != 0;
        date = source.readInt();
        current = source.readInt();
        rowno = source.readInt();
        item = source.readString();
        selected = source.readByte() != 0;
        share_id = source.readLong();
        nickname = source.readString();
        phoneno = source.readInt();
        email = source.readString();
        share_name  = source.readString();
    }


    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "Parcelling item");
        dest.writeString(name);
        dest.writeString(make);
        dest.writeString(model);
        dest.writeString(color);
        dest.writeInt(year);
        dest.writeDouble(price);
        dest.writeInt(miles);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zip);
        dest.writeString(notes);
        dest.writeString(album_name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(area);
        dest.writeDouble(beds);
        dest.writeDouble(baths);
        dest.writeString(picurl);
        dest.writeByte((byte) (hidden ? 1 : 0));
        dest.writeInt(date);
        dest.writeInt(current);
        dest.writeInt(rowno);
        dest.writeString(item);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeLong(share_id);
        dest.writeString(nickname);
        dest.writeInt(phoneno);
        dest.writeString(email);
        dest.writeString(share_name);
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }



    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }



    public double getBeds() {
        return beds;
    }

    public void setBeds(double beds) {
        this.beds = beds;
    }



    public double getBaths() {
        return baths;
    }

    public void setBaths(double baths) {
        this.baths = baths;
    }



    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }



    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }



    public void setStreet(String st)
    {
        street = st;
    }

    public String getStreet()
    {
        return street;
    }

    public void  setMiles(int m)
    {
        miles = m;
    }

    public int getMiles()
    {
        return miles;
    }

    public void setPrice(double p)
    {
        price = p;
    }

    public double getPrice()
    {
        return price;
    }

    public void setYear (int y)
    {
        year = y;
    }

    public int getYear ()
    {
        return year;
    }

    public void setColor(String clr)
    {
        color = clr;
    }

    public String getColor()
    {
        return color;
    }


    public void setModel(String mdl)
    {
        model = mdl;
    }

    public String getModel()
    {
        return model;
    }

    public  void setMake(String mk)
    {
        make = mk;
    }

    public String getMake()
    {
        return make;
    }

    public void setName(String nm)
    {
        name =nm;
    }

    public String getName()
    {
        return name;
    }


}
