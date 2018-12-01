package org.affordablehousing.chi.housingapp.model;

public class MarkerTag {

    private String mPropertyType;
    private String mCommunityName;

    public MarkerTag(String mPropertyType, String mCommunityName) {
        this.mPropertyType = mPropertyType;
        this.mCommunityName = mCommunityName;
    }

    public MarkerTag(String mPropertyType) {
        this.mPropertyType = mPropertyType;
    }


    public String getPropertyType() {
        return mPropertyType;
    }

    public String getCommunityName() {
        return mCommunityName;
    }

    public void setPropertyType(String mPropertyType) {
        this.mPropertyType = mPropertyType;
    }

    public void setCommunityName(String mCommunityName) {
        this.mCommunityName = mCommunityName;
    }
}
