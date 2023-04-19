package my.edu.utar.inventorycontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    EditText date;
    DatePickerDialog datePickerDialog;
    String day;

    EditText datetxt, quantitytxt, remarktxt;
    Spinner producttxt;
    List<String> productsName, productsId, productsQuantity;
    List<Boolean> firstQuantity;
    RadioGroup radioGroup;
    RadioButton typetxt;
    Button doneBtn, cancelBtn;
    DatabaseReference databaseReference, productRef;

    boolean error = false, typeError = false;
    int updatedProductQuantity;

    //Navigation
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    //export
    String type, productName,productId;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

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

        date = (EditText) findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(AddTransaction.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // set day of month , month and year value in the edit text
                            date.setText(dayOfMonth + "/"
                                    + (monthOfYear + 1) + "/" + year);

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH);
                                Date myDate = null;
                                myDate = sdf.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                sdf.applyPattern("EEEE");
                                day = sdf.format(myDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });

        //find view
        datetxt = (EditText) findViewById(R.id.date);
        quantitytxt = (EditText) findViewById(R.id.quantity);
        remarktxt = (EditText) findViewById(R.id.remark);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        producttxt = (Spinner) findViewById(R.id.product);
        doneBtn = (Button) findViewById(R.id.done);
        cancelBtn = (Button) findViewById(R.id.cancel);

        //products spinner
        productsName = new ArrayList<>();
        productsId = new ArrayList<>();
        productsQuantity = new ArrayList<>();
        firstQuantity = new ArrayList<>();
        productsName.add(0, "Select product");
        productRef = FirebaseDatabase.getInstance().getReference();
        productRef.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String productName = dataSnapshot.child("productName").getValue(String.class);
                    String productId = dataSnapshot.child("productID").getValue().toString();
                    productsName.add(productName);
                    productsId.add(productId);

                    if (dataSnapshot.hasChild("productQuantity")){
//                        firstQuantity = false;
                        firstQuantity.add(false);
                        String productQuantity = dataSnapshot.child("productQuantity").getValue().toString();
                        productsQuantity.add(productQuantity);
                    }
                    else{
                        String productQuantity = "";
                        productsQuantity.add(productQuantity);
//                        firstQuantity = true;
                        firstQuantity.add(true);
                    }

                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddTransaction.this, android.R.layout.simple_spinner_item, productsName);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                producttxt.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //insert data into firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Transaction/Transaction" + Transaction.count + "/");

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton typeIn = findViewById(R.id.typeIn);
                RadioButton typeOut = findViewById(R.id.typeOut);

                if(typeIn.isChecked() || typeOut.isChecked()){
                    typetxt = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                    typeError=false;
                }
                else{
                    typeError = true;
                }

                addArraylist();

                if(!error){
//                    Intent intent = new Intent(AddTransaction.this, TransactionMain.class);
//                    startActivity(intent);

                    Toast.makeText(AddTransaction.this, "Transaction added successfully!", Toast.LENGTH_LONG).show();
                    showDialog();
                }

            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransaction.this, TransactionMain.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    private void addArraylist(){

        if(producttxt.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Please choose the product!", Toast.LENGTH_LONG).show();
            error = true;
        }
        else if(typeError == true){
            Toast.makeText(AddTransaction.this, "Please choose the transaction type!", Toast.LENGTH_LONG).show();
        }
        else {

            String tId = "T" + (Transaction.count);
            String date = datetxt.getText().toString().trim();
            String quantity = quantitytxt.getText().toString().trim();
            String remark = remarktxt.getText().toString().trim();
            type = typetxt.getText().toString().trim();
            productName = producttxt.getSelectedItem().toString().trim();
            productId = productsId.get(producttxt.getSelectedItemPosition()-1).toString().trim();

            if(TextUtils.isEmpty(date)){
                Toast.makeText(this, "Please select a date!", Toast.LENGTH_LONG).show();
                error=true;
            }else if(TextUtils.isEmpty(quantity)){
                Toast.makeText(this, "Please enter the quantity!", Toast.LENGTH_LONG).show();
                error=true;
            }
            else{
//            String id = databaseReference.push().getKey();
                Transaction transaction = new Transaction(tId, date, day, productName, productId, type, quantity, remark);
                databaseReference.setValue(transaction);


                int inputQuantity = Integer.valueOf(quantitytxt.getText().toString().trim());

                productRef = FirebaseDatabase.getInstance().getReference("Product/Product" + productsId.get(producttxt.getSelectedItemPosition()-1) + "/productQuantity");
                if(firstQuantity.get(producttxt.getSelectedItemPosition()-1)){
                    if(type.equalsIgnoreCase("In") && inputQuantity > 0){
                        updatedProductQuantity = inputQuantity;
                    }
                    else if (type.equalsIgnoreCase("Out")){
                        Toast.makeText(AddTransaction.this, "Input Error: There are no any transaction in for this product", Toast.LENGTH_LONG).show();
                        error=true;
                    }
                }
                else{
                    int productQuantity = Integer.valueOf(productsQuantity.get(producttxt.getSelectedItemPosition()-1));
                    if(type.equalsIgnoreCase("In")){
                        if(inputQuantity <= 0){
                            Toast.makeText(AddTransaction.this, "Input Error: Quantity of transaction in should be > 0", Toast.LENGTH_LONG).show();
                            error = true;
                        }
                        else{
                            updatedProductQuantity = (inputQuantity + productQuantity);
                        }
                    } else if(type.equalsIgnoreCase("Out")){
                        if(inputQuantity > productQuantity){
                            Toast.makeText(AddTransaction.this, "Input Error: Quantity of transaction out has exceeded the available product quantity", Toast.LENGTH_LONG).show();
                            error = true;
                        }
                        else{
                            updatedProductQuantity = (productQuantity - inputQuantity);
                        }
                    }
                }

                productRef.setValue(updatedProductQuantity);
                error=false;
            }
        }
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
                Intent intent = new Intent(AddTransaction.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_products:
                Intent intent2 = new Intent(AddTransaction.this,Products.class);
                startActivity(intent2);
                break;
            case R.id.nav_transaction:
                break;
            case R.id.nav_expiry:
                Intent intent4 = new Intent(AddTransaction.this,ExpiryMain.class);
                startActivity(intent4);
                break;
            case R.id.nav_lowstock:
                Intent intent5 = new Intent(AddTransaction.this,LowStockMain.class);
                startActivity(intent5);
                break;
            case R.id.nav_profile:
                Intent intent6 = new Intent(AddTransaction.this,UserProfileActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(AddTransaction.this,SplashActivity.class);
                startActivity(intent3);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //export
    private void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_custom_dialog);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);
        Button btnNo = dialog.findViewById(R.id.btn_no);
        Button btnYes = dialog.findViewById(R.id.btn_yes);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTransaction.this,TransactionMain.class);
                startActivity(intent);
            }
        });


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printPDF();
                Intent intent = new Intent(AddTransaction.this,TransactionMain.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    //export
    private void printPDF() {
        PdfDocument myPdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Paint paint3 = new Paint();
        Paint paint4 = new Paint();
        Paint forLinePaint = new Paint();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(350,350,1).create();
        PdfDocument.Page myPage= myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        paint.setTextSize(20.5f);
        paint.setColor(Color.rgb(83,53,51));
        paint.setTypeface(Typeface.create("Arial",Typeface.BOLD));
        canvas.drawText("Inventory Control",20,20,paint);

        paint2.setTextSize(15.5f);
        paint2.setColor(Color.rgb(43,112,133));
        paint2.setTypeface(Typeface.create("Arial",Typeface.BOLD_ITALIC));
        canvas.drawText("Transaction Details",20,50,paint2);

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        forLinePaint.setStrokeWidth(2);
        forLinePaint.setColor(Color.rgb(83,53,51));
        canvas.drawLine(20,65,350,65,forLinePaint);

        paint3.setTextSize(15.5f);
        paint3.setColor(Color.rgb(83,53,51));
        paint3.setTypeface(Typeface.create("Arial",Typeface.BOLD));

        paint4.setTextSize(15.5f);
        paint4.setColor(Color.rgb(83,53,51));
        paint4.setTypeface(Typeface.create("Arial",Typeface.NORMAL));

        canvas.drawText("Product Name: ",20,105,paint3);
        canvas.drawText(productName,170,105,paint4);
        canvas.drawText("Product ID: ",20,140,paint3);
        canvas.drawText(productId,170,140,paint4);
        canvas.drawText("Transaction type: ",20,175,paint3);
        canvas.drawText(type,170,175,paint4);
        canvas.drawText("Quantity:",20,210,paint3);
        canvas.drawText(quantitytxt.getText().toString(),170,210,paint4);
        canvas.drawText("Remark:",20,245,paint3);
        canvas.drawText(remarktxt.getText().toString(),170,245,paint4);

        paint4.setTextSize(12.5f);
        canvas.drawText("Generated When:"+simpleDateFormat.format(new Date().getTime()),20,300,paint4);

        myPdfDocument.finishPage(myPage);
        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"Transactions.pdf");

        try{
            myPdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(AddTransaction.this, "file open successfully", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(AddTransaction.this, "file open failed", Toast.LENGTH_SHORT).show();
        }

        myPdfDocument.close();
    }
}