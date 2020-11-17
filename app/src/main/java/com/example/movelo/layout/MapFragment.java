package com.example.movelo.layout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.movelo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {


    private GoogleMap mMap;
    private LatLng origin_latlng;
    private LatLng destination_latlng;
    private ArrayList<LatLng> polyline_points = new ArrayList<>();
    private String distance = "";

    final String API_KEY = "AIzaSyB6VDVQE8QPCLFaa1Z11Fli_5LM3DZJh8I";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getMapAsync(this);
        MapFragmentArgs args = MapFragmentArgs.fromBundle(getArguments());

        double origin_latitude = 0;
        double origin_longitude = 0;
        double destination_latitude = 0;
        double destination_longitude = 0;

        if (args.getOriginLatitude().equals("No Latitude") ||
                args.getOriginLongitude().equals("No Longitude") ||
                args.getDestinationLatitude().equals("No Latitude") ||
                args.getDestinationLongitude().equals("No Longitude") ||
                args.getOriginLatitude().equals("") ||
                args.getOriginLongitude().equals("") ||
                args.getDestinationLatitude().equals("") ||
                args.getDestinationLongitude().equals("")) {
                Toast.makeText(getActivity(), "Error al generar la ruta. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();

        } else {
            origin_latitude = Double.parseDouble(args.getOriginLatitude());
            origin_longitude = Double.parseDouble(args.getOriginLongitude());
            destination_latitude = Double.parseDouble(args.getDestinationLatitude());
            destination_longitude = Double.parseDouble(args.getDestinationLongitude());
            origin_latlng = new LatLng(origin_latitude, origin_longitude);
            destination_latlng = new LatLng(destination_latitude, destination_longitude);
        }

        System.out.println("Latitud origen " + args.getOriginLatitude());
        System.out.println("Longitud origen " + args.getOriginLongitude());
        System.out.println("Latitud destino " +  args.getDestinationLatitude());
        System.out.println("Longitud destino " + args.getDestinationLongitude());


        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
                                distance = leg.getDistance().getText();
                                polyline_points.addAll(leg.getDirectionPoint());
                                Toast.makeText(getActivity(), "Ruta Encontrada", Toast.LENGTH_SHORT).show();

                            } else if (status.equals(RequestResult.NOT_FOUND)) {
                                Toast.makeText(getActivity(), "Ruta no Encontrada", Toast.LENGTH_SHORT).show();

                            }

                            if (polyline_points.size() > 0) {

                                LatLng origin = polyline_points.get(0);
                                LatLng destination = polyline_points.get(polyline_points.size() - 1);

                                mMap.addMarker(new MarkerOptions().position(origin).title("Origen"));
                                mMap.addMarker(new MarkerOptions().position(destination).title("Destino"));

                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(getContext(), polyline_points, 5, Color.BLUE);
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
            System.out.println("Alguna latitud nula");
        }


    }


}
