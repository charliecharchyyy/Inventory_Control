package my.edu.utar.inventorycontrol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.annotation.NonNullApi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.lang.invoke.LambdaConversionException;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Transaction> list;

    Button btnDel, btnEdit;
    TextView pNameFirst, pName, pId, tDate, tType, tQuantity, tRemark;
    ImageView closeIV;

    public MyAdapter(Context context, ArrayList<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Transaction transaction = list.get(position);

        holder.date.setText(transaction.getDate());
        holder.day.setText(transaction.getDay());
        holder.type.setText(transaction.getType());
        holder.amount.setText(transaction.getQuantity());
        holder.productId.setText(transaction.getProductId());
        holder.productName.setText(transaction.getProductName());

        if(transaction.getType().equalsIgnoreCase("In")){
            holder.type.setTextColor(Color.GREEN);;
            holder.amount.setTextColor(Color.GREEN);
        }else if (transaction.getType().equalsIgnoreCase("Out")){
            holder.type.setTextColor(Color.RED);
            holder.amount.setTextColor(Color.RED);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog customDialog = new Dialog(context);
                customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customDialog.setContentView(R.layout.activity_transaction_details_dialog);
                customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                customDialog.setCancelable(false);
                customDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                btnDel = customDialog.findViewById(R.id.del);
                btnEdit = customDialog.findViewById(R.id.edit);
                pNameFirst = customDialog.findViewById(R.id.pNameFirst);
                pName = customDialog.findViewById(R.id.pName);
                pId = customDialog.findViewById(R.id.pId);
                tDate = customDialog.findViewById(R.id.tDate);
                tType = customDialog.findViewById(R.id.tType);
                tQuantity = customDialog.findViewById(R.id.tQuantity);
                tRemark = customDialog.findViewById(R.id.tRemark);
                closeIV = customDialog.findViewById(R.id.close);

                pNameFirst.setText(transaction.getProductName().substring(0,1));
                pName.setText(transaction.getProductName());
                pId.setText(transaction.getProductId());
                tDate.setText(transaction.getDate());
                tType.setText(transaction.getType());
                tQuantity.setText(transaction.getQuantity());
                tRemark.setText(transaction.getRemark());

                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Dialog customDialog1 = new Dialog(context);
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
                                delete(position, "Transaction" + transaction.getId().substring(1));
                                customDialog1.dismiss();
                                customDialog.dismiss();
                            }
                        });
                        customDialog1.show();
                    }
                });

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        Intent intent = new Intent(context, EditTransaction.class);
                        intent.putExtra("id", transaction.getId());
                        intent.putExtra("date", transaction.getDate());
                        intent.putExtra("day", transaction.getDay());
                        intent.putExtra("type", transaction.getType());
                        intent.putExtra("quantity", transaction.getQuantity());
                        intent.putExtra("productId", transaction.getProductId());
                        intent.putExtra("productName", transaction.getProductName());
                        intent.putExtra("remark", transaction.getRemark());
                        context.startActivity(intent);
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
        });
    }

    private void delete(int position, String time) {
        // creating a variable for our Database
        // Reference for Firebase.
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("Transaction");
        // we are use add listerner
        // for event listener method
        // which is called with query.
        Query query=dbref.child(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
                Toast.makeText(context, "Transaction deleted successfully!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView date, day, type, amount, productId, productName;

        public MyViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            day = itemView.findViewById(R.id.day);
            type = itemView.findViewById(R.id.type);
            amount = itemView.findViewById(R.id.amount);
            productId = itemView.findViewById(R.id.productId);
            productName = itemView.findViewById(R.id.productName);

        }
    }

}
