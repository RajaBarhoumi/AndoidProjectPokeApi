package com.example.pokeapi.Retrofit;

import io.reactivex.Observable;

import com.example.pokeapi.Model.Pokedex;

import retrofit2.http.GET;

public interface IPokemonDex {

    @GET("pokedex.json")
    Observable<Pokedex> getListPokemon();



}
