package com.example.movelo.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.movelo.MainActivity;
import com.example.movelo.R;
import com.example.movelo.model.Mensaje;
import com.example.movelo.services.ApiService;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.INTERNET;

public class RegisterFragment extends Fragment {

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
        return inflater.inflate(R.layout.registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button register = view.findViewById(R.id.register);
        Button goToLogin = view.findViewById(R.id.go_to_login);
        EditText editText_id = view.findViewById(R.id.id);
        EditText editText_nombre = view.findViewById(R.id.nombre);
        EditText editText_direccion = view.findViewById(R.id.direccion);
        EditText editText_telefono = view.findViewById(R.id.telefono);
        EditText editText_correo = view.findViewById(R.id.email);
        EditText editText_contrasena = view.findViewById(R.id.password);
        RadioGroup radioGroup = view.findViewById(R.id.radiogroup);


        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.registration_to_login);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                String id = editText_id.getText().toString();
                String nombre = editText_nombre.getText().toString();
                String direccion = editText_direccion.getText().toString();
                String telefono = editText_telefono.getText().toString();
                String correo = editText_correo.getText().toString();
                String contrasena = editText_contrasena.getText().toString();

                int selected = radioGroup.getCheckedRadioButtonId();

                if (selected == 1) {
                    if(correo.equals("")||contrasena.equals("")||nombre.equals("")||direccion.equals("")||telefono.equals("")||id.equals("")) {
                        Toast.makeText(getActivity(), "Por favor rellene todos los campos", Toast.LENGTH_SHORT).show();
                    }else{
                        Call<Mensaje> call = service.registrarBiciusuario(
                                correo, contrasena, id, nombre, direccion, telefono
                        );
                        call.enqueue(new Callback<Mensaje>() {

                            @Override
                            public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().getMessage().equals("Este correo ya está en uso.")) {
                                        Toast.makeText(getActivity(), "Correo ya registrado", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Registro Exitoso", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(view).navigate(R.id.registration_to_login);
                                    }
                                    System.out.println("El mensaje es " + response.body().getMessage());
                                } else {
                                    Toast.makeText(getActivity(), "Error del servidor al procesar el registro", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Mensaje> call, Throwable t) {
                                System.out.println(t.toString());
                                Toast.makeText(getActivity(), "Imposible contactar con el servidor. Revise su conexión a internet", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                }else if(selected==2){

                }else {
                    Toast.makeText(getActivity(), "Seleccione si es Biciusuario o Empresa", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
