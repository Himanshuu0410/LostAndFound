package com.example.lostandfound.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lostandfound.DatabaseHelper;
import com.example.lostandfound.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private EditText   etItemName, etPersonName, etPhone, etAddress, etDescription;
    private Spinner    spinnerCategory;
    private ImageView  imgPreview;
    private Button     btnSelectImage, btnSave;
    private TextView   tvTitle, tvBack, tvError;
    private Uri        imageUri;
    private DatabaseHelper db;
    private String     type; // "lost" or "found"

    private static final String[] CATEGORIES = {
            "Electronics", "Accessories", "Bags", "Keys",
            "Jewelry", "Documents", "Clothing", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        type = getIntent().getStringExtra("type");
        if (type == null) type = "lost";

        db            = new DatabaseHelper(this);
        tvTitle       = findViewById(R.id.tvTitle);
        tvBack        = findViewById(R.id.tvBack);
        etItemName    = findViewById(R.id.etItemName);
        etPersonName  = findViewById(R.id.etPersonName);
        etPhone       = findViewById(R.id.etPhone);
        etAddress     = findViewById(R.id.etAddress);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imgPreview    = findViewById(R.id.imgPreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave       = findViewById(R.id.btnSave);
        tvError       = findViewById(R.id.tvError);

        // Title
        if ("lost".equals(type)) {
            tvTitle.setText("🔍  Report Lost Item");
            btnSave.setBackgroundResource(R.drawable.btn_primary_lost);
            btnSave.setText("Submit Lost Report");
        } else {
            tvTitle.setText("✅  Report Found Item");
            btnSave.setBackgroundResource(R.drawable.btn_primary_found);
            btnSave.setText("Submit Found Report");
        }

        // Category spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATEGORIES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        tvBack.setOnClickListener(v -> finish());

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        btnSave.setOnClickListener(v -> saveItem());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
            imgPreview.setVisibility(View.VISIBLE);
            btnSelectImage.setText("Change Photo");
        }
    }

    private void saveItem() {
        String itemName    = etItemName.getText().toString().trim();
        String personName  = etPersonName.getText().toString().trim();
        String phone       = etPhone.getText().toString().trim();
        String address     = etAddress.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category    = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(itemName)) { showError("Please enter the item name");      return; }
        if (TextUtils.isEmpty(personName)) { showError("Please enter your name");        return; }
        if (TextUtils.isEmpty(phone)) { showError("Please enter a contact number");      return; }
        if (TextUtils.isEmpty(address)) { showError("Please enter the location");        return; }

        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        String uri  = imageUri != null ? imageUri.toString() : "";

        boolean ok = db.addItem(type, itemName, category, personName, phone, address, description, uri, date);

        if (ok) {
            Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showError("Failed to save. Please try again.");
        }
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText("⚠  " + msg);
    }
}
