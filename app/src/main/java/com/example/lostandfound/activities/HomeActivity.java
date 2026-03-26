package com.example.lostandfound.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lostandfound.DatabaseHelper;
import com.example.lostandfound.R;
import com.example.lostandfound.adapter.ItemAdapter;
import com.example.lostandfound.model.ItemModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView   rvItems;
    private ItemAdapter    adapter;
    private List<ItemModel> itemList = new ArrayList<>();

    private TextView tvTotal, tvLost, tvFound, tvUserName;
    private EditText etSearch;
    private Button   btnAll, btnLost, btnFound;
    private ExtendedFloatingActionButton fabAdd;
    private LinearLayout tvEmpty;

    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DatabaseHelper(this);

        // Bind views
        tvTotal    = findViewById(R.id.tvTotal);
        tvLost     = findViewById(R.id.tvLost);
        tvFound    = findViewById(R.id.tvFound);
        tvUserName = findViewById(R.id.tvUserName);
        etSearch   = findViewById(R.id.etSearch);
        btnAll     = findViewById(R.id.btnAll);
        btnLost    = findViewById(R.id.btnFilterLost);
        btnFound   = findViewById(R.id.btnFilterFound);
        fabAdd     = findViewById(R.id.fabAdd);
        tvEmpty    = findViewById(R.id.tvEmpty);
        rvItems    = findViewById(R.id.rvItems);

        // User name from session
        SharedPreferences prefs = getSharedPreferences("LFSession", MODE_PRIVATE);
        String name = prefs.getString("name", "User");
        tvUserName.setText("Hello, " + name + " 👋");

        // RecyclerView — 2-column grid
        rvItems.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ItemAdapter(this, itemList);
        rvItems.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> showItemDetail(item));

        // Filter buttons
        btnAll.setOnClickListener(v   -> { currentFilter = "all";   setActiveFilter(btnAll);   loadItems(); });
        btnLost.setOnClickListener(v  -> { currentFilter = "lost";  setActiveFilter(btnLost);  loadItems(); });
        btnFound.setOnClickListener(v -> { currentFilter = "found"; setActiveFilter(btnFound); loadItems(); });

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { loadItems(); }
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c)     {}
        });

        // FAB — Report dialog
        fabAdd.setOnClickListener(v -> showReportTypeDialog());

        // Sign out
        ImageButton btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        loadItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        String query = etSearch.getText().toString().trim();
        Cursor cursor;

        if (!query.isEmpty()) {
            cursor = db.searchItems(query);
        } else {
            cursor = db.getItemsByType(currentFilter);
        }

        itemList.clear();

        if (cursor.moveToFirst()) {
            do {
                itemList.add(new ItemModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_PERSON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_STATUS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();

        // Stats
        tvTotal.setText(String.valueOf(db.getTotalCount()));
        tvLost.setText(String.valueOf(db.getLostCount()));
        tvFound.setText(String.valueOf(db.getFoundCount()));

        // Empty state
        tvEmpty.setVisibility(itemList.isEmpty() ? View.VISIBLE : View.GONE);
        rvItems.setVisibility(itemList.isEmpty() ? View.GONE    : View.VISIBLE);
    }

    private void showReportTypeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("What would you like to report?")
                .setItems(new String[]{"🔍  I Lost Something", "✅  I Found Something"}, (dialog, which) -> {
                    Intent intent = new Intent(this, AddItemActivity.class);
                    intent.putExtra("type", which == 0 ? "lost" : "found");
                    startActivity(intent);
                })
                .show();
    }

    private void showItemDetail(ItemModel item) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra("id",          item.getId());
        intent.putExtra("type",        item.getType());
        intent.putExtra("itemName",    item.getItemName());
        intent.putExtra("category",    item.getCategory());
        intent.putExtra("personName",  item.getPersonName());
        intent.putExtra("phone",       item.getPhone());
        intent.putExtra("address",     item.getAddress());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("image",       item.getImageUri());
        intent.putExtra("date",        item.getDate());
        intent.putExtra("status",      item.getStatus());
        startActivity(intent);
    }

    private void setActiveFilter(Button active) {
        int inactive = 0xFF2A2E4A, activeTextLost  = 0xFFFF4D6D;
        int activeTextFound = 0xFF00C896, activeTextAll = 0xFFFF6B35;

        btnAll.setBackgroundResource(R.drawable.btn_filter_inactive);
        btnLost.setBackgroundResource(R.drawable.btn_filter_inactive);
        btnFound.setBackgroundResource(R.drawable.btn_filter_inactive);

        if (active == btnAll) {
            btnAll.setBackgroundResource(R.drawable.btn_filter_active_all);
            btnAll.setTextColor(activeTextAll);
        } else if (active == btnLost) {
            btnLost.setBackgroundResource(R.drawable.btn_filter_active_lost);
            btnLost.setTextColor(activeTextLost);
        } else {
            btnFound.setBackgroundResource(R.drawable.btn_filter_active_found);
            btnFound.setTextColor(activeTextFound);
        }
    }
}
