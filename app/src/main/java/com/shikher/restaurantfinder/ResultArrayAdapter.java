package com.shikher.restaurantfinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by shikher on 18-Mar-16.
 */

public class ResultArrayAdapter extends ArrayAdapter<RestaurantDetail> {

    Context context;
    int layoutResourceId;
    List<RestaurantDetail> list = null;
    ImageView restaurantImage;


    public ResultArrayAdapter(Context context, int resource, List<RestaurantDetail> objects) {
        super(context, resource, objects);
        this.context = context;
        layoutResourceId = resource;
        list = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        LinearLayout row = (LinearLayout)inflater.inflate(layoutResourceId, parent, false);

        restaurantImage = (ImageView)row.findViewById(R.id.restaurant_sample_image);
        TextView restaurantBusinessName = (TextView)row.findViewById(R.id.restaurant_business_name);
        TextView restaurantRating = (TextView)row.findViewById(R.id.restaurant_rating);
        TextView restaurantAddress = (TextView)row.findViewById(R.id.restaurant_address);

        //get clicked item
        RestaurantDetail rd = list.get(position);

        //set business name

        restaurantBusinessName.setText(rd.getBusinessName());

        //set restaurant rating

        restaurantRating.setText("Rating : " + rd.getRating() + " stars");

        restaurantAddress.setText(rd.getDisplayAddress());
        restaurantImage.setImageBitmap(rd.getBitmapImage());

        return row;
    }

}
