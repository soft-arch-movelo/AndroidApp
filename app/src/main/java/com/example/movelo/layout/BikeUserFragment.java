package com.example.movelo.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.example.movelo.R;
import com.example.movelo.model.getLatLng.LatLngResults;
import com.example.movelo.services.ApiService;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BikeUserFragment extends Fragment {

    Retrofit latlngRetrofit;
    ApiService latlngService;

    EditText origin;
    EditText destination;
    Button startRoute;
    Button mihuella;

    String token = "";



    final String API_KEY = "AIzaSyB6VDVQE8QPCLFaa1Z11Fli_5LM3DZJh8I";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        latlngRetrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        latlngService = latlngRetrofit.create(ApiService.class);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BikeUserFragmentArgs args = BikeUserFragmentArgs.fromBundle(getArguments());
        if(!args.getUserToken().equals("")){
            token = args.getUserToken();
            System.out.println(token);
        }else{
            System.out.println("No pude recibir el token");
        }

        return inflater.inflate(R.layout.b_user_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);

        origin = view.findViewById(R.id.origin);
        destination = view.findViewById(R.id.destination);
        startRoute = view.findViewById(R.id.startRoute);
        mihuella = view.findViewById(R.id.miHuella);

        BikeUserFragmentDirections.GoToRoute action = BikeUserFragmentDirections.goToRoute();
        BikeUserFragmentDirections.GoToCo2 goToCo2 = BikeUserFragmentDirections.goToCo2();

        startRoute.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String origen = origin.getText().toString();
                String destino = destination.getText().toString();


                Call<LatLngResults> originLatLngResultsCall = latlngService.getLatLng(origen, API_KEY);
                originLatLngResultsCall.enqueue(new Callback<LatLngResults>() {
                    @Override
                    public void onResponse(Call<LatLngResults> call, Response<LatLngResults> response) {
                        String latitud = "";
                        String longitud = "";
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("ZERO_RESULTS")) {
                                Toast.makeText(getActivity(), "No se pudo encontrar la dirección de origen", Toast.LENGTH_SHORT).show();
                            } else {
                                latitud = Double.toString(response.body().getResults().get(0).getGeometry().getLocation().getLat());
                                longitud = Double.toString(response.body().getResults().get(0).getGeometry().getLocation().getLng());
                            }


                        } else {
                            Toast.makeText(getActivity(), "No se pudo encontrar la dirección de origen", Toast.LENGTH_SHORT).show();
                        }
                        action.setOriginLatitude(latitud);
                        action.setOriginLongitude(longitud);
                        System.out.println("Colocadas latitud " + latitud + " y longitud " + longitud);
                    }

                    @Override
                    public void onFailure(Call<LatLngResults> call, Throwable t) {
                        Toast.makeText(getActivity(), "Imposible contactar con el servidor. Revise su conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                });

                Call<LatLngResults> destinationLatLngResultsCall = latlngService.getLatLng(destino, API_KEY);
                destinationLatLngResultsCall.enqueue(new Callback<LatLngResults>() {
                    @Override
                    public void onResponse(Call<LatLngResults> call, Response<LatLngResults> response) {
                        String latitud = "";
                        String longitud = "";
                        if (response.body() != null) {
                            if (response.body().getStatus().equals("ZERO_RESULTS")) {
                                Toast.makeText(getActivity(), "No se pudo encontrar la dirección de destino", Toast.LENGTH_LONG).show();

                            } else {
                                latitud = Double.toString(response.body().getResults().get(0).getGeometry().getLocation().getLat());
                                longitud = Double.toString(response.body().getResults().get(0).getGeometry().getLocation().getLng());
                            }
                        } else {
                            Toast.makeText(getActivity(), "No se pudo encontrar la dirección de destino", Toast.LENGTH_LONG).show();

                        }
                        action.setDestinationLatitude(latitud);
                        action.setDestinationLongitude(longitud);
                        action.setUserToken(token);


                        System.out.println("Colocadas latitud " + latitud + " y longitud " + longitud);


                        Navigation.findNavController(view).navigate(action);


                    }

                    @Override
                    public void onFailure(Call<LatLngResults> call, Throwable t) {
                        Toast.makeText(getActivity(), "Imposible contactar con el servidor. Revise su conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        mihuella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCo2.setUserToken(token);

                Navigation.findNavController(view).navigate(goToCo2);
            }
        });

    }

}

