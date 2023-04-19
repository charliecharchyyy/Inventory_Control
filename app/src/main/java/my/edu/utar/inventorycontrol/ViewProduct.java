package my.edu.utar.inventorycontrol;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewProduct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    List<Product> productList = new ArrayList<Product>();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        //Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

//        Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

//        Navigation

        // Hide or show items
        Menu menu = navigationView.getMenu();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        FirebaseDatabase firebasedatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebasedatabase.getReference("Product");

        ListView myList;
        myList = findViewById(R.id.list);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productList.clear();
                for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {

                    Product product = new Product();
                    product.setProductID(zoneSnapshot.child("productID").getValue(Integer.class));
                    product.setProductAmount(zoneSnapshot.child("productAmount").getValue(Integer.class));
                    product.setProductName(zoneSnapshot.child("productName").getValue(String.class));
                    product.setProductCategory(zoneSnapshot.child("productCategory").getValue(String.class));
                    product.setProductDescription(zoneSnapshot.child("productDescription").getValue(String.class));

                    productList.add(product);
                }

                ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(ViewProduct.this, android.R.layout.simple_list_item_1, productList);
                myList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product item = (Product) parent.getItemAtPosition(position);

                PopupMenu popup = new PopupMenu(ViewProduct.this, view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuitem) {
                        switch (menuitem.getItemId()) {
                            case R.id.item1:
                                Intent intent = new Intent(ViewProduct.this, UpdateProduct.class);
                                intent.putExtra("idChange", item.getProductID());
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Clicked Update", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.item3:
                                Dialog customDialog1 = new Dialog(ViewProduct.this);
                                customDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                customDialog1.setContentView(R.layout.custom_alert);
                                customDialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                customDialog1.setCancelable(false);
                                customDialog1.getWindow().getAttributes().windowAnimations = R.style.animation;
                                customDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                TextView title = customDialog1.findViewById(R.id.title);
                                TextView content = customDialog1.findViewById(R.id.content);
                                Button btn_no = customDialog1.findViewById(R.id.btn_no);
                                Button btn_yes = customDialog1.findViewById(R.id.btn_yes);

                                title.setText("Confirm Delete");
                                content.setText("Are you sure you want to delete?");

                                btn_no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customDialog1.dismiss();
                                    }
                                });
                                btn_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        delete(position, "Transaction" + transaction.getId().substring(1));
                                        FirebaseDatabase firebasedatabase = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference = firebasedatabase.getReference("Product");
                                        databaseReference.child("Product" + item.getProductID()).removeValue();
                                        customDialog1.dismiss();
                                    }
                                });
                                customDialog1.show();
                                Toast.makeText(getApplicationContext(), "Clicked Delete", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();

            }
        });

    }

    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent = new Intent(ViewProduct.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_products:
                break;
            case R.id.nav_transaction:
                Intent intent2 = new Intent(ViewProduct.this,TransactionMain.class);
                startActivity(intent2);
                break;
            case R.id.nav_expiry:
                Intent intent4 = new Intent(ViewProduct.this,ExpiryMain.class);
                startActivity(intent4);
                break;
            case R.id.nav_lowstock:
                Intent intent5 = new Intent(ViewProduct.this,LowStockMain.class);
                startActivity(intent5);
                break;
            case R.id.nav_profile:
                Intent intent6 = new Intent(ViewProduct.this,UserProfileActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(ViewProduct.this,SplashActivity.class);
                startActivity(intent3);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}