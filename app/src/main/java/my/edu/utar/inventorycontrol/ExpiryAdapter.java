package my.edu.utar.inventorycontrol;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpiryAdapter extends RecyclerView.Adapter<ExpiryAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ProductExpiry> myDataList;
    DatePickerDialog datePickerDialog;
    String dayDifference;
    boolean expired;

    private String id, note, item, name, date, quantity;
    Integer totalExpiringItems = 0, totalExpiredItems = 0;

    public ExpiryAdapter(Context mContext, ArrayList<ProductExpiry> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.expiry_retrieve, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ProductExpiry data = myDataList.get(position);

        holder.item.setText("Item: " + data.getItem());
        holder.name.setText("Name: " + data.getName());
        holder.quantity.setText("Quantity: " + data.getQuantity());
        holder.notes.setText("Note: " + data.getNotes());
        holder.date.setText("Expiry Date: " + data.getDate());

        //calculation for day difference

        dateDiff(data.getDate());

        if (expired) {
            holder.item.setTextColor(Color.RED);
            holder.reminder.setTextColor(Color.RED);
            holder.reminder.setText("Item is expired!");
        } else {
            if (Integer.valueOf(dayDifference) > 7) {
                holder.item.setTextColor(Color.rgb(48, 184, 105));
                holder.reminder.setTextColor(Color.rgb(48, 184, 105));
                holder.reminder.setText("Item will in " + dayDifference + " days.");
            } else {
                holder.item.setTextColor(Color.rgb(255, 160, 24));
                holder.reminder.setTextColor(Color.rgb(255, 160, 24));
                if (Integer.valueOf(dayDifference) == 0) {
                    holder.reminder.setText("Item is expiring today!");
                } else {
                    holder.reminder.setText("Item is expiring in " + dayDifference + " days!");
                }
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = data.getId();
                name = data.getName();
                note = data.getNotes();
                quantity = data.getQuantity();
                item = data.getItem();
                date = data.getDate();

//                Toast.makeText(mContext, "Expiry" + position, Toast.LENGTH_LONG).show();

                updateData();
            }
        });
    }


    private void updateData() {

        Dialog customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.expiry_update);
        customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customDialog.setCancelable(false);
        customDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        Spinner mItem = customDialog.findViewById(R.id.updateItem);
        Spinner mName = customDialog.findViewById(R.id.updateName);
        EditText mQuantity = customDialog.findViewById(R.id.updateQuantity);
        EditText mNote = customDialog.findViewById(R.id.updateNote);
        EditText mDate = customDialog.findViewById(R.id.updateExpiryDate);
        Button updateBtn = customDialog.findViewById(R.id.update);
        Button deleteBtn = customDialog.findViewById(R.id.delete);
        ImageView closeIV = customDialog.findViewById(R.id.close);

        //category spinner
        List<String> category = Arrays.asList("Select category", "Fruit", "Canned Goods",
                "Canned Beverage", "Beverage", "Vegetable", "Snack");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, category);
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mItem.setAdapter(itemsAdapter);
        mItem.post(new Runnable() {
            @Override
            public void run() {
                mItem.setSelection(category.indexOf(item));
            }
        });


        //products spinner
        List<String> productsName, productsId;
        DatabaseReference productRef;
        productsName = new ArrayList<>();
        productsId = new ArrayList<>();
        productsName.add(0, "Select product");
        productRef = FirebaseDatabase.getInstance().getReference();
        productRef.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String productName = dataSnapshot.child("productName").getValue(String.class);
                    String productId = dataSnapshot.child("productID").getValue().toString();
                    productsName.add(productName);
                    productsId.add(productId);

                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, productsName);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                mName.setAdapter(arrayAdapter);

                mName.post(new Runnable() {
                    @Override
                    public void run() {
                        mName.setSelection(productsName.indexOf(name));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //date picker
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(mContext,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // set day of month , month and year value in the edit text
                            mDate.setText(dayOfMonth + "/"
                                    + (monthOfYear + 1) + "/" + year);

                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });

        //set value to field
        mQuantity.setText(String.valueOf(quantity));
        mNote.setText(note);
        mDate.setText(date);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Expiry/Expiry" + id.substring(1) + "/");
                String Id = id;
                String sName = mName.getSelectedItem().toString().trim();
                String sQuantity = mQuantity.getText().toString().trim();
                String sNotes = mNote.getText().toString().trim();
                String sDate = mDate.getText().toString().trim();
                String item = mItem.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(sName)) {
                    Toast.makeText(mContext, "Please select a product!", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(sQuantity)) {
                    Toast.makeText(mContext, "Please enter the quantity!", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(sNotes)) {
                    Toast.makeText(mContext, "Please enter the remark!", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(sDate)) {
                    Toast.makeText(mContext, "Please select a date!", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(item)) {
                    Toast.makeText(mContext, "Please select a category!", Toast.LENGTH_LONG).show();
                } else {

                    ProductExpiry productExpiry = new ProductExpiry(item, sName, sDate, Id, sNotes, sQuantity);
                    databaseReference.setValue(productExpiry);

                    Toast.makeText(mContext, "Expiry is updated", Toast.LENGTH_LONG).show();

                }
                customDialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog customDialog1 = new Dialog(mContext);
                customDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customDialog1.setContentView(R.layout.custom_alert);
                customDialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                customDialog1.setCancelable(false);
                customDialog1.getWindow().getAttributes().windowAnimations = R.style.animation;
                customDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView title = customDialog1.findViewById(R.id.title);
                TextView content = customDialog1.findViewById(R.id.content);
                Button btn_no = customDialog1.findViewById(R.id.btn_no);
                Button btn_yes = customDialog1.findViewById(R.id.btn_yes);

                title.setText("Confirm Delete");
                content.setText("Are you sure you want to delete?");

                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog1.dismiss();
                    }
                });
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expiry/Expiry" + id.substring(1) + "/");
                        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    customDialog1.dismiss();
                                    customDialog.dismiss();
                                } else {
                                    Toast.makeText(mContext, "Failed " + task.getException(), Toast.LENGTH_SHORT).show();
                                    customDialog1.dismiss();
                                    customDialog.dismiss();
                                }
                            }
                        });
                        customDialog1.dismiss();
                        customDialog.dismiss();
                    }
                });
                customDialog1.show();
            }
        });


        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public int getExpiringItemCount() {

        for (int i = 0; i < getItemCount(); i++) {
            ProductExpiry data = myDataList.get(i);
            dateDiff(data.getDate());

            if (!expired) {
                if (Integer.valueOf(dayDifference) > 0 && Integer.valueOf(dayDifference) <= 7) {
                    totalExpiringItems++;
                }
            }

        }
        return totalExpiringItems;
    }

    //
    public int getExpiredItemCount() {

        for (int i = 0; i < getItemCount(); i++) {
            ProductExpiry data = myDataList.get(i);
            dateDiff(data.getDate());

            if (expired) {
                totalExpiredItems++;
            }
        }
        return totalExpiredItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView item, name, quantity, date, notes, reminder;

        public ViewHolder(View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.Item);
            name = itemView.findViewById(R.id.Name);
            quantity = itemView.findViewById(R.id.Quantity);
            notes = itemView.findViewById(R.id.Note);
            date = itemView.findViewById(R.id.Date);
            reminder = itemView.findViewById(R.id.Reminder);

        }
    }

    public void dateDiff(String expiredDate) {

        try {

            String CurrentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            String FinalDate = expiredDate;
            Date date1;
            Date date2;
            SimpleDateFormat dates = new SimpleDateFormat("dd/MM/yyyy");
            date1 = dates.parse(CurrentDate);
            date2 = dates.parse(FinalDate);

            if (date1.after(date2)) {
                expired = true;
            } else {
                expired = false;
                long difference = Math.abs(date2.getTime() - date1.getTime());
                long differenceDates = difference / (24 * 60 * 60 * 1000);
                dayDifference = Long.toString(differenceDates);
            }

        } catch (Exception exception) {
            Toast.makeText(mContext, "Unable to find difference", Toast.LENGTH_SHORT).show();
        }
    }
}
