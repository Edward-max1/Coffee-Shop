package com.example.coffeeapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import android.graphics.pdf.PdfDocument;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receipt);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();
        int quantity = intent.getIntExtra("QUANTITY", 0);
        int totalPrice = intent.getIntExtra("TOTAL_PRICE", 0);
        boolean hasChapati = intent.getBooleanExtra("HAS_CHAPATI", false);
        boolean hasMandazi = intent.getBooleanExtra("HAS_MANDAZI", false);
        boolean hasGitheri = intent.getBooleanExtra("HAS_GITHERI", false);
        boolean hasBread = intent.getBooleanExtra("HAS_BREAD", false);
        int chapatiQty = intent.getIntExtra("CHAPATI_QTY", 1);
        int mandaziQty = intent.getIntExtra("MANDAZI_QTY", 1);
        int githeriQty = intent.getIntExtra("GITHERI_QTY", 1);
        int breadQty = intent.getIntExtra("BREAD_QTY", 1);
        String paymentMethod = intent.getStringExtra("PAYMENT_METHOD");
        String customerName = intent.getStringExtra("NAME");


        TextView coffeeQty = findViewById(R.id.coffeeQty);
        TextView coffeePrice = findViewById(R.id.coffeePrice);
        TextView overallTotal = findViewById(R.id.overallTotal);
        TextView nameText = findViewById(R.id.receiptCustomerName);
        TextView dateTimeText = findViewById(R.id.receiptDateTime);
        TextView paymentMethodText = findViewById(R.id.receiptPaymentMethod);
        MaterialButton backBtn = findViewById(R.id.backToHome);
        MaterialButton downloadBtn = findViewById(R.id.downloadBtn);
        View receiptContent = findViewById(R.id.receiptContent);
        TableRow toppingsHeader = findViewById(R.id.toppingsHeaderRow);


        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        dateTimeText.setText("Date: " + currentDateTime);


        nameText.setText("Customer: " + (customerName != null ? customerName : "N/A"));


        paymentMethodText.setText("Payment: " + (paymentMethod != null ? paymentMethod : "N/A"));


        coffeeQty.setText(String.valueOf(quantity));
        coffeePrice.setText("KES " + (quantity * 50));


        boolean anyToppings = hasChapati || hasMandazi || hasGitheri || hasBread;
        if (anyToppings) {
            toppingsHeader.setVisibility(View.VISIBLE);
        }


        setupToppingRow(R.id.chapatiRow, R.id.chapatiQty, R.id.chapatiPrice, hasChapati, chapatiQty, 20);
        setupToppingRow(R.id.mandaziRow, R.id.mandaziQty, R.id.mandaziPrice, hasMandazi, mandaziQty, 10);
        setupToppingRow(R.id.githeriRow, R.id.githeriQty, R.id.githeriPrice, hasGitheri, githeriQty, 30);
        setupToppingRow(R.id.breadRow, R.id.breadQty, R.id.breadPrice, hasBread, breadQty, 35);

        overallTotal.setText("KES " + totalPrice);

        backBtn.setOnClickListener(v -> {
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        });

        downloadBtn.setOnClickListener(v -> {
            downloadReceipt(receiptContent);
        });
    }

    private void setupToppingRow(int rowId, int qtyId, int priceId, boolean hasTopping, int quantity, int unitPrice) {
        TableRow row = findViewById(rowId);
        if (hasTopping) {
            row.setVisibility(View.VISIBLE);
            TextView qtyText = findViewById(qtyId);
            TextView priceText = findViewById(priceId);
            qtyText.setText(String.valueOf(quantity));
            priceText.setText("KES " + (quantity * unitPrice));
        } else {
            row.setVisibility(View.GONE);
        }
    }

    private void downloadReceipt(View view) {
        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(view.getWidth(), view.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);


        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        document.finishPage(page);

        try {
            String filename = "CoffeeReceipt_" + System.currentTimeMillis() + ".pdf";
            OutputStream fos;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri pdfUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                fos = getContentResolver().openOutputStream(pdfUri);
            } else {
                java.io.File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                java.io.File file = new java.io.File(downloadsDir, filename);
                fos = new java.io.FileOutputStream(file);
            }

            if (fos != null) {
                document.writeTo(fos);
                document.close();
                fos.close();
                Toast.makeText(this, "Receipt downloaded as PDF to Downloads folder", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error downloading PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            document.close();
        }
    }
}
