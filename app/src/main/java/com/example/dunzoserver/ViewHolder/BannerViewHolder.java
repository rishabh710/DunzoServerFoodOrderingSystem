package com.example.dunzoserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dunzoserver.Common.Common;
import com.example.dunzoserver.Interface.ItemClickListener;
import com.example.dunzoserver.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public TextView banner_name;
    public ImageView banner_image;


    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);

        banner_name = (TextView) itemView.findViewById(R.id.banner_name);
        banner_image = (ImageView) itemView.findViewById(R.id.banner_image);

        itemView.setOnCreateContextMenuListener(this);

    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Select the Action");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);

    }
}
