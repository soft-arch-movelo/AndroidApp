package com.example.movelo.services;

import com.example.movelo.model.Mensaje;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("/registrarBiciusuario")
    Call<Mensaje> registrarBiciusuario(
            @Field("correo") String correo,
            @Field("contrasena") String contrasena,
            @Field("cc") String cc,
            @Field("nombre") String nombre,
            @Field("direccion") String direccion,
            @Field("telefono") String telefono
    );
    @FormUrlEncoded
    @POST("/login")
    Call<Mensaje> login(
            @Field("correo") String correo,
            @Field("contrasena") String contrasena
    );
}
