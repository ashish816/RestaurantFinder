package com.shikher.restaurantfinder;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shikher on 18-Mar-16.
 */
public class JsonUtil {

    FileOutputStream fos;
    FileInputStream fis;
    BufferedInputStream bis;
    Context context;

    public JsonUtil(Context context) {
        this.context = context;
    }

    public void addRestaurantToFavourites(RestaurantDetail restaurantDetail) {


        JSONArray data = getAllFavouritesInJsonArray();

        if (data == null) {
            data = new JSONArray();
        }


        JSONObject restaurantDetailJsonObject = new JSONObject();
        try {

            restaurantDetailJsonObject.put("businessname", restaurantDetail.businessName);
            restaurantDetailJsonObject.put("snippet", restaurantDetail.snippet);
            restaurantDetailJsonObject.put("countOfReviews", restaurantDetail.countOfReviews);
            restaurantDetailJsonObject.put("rating", restaurantDetail.rating);
            restaurantDetailJsonObject.put("phoneNumber", restaurantDetail.phoneNumber);
            restaurantDetailJsonObject.put("staticMapAddresslat", restaurantDetail.staticMapAddresslat);
            restaurantDetailJsonObject.put("staticMapAddresslong", restaurantDetail.staticMapAddresslong);
            restaurantDetailJsonObject.put("imageUrl",restaurantDetail.imageUrl);
            restaurantDetailJsonObject.put("displayAddress",restaurantDetail.displayAddress);

            data.put(restaurantDetailJsonObject);

            String alljson = data.toString();

            fos = context.openFileOutput("FavouritesData", Context.MODE_PRIVATE);
            fos.write(alljson.getBytes());
            fos.close();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON EXCEPETION", "line 65");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FileNotFound Exception", "line 68");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException EXCEPETION", "line 71");
        }
    }

    public void removeRestaurantFromFavourites(RestaurantDetail restaurantDetail) {

        List<RestaurantDetail> restaurantDetailList = getAllFavouriteRestaurants();

        if (restaurantDetailList.size() == 0) {
            return;
        }

        for (RestaurantDetail rd : restaurantDetailList) {

            if (rd.businessName.equals(restaurantDetail.businessName)
                    && rd.staticMapAddresslat.equals(rd.staticMapAddresslat)
                    && rd.staticMapAddresslong.equals(rd.staticMapAddresslong)) {
                restaurantDetailList.remove(rd);
                break;
            }
        }


        //empty file

        try {
            String empty = "";
            fos = context.openFileOutput("FavouritesData", Context.MODE_PRIVATE);
            fos.write(empty.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FileNotFoundException","Line 103");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", "Line 106");
        }

        //write again

        for (RestaurantDetail rd : restaurantDetailList) {
            addRestaurantToFavourites(rd);
        }

    }

    private JSONArray getAllFavouritesInJsonArray() {
        JSONArray data = null;

        try {
            fis = context.openFileInput("FavouritesData");
            bis = new BufferedInputStream(fis);

            StringBuilder alljson = new StringBuilder();

            while (bis.available() != 0) {
                char c = (char) bis.read();
                alljson.append(c);
            }

            bis.close();
            fis.close();

            data = new JSONArray(alljson.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }


    public List<RestaurantDetail> getAllFavouriteRestaurants() {
        List<RestaurantDetail> favouritesList = new ArrayList<>();
        JSONArray data = getAllFavouritesInJsonArray();

        if (data != null) {

            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject jsonObject = data.getJSONObject(i);
                    RestaurantDetail restaurantDetail = new RestaurantDetail();

                    restaurantDetail.businessName = jsonObject.getString("businessname");
                    restaurantDetail.snippet = jsonObject.getString("snippet");
                    restaurantDetail.countOfReviews = jsonObject.getString("countOfReviews");
                    restaurantDetail.rating = jsonObject.getString("rating");
                    restaurantDetail.phoneNumber = jsonObject.getString("phoneNumber");
                    restaurantDetail.staticMapAddresslat = jsonObject.getString("staticMapAddresslat");
                    restaurantDetail.staticMapAddresslong = jsonObject.getString("staticMapAddresslong");
                    restaurantDetail.imageUrl = jsonObject.getString("imageUrl");
                    restaurantDetail.displayAddress = jsonObject.getString("displayAddress");

                    favouritesList.add(restaurantDetail);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON EXCEPETION", "line 166");
                }


            }
        }
        return favouritesList;
    }


    public boolean isRestaurantFavourite(RestaurantDetail restaurantDetail) {
        List<RestaurantDetail> favouritesList = getAllFavouriteRestaurants();

        if(favouritesList.contains(restaurantDetail)) {
            return true;
        } else {
            return false;
        }
    }

}
