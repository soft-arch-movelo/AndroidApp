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

public class RouteFragment extends Fragment {

    RouteFragmentDirections.GoToMap action = RouteFragmentDirections.goToMap();
    private LatLng origin_latlng;
    private LatLng destination_latlng;
    Button seeMap;
    Button endRoute;
    String distance = "";
    String token = "";
    final String API_KEY = "AIzaSyB6VDVQE8QPCLFaa1Z11Fli_5LM3DZJh8I";

    Retrofit retrofit;
    ApiService service;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://movelo.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        RouteFragmentArgs args = RouteFragmentArgs.fromBundle(getArguments());


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
            action.setOriginLatitude(args.getOriginLatitude());
            action.setOriginLongitude(args.getOriginLongitude());
            action.setDestinationLatitude(args.getDestinationLatitude());
            action.setDestinationLongitude(args.getDestinationLongitude());
            origin_latlng = new LatLng(Double.parseDouble(args.getOriginLatitude()), Double.parseDouble(args.getOriginLongitude()));
            destination_latlng = new LatLng(Double.parseDouble(args.getDestinationLatitude()),Double.parseDouble(args.getDestinationLongitude()));
        }

        if(!args.getUserToken().equals("")){
            token = args.getUserToken();
        }else{
            System.out.println("No recibi token en ruta");
        }
        System.out.println("Latitud origen " + args.getOriginLatitude());
        System.out.println("Longitud origen " + args.getOriginLongitude());
        System.out.println("Latitud destino " +  args.getDestinationLatitude());
        System.out.println("Longitud destino " + args.getDestinationLongitude());


        return inflater.inflate(R.layout.route_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        seeMap = view.findViewById(R.id.startRoute);
        endRoute = view.findViewById(R.id.end);

        if (origin_latlng != null && destination_latlng != null) {
            GoogleDirection.withServerKey(API_KEY)
                    .from(origin_latlng)
                    .to(destination_latlng)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction) {
                            String status = direction.getStatus();
                            if (status.equals(RequestResult.OK)) {
                                System.out.println("Todo correcto");
                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                distance = leg.getDistance().getText();
                                Toast.makeText(getActivity(), "Ruta Encontrada", Toast.LENGTH_SHORT).show();
                                System.out.println("DISTANCIA:  " + distance);
                            } else if (status.equals(RequestResult.NOT_FOUND)) {
                                Toast.makeText(getActivity(), "Ruta no Encontrada", Toast.LENGTH_SHORT).show();
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

        seeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(action);
            }
        });



        endRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formattedDistance = distance.replace("km", "");
                String finalDistance = formattedDistance.replace(" ", "");
                double double_distance = Double.parseDouble(finalDistance);
                int mt_distance = ((int) double_distance) * 1000;

                Call <Mensaje> call = service.enviarDistanciaRecorrida(token, mt_distance);
                call.enqueue(new Callback<Mensaje>() {
                    @Override
                    public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getActivity(), "¡Que tengas un buen día!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.finish_route);
                        }else{
                            Toast.makeText(getActivity(), "Error del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Mensaje> call, Throwable t) {
                        Toast.makeText(getActivity(), "Imposible contactar con el servidor. Revise su conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                });
                System.out.println("DISTANCIA:  " + mt_distance + " metros");
                System.out.println("TOKEN:   " + token);
            }
        });
    }
}
