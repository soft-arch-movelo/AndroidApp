package com.example.movelo.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.movelo.R;
import com.example.movelo.model.Mensaje;
import com.example.movelo.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogFragment extends Fragment {

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
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button login = view.findViewById(R.id.login);
        Button reg_here = view.findViewById(R.id.reg_here);
        EditText editText_correo = view.findViewById(R.id.email);
        EditText editText_contrasena = view.findViewById(R.id.password);

        LogFragmentDirections.LoginToBuserMenu action = LogFragmentDirections.loginToBuserMenu();

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String correo = editText_correo.getText().toString();
                String contrasena = editText_contrasena.getText().toString();

                if (correo.equals("") || contrasena.equals("")) {
                    Toast.makeText(getActivity(), "Por favor rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    Call<Mensaje> call = service.login(
                            correo, contrasena
                    );
                    call.enqueue(new Callback<Mensaje>() {

                        @Override
                        public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getMessage().equals("Credenciales incorrectas.")) {
                                    Toast.makeText(getActivity(), "Correo o contraseña inválidos", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Bienvenido", Toast.LENGTH_SHORT).show();
                                    action.setUserToken(response.body().getMessage());
                                    Navigation.findNavController(view).navigate(action);
                                }
                                System.out.println("Token: " + response.body().getMessage());
                            } else {
                                Toast.makeText(getActivity(), "Error del servidor", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Mensaje> call, Throwable t) {
                            Toast.makeText(getActivity(), "Imposible contactar con el servidor. Revise su conexión a internet", Toast.LENGTH_SHORT).show();
                        }

                    });

                }
            }
        });

        reg_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.login_to_registration);
            }
        });
    }
}

