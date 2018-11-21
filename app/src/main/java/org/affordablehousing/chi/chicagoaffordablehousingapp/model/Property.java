package org.affordablehousing.chi.chicagoaffordablehousingapp.model;

import com.google.gson.annotations.SerializedName;

public class Property {

    @SerializedName("property_name")
    private String property_name;
    @SerializedName("location_state")
    private String location_state;
    @SerializedName("zip_code")
    private String zip_code;
    @SerializedName("location_zip")
    private String location_zip;
    @SerializedName("phone_number")
    private String phone_number;
    @SerializedName("x_coordinate")
    private double x_coordinate;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("property_type")
    private String property_type;
    @SerializedName("location_address")
    private String location_address;
    @SerializedName("location_city")
    private String location_city;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("y_coordinate")
    private double y_coordinate;
    @SerializedName("units")
    private int units;
    @SerializedName("community_area")
    private String community_area;
    @SerializedName("address")
    private String address;
    @SerializedName("community_area_number")
    private String community_area_number;

    public Property(String property_name, String location_state, String zip_code, String location_zip, String phone_number, double x_coordinate, double latitude, String property_type, String location_address, String location_city, double longitude, double y_coordinate, int units, String community_area, String address, String community_area_number, String management_company) {
        this.property_name = property_name;
        this.location_state = location_state;
        this.zip_code = zip_code;
        this.location_zip = location_zip;
        this.phone_number = phone_number;
        this.x_coordinate = x_coordinate;
        this.latitude = latitude;
        this.property_type = property_type;
        this.location_address = location_address;
        this.location_city = location_city;
        this.longitude = longitude;
        this.y_coordinate = y_coordinate;
        this.units = units;
        this.community_area = community_area;
        this.address = address;
        this.community_area_number = community_area_number;
        this.management_company = management_company;
    }


    @SerializedName("management_compan")
    private String management_company;


    public String getProperty_name() {
        return property_name;
    }

    public void setProperty_name(String property_name) {
        this.property_name = property_name;
    }

    public String getLocation_state() {
        return location_state;
    }

    public void setLocation_state(String location_state) {
        this.location_state = location_state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getLocation_zip() {
        return location_zip;
    }

    public void setLocation_zip(String location_zip) {
        this.location_zip = location_zip;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public double getX_coordinate() {
        return x_coordinate;
    }

    public void setX_coordinate(double x_coordinate) {
        this.x_coordinate = x_coordinate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public String getProperty_type() {
        return property_type;
    }

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    public String getLocation_address() {
        return location_address;
    }

    public void setLocation_address(String location_address) {
        this.location_address = location_address;
    }

    public String getLocation_city() {
        return location_city;
    }

    public void setLocation_city(String location_city) {
        this.location_city = location_city;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getY_coordinate() {
        return y_coordinate;
    }

    public void setY_coordinate(double y_coordinate) {
        this.y_coordinate = y_coordinate;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getCommunity_area() {
        return community_area;
    }

    public void setCommunity_area(String community_area) {
        this.community_area = community_area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommunity_area_number() {
        return community_area_number;
    }

    public void setCommunity_area_number(String community_area_number) {
        this.community_area_number = community_area_number;
    }

    public String getManagement_company() {
        return management_company;
    }

    public void setManagement_company(String management_company) {
        this.management_company = management_company;
    }


}
