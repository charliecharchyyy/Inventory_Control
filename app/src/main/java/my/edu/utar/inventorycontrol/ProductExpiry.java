package my.edu.utar.inventorycontrol;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductExpiry {

    public static int count;
    String item, name, id, notes, quantity, date, reminder;

    public ProductExpiry(){}

    public ProductExpiry(String item, String name, String date, String id, String notes, String quantity){
        this.item = item;
        this.name = name;
        this.id = id;
        this.notes = notes;
        this.quantity = quantity;
        this.date = date;
    }


    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static int getNewId(){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        ArrayList<String> arrayList = new ArrayList<>();
        db.child("Expiry").addValueEventListener(new ValueEventListener() {
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
}
