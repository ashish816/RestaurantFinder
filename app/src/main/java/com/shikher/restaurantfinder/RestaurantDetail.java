package com.shikher.restaurantfinder;

import android.graphics.Bitmap;

/**
 * Created by Ashish on 3/15/16.
 */
public class RestaurantDetail {

    String businessName;
    String rating;
    String countOfReviews;
    String staticMapAddresslat;
    String staticMapAddresslong;
    String phoneNumber;
    String snippet;
    String imageUrl;
    Bitmap bitmapImage;
    String displayAddress;

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public String getDisplayAddress() {
        return displayAddress;
    }

    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCountOfReviews() {
        return countOfReviews;
    }

    public void setCountOfReviews(String countOfReviews) {
        this.countOfReviews = countOfReviews;
    }

    public String getStaticMapAddresslong() {
        return staticMapAddresslong;
    }

    public void setStaticMapAddresslong(String staticMapAddresslong) {
        this.staticMapAddresslong = staticMapAddresslong;
    }

    public String getStaticMapAddresslat() {
        return staticMapAddresslat;
    }

    public void setStaticMapAddresslat(String staticMapAddresslat) {
        this.staticMapAddresslat = staticMapAddresslat;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantDetail that = (RestaurantDetail) o;

        if (businessName != null ? !businessName.equals(that.businessName) : that.businessName != null)
            return false;
        if (rating != null ? !rating.equals(that.rating) : that.rating != null) return false;
        if (countOfReviews != null ? !countOfReviews.equals(that.countOfReviews) : that.countOfReviews != null)
            return false;
        if (staticMapAddresslat != null ? !staticMapAddresslat.equals(that.staticMapAddresslat) : that.staticMapAddresslat != null)
            return false;
        if (staticMapAddresslong != null ? !staticMapAddresslong.equals(that.staticMapAddresslong) : that.staticMapAddresslong != null)
            return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null)
            return false;
        return !(snippet != null ? !snippet.equals(that.snippet) : that.snippet != null);

    }

    @Override
    public int hashCode() {
        return 0;
    }



}
