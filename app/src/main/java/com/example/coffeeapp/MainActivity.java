package com.example.coffeeapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText coffeeNum = findViewById(R.id.coffeeNum);
        EditText nameInput = findViewById(R.id.nameInput);
        Button minusBtn = findViewById(R.id.minusBtn);
        Button plusBtn = findViewById(R.id.plusBtn);
        Button orderBtn = findViewById(R.id.order);
        CheckBox chapati = findViewById(R.id.chapati);
        CheckBox mandazi = findViewById(R.id.mandazi);
        CheckBox githeri = findViewById(R.id.githeri);
        CheckBox bread = findViewById(R.id.bread);

        plusBtn.setOnClickListener(v -> {
            int current = getQuantity(coffeeNum);
            current++;
            coffeeNum.setText(String.valueOf(current));
        });

        minusBtn.setOnClickListener(v -> {
            int current = getQuantity(coffeeNum);
            if (current > 0) {
                current--;
            }
            coffeeNum.setText(String.valueOf(current));
        });

        orderBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            int quantity = getQuantity(coffeeNum);
            
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder toppings = new StringBuilder();
            if (chapati.isChecked()) toppings.append("Chapati ");
            if (mandazi.isChecked()) toppings.append("Mandazi ");
            if (githeri.isChecked()) toppings.append("Githeri ");
            if (bread.isChecked()) toppings.append("Bread ");

            String message = "Order for " + name + "\n" +
                    "Quantity: " + quantity + "\n" +
                    "Toppings: " + (toppings.length() > 0 ? toppings.toString() : "None");

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    private int getQuantity(EditText et) {
        String value = et.getText().toString();
        if (value.isEmpty()) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}