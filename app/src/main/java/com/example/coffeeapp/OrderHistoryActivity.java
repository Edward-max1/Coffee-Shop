package com.example.coffeeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private TextView noOrdersText;
    private DatabaseReference mDatabase;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        noOrdersText = findViewById(R.id.noOrdersText);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList);
        ordersRecyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        username = prefs.getString("username", "");

        mDatabase = FirebaseDatabase.getInstance().getReference("orders");

        fetchOrders();
    }

    private void fetchOrders() {
        Query query;
        // Assume 'admin' is the username for the administrator
        if (username.equals("admin")) {
            query = mDatabase.orderByChild("timestamp");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("All Orders (Admin)");
            }
        } else {
            query = mDatabase.orderByChild("username").equalTo(username);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("My Order History");
            }
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Order order = postSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                
                // Sort by timestamp descending (newest first)
                Collections.sort(orderList, (o1, o2) -> Long.compare(o2.timestamp, o1.timestamp));
                
                adapter.notifyDataSetChanged();

                if (orderList.isEmpty()) {
                    noOrdersText.setVisibility(View.VISIBLE);
                } else {
                    noOrdersText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
