package my.edu.utar.inventorycontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextInputLayout fullName, email, phoneNo, password;
    TextView fullNameLabel, usernameLabel;

    Button exit;

    String user_name, user_username, user_email, user_phoneNo, user_password;

    DatabaseReference reference;

    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        reference = FirebaseDatabase.getInstance().getReference("SignUp");

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

        navigationView.setCheckedItem(R.id.nav_profile);

        //Hooks
        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phoneNo_profile);
        password = findViewById(R.id.password_profile);
//        fullNameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);
        exit = findViewById(R.id.exitBtn);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Alert Dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this, R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialog = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_alert,viewGroup,false);
                Button btn_no = dialog.findViewById(R.id.btn_no);
                Button btn_yes = dialog.findViewById(R.id.btn_yes);
                builder.setView(dialog);

                final AlertDialog alertDialog = builder.create();
                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }
        });

        //ShowAllData
        showAllUserData();
    }

    private void showAllUserData(){

        user_username = UserClass.username;
        user_name = UserClass.name;
        user_phoneNo = UserClass.phoneNo;
        user_email = UserClass.email;
        user_password = UserClass.password;

//        fullNameLabel.setText(UserClass.name);
        usernameLabel.setText(user_username);
        fullName.getEditText().setText(user_name);
        email.getEditText().setText(user_email);
        phoneNo.getEditText().setText(user_phoneNo);
        password.getEditText().setText(user_password);

    }

    public void updateInfo(View view) {
        if(isNameChanged() || isPasswordChanged() || isEmailChanged() || isPhoneNoChanged()){
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Data is same and cannot be updated", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isPhoneNoChanged(){
        if(!Objects.equals(phoneNo.getEditText().getText().toString(),user_phoneNo)){
            reference.child(user_username).child("phoneNo").setValue(phoneNo.getEditText().getText().toString());
            user_phoneNo = phoneNo.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }
    }


    private boolean isEmailChanged() {
        if(!Objects.equals(email.getEditText().getText().toString(),user_email)){
            reference.child(user_username).child("email").setValue(email.getEditText().getText().toString());
            user_email = email.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isPasswordChanged(){
        if(!Objects.equals(password.getEditText().getText().toString(),user_password)){
            reference.child(user_username).child("password").setValue(password.getEditText().getText().toString());
            user_password = password.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isNameChanged() {
        if(!Objects.equals(fullName.getEditText().getText().toString(),user_name)){
            reference.child(user_username).child("name").setValue(fullName.getEditText().getText().toString());
            user_name = fullName.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    public void GoHome(View view) {
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(intent);
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
                Intent intent6 = new Intent(UserProfileActivity.this,MainActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_products:
                Intent intent = new Intent(UserProfileActivity.this,Products.class);
                startActivity(intent);
                break;
            case R.id.nav_transaction:
                Intent intent2 = new Intent(UserProfileActivity.this,TransactionMain.class);
                startActivity(intent2);
                break;
            case R.id.nav_expiry:
                Intent intent4 = new Intent(UserProfileActivity.this,ExpiryMain.class);
                startActivity(intent4);
                break;
            case R.id.nav_lowstock:
                Intent intent5 = new Intent(UserProfileActivity.this,LowStockMain.class);
                startActivity(intent5);
                break;
            case R.id.nav_profile:
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(UserProfileActivity.this,SplashActivity.class);
                startActivity(intent3);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}