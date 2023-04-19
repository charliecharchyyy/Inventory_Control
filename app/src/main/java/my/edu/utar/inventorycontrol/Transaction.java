package my.edu.utar.inventorycontrol;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Transaction {

    public static int count;

    String id, date, day, type, quantity, productName, productId, remark;

    public Transaction(){}
    public Transaction(String id, String date, String day, String productName, String productId, String type, String quantity, String remark) {
        this.id = id;
        this.date = date;
        this.day = day;
        this.productName = productName;
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
        this.remark = remark;


    }

    public String getId() { return id; }

    public static int getNewId(){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        ArrayList<String> arrayList = new ArrayList<>();
        db.child("Transaction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String id = dataSnapshot.child("id").getValue(String.class);
                    arrayList.add(id);
                }

                if(arrayList.isEmpty()){
                    count=0;
                }else{
                    count = Integer.valueOf(arrayList.get(arrayList.size()-1).substring(1))+1;
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });



        return count;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getType() {
        return type;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductId() {
        return productId;
    }

    public String getRemark() {
        return remark;
    }

}


