package my.edu.utar.inventorycontrol;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UpdateProduct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button submitButton;
    EditText productName, productAmount, productCategory, productDesc;

    //Navigation
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        int value = this.getIntent().getIntExtra("idChange", 1);

        //Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

//        Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Hide or show items
        Menu menu = navigationView.getMenu();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_products);

        submitButton = findViewById(R.id.submitupdate);
        productName = findViewById(R.id.productNameUpdate);
        productAmount = findViewById(R.id.productAmountUpdate);
        productCategory = findViewById(R.id.productCategoryUpdate);
        productDesc = findViewById(R.id.productDescriptionUpdate);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString();
                int amt = Integer.valueOf(productAmount.getText().toString());
                String cate = productCategory.getText().toString();
                String desc = productDesc.getText().toString();

                if (TextUtils.isEmpty(name)  && TextUtils.isEmpty(String.valueOf(amt)) && TextUtils.isEmpty(cate) && TextUtils.isEmpty(desc) )
                    Toast.makeText(UpdateProduct.this, "Please add some data.", Toast.LENGTH_SHORT).show();
                else {
                    deleteOld(value);
                    updateDataFirebase(name, amt, cate, desc, value);
                }
            }
        });

    }

    private void updateDataFirebase(String name, int amt, String cate, String desc, int value) {

        Product product = new Product();
        product.setProductID(value);
        product.setProductName(name);
        product.setProductAmount(amt);
        product.setProductCategory(cate);
        product.setProductDescription(desc);

        FirebaseDatabase firebasedatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebasedatabase.getReference("Product/Product" + value + "/");

        databaseReference.setValue(product);
        Toast.makeText(UpdateProduct.this, "data changed", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(UpdateProduct.this, ViewProduct.class);
        startActivity(intent);
        finish();


    }

    private void deleteOld(int value) {
        FirebaseDatabase firebasedatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebasedatabase.getReference("Product");
        databaseReference.child("Product" + value).removeValue();
    }

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
                Intent intent = new Intent(UpdateProduct.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_products:
                break;
            case R.id.nav_transaction:
                Intent intent2 = new Intent(UpdateProduct.this,TransactionMain.class);
                startActivity(intent2);
                break;
            case R.id.nav_expiry:
                Intent intent4 = new Intent(UpdateProduct.this,ExpiryMain.class);
                startActivity(intent4);
                break;
            case R.id.nav_lowstock:
                Intent intent5 = new Intent(UpdateProduct.this,LowStockMain.class);
                startActivity(intent5);
                break;
            case R.id.nav_profile:
                Intent intent6 = new Intent(UpdateProduct.this,UserProfileActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(UpdateProduct.this,SplashActivity.class);
                startActivity(intent3);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}