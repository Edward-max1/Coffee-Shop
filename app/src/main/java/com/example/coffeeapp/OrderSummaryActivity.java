package com.example.coffeeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_summary);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView summaryText = findViewById(R.id.orderSummaryText);
        MaterialButton payBtn = findViewById(R.id.payBtn);

        String orderSummary = getIntent().getStringExtra("ORDER_SUMMARY");
        if (orderSummary != null) {
            summaryText.setText(orderSummary);
        }

        payBtn.setOnClickListener(v -> {
            RadioGroup paymentGroup = findViewById(R.id.paymentGroup);
            int selectedId = paymentGroup.getCheckedRadioButtonId();
            RadioButton selectedButton = findViewById(selectedId);
            String paymentMethod = selectedButton != null ? selectedButton.getText().toString() : "Unknown";

            saveOrderToFirebase(paymentMethod);

            Toast.makeText(this, "Order Confirmed. Processing Payment...", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, ReceiptActivity.class);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                intent.putExtras(extras);
            }
            intent.putExtra("PAYMENT_METHOD", paymentMethod);
            startActivity(intent);
        });
    }

    private void saveOrderToFirebase(String paymentMethod) {
        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        int quantity = intent.getIntExtra("QUANTITY", 0);
        int totalPrice = intent.getIntExtra("TOTAL_PRICE", 0);
        
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = prefs.getString("username", "guest");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("orders");
        String orderId = mDatabase.push().getKey();

        Map<String, Integer> toppings = new HashMap<>();
        if (intent.getBooleanExtra("HAS_CHAPATI", false)) toppings.put("Chapati", intent.getIntExtra("CHAPATI_QTY", 0));
        if (intent.getBooleanExtra("HAS_MANDAZI", false)) toppings.put("Mandazi", intent.getIntExtra("MANDAZI_QTY", 0));
        if (intent.getBooleanExtra("HAS_GITHERI", false)) toppings.put("Githeri", intent.getIntExtra("GITHERI_QTY", 0));
        if (intent.getBooleanExtra("HAS_BREAD", false)) toppings.put("Bread", intent.getIntExtra("BREAD_QTY", 0));

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Order order = new Order(orderId, username, name, date, quantity, toppings, totalPrice, paymentMethod, System.currentTimeMillis());

        if (orderId != null) {
            mDatabase.child(orderId).setValue(order);
        }
    }
}
