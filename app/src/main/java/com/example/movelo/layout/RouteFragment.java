package com.example.movelo.layout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.movelo.R;
import com.example.movelo.model.Mensaje;
import com.example.movelo.services.ApiService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteFragment extends SupportMapFragment implements OnMapReadyCallback {


    private GoogleMap mMap;
    private LatLng origin_latlng;
    private LatLng destination_latlng;
    private ArrayList <LatLng> polyline_points = new ArrayList<>();

    final String API_KEY = "AIzaSyB6VDVQE8QPCLFaa1Z11Fli_5LM3DZJh8I";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getMapAsync(this);

        RouteFragmentArgs args = RouteFragmentArgs.fromBundle(getArguments());



       double origin_latitude = Double.parseDouble(args.getOriginLatitude());
        double origin_longitude = Double.parseDouble(args.getOriginLongitude());
        double destination_latitude = Double.parseDouble(args.getDestinationLatitude());
        double destination_longitude = Double.parseDouble(args.getDestinationLongitude());

        System.out.println("Latitud origen " + origin_latitude);
        System.out.println("Longitud origen " + origin_longitude);
        System.out.println("Latitud destino " + destination_latitude);
        System.out.println("Longitud destino " + destination_longitude);


        origin_latlng = new LatLng(origin_latitude, origin_longitude);
        destination_latlng = new LatLng(destination_latitude, destination_longitude);

        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (origin_latlng != null && destination_latlng != null) {
            GoogleDirection.withServerKey(API_KEY)
                    .from(origin_latlng)
                    .to(destination_latlng)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction) {
                            String status = direction.getStatus();
                            if (status.equals(RequestResult.OK)) {
                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                polyline_points.addAll(leg.getDirectionPoint());
                                System.out.println("Encontrados puntos");

                            } else if (status.equals(RequestResult.NOT_FOUND)) {
                                System.out.println("No se encontró una ruta");
                            }

                            if(polyline_points.size()>0){
                                System.out.println(polyline_points);
                                LatLng origin = polyline_points.get(0);
                                LatLng destination = polyline_points.get(polyline_points.size()-1);

                                mMap.addMarker(new MarkerOptions().position(origin).title("Origen"));
                                mMap.addMarker(new MarkerOptions().position(destination).title("Destino"));

                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(getContext(), polyline_points, 5, Color.RED);
                                mMap.addPolyline(polylineOptions);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                mMap.getUiSettings().setZoomControlsEnabled(true);
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            System.out.println("F");
                        }
                    });
        } else {
            System.out.println("Latitudes longitudes nulas");
        }






    }




}
