package com.example.fooddeliveryapplication.Fragments.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fooddeliveryapplication.Activities.Home.FindActivity;
import com.example.fooddeliveryapplication.Adapters.Home.FoodDrinkFrgAdapter;
import com.example.fooddeliveryapplication.Model.Product;
import com.example.fooddeliveryapplication.databinding.FragmentDrinkHomeFrgBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodHomeFrg extends Fragment {
    private FragmentDrinkHomeFrgBinding binding;
    private ArrayList<Product> dsFood;
    private FoodDrinkFrgAdapter adapter;
    private String userId;

    public FoodHomeFrg(String id) {
        userId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDrinkHomeFrgBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initUI();
        initData();
        return view;
    }

    private void initUI() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rycDrinkHome.setLayoutManager(linearLayoutManager);
        dsFood = new ArrayList<>(); // Initialize dsFood here
        adapter = new FoodDrinkFrgAdapter(dsFood, userId, getContext());
        binding.rycDrinkHome.setAdapter(adapter);
        binding.rycDrinkHome.setHasFixedSize(true);
        binding.txtSeemoreDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FindActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        FirebaseDatabase.getInstance().getReference("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FoodHomeFrg", "Data snapshot: " + snapshot.toString());
                dsFood.clear(); // Clear previous data to avoid duplicates

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Product product = ds.getValue(Product.class);
                        if (product != null) {
                            Log.d("FoodHomeFrg", "Product: " + product.toString());
                            String state = product.getState();
                            String productType = product.getProductType();
                            String publisherId = product.getPublisherId();
                            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            if (state != null && !state.equals("deleted") &&
                                    productType != null && productType.equalsIgnoreCase("Food") &&
                                    publisherId != null && !publisherId.equals(currentUserId)) {
                                dsFood.add(product);
                            }
                        }
                    }
                } else {
                    Log.d("FoodHomeFrg", "No products found");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("DatabaseError", error.getMessage());
            }
        });
    }

}
