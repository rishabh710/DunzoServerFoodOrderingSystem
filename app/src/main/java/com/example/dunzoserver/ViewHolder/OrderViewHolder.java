package com.example.dunzoserver.ViewHolder;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import com.example.dunzoserver.R;


public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone, txtOrderAddress,txtOrderDate;

    public Button btnEdit,btnRemove,btnDetail,btnDirection;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderDate=(TextView)itemView.findViewById(R.id.order_date);
        btnEdit=(Button)itemView.findViewById(R.id.btnEdit);
        btnRemove=(Button)itemView.findViewById(R.id.btnRemove);
        btnDetail=(Button)itemView.findViewById(R.id.btnDetail);


    }
}
