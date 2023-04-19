package my.edu.utar.inventorycontrol;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Product> myDataList;

    final Integer LOW_STOCK = 10;

    int id, quantity;
    String name;
    Integer totalLowStockItems = 0;

    public LowStockAdapter(Context mContext, ArrayList<Product> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.low_stock_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Product data = myDataList.get(position);
        id = data.getProductID();
        name = data.getProductName();
        quantity = data.getProductQuantity();

        holder.id.setText("Product ID: " + data.getProductID());
        holder.name.setText("Product Name: " + data.getProductName());
        holder.quantity.setText("Quantity Left: " + data.getProductQuantity());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext, AddTransaction.class);
//                mContext.startActivity(intent);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public Integer getTotalLowStock() {

        for(int i=0;i<getItemCount();i++){
            Product data = myDataList.get(i);
            if(data.getProductQuantity() <= LOW_STOCK){
                totalLowStockItems++;
            }
        }

        return totalLowStockItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView id, name, quantity;

        public ViewHolder(View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.Id);
            name = itemView.findViewById(R.id.Name);
            quantity = itemView.findViewById(R.id.Quantity);

        }
    }
}

