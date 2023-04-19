package my.edu.utar.inventorycontrol;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.inappmessaging.model.ImageData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddProduct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Button submitButton;
    EditText productName, productID, productAmount, productCategory, productDesc;

    //Navigation
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    //export
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

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

        navigationView.setCheckedItem(R.id.nav_products);

        submitButton = findViewById(R.id.productSubmit);
        productName = findViewById(R.id.productName);
        productID = findViewById(R.id.productID);
        productAmount = findViewById(R.id.productAmount);
        productCategory = findViewById(R.id.productCategory);
        productDesc = findViewById(R.id.productDesc);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString();
                int id = Integer.valueOf(productID.getText().toString());
                int amt = Integer.valueOf(productAmount.getText().toString());
                String cate = productCategory.getText().toString();
                String desc = productDesc.getText().toString();

                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(String.valueOf(id)) && TextUtils.isEmpty(String.valueOf(amt)) && TextUtils.isEmpty(cate) && TextUtils.isEmpty(desc) )
                    Toast.makeText(AddProduct.this, "Please add some data.", Toast.LENGTH_SHORT).show();
                else{
                    addDatatoFirebase(name, id, amt, cate, desc);

                    //export
                    showDialog();
                }
            }
        });

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
                Intent intent = new Intent(AddProduct.this,Products.class);
                startActivity(intent);
            }
        });


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printPDF();
                Intent intent = new Intent(AddProduct.this,ViewProduct.class);
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
        canvas.drawText("Product Details",20,50,paint2);

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
        canvas.drawText(productName.getText().toString(),170,105,paint4);
        canvas.drawText("Product ID  : ",20,140,paint3);
        canvas.drawText(productID.getText().toString(),170,140,paint4);
        canvas.drawText("Quantity    : ",20,175,paint3);
        canvas.drawText(productAmount.getText().toString(),170,175,paint4);
        canvas.drawText("Category    : ",20,210,paint3);
        canvas.drawText(productCategory.getText().toString(),170,210,paint4);
        canvas.drawText("Description : ",20,245,paint3);
        canvas.drawText(productDesc.getText().toString(),170,245,paint4);

        paint4.setTextSize(12.5f);
        canvas.drawText("Generated When:"+simpleDateFormat.format(new Date().getTime()),20,300,paint4);

        myPdfDocument.finishPage(myPage);
        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"Product.pdf");

        try{
            myPdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(AddProduct.this, "file open successfully", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(AddProduct.this, "file open failed", Toast.LENGTH_SHORT).show();
        }

        myPdfDocument.close();
    }

    private void addDatatoFirebase(String name, int id, int amt, String cate, String desc) {

        Product product = new Product();
        product.setProductName(name);
        product.setProductID(id);
        product.setProductAmount(amt);
        product.setProductCategory(cate);
        product.setProductDescription(desc);

        FirebaseDatabase firebasedatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebasedatabase.getReference("Product/Product" + id + "/");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {

                databaseReference.setValue(product);

                Toast.makeText(AddProduct.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled( DatabaseError error) {

                Toast.makeText(AddProduct.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
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
                Intent intent = new Intent(AddProduct.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_products:
                break;
            case R.id.nav_transaction:
                Intent intent2 = new Intent(AddProduct.this,TransactionMain.class);
                startActivity(intent2);
                break;
            case R.id.nav_expiry:
                Intent intent4 = new Intent(AddProduct.this,ExpiryMain.class);
                startActivity(intent4);
                break;
            case R.id.nav_lowstock:
                Intent intent5 = new Intent(AddProduct.this,LowStockMain.class);
                startActivity(intent5);
                break;
            case R.id.nav_profile:
                Intent intent6 = new Intent(AddProduct.this,UserProfileActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_signOut:
                Intent intent3 = new Intent(AddProduct.this,SplashActivity.class);
                startActivity(intent3);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}