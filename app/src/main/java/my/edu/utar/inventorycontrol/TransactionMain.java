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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TransactionMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    ArrayList<Transaction> list;

    FloatingActionButton addFab;

    //Navigation
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_main);

        recyclerView = findViewById(R.id.transactionLV);
        databaseReference = FirebaseDatabase.getInstance().getReference("Transaction");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

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

        navigationView.setCheckedItem(R.id.nav_transaction);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    list.add(transaction);
                }

                Collections.sort(list, new Comparator<Transaction>() {

                    @Override
                    public int compare(Transaction lhs, Transaction rhs) {
                        return rhs.getDate().compareTo(lhs.getDate());
                    }
                });

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        addFab = findViewById(R.id.addFab);
        addFab.setOnClickListener(view -> {

            Transaction.getNewId();
            Intent intent = new Intent(this, AddTransaction.class);
            startActivity(intent);
        });

    }

    //navigation
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    //Navigation
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent = new Intent(TransactionMain.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_products:
                Intent intent2 = new Intent(TransactionMain.this,Products.class);
                startActivity(intent2);
                break;
            case R.id.nav_transaction:
                break;
            case R.id.nav_expiry:
                Intent intent4 = new Intent(TransactionMain.this,ExpiryMain.class);
                startActivity(intent4);
                break;
            case R.id.nav_lowstock:
                Intent intent5 = new Intent(TransactionMain.this,LowStockMain.class);
                startActivity(intent5);
                break;
            case R.id.nav_profile:
                Intent intent6 = new Intent(TransactionMain.this,UserProfileActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(TransactionMain.this,SplashActivity.class);
                startActivity(intent3);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}