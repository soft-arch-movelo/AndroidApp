package com.example.movelo.services;

import com.example.movelo.model.Mensaje;
import com.example.movelo.model.getLatLng.LatLngResults;
import com.example.movelo.model.getLatLng.Result;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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
    @POST("/registrarEmpresa")
    Call<Mensaje> registrarEmpresa(
            @Field("correo") String correo,
            @Field("contrasena") String contrasena,
            @Field("nit") String nit,
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

    @GET("/maps/api/geocode/json?")
    Call<LatLngResults> getLatLng(
            @Query("address") String address,
            @Query("key") String key
    );

    @FormUrlEncoded
    @POST("/enviarDistanciaRecorrido")
    Call<Mensaje> enviarDistanciaRecorrida(
            @Field("token") String token,
            @Field("distancia") int distancia
    );

    @GET("/consultarHuellaDeCarbono")
    Call<Mensaje> getHuella(
            @Query("token") String token
    );
}
