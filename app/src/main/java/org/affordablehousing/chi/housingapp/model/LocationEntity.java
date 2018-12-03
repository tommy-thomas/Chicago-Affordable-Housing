package org.affordablehousing.chi.housingapp.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "location")
public class LocationEntity implements Location {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "locationId")
    public int locationId;
    @ColumnInfo(name = "property_name")
    @SerializedName("property_name")
    public String property_name;
    @ColumnInfo(name = "location_state")
    @SerializedName("location_state")
    public String location_state;
    @ColumnInfo(name = "zip_code")
    @SerializedName("zip_code")
    public String zip_code;
    @ColumnInfo(name = "location_zip")
    @SerializedName("location_zip")
    public String location_zip;
    @ColumnInfo(name = "phone_number")
    @SerializedName("phone_number")
    public String phone_number;
    @ColumnInfo(name = "x_coordinate")
    @SerializedName("x_coordinate")
    public double x_coordinate;
    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    public double latitude;
    @ColumnInfo(name = "property_type")
    @SerializedName("property_type")
    public String property_type;
    @ColumnInfo(name = "location_address")
    @SerializedName("location_address")
    public String location_address;
    @ColumnInfo(name = "location_city")
    @SerializedName("location_city")
    public String location_city;
    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    public double longitude;
    @ColumnInfo(name = "y_coordinate")
    @SerializedName("y_coordinate")
    public double y_coordinate;
    @ColumnInfo(name = "units")
    @SerializedName("units")
    public int units;
    @ColumnInfo(name = "community_area")
    @SerializedName("community_area")
    public String community_area;
    @ColumnInfo(name = "address")
    @SerializedName("address")
    public String address;
    @ColumnInfo(name = "community_area_number")
    @SerializedName("community_area_number")
    public String community_area_number;
    @ColumnInfo(name = "management_company")
    @SerializedName("management_company")
    public String management_company;

    @Ignore
    public LocationEntity(String property_name, String location_state, String zip_code, String location_zip, String phone_number, double x_coordinate, double latitude, String property_type, String location_address, String location_city, double longitude, double y_coordinate, int units, String community_area, String address, String community_area_number, String management_company) {
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

    public LocationEntity(){}

    public LocationEntity(Location property) {
        this.locationId= property.getLocationId();
        this.property_name = property.getProperty_name();
        this.location_state =  property.getLocation_state();
        this.zip_code = property.getZip_code();
        this.location_zip = property.getLocation_zip();
        this.phone_number = property.getPhone_number();
        this.x_coordinate = property.getX_coordinate();
        this.latitude = property.getLatitude();
        this.property_type = property.getProperty_type();
        this.location_address = property.getLocation_address();
        this.location_city = property.getLocation_city();
        this.longitude = property.getLongitude();
        this.y_coordinate = property.getY_coordinate();
        this.units = property.getUnits();
        this.community_area = property.getCommunity_area();
        this.address = property.getAddress();
        this.community_area_number = property.getCommunity_area_number();
        this.management_company = property.getManagement_company();
    }

    @Override
    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    @Override
    public String getProperty_name() {
        return property_name;
    }

    public void setProperty_name(String property_name) {
        this.property_name = property_name;
    }
    @Override
    public String getLocation_state() {
        return location_state;
    }

    public void setLocation_state(String location_state) {
        this.location_state = location_state;
    }

    @Override
    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    @Override
    public String getLocation_zip() {
        return location_zip;
    }

    public void setLocation_zip(String location_zip) {
        this.location_zip = location_zip;
    }

    @Override
    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public double getX_coordinate() {
        return x_coordinate;
    }

    public void setX_coordinate(double x_coordinate) {
        this.x_coordinate = x_coordinate;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String getProperty_type() {
        return property_type;
    }

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    @Override
    public String getLocation_address() {
        return location_address;
    }

    public void setLocation_address(String location_address) {
        this.location_address = location_address;
    }

    @Override
    public String getLocation_city() {
        return location_city;
    }

    public void setLocation_city(String location_city) {
        this.location_city = location_city;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public double getY_coordinate() {
        return y_coordinate;
    }

    public void setY_coordinate(double y_coordinate) {
        this.y_coordinate = y_coordinate;
    }

    @Override
    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    @Override
    public String getCommunity_area() {
        return community_area;
    }

    public void setCommunity_area(String community_area) {
        this.community_area = community_area;
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getCommunity_area_number() {
        return community_area_number;
    }

    public void setCommunity_area_number(String community_area_number) {
        this.community_area_number = community_area_number;
    }

    @Override
    public String getManagement_company() {
        return management_company;
    }

    public void setManagement_company(String management_company) {
        this.management_company = management_company;
    }


}
