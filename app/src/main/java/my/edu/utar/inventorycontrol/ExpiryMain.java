package my.edu.utar.inventorycontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ExpiryMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView totalItemsView, totalExpiringItemsView, totalExpiredItemsView;

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ExpiryAdapter myAdapter;
    ArrayList<ProductExpiry> list;

    Integer totalItems = 0;
    Integer totalExpiringItems = 0;
    Integer totalExpiredItems = 0;

    DatePickerDialog datePickerDialog;
    boolean error;

    FloatingActionButton addFab;

    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiry_main);

        //Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Hide or show items
        Menu menu = navigationView.getMenu();
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_expiry);

        totalItemsView= findViewById(R.id.totalItems);
        totalExpiringItemsView = findViewById(R.id.totalExpiringItems);
        totalExpiredItemsView = findViewById(R.id.totalExpiredItems);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Expiry");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ProductExpiry productExpiry = dataSnapshot.getValue(ProductExpiry.class);
                    list.add(productExpiry);
                }

                Collections.sort(list, new Comparator<ProductExpiry>() {

                    @Override
                    public int compare(ProductExpiry lhs, ProductExpiry rhs) {
                        return rhs.getId().compareTo(lhs.getId());
                    }
                });


                myAdapter = new ExpiryAdapter(ExpiryMain.this, list);
                recyclerView.setAdapter(myAdapter);
                totalItems = myAdapter.getItemCount();
                totalItemsView.setText("Total Items: "+ totalItems);
                totalExpiringItems = myAdapter.getExpiringItemCount();
                totalExpiringItemsView.setText("Total Expiring Items: "+totalExpiringItems);
                totalExpiredItems = myAdapter.getExpiredItemCount();
                totalExpiredItemsView.setText("Total Expired Items: "+totalExpiredItems);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ExpiryMain.this, ""+error, Toast.LENGTH_SHORT).show();

            }
        });

        addFab = findViewById(R.id.fab);
        addFab.setOnClickListener(view -> {

            ProductExpiry.getNewId();

            addItem();

        });

    }

    private void addItem() {

        Dialog customDialog = new Dialog(ExpiryMain.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.expiry_input);
        customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customDialog.setCancelable(false);
        customDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        EditText quantity, note, expiryDate;
        Button btnCancel, btnSave;
        Spinner productName, itemsSpinner;

        productName = customDialog.findViewById(R.id.name);
        quantity = customDialog.findViewById(R.id.quantity);
        note = customDialog.findViewById(R.id.note);
        expiryDate = customDialog.findViewById(R.id.expiryDate);
        btnCancel = customDialog.findViewById(R.id.cancel);
        btnSave = customDialog.findViewById(R.id.save);
        itemsSpinner = customDialog.findViewById(R.id.spinner);

//        //category spinner
        List<String> category = Arrays.asList("Select category", "Fruit", "Canned Goods",
                "Canned Beverage", "Beverage", "Vegetable", "Snack");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,category);
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemsSpinner.setAdapter(itemsAdapter);

        List<String> productsName, productsId, productsAmount;
        DatabaseReference productRef;
        //products spinner
        productsName = new ArrayList<>();
        productsId = new ArrayList<>();
        productsAmount = new ArrayList<>();
        productsName.add(0, "Select product");
        productRef = FirebaseDatabase.getInstance().getReference();
        productRef.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String productName = dataSnapshot.child("productName").getValue(String.class);
                    String productId = dataSnapshot.child("productID").getValue().toString();
                    String productAmount = dataSnapshot.child("productAmount").getValue().toString();
                    productsName.add(productName);
                    productsId.add(productId);
                    productsAmount.add(productAmount);

                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ExpiryMain.this, android.R.layout.simple_list_item_1, productsName);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productName.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //date picker
        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(ExpiryMain.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // set day of month , month and year value in the edit text
                            expiryDate.setText(dayOfMonth + "/"
                                    + (monthOfYear + 1) + "/" + year);

                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference = FirebaseDatabase.getInstance().getReference("Expiry/Expiry" + ProductExpiry.count + "/");
                String Id = "E" + (ProductExpiry.count);
                String mName = productName.getSelectedItem().toString().trim();
                String mQuantity = quantity.getText().toString().trim();
                String mNotes = note.getText().toString().trim();
                String mDate = expiryDate.getText().toString().trim();
                String item = itemsSpinner.getSelectedItem().toString().trim();

                if(TextUtils.isEmpty(mName)){
                    Toast.makeText(ExpiryMain.this, "Please select a product!", Toast.LENGTH_LONG).show();
                    error=true;
                }else if(TextUtils.isEmpty(mQuantity)){
                    Toast.makeText(ExpiryMain.this, "Please enter the quantity!", Toast.LENGTH_LONG).show();
                    error=true;
                }else if(TextUtils.isEmpty(mNotes)){
                    Toast.makeText(ExpiryMain.this, "Please enter the remark!", Toast.LENGTH_LONG).show();
                    error=true;
                }else if(TextUtils.isEmpty(mDate)){
                    Toast.makeText(ExpiryMain.this, "Please select a date!", Toast.LENGTH_LONG).show();
                    error=true;
                }else if(TextUtils.isEmpty(item)){
                    Toast.makeText(ExpiryMain.this, "Please select a category!", Toast.LENGTH_LONG).show();
                    error=true;
                }else{

                    ProductExpiry productExpiry = new ProductExpiry(item, mName, mDate, Id, mNotes, mQuantity);
                    databaseReference.setValue(productExpiry);

                    Toast.makeText(ExpiryMain.this, "Expiry is added", Toast.LENGTH_LONG).show();

                }

                customDialog.dismiss();

            }
        });

        customDialog.show();
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent4 = new Intent(ExpiryMain.this,MainActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_products:
                Intent intent = new Intent(ExpiryMain.this,Products.class);
                startActivity(intent);
                break;
            case R.id.nav_transaction:
                Intent intent2 = new Intent(ExpiryMain.this,TransactionMain.class);
                startActivity(intent2);
                break;
            case R.id.nav_expiry:
                break;
            case R.id.nav_lowstock:
                Intent intent5 = new Intent(ExpiryMain.this,LowStockMain.class);
                startActivity(intent5);
                break;
            case R.id.nav_profile:
                Intent intent6 = new Intent(ExpiryMain.this,UserProfileActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(ExpiryMain.this,SplashActivity.class);
                startActivity(intent3);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}