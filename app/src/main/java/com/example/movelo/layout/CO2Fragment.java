package com.example.movelo.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.movelo.R;
import com.example.movelo.model.Mensaje;
import com.example.movelo.model.getLatLng.LatLngResults;
import com.example.movelo.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CO2Fragment extends Fragment {

    Retrofit retrofit;
    ApiService service;
    TextView co2_bike;
    TextView co2_car;
    Button okay;

    String token = "";

    CO2FragmentDirections.GoToBuser action = CO2FragmentDirections.goToBuser();


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
        CO2FragmentArgs args = CO2FragmentArgs.fromBundle(getArguments());
        if(!args.getUserToken().equals("")){
            token = args.getUserToken();
            System.out.println("TOKEN:  " + token);
        }else{
            System.out.println("No pude recibir el token");
        }

        return inflater.inflate(R.layout.co2_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        co2_bike = view.findViewById(R.id.co2_bike);
        co2_car = view.findViewById(R.id.co2_car);
        okay = view.findViewById(R.id.okay);

        Call <Mensaje> call = service.getHuella(token);
        call.enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                if(response.isSuccessful()){
                    String message = response.body().getMessage();
                    double double_co2_bike = Double.parseDouble(message)/1000;
                    co2_bike.setText(Double.toString(double_co2_bike) + " kg de CO2 generado");

                    co2_car.setText(Double.toString((double_co2_bike*7.5)) + " kg de CO2 generado");

                }else {
                    Toast.makeText(getActivity(), "Error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Mensaje> call, Throwable t) {
                Toast.makeText(getActivity(), "Imposible contactar con el servidor. Revise su conexión a internet", Toast.LENGTH_SHORT).show();

            }
        });

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.setUserToken(token);
                Navigation.findNavController(view).navigate(action);
            }
        });
        super.onViewCreated(view, savedInstanceState);



    }

}

