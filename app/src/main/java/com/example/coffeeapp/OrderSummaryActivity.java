package com.example.coffeeapp;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class OrderSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        String summary = getIntent().getStringExtra("ORDER_SUMMARY");
        TextView summaryTextView = findViewById(R.id.orderSummaryText);
        summaryTextView.setText(summary);

        MaterialButton payBtn = findViewById(R.id.payBtn);
        RadioGroup paymentGroup = findViewById(R.id.paymentGroup);

        payBtn.setOnClickListener(v -> {
            int selectedId = paymentGroup.getCheckedRadioButtonId();
            RadioButton selectedButton = findViewById(selectedId);
            
            if (selectedButton != null) {
                String method = selectedButton.getText().toString();
                Toast.makeText(this, getString(R.string.msg_payment_success) + " via " + method, Toast.LENGTH_LONG).show();
                // Here you would typically integrate a real payment gateway
                finish();
            }
        });
    }
}