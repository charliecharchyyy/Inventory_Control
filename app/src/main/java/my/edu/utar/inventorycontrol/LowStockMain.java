package my.edu.utar.inventorycontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LowStockMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView totalLowStockView;

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    LowStockAdapter lowStockAdapter;
    ArrayList<Product> list;

    Integer totalLowStock = 0;

    final Integer LOW_STOCK = 10;

    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_stock_main);

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

        navigationView.setCheckedItem(R.id.nav_lowstock);

        totalLowStockView = findViewById(R.id.totalLowStock);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product.getProductQuantity() <= LOW_STOCK){
                        list.add(product);
                    }
                }

//                Collections.sort(list, new Comparator<Product>() {
//
//                    @Override
//                    public int compare(Product lhs, Product rhs) {
//                        return rhs.getId().compareTo(lhs.getId());
//                    }
//                });

                lowStockAdapter = new LowStockAdapter(LowStockMain.this, list);
                recyclerView.setAdapter(lowStockAdapter);
                totalLowStock = lowStockAdapter.getItemCount();
                totalLowStockView.setText("Total Low Stock Items: "+ totalLowStock);
                lowStockAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LowStockMain.this, ""+error, Toast.LENGTH_SHORT).show();

            }
        });
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
                Intent intent5 = new Intent(LowStockMain.this,MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_products:
                Intent intent = new Intent(LowStockMain.this,Products.class);
                startActivity(intent);
                break;
            case R.id.nav_transaction:
                Intent intent2 = new Intent(LowStockMain.this,TransactionMain.class);
                startActivity(intent2);
                break;
            case R.id.nav_expiry:
                Intent intent4 = new Intent(LowStockMain.this,ExpiryMain.class);
                startActivity(intent4);
                break;
            case R.id.nav_lowstock:
                break;
            case R.id.nav_profile:
                Intent intent6 = new Intent(LowStockMain.this,UserProfileActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(LowStockMain.this,SplashActivity.class);
                startActivity(intent3);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}