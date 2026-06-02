package com.example.coffeeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

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
            Toast.makeText(this, "Order Confirmed. Processing Payment...", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, ReceiptActivity.class);
            intent.putExtras(getIntent().getExtras()); // Pass all extras to Receipt
            startActivity(intent);
        });
    }

    private void composeEmail(String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_subject));
        intent.putExtra(Intent.EXTRA_TEXT, body);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.msg_no_email), Toast.LENGTH_LONG).show();
        }
    }
}
