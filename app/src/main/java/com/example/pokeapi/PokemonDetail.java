package com.example.pokeapi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pokeapi.Common.Common;
import com.example.pokeapi.Model.Pokemon;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PokemonDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PokemonDetail extends Fragment {

    ImageView pokemon_image;
    TextView pokemon_name, pokemon_weight, pokemon_height;

    static PokemonDetail instance;
    public static PokemonDetail getInstance(){
        if(instance == null)
            instance = new PokemonDetail();
        return instance;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PokemonDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PokemonDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static PokemonDetail newInstance(String param1, String param2) {
        PokemonDetail fragment = new PokemonDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_pokemon_detail, container, false);
        Pokemon pokemon;

        if(getArguments().get("num") == null)
            pokemon = Common.commonPokemonList.get(getArguments().getInt("position"));
        else
            pokemon = null;
        pokemon_image = itemView.findViewById(R.id.pokemon_image);
        pokemon_name = itemView.findViewById(R.id.name);

        pokemon_height = itemView.findViewById(R.id.height);
        pokemon_weight = itemView.findViewById(R.id.weight);


        setDetailPokemon(pokemon);
        return itemView;
    }

    private void setDetailPokemon(Pokemon pokemon) {
        Glide.with(getActivity()).load(pokemon.getImg()).into(pokemon_image);
        pokemon_image.bringToFront();
        pokemon_name.setText((pokemon.getName()));
        pokemon_weight.setText("Weight "+pokemon.getWeight());
        pokemon_height.setText("Height "+ pokemon.getHeight());



    }
}