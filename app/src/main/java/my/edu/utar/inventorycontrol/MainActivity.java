package my.edu.utar.inventorycontrol;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        navigationView.setCheckedItem(R.id.nav_home);

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
                break;
            case R.id.nav_products:
                Intent intent = new Intent(MainActivity.this,Products.class);
                startActivity(intent);
                break;
            case R.id.nav_transaction:
                Intent intent2 = new Intent(MainActivity.this,TransactionMain.class);
                startActivity(intent2);
                break;
            case R.id.nav_expiry:
                Intent intent4 = new Intent(MainActivity.this,ExpiryMain.class);
                startActivity(intent4);
                break;
            case R.id.nav_lowstock:
                Intent intent5 = new Intent(MainActivity.this,LowStockMain.class);
                startActivity(intent5);
                break;
            case R.id.nav_profile:
                Intent intent6 = new Intent(MainActivity.this,UserProfileActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(MainActivity.this,SplashActivity.class);
                startActivity(intent3);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void productBtn(View view) {
        showDialog();
    }

    public void transBtn(View view) {
        showDialog2();
    }

    public void exportBtn(View view) {
        showDialog3();
    }

    public void profileBtn(View view) {
        showDialog4();
    }

    public void expiryBtn(View view) {
        showDialog5();
    }

    public void aboutUsBtn(View view) {
        showDialog6();
    }

    private void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_product_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);
        Button btnYes = dialog.findViewById(R.id.btn_yes);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Products.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void showDialog2() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_transaction_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);
        Button btnYes = dialog.findViewById(R.id.btn_yes);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TransactionMain.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void showDialog3() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_export_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);
        Button btnYes = dialog.findViewById(R.id.btn_yes);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDialog4() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_profile_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);
        Button btnYes = dialog.findViewById(R.id.btn_yes);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void showDialog5() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_expiry_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);
        Button btnYes = dialog.findViewById(R.id.btn_yes);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ExpiryMain.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void showDialog6() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_aboutus_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);
        Button btnYes = dialog.findViewById(R.id.btn_yes);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}