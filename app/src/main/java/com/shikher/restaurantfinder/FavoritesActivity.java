package com.shikher.restaurantfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    TextView favouritesTextView;
    ListView favouritesListView;
    JsonUtil jsonUtil = null;
    List<RestaurantDetail> allFavourites = null;
    Context context;
    ResultArrayAdapter resultArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //instantiate jsonUtil and context
        jsonUtil = new JsonUtil(this);
        context = this;

        //get textview and listview
        favouritesTextView = (TextView) findViewById(R.id.favourites_info_text_view);
        favouritesListView = (ListView) findViewById(R.id.favourites_result_listView);


        //display all favourites
        displayAllFavourites();


        favouritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RestaurantDetail restaurantDetail = allFavourites.get(i);

                //start a new detail activity and pass restaurant details

                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra("businessname", restaurantDetail.businessName);
                detailIntent.putExtra("snippet", restaurantDetail.snippet);
                detailIntent.putExtra("countOfReviews", restaurantDetail.countOfReviews);
                detailIntent.putExtra("rating", restaurantDetail.rating);
                detailIntent.putExtra("phoneNumber", restaurantDetail.phoneNumber);
                detailIntent.putExtra("staticMapAddresslat", restaurantDetail.staticMapAddresslat);
                detailIntent.putExtra("staticMapAddresslong", restaurantDetail.staticMapAddresslong);
                detailIntent.putExtra("displayAddress", restaurantDetail.displayAddress);

                startActivity(detailIntent);
            }
        });
    }


    public void displayAllFavourites() {

        allFavourites = jsonUtil.getAllFavouriteRestaurants();

        if (allFavourites.size() == 0) {
            return;
        } else {
            favouritesTextView.setVisibility(View.GONE);
            favouritesListView.setVisibility(View.VISIBLE);
        }

        resultArrayAdapter = new ResultArrayAdapter(this, R.layout.restaurant_list_item, allFavourites);
        favouritesListView.setAdapter(resultArrayAdapter);

        //get all images and update favourites list
        List[] input = {allFavourites};
        new RestaurantImageDownloader().execute(input);
    }


    class RestaurantImageDownloader extends AsyncTask<List<RestaurantDetail>, String, String> {

        @Override
        protected String doInBackground(List<RestaurantDetail>... lists) {

            String result="";
            for (RestaurantDetail rd : lists[0]) {
                InputStream in = null;
                try {
                    in = new URL(rd.imageUrl).openStream();
                    rd.bitmapImage = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            //clear listview and refresh

            if (resultArrayAdapter != null) {
                resultArrayAdapter.notifyDataSetChanged();
            }
        }
    }

}
