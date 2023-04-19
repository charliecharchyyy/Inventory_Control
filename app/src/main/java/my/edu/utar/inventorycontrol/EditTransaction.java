package my.edu.utar.inventorycontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditTransaction extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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

    Transaction transaction;
    int updatedProductQuantity;
    boolean error = false, quantityError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        //find view
        datetxt = (EditText) findViewById(R.id.dateEdit);
        quantitytxt = (EditText) findViewById(R.id.quantityEdit);
        remarktxt = (EditText) findViewById(R.id.remarkEdit);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupEdit);
        producttxt = (Spinner) findViewById(R.id.productEdit);
        doneBtn = (Button) findViewById(R.id.doneEdit);
        cancelBtn = (Button) findViewById(R.id.cancelEdit);

        //pass data from other activity
        Intent intent = getIntent();
        transaction = new Transaction(intent.getExtras().getString("id"), intent.getExtras().getString("date"), intent.getExtras().getString("day"),
                intent.getExtras().getString("productName"), intent.getExtras().getString("productId"), intent.getExtras().getString("type"),
                intent.getExtras().getString("quantity"), intent.getExtras().getString("remark"));

        //set value to the input column
        datetxt.setText(transaction.getDate());
        quantitytxt.setText(transaction.getQuantity());
        remarktxt.setText(transaction.getRemark());
        if(transaction.getType().equals("In")){
            radioGroup.check(R.id.typeInEdit);
        }else if(transaction.getType().equals("Out")){
            radioGroup.check(R.id.typeOutEdit);
        }

        //date picker
        date = (EditText) findViewById(R.id.dateEdit);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(EditTransaction.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // set day of month , month and year value in the edit text
                            date.setText(dayOfMonth + "/"
                                    + (monthOfYear + 1) + "/" + year);




                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });

        //products spinner
        productsName = new ArrayList<>();
        productsId = new ArrayList<>();
        productsQuantity = new ArrayList<>();
        firstQuantity = new ArrayList<>();
        productsName.add(0, "Select product");
        productsId.add(0, "");
        productsQuantity.add(0, "");
        firstQuantity.add(0, false);
        productRef = FirebaseDatabase.getInstance().getReference();
        productRef.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EditTransaction.this, android.R.layout.simple_spinner_item, productsName);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                producttxt.setAdapter(arrayAdapter);

                producttxt.post(new Runnable() {
                    @Override
                    public void run() {
                        producttxt.setSelection(productsId.indexOf(transaction.getProductId()));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //insert data into firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Transaction/Transaction" + transaction.getId().substring(1) + "/");

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                typetxt = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                updateArraylist();

                if(!error){

                    Intent intent = new Intent(EditTransaction.this, TransactionMain.class);
                    startActivity(intent);
                    Toast.makeText(EditTransaction.this, "Transaction updated successfully!", Toast.LENGTH_LONG).show();
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTransaction.this, TransactionMain.class);
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

    private void updateArraylist(){

        String tId = transaction.getId();
        String date = datetxt.getText().toString().trim();
        String quantity = quantitytxt.getText().toString().trim();
        String remark = remarktxt.getText().toString().trim();
        String type = typetxt.getText().toString().trim();
        String productName = producttxt.getSelectedItem().toString().trim();
        String productId = productsId.get(producttxt.getSelectedItemPosition()).toString().trim();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH);
            Date myDate = null;
            myDate = sdf.parse(datetxt.getText().toString());
            sdf.applyPattern("EEEE");
            day = sdf.format(myDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(TextUtils.isEmpty(date)){
            Toast.makeText(this, "Please select a date!", Toast.LENGTH_LONG).show();
            error = true;
        }else if(TextUtils.isEmpty(quantity)){
            Toast.makeText(this, "Please enter the quantity!", Toast.LENGTH_LONG).show();
            error = true;
        }else if(TextUtils.isEmpty(type)){
            Toast.makeText(this, "Please choose the transaction type!", Toast.LENGTH_LONG).show();
            error = true;
        }else if(TextUtils.isEmpty(productName)){
            Toast.makeText(this, "Please choose the product!", Toast.LENGTH_LONG).show();
            error = true;
        }else{
//            String id = databaseReference.push().getKey();
            Transaction updatedtransaction = new Transaction(tId, date, day, productName, productId, type, quantity, remark);
            databaseReference.setValue(updatedtransaction);

            int inputQuantity = Integer.valueOf(quantitytxt.getText().toString().trim());
            int productQuantity = Integer.valueOf(productsQuantity.get(producttxt.getSelectedItemPosition()));
            int productQuantityPrev = Integer.valueOf(productsQuantity.get(productsId.indexOf(transaction.getProductId())));

            //validate input quantity and update database product amount
            productRef = FirebaseDatabase.getInstance().getReference("Product/Product" + productsId.get(producttxt.getSelectedItemPosition()) + "/productQuantity");
            DatabaseReference productRef2 = FirebaseDatabase.getInstance().getReference("Product/Product" + transaction.getProductId() + "/productQuantity");

            if(transaction.getProductName().equalsIgnoreCase(productName)){

                if(transaction.getType().equalsIgnoreCase(type)){
                    if(type.equalsIgnoreCase("In")){
                        if(inputQuantity <= 0){
                            Toast.makeText(EditTransaction.this, "Input Error: Quantity of transaction in should be > 0", Toast.LENGTH_LONG).show();
                            quantityError = true;
                            error = true;
                        }
                        else{
                            updatedProductQuantity = (productQuantity - Integer.valueOf(transaction.getQuantity()) + inputQuantity);
                        }
                    }
                    else if(type.equalsIgnoreCase("Out")){
                        updatedProductQuantity = (productQuantity + Integer.valueOf(transaction.getQuantity()) - inputQuantity);
                        if(updatedProductQuantity < 0){
                            Toast.makeText(EditTransaction.this, "Input Error: Quantity of transaction out has exceeded the product quantity", Toast.LENGTH_LONG).show();
                            quantityError=true;
                            error = true;
                        }
                    }

                }else{
                    if(type.equalsIgnoreCase("In")){
                        if(inputQuantity <= 0){
                            Toast.makeText(EditTransaction.this, "Input Error: Quantity of transaction in should be > 0", Toast.LENGTH_LONG).show();
                            quantityError = true;
                            error = true;
                        }
                        else{
                            updatedProductQuantity = (productQuantity + Integer.valueOf(transaction.getQuantity()) + inputQuantity);
                        }
                    }
                    if(type.equalsIgnoreCase("Out")){

                        updatedProductQuantity = (productQuantity - Integer.valueOf(transaction.getQuantity()) - inputQuantity);
                        if(updatedProductQuantity < 0){
                            Toast.makeText(EditTransaction.this, "Input Error: Quantity of transaction out has exceeded the product quantity", Toast.LENGTH_LONG).show();
                            quantityError=true;
                            error = true;
                        }

                    }

                }


            }else{

                if(firstQuantity.get(producttxt.getSelectedItemPosition())){

                    if(type.equalsIgnoreCase("In") && inputQuantity > 0){
                        updatedProductQuantity = inputQuantity;

                        if(transaction.getType().equalsIgnoreCase("In")) {
                            productRef2.setValue(productQuantityPrev - Integer.valueOf(transaction.getQuantity()));
                        }else{
                            productRef2.setValue(productQuantityPrev + Integer.valueOf(transaction.getQuantity()));
                        }
//                        productRef.child("Product"+transaction.getProductId()).child("productQuantity").setValue()
//                        //minus previous added value - Transaction.getProductId()
                        //same problem at all here: no set back original value if change product
                    }
                    else if (type.equalsIgnoreCase("Out")){
                        Toast.makeText(EditTransaction.this, "Input Error: There are no any transaction in for this product", Toast.LENGTH_LONG).show();
                        quantityError = true;
                        error=true;
                    }
                    else{
                        Toast.makeText(EditTransaction.this, "Input Error: Quantity of transaction in should be > 0", Toast.LENGTH_LONG).show();
                        quantityError = true;
                        error=true;
                    }
                }
                else{

                    if(type.equalsIgnoreCase("In")){
                        if(inputQuantity <= 0){
                            Toast.makeText(EditTransaction.this, "Input Error: Quantity of transaction in should be > 0", Toast.LENGTH_LONG).show();
                            quantityError = true;
                            error = true;
                        }
                        else{
                            updatedProductQuantity = (productQuantity + inputQuantity);
                            if(transaction.getType().equalsIgnoreCase("In")) {
                                productRef2.setValue(productQuantityPrev - Integer.valueOf(transaction.getQuantity()));
                            }
                            else{
                                productRef2.setValue(productQuantityPrev + Integer.valueOf(transaction.getQuantity()));
                            }
                        }
                    }
                    else if(type.equalsIgnoreCase("Out")){
                        updatedProductQuantity = (productQuantity - inputQuantity);
                        if(updatedProductQuantity < 0){
                            Toast.makeText(EditTransaction.this, "Input Error: Quantity of transaction out has exceeded the product quantity", Toast.LENGTH_LONG).show();
                            quantityError=true;
                            error = true;
                        }
                        else{
                            if(transaction.getType().equalsIgnoreCase("In")) {
                                productRef2.setValue(productQuantityPrev - Integer.valueOf(transaction.getQuantity()));
                            }
                            else{
                                productRef2.setValue(productQuantityPrev + Integer.valueOf(transaction.getQuantity()));
                            }
                        }

                    }

                }

            }

            if(!quantityError){
                productRef.setValue(updatedProductQuantity);
                error = false;

            }

        }
    }

}