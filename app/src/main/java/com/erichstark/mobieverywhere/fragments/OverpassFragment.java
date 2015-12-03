package com.erichstark.mobieverywhere.fragments;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.erichstark.mobieverywhere.ComplexPreferences;
import com.erichstark.mobieverywhere.R;
import com.erichstark.mobieverywhere.overpass.GeoLocation;
import com.erichstark.mobieverywhere.overpass.ItemPOI;
import com.erichstark.mobieverywhere.overpass.OPAdapter;
import com.erichstark.mobieverywhere.overpass.OPElement;
import com.erichstark.mobieverywhere.overpass.OPResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.pavelsikun.seekbarpreference.MaterialSeekBarView;
import com.pavelsikun.seekbarpreference.Persistable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Erich on 02/12/15.
 */
public class OverpassFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    public static final String FAVORITED_ITEM_KEY = "favorited-item";
    private RecyclerView mRecyclerView;
    private MaterialSeekBarView seekBar;
    private View progressBar;
    private OPAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest mLocationRequest;
    private AtomicBoolean searchIsRunning = new AtomicBoolean(false);
    private MenuItem refreshMenuItem;
    private boolean autoSelectFavoritedItem;
    private ItemPOI favoritedItem;
    private ComplexPreferences complexPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overpass, container, false);
        seekBar = (MaterialSeekBarView) view.findViewById(R.id.searchDistance);
        seekBar.setOnPersistListener(new Persistable() {
            @Override
            public void onPersist(int value) {
                search();
            }
        });
        progressBar = view.findViewById(R.id.progressView);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OPAdapter(inflater, new MyOnClickListener());
        mRecyclerView.setAdapter(adapter);

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        complexPreferences = ComplexPreferences.getComplexPreferences(getContext(), Context.MODE_PRIVATE);
        favoritedItem = complexPreferences.getObject(FAVORITED_ITEM_KEY, ItemPOI.class);
        autoSelectFavoritedItem = favoritedItem == null;
        setHasOptionsMenu(true);
        return view;
    }


    public void search() {
        if (location != null && !searchIsRunning.getAndSet(true)) {
            new OverpassAsyncTask().execute(location.getLatitude(), location.getLongitude(), (double) seekBar.getCurrentValue());
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("TAG", "connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        if (this.location == null) {
            this.location = location;
            search();
        } else {
            this.location = location;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        } else {
            mGoogleApiClient.connect();
        }
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_overpass, menu);
        refreshMenuItem = menu.getItem(0);
        refreshMenuItem.setEnabled(searchIsRunning.get());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                search();
                break;
            default:
                return false;
        }
        return true;
    }

    public class OverpassAsyncTask extends AsyncTask<Double, Void, List<ItemPOI>> {
        private static final String BASE_URL = "http://overpass-api.de/api/interpreter";
        private static final String DATA = "[out:json];node[amenity=ItemPOI](%f,%f,%f,%f);out;";
        private static final double EARTH_RADIUS = 6371.01 * 1000;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            seekBar.setEnabled(false);
            if (refreshMenuItem != null) {
                refreshMenuItem.setEnabled(false);
            }
        }

        @Override
        protected List<ItemPOI> doInBackground(Double... params) {
            if (params.length != 3) {
                return null;
            }
            Double latitude = params[0];
            Double longitude = params[1];
            GeoLocation currentGeoLocation = GeoLocation.fromDegrees(latitude, longitude);
            Double distance = params[2];
            GeoLocation[] boundingBox = currentGeoLocation.boundingCoordinates(distance, EARTH_RADIUS);
            StringBuilder chaine = new StringBuilder("");
            try {
                URL url = new URL(BASE_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(15000);
                con.setConnectTimeout(15000);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(
                        getPostDataString(Collections.singletonMap(
                                "data",
                                String.format(Locale.ENGLISH, DATA,
                                        boundingBox[0].getLatitudeInDegrees(),
                                        boundingBox[0].getLongitudeInDegrees(),
                                        boundingBox[1].getLatitudeInDegrees(),
                                        boundingBox[1].getLongitudeInDegrees()))));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = con.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        chaine.append(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("POIKO", "data: " + chaine.toString());

            OPResponse oe = new Gson().fromJson(chaine.toString(), OPResponse.class);
            List<ItemPOI> bars = new ArrayList<>(oe.getElements().size());
            boolean favoriteItemFound = false;
            for (OPElement element : oe.getElements()) {
                if (!element.getTag("name").isEmpty()) {
                    GeoLocation elementLocation = GeoLocation.fromDegrees(element.getLat(), element.getLon());
                    double elementDistance = currentGeoLocation.distanceTo(elementLocation, EARTH_RADIUS);
                    if (distance >= elementDistance) {
                        if (!autoSelectFavoritedItem && favoritedItem.getId().equals(element.getId())) {
                            favoriteItemFound = true;
                        }
                        bars.add(new ItemPOI(
                                element.getId(),
                                element.getTag("name"),
                                elementDistance));
                    }
                }
            }
            Collections.sort(bars, new Comparator<ItemPOI>() {
                @Override
                public int compare(ItemPOI lhs, ItemPOI rhs) {
                    return Double.compare(lhs.getDistance(), rhs.getDistance());
                }
            });
            if (!favoriteItemFound) {
                autoSelectFavoritedItem = true;
                if (!bars.isEmpty()) {
                    favoritedItem = bars.get(0);
                }
            }
            return bars;
        }

        @Override
        protected void onPostExecute(List<ItemPOI> bars) {
            searchIsRunning.set(false);
            progressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            seekBar.setEnabled(true);
            adapter.setBars(bars);
            if (!bars.isEmpty()) {
                adapter.setFavoriteItemID(favoritedItem.getId());
            }
            adapter.notifyDataSetChanged();
            if (refreshMenuItem != null) {
                refreshMenuItem.setEnabled(true);
            }
        }

        private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            autoSelectFavoritedItem = false;
            ItemPOI ItemPOI = adapter.getBars().get(mRecyclerView.getChildLayoutPosition(v));
            if (((CheckBox) v.findViewById(R.id.itemPrefered)).isChecked()) {
                favoritedItem = null;
                adapter.setFavoriteItemID(-1L);
                complexPreferences.remove(FAVORITED_ITEM_KEY);
            } else {
                favoritedItem = ItemPOI;
                adapter.setFavoriteItemID(favoritedItem.getId());
                complexPreferences.putObject(FAVORITED_ITEM_KEY, favoritedItem);
            }
            adapter.notifyDataSetChanged();
            complexPreferences.commit();
        }
    }
}

