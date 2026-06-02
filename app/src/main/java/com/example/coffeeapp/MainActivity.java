package com.example.coffeeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;



public class MainActivity extends AppCompatActivity {


    private static final int PRICE_COFFEE = 100;
    private static final int PRICE_CHAPATI = 20;
    private static final int PRICE_MANDAZI = 10;
    private static final int PRICE_GITHERI = 50;
    private static final int PRICE_BREAD = 35;

    Date date = new Date();
    private boolean isLoggedIn;
    private String userFullName;
    private String userEmail;

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

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        userFullName = prefs.getString("fullName", "");
        userEmail = prefs.getString("email", "");

        TextView greetingText = findViewById(R.id.greetingText);
        View nameInputLayout = findViewById(R.id.nameInputLayout);
        ImageButton logoutBtn = findViewById(R.id.logoutBtn);

        if (isLoggedIn && !userFullName.isEmpty()) {
            greetingText.setVisibility(View.VISIBLE);
            greetingText.setText(getGreeting(userFullName));
            nameInputLayout.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
        } else {
            greetingText.setVisibility(View.GONE);
            nameInputLayout.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
        }

        logoutBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        final EditText coffeeNum = findViewById(R.id.coffeeNum);
        final TextInputEditText nameInput = findViewById(R.id.nameInput);
        MaterialButton minusBtn = findViewById(R.id.minusBtn);
        MaterialButton plusBtn = findViewById(R.id.plusBtn);
        MaterialButton orderBtn = findViewById(R.id.order);
        final CheckBox chapati = findViewById(R.id.chapati);
        final CheckBox mandazi = findViewById(R.id.mandazi);
        final CheckBox githeri = findViewById(R.id.githeri);
        final CheckBox bread = findViewById(R.id.bread);


        plusBtn.setOnClickListener(v -> {
            int current = getQuantity(coffeeNum);
            current++;
            coffeeNum.setText(String.valueOf(current));
        });


        minusBtn.setOnClickListener(v -> {
            int current = getQuantity(coffeeNum);
            if (current > 1) {
                current--;
                coffeeNum.setText(String.valueOf(current));
            } else {
                Toast.makeText(this, getString(R.string.msg_min_order), Toast.LENGTH_SHORT).show();
            }
        });


        orderBtn.setOnClickListener(v -> {
            String name;
            if (isLoggedIn && !userFullName.isEmpty()) {
                name = userFullName;
            } else {
                name = Objects.requireNonNull(nameInput.getText()).toString().trim();
                if (name.isEmpty()) {
                    nameInput.setError(getString(R.string.err_empty_name));
                    Toast.makeText(this, getString(R.string.err_empty_name), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            int quantity = getQuantity(coffeeNum);


            int totalPrice = quantity * PRICE_COFFEE;
            StringBuilder toppingsList = new StringBuilder();

            if (chapati.isChecked()) {
                totalPrice += PRICE_CHAPATI;
                toppingsList.append("- Chapati (KES ").append(PRICE_CHAPATI).append(")\n");
            }
            if (mandazi.isChecked()) {
                totalPrice += PRICE_MANDAZI;
                toppingsList.append("- Mandazi (KES ").append(PRICE_MANDAZI).append(")\n");
            }
            if (githeri.isChecked()) {
                totalPrice += PRICE_GITHERI;
                toppingsList.append("- Githeri (KES ").append(PRICE_GITHERI).append(")\n");
            }
            if (bread.isChecked()) {
                totalPrice += PRICE_BREAD;
                toppingsList.append("- Bread (KES ").append(PRICE_BREAD).append(")\n");
            }


            String orderSummary = createOrderSummary(name, quantity, toppingsList.toString(), totalPrice);

            Intent intent = new Intent(this, OrderSummaryActivity.class);
            intent.putExtra("ORDER_SUMMARY", orderSummary);
            intent.putExtra("NAME", name);
            intent.putExtra("QUANTITY", quantity);
            intent.putExtra("HAS_CHAPATI", chapati.isChecked());
            intent.putExtra("HAS_MANDAZI", mandazi.isChecked());
            intent.putExtra("HAS_GITHERI", githeri.isChecked());
            intent.putExtra("HAS_BREAD", bread.isChecked());
            intent.putExtra("TOTAL_PRICE", totalPrice);
            startActivity(intent);
        });
    }

    private String getGreeting(String name) {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay < 12) {
            greeting = "Good Morning";
        } else if (timeOfDay < 16) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }
        return greeting + ", " + name;
    }

    private String createOrderSummary(String name, int quantity, String toppings, int price) {
        return  " **** Order Summary **** \n" +
                "-----------------------------------\n" +
                "Name: " + name + "\n" +
                "Date: " + date + "\n\n" +
                "Coffee Quantity: " + quantity + "\n" +
                "Toppings:\n" + (toppings.isEmpty() ? "None\n" : toppings) +
                "Total Price: KES " + price + "\n" +
                "-----------------------------------\n" +
                "Thank you for your order!";
    }



    private int getQuantity(EditText et) {
        String value = et.getText().toString();
        if (value.isEmpty()) return 1;
        try {
            int val = Integer.parseInt(value);
            return Math.max(val, 1);
        } catch (NumberFormatException e) {
            return 1;
        }
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
