package com.shikher.restaurantfinder;

import android.app.ActionBar;
import android.app.admin.SystemUpdatePolicy;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    TextView restaurantBusinessNameTextView,restaurantRatingTextView,restaurantReviewsTextView,restaurantPhonenoTextView,restaurantSnippetTextView,restaurantAddressTextView;
    ImageView restaurantMapView;
    ImageButton restaurantFavouriteButton;
    private boolean isFavourite = false;
    private String tag = "DetailActivity";
    private JsonUtil jsonUtil = null;
    private RestaurantDetail restaurantDetailFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //create new instance of jsonUtil
        jsonUtil = new JsonUtil(this);

        //get Intent data and create a restaurant object
        Intent intent = getIntent();

        restaurantDetailFromIntent = new RestaurantDetail();
        restaurantDetailFromIntent.businessName = intent.getStringExtra("businessname");
        restaurantDetailFromIntent.snippet = intent.getStringExtra("snippet");
        restaurantDetailFromIntent.countOfReviews = intent.getStringExtra("countOfReviews");
        restaurantDetailFromIntent.rating = intent.getStringExtra("rating");
        restaurantDetailFromIntent.phoneNumber = intent.getStringExtra("phoneNumber");
        restaurantDetailFromIntent.staticMapAddresslat = intent.getStringExtra("staticMapAddresslat");
        restaurantDetailFromIntent.staticMapAddresslong = intent.getStringExtra("staticMapAddresslong");
        restaurantDetailFromIntent.displayAddress = intent.getStringExtra("displayAddress");
        restaurantDetailFromIntent.imageUrl = intent.getStringExtra("imageUrl");

        //get views
        restaurantBusinessNameTextView = (TextView) findViewById(R.id.detail_restaurant_business_name);
        restaurantMapView = (ImageView) findViewById(R.id.detail_restaurant_map_view);
        restaurantFavouriteButton = (ImageButton) findViewById(R.id.detail_restaurant_favourite_button);
        restaurantAddressTextView = (TextView) findViewById(R.id.detail_restaurant_address);
        restaurantSnippetTextView = (TextView) findViewById(R.id.detail_restaurant_snippet);
        restaurantRatingTextView = (TextView) findViewById(R.id.detail_restaurant_rating);
        restaurantReviewsTextView = (TextView) findViewById(R.id.detail_restaurant_reviews);
        restaurantPhonenoTextView = (TextView) findViewById(R.id.detail_restaurant_phoneno);

        //set properties
        restaurantBusinessNameTextView.setText(restaurantDetailFromIntent.businessName);
        restaurantSnippetTextView.setText(restaurantDetailFromIntent.snippet);
        restaurantReviewsTextView.setText(restaurantDetailFromIntent.countOfReviews + " Reviews");
        restaurantAddressTextView.setText(restaurantDetailFromIntent.displayAddress);
        restaurantPhonenoTextView.setText("Phone : " + restaurantDetailFromIntent.phoneNumber);
        restaurantRatingTextView.setText("Rating : " + restaurantDetailFromIntent.rating + " stars");


        //request map view image
        String lat = restaurantDetailFromIntent.staticMapAddresslat;
        String lng = restaurantDetailFromIntent.staticMapAddresslong;

        String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=360x360&sensor=false&&markers=color:red%7Clabel:D%7C" + lat + "," + lng;
        String[] urls = {url};
        new RequestMapTask().execute(urls);


        //update favourite icon

        if (new JsonUtil(this).isRestaurantFavourite(restaurantDetailFromIntent)) {
            restaurantFavouriteButton.setTag(new String("true"));
            restaurantFavouriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_favorite_red));
        } else {
            restaurantFavouriteButton.setTag(new String("false"));
            restaurantFavouriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_favorite_red_outline));
        }
    }


    public void setFavourite(View view) {

        ImageButton imageButton = (ImageButton) view;

        if (view.getTag().toString().equals("false")) {
            isFavourite = false;
        } else if (view.getTag().toString().equals("true")) {
            isFavourite = true;
        }

        if (!isFavourite) {
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_favorite_red));
            isFavourite = true;
            view.setTag("true");
            jsonUtil.addRestaurantToFavourites(restaurantDetailFromIntent);
        } else {
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_favorite_red_outline));
            isFavourite = false;
            view.setTag("false");
            jsonUtil.removeRestaurantFromFavourites(restaurantDetailFromIntent);
        }

    }

    class RequestMapTask extends AsyncTask<String, Bitmap, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;

            try {

                InputStream in = new URL(urls[0]).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                in.close();

            } catch (final Exception e) {
                Log.e(tag, e.getMessage());
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                restaurantMapView.setImageBitmap(result);
            }
        }
    }

}
