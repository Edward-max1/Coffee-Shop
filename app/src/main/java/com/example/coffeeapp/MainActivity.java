package com.example.coffeeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import java.lang.reflect.Method;
import android.view.Menu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;



public class MainActivity extends AppCompatActivity {


    private static final int PRICE_COFFEE = 50;
    private static final int PRICE_CHAPATI = 20;
    private static final int PRICE_MANDAZI = 10;
    private static final int PRICE_GITHERI = 30;
    private static final int PRICE_BREAD = 35;

    Date date = new Date();
    private boolean isLoggedIn;
    private String userFullName;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            SpannableString s = new SpannableString(getString(R.string.title_coffee_shop));
            s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(s);
        }

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        userFullName = prefs.getString("fullName", "");

        TextView greetingText = findViewById(R.id.greetingText);
        View nameInputLayout = findViewById(R.id.nameInputLayout);

        if (isLoggedIn && !userFullName.isEmpty()) {
            greetingText.setVisibility(View.VISIBLE);
            greetingText.setText(getGreeting(userFullName));
            nameInputLayout.setVisibility(View.GONE);
        } else {
            greetingText.setVisibility(View.GONE);
            nameInputLayout.setVisibility(View.VISIBLE);
        }


        final EditText coffeeNum = findViewById(R.id.coffeeNum);
        final TextInputEditText nameInput = findViewById(R.id.nameInput);
        MaterialButton minusBtn = findViewById(R.id.minusBtn);
        MaterialButton plusBtn = findViewById(R.id.plusBtn);
        MaterialButton orderBtn = findViewById(R.id.order);
        final CheckBox chapati = findViewById(R.id.chapati);
        final CheckBox mandazi = findViewById(R.id.mandazi);
        final CheckBox githeri = findViewById(R.id.githeri);
        final CheckBox bread = findViewById(R.id.bread);

        // Topping Quantity Controls
        final TextView chapatiQty = findViewById(R.id.chapatiQty);
        final TextView mandaziQty = findViewById(R.id.mandaziQty);
        final TextView githeriQty = findViewById(R.id.githeriQty);
        final TextView breadQty = findViewById(R.id.breadQty);

        setupToppingCounter(findViewById(R.id.chapatiPlus), findViewById(R.id.chapatiMinus), chapatiQty);
        setupToppingCounter(findViewById(R.id.mandaziPlus), findViewById(R.id.mandaziMinus), mandaziQty);
        setupToppingCounter(findViewById(R.id.githeriPlus), findViewById(R.id.githeriMinus), githeriQty);
        setupToppingCounter(findViewById(R.id.breadPlus), findViewById(R.id.breadMinus), breadQty);


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

            int cQty = Integer.parseInt(chapatiQty.getText().toString());
            int mQty = Integer.parseInt(mandaziQty.getText().toString());
            int gQty = Integer.parseInt(githeriQty.getText().toString());
            int bQty = Integer.parseInt(breadQty.getText().toString());

            if (chapati.isChecked()) {
                totalPrice += (cQty * PRICE_CHAPATI);
                toppingsList.append("- Chapati x").append(cQty).append(" (KES ").append(cQty * PRICE_CHAPATI).append(")\n");
            }
            if (mandazi.isChecked()) {
                totalPrice += (mQty * PRICE_MANDAZI);
                toppingsList.append("- Mandazi x").append(mQty).append(" (KES ").append(mQty * PRICE_MANDAZI).append(")\n");
            }
            if (githeri.isChecked()) {
                totalPrice += (gQty * PRICE_GITHERI);
                toppingsList.append("- Githeri x").append(gQty).append(" (KES ").append(gQty * PRICE_GITHERI).append(")\n");
            }
            if (bread.isChecked()) {
                totalPrice += (bQty * PRICE_BREAD);
                toppingsList.append("- Bread x").append(bQty).append(" (KES ").append(bQty * PRICE_BREAD).append(")\n");
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
            intent.putExtra("CHAPATI_QTY", cQty);
            intent.putExtra("MANDAZI_QTY", mQty);
            intent.putExtra("GITHERI_QTY", gQty);
            intent.putExtra("BREAD_QTY", bQty);
            intent.putExtra("TOTAL_PRICE", totalPrice);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // This method enables icons to be shown in the overflow menu
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null && menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToppingCounter(View plus, View minus, final TextView qtyText) {
        plus.setOnClickListener(v -> {
            int current = Integer.parseInt(qtyText.getText().toString());
            qtyText.setText(String.valueOf(current + 1));
        });
        minus.setOnClickListener(v -> {
            int current = Integer.parseInt(qtyText.getText().toString());
            if (current > 1) {
                qtyText.setText(String.valueOf(current - 1));
            }
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
}
