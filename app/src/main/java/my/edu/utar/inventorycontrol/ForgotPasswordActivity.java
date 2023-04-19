package my.edu.utar.inventorycontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputLayout check_username, new_password;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);

        reference = FirebaseDatabase.getInstance().getReference("SignUp");

        //Hooks
        check_username = findViewById(R.id.check_username);
        new_password= findViewById(R.id.new_password);
    }

    private Boolean validateUsername() {
        String val = check_username.getEditText().getText().toString();

        if (val.isEmpty()) {
            check_username.setError("Field cannot be empty");
            return false;
        } else {
            check_username.setError(null);
            check_username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = check_username.getEditText().getText().toString();

        if (val.isEmpty()) {
            new_password.setError("Field cannot be empty");
            return false;
        } else {
            check_username.setError(null);
            check_username.setErrorEnabled(false);
            return true;
        }
    }

    public void ReturnLogin(View view) {
        if(!validateUsername() | !validatePassword()) {
            return;
        }
        else{
            isForgot();
        }
    }

    private void isForgot(){
        String userEnteredUsername = check_username.getEditText().getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SignUp");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    check_username.setError(null);
                    check_username.setErrorEnabled(false);

                    reference.child(userEnteredUsername).child("password").setValue(new_password.getEditText().getText().toString());
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                else{
                    check_username.setError("No such user exist");
                    check_username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void CancelReset(View view) {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}