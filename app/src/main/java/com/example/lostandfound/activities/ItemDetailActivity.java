package com.example.lostandfound.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lostandfound.DatabaseHelper;
import com.example.lostandfound.R;

public class ItemDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private int    itemId;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        db = new DatabaseHelper(this);

        Intent in = getIntent();
        itemId             = in.getIntExtra("id", -1);
        String type        = in.getStringExtra("type");
        String itemName    = in.getStringExtra("itemName");
        String category    = in.getStringExtra("category");
        String personName  = in.getStringExtra("personName");
        phone              = in.getStringExtra("phone");
        String address     = in.getStringExtra("address");
        String description = in.getStringExtra("description");
        String imageUri    = in.getStringExtra("image");
        String date        = in.getStringExtra("date");
        String status      = in.getStringExtra("status");

        TextView tvBack        = findViewById(R.id.tvBack);
        TextView tvBadge       = findViewById(R.id.tvBadge);
        TextView tvItemName    = findViewById(R.id.tvItemName);
        TextView tvCategory    = findViewById(R.id.tvCategory);
        TextView tvPersonName  = findViewById(R.id.tvPersonName);
        TextView tvPhone       = findViewById(R.id.tvPhone);
        TextView tvAddress     = findViewById(R.id.tvAddress);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvDate        = findViewById(R.id.tvDate);
        TextView tvStatus      = findViewById(R.id.tvStatus);
        ImageView imgItem      = findViewById(R.id.imgItem);
        Button btnContact      = findViewById(R.id.btnContact);
        Button btnDelete       = findViewById(R.id.btnDelete);
        Button btnResolve      = findViewById(R.id.btnResolve);

        // Badge colour
        boolean isLost = "lost".equals(type);
        tvBadge.setText(isLost ? "LOST" : "FOUND");
        tvBadge.setBackgroundResource(isLost ? R.drawable.badge_lost : R.drawable.badge_found);
        tvBadge.setTextColor(isLost ? 0xFFFF4D6D : 0xFF00C896);

        tvItemName.setText(itemName);
        tvCategory.setText(category);
        tvPersonName.setText(personName);
        tvPhone.setText(phone);
        tvAddress.setText(address);
        tvDescription.setText(description != null && !description.isEmpty() ? description : "No description provided.");
        tvDate.setText("Reported: " + date);

        if ("resolved".equals(status)) {
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText("✓ Resolved");
            btnResolve.setVisibility(View.GONE);
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        // Image
        if (imageUri != null && !imageUri.isEmpty()) {
            try {
                imgItem.setImageURI(Uri.parse(imageUri));
                imgItem.setVisibility(View.VISIBLE);
            } catch (Exception e) { imgItem.setVisibility(View.GONE); }
        } else {
            imgItem.setVisibility(View.GONE);
        }

        tvBack.setOnClickListener(v -> finish());

        // Call contact
        btnContact.setOnClickListener(v -> {
            Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(call);
        });

        // Delete
        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Report")
                        .setMessage("Are you sure you want to delete this report?")
                        .setPositiveButton("Delete", (d, w) -> {
                            db.deleteItem(itemId);
                            Toast.makeText(this, "Report deleted.", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        // Mark resolved
        btnResolve.setOnClickListener(v -> {
            db.markResolved(itemId);
            Toast.makeText(this, "Marked as resolved!", Toast.LENGTH_SHORT).show();
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText("✓ Resolved");
            btnResolve.setVisibility(View.GONE);
        });
    }
}
