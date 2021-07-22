package com.example.food4u.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.food4u.HomeAdapter;
import com.example.food4u.MainActivity;
import com.example.food4u.R;
import com.example.food4u.Recipe;
import com.example.food4u.databinding.FragmentHomeBinding;
import com.example.food4u.databinding.FragmentMealBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MealFragment extends Fragment {

    FragmentMealBinding binding;
    public static final String TAG = "MealFragment";
    protected HomeAdapter adapter;
    protected List<Recipe> allMeals;

    public MealFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMealBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Find RecyclerView and bind to adapter
        // allows for optimizations
        binding.rvMeals.setHasFixedSize(true);

        // Define 1 column grid layout
        final GridLayoutManager layout = new GridLayoutManager(getContext(), 1);

        allMeals = new ArrayList<>();

        //add calorie limit= age,gender, height, and activity
        //cuisine type
        //specific food limit = no onion
        //time restriction = min and max 30 min -1hr
        //current pantry list based on items to finalize=
        //based on your current time it will give you suggestions to eat


        // Create an adapter
        adapter = new HomeAdapter(getContext(), allMeals);
        // Bind adapter to list
        binding.rvMeals.setAdapter(adapter);
        //set layout manager
        binding.rvMeals.setLayoutManager(layout);

        retrieveFromAPI(MainActivity.REQUEST_URL);

    }

    public void retrieveFromAPI(String url){
        //retrieve api
        RequestQueue recipeQueue = Volley.newRequestQueue(getContext());
        //filter different pages
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("hits");
                    Log.i(TAG, "Results" + results.toString());
                    Log.i(TAG, "OnSuccess");
                    processResults(results);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "OnFailure");
                error.printStackTrace();

            }
        });
        recipeQueue.add(jsonObjectRequest);
    }

    public void processResults(JSONArray response){
        try {
            for (int i = 0; i < response.length(); i++) {
                //gets specific hit
                JSONObject recipeJSON = response.getJSONObject(i);
                //goes into the recipe portion of hit
                JSONObject currentRecipe = recipeJSON.getJSONObject("recipe");
                String recipeName = currentRecipe.getString("label");
                String image = currentRecipe.getString("image");
                String recipeURL = currentRecipe.getString("url");
                String calories = Integer.toString(currentRecipe.getInt("calories"));
                String servings = Integer.toString(currentRecipe.getInt("yield"));

                //looks into an array of ingredients and add them to the recipe
                ArrayList<String> ingredients = new ArrayList<>();
                JSONArray ingredientList = currentRecipe.getJSONArray("ingredientLines");
                for (int j = 0; j < ingredientList.length(); j++) {
                    ingredients.add(ingredientList.get(j).toString());
                }

                Recipe recipe = new Recipe(recipeName, image, recipeURL, ingredients, calories, servings);
                allMeals.add(recipe);
                Collections.shuffle(allMeals);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}