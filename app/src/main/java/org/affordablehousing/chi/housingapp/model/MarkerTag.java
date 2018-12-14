package org.affordablehousing.chi.housingapp.model;

public class MarkerTag {

    private LocationEntity mLocationEntity;

    public MarkerTag(LocationEntity mLocationEntity) {
        this.mLocationEntity = mLocationEntity;
    }

    public LocationEntity getLocationEntity() {
        return mLocationEntity;
    }

    public void setLocationEntity(LocationEntity mLocationEntity) {
        this.mLocationEntity = mLocationEntity;
    }

    public String getPropertyType(){
        return this.mLocationEntity.getProperty_type();
    }
}
