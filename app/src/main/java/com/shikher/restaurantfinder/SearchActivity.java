package com.shikher.restaurantfinder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_BUSINESS = "businesses";
    private static final String TAG_NAME = "name";
    private static final String TAG_REVIEW_COUNT = "review_count";
    private static final String TAG_LOCATIONLAT = "latitude";
    private static final String TAG_LOCATIONLONG = "longitude";
    private static final String TAG_RATING = "rating";
    private static final String TAG_PHONENUMBER = "phone";
    private static final String TAG_SNIPPET = "snippet_text";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_ImageURL = "image_url";
    int PLACE_PICKER_REQUEST = 1;


    ListView resultList = null;
    TextView infoTextView = null;
    List<RestaurantDetail> arrayList = new ArrayList<RestaurantDetail>();
    Context searchActivityContext = null;
    ResultArrayAdapter resultArrayAdapter = null;

    String currentLocation;
    String currentSearchItem;
    String sortOption = "0";
    LatLngBounds latLngBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handleIntent(getIntent());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get resultList
        resultList = (ListView) findViewById(R.id.result_listView);

        //assign single click behaviour for resultlist

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RestaurantDetail restaurantDetail = arrayList.get(i);

                //start a new detail activity and pass restaurant details

                Intent detailIntent = new Intent(searchActivityContext, DetailActivity.class);
                detailIntent.putExtra("businessname", restaurantDetail.businessName);
                detailIntent.putExtra("snippet", restaurantDetail.snippet);
                detailIntent.putExtra("countOfReviews", restaurantDetail.countOfReviews);
                detailIntent.putExtra("rating", restaurantDetail.rating);
                detailIntent.putExtra("phoneNumber", restaurantDetail.phoneNumber);
                detailIntent.putExtra("staticMapAddresslat", restaurantDetail.staticMapAddresslat);
                detailIntent.putExtra("staticMapAddresslong", restaurantDetail.staticMapAddresslong);
                detailIntent.putExtra("displayAddress", restaurantDetail.displayAddress);
                detailIntent.putExtra("imageUrl",restaurantDetail.imageUrl);

                startActivity(detailIntent);
            }
        });


        //get info text view
        infoTextView = (TextView) findViewById(R.id.info_text_view);

        //setting context
        searchActivityContext = this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_view).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.place_picker) {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            if(latLngBounds != null) {
                builder.setLatLngBounds(latLngBounds);
            }

            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }


            return true;
        } else if(id == R.id.sort_by_distance) {

            fetchResults(currentSearchItem, currentLocation, "1");

            //clear listview and refresh
            if (resultArrayAdapter != null) {
                resultArrayAdapter.notifyDataSetChanged();
            }

            return true;
        } else if(id == R.id.sort_by_relevance) {

            fetchResults(currentSearchItem, currentLocation, "0");
            //clear listview and refresh
            if (resultArrayAdapter != null) {
                resultArrayAdapter.notifyDataSetChanged();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                latLngBounds = PlacePicker.getLatLngBounds(data);
                Place place = PlacePicker.getPlace(data, this);
                //String toastMsg = String.format("Place: %s", place.getName());
                 String location = place.getAddress().toString();
                fetchResults(null,location ,sortOption);
                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favourites) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
             String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            fetchResults(searchQuery, currentLocation, sortOption);
        }
    }

    private void fetchResults(String searchQuery, String selectedPlace, String sortOption) {

        if(searchQuery == null){
             searchQuery = "";
        }
        if(selectedPlace == null){
            selectedPlace = "San Jose";
        }
        currentLocation = selectedPlace;
        currentSearchItem = searchQuery;
        //set visibility of listView and disable infoTextView
        resultList.setVisibility(View.VISIBLE);
        infoTextView.setVisibility(View.GONE);
        //clear listview

        if (resultArrayAdapter != null) {
            arrayList.clear();
            resultArrayAdapter.notifyDataSetChanged();
        }

        String[] input = {searchQuery, selectedPlace, sortOption};
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(input);
    }


    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {
                String consumerKey = "dSHPN7Wt8CHdFL6X8byvZg";
                String consumerSecret = "yMpq6hzQN73pyxFBHRKHNdhO-Jg";
                String token = "2Vuuf8Z_DxiWo1ZqgwwoPaNvfUa1AT7e";
                String tokenSecret = "WWTS8tggLpfIbdm2ZdqJG7qojfM";
                YelpConnector yelp = new YelpConnector(consumerKey, consumerSecret, token, tokenSecret);
                response = yelp.search(params[0], params[1],params[2]);
                parseJsonObject(response);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        public void parseJsonObject(String response) {
            // Making a request to url and getting response
            String jsonStr = response;
            JSONArray restaurants = null;

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    restaurants = jsonObj.getJSONArray(TAG_BUSINESS);

                    // looping through All Contacts
                    for (int i = 0; i < restaurants.length(); i++) {
                        JSONObject c = restaurants.getJSONObject(i);
                        String businessname = c.getString(TAG_NAME);
                        String snippet = c.getString(TAG_SNIPPET);
                        String reviewcount = c.getString(TAG_REVIEW_COUNT);
                        String rating = c.getString(TAG_RATING);
                        String phonenumber = c.getString(TAG_PHONENUMBER);
                        String imageUrl = c.getString(TAG_ImageURL);


                        JSONObject location = c.getJSONObject(TAG_LOCATION).getJSONObject("coordinate");
                        String latitude = location.getString(TAG_LOCATIONLAT);
                        String longitude = location.getString(TAG_LOCATIONLONG);

                        JSONArray addressArray = c.getJSONObject(TAG_LOCATION).getJSONArray("display_address");
                        String displayAddress = "";
                        for (int j = 0; j < addressArray.length(); j++) {
                            displayAddress = displayAddress + " " + addressArray.get(j);
                        }

                        RestaurantDetail restaurantDetail = new RestaurantDetail();
                        restaurantDetail.businessName = businessname;
                        restaurantDetail.snippet = snippet;
                        restaurantDetail.countOfReviews = reviewcount;
                        restaurantDetail.rating = rating;
                        restaurantDetail.phoneNumber = phonenumber;
                        restaurantDetail.staticMapAddresslat = latitude;
                        restaurantDetail.staticMapAddresslong = longitude;
                        restaurantDetail.displayAddress = displayAddress;
                        restaurantDetail.imageUrl = imageUrl;

                        arrayList.add(restaurantDetail);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }

        @Override
        protected void onPostExecute(String result) {

//            Log.i("Results length : ", String.valueOf(arrayList.size()));
//            for (int i = 0; i < arrayList.size(); i++) {
//                RestaurantDetail restaurantDetail = arrayList.get(i);
//                restaurantDetail.attachBitmap();
//            }

            if(arrayList.size() > 0) {
                resultArrayAdapter = new ResultArrayAdapter(searchActivityContext, R.layout.restaurant_list_item, arrayList);
                resultList.setAdapter(resultArrayAdapter);

                List[] input = {arrayList};
                new RestaurantImageDownloader().execute(input);
                infoTextView.setVisibility(View.GONE);
                resultList.setVisibility(View.VISIBLE);
            } else {
                infoTextView.setText("No restaurants nearby.");
                resultList.setVisibility(View.GONE);
                infoTextView.setVisibility(View.VISIBLE);
            }

        }
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


    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
