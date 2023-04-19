package my.edu.utar.inventorycontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    //Variables
    Button callLogin, regBtn;
    TextInputLayout regName, regUserName,regEmail, regPhoneNo, regPassword;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        //Hooks
        regName = findViewById(R.id.name);
        regUserName = findViewById(R.id.username);
        regEmail = findViewById(R.id.email);
        regPhoneNo = findViewById(R.id.phoneNumber);
        regPassword = findViewById(R.id.password);
        regBtn = findViewById(R.id.register);
        callLogin = findViewById(R.id.login_screen);

        callLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private Boolean validateName(){
        String val = regName.getEditText().getText().toString();

        if(val.isEmpty()){
            regName.setError("Field cannot be empty");
            return false;
        }
        else{
            regName.setError(null);
            return true;
        }
    }

    private Boolean validateUsername(){
        String val = regUserName.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if(val.isEmpty()){
            regUserName.setError("Field cannot be empty");
            return false;
        }
        else if(val.length() >= 15){
            regUserName.setError("Username too long");
            return false;
        }
        else if(!val.matches(noWhiteSpace)){
            regUserName.setError("White Spaces are not allowed");
            return false;
        }
        else{
            regUserName.setError(null);
            regUserName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail(){
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(val.isEmpty()){
            regEmail.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(emailPattern)){
            regEmail.setError("Invalid email address");
            return false;
        }
        else{
            regEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePhoneNo(){
        String val = regPhoneNo.getEditText().getText().toString();

        if(val.isEmpty()){
            regPhoneNo.setError("Field cannot be empty");
            return false;
        }
        else{
            regPhoneNo.setError(null);
            return true;
        }
    }

    private Boolean validatePassword (){
        String val = regPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +        //any letter
                "(?=.*[@#$%^&+=])" +      //at least 1 special character
                "(?=\\S+$)" +             //no white spaces
                ".{4,}" +                 //at least 4 characters
                "$";

        if(val.isEmpty()){
            regPassword.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(passwordVal)) {
            regPassword.setError("Password is too weak");
            return false;
        }
        else{
            regPassword.setError(null);
            return true;
        }
    }

    //Save data in FireBase
    public void registerUser(View view) {
        if(!validateName() | !validateUsername() | !validateEmail() | !validatePhoneNo() | !validatePassword()){
            return;
        }

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("SignUp");

        //Get all the values
        String name = regName.getEditText().getText().toString();
        String username = regUserName.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String phone = regPhoneNo.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();

        UserHelperClass helperClass = new UserHelperClass(name,username,email,phone,password);

        reference.child(username).setValue(helperClass);

        Toast.makeText(this, "Sign Up Successfully", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}