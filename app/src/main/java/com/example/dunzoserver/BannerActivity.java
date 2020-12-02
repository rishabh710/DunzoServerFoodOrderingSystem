package com.example.dunzoserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dunzoserver.Common.Common;
import com.example.dunzoserver.Model.Banner;
import com.example.dunzoserver.Model.Food;
import com.example.dunzoserver.ViewHolder.BannerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BannerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;
    RelativeLayout rootLayout;

    FirebaseDatabase db;
    DatabaseReference banners;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;

    MaterialEditText edtName,edtFoodId;
    FButton btnUpload,btnSelect;

    Banner newBanner;
    Uri filePath;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/UbuntuMedium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_banner);

        db=FirebaseDatabase.getInstance();
        banners=db.getReference("Banner");

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        recyclerView=(RecyclerView)findViewById(R.id.recycler_banner);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout);

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBanner();
            }
        });

        if (Common.isConnectedToInternet(getBaseContext()))
            loadListBanner();
        else
            Toast.makeText(BannerActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();



    }

    private void loadListBanner() {
        FirebaseRecyclerOptions<Banner> allBanner = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banners,Banner.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(allBanner) {
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {
                holder.banner_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(holder.banner_image);

            }

            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_layout,parent,false);
                return new BannerViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showAddBanner() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Add new Banner");
        alertDialog.setMessage("Please fill all the details");

        LayoutInflater inflater=this.getLayoutInflater();
        View v=inflater.inflate(R.layout.add_new_banner,null);

        edtFoodId= v.findViewById(R.id.edtFoodId);
        edtName=v.findViewById(R.id.edtFoodName);

        btnSelect=v.findViewById(R.id.btnSelect);
        btnUpload=v.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture();
            }
        });

        alertDialog.setView(v);
        alertDialog.setIcon(R.drawable.ic_baseline_laptop_24);

        alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newBanner!=null)
                    banners.push()
                        .setValue(newBanner);
                loadListBanner();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                newBanner=null;
                loadListBanner();

            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data != null && data.getData() != null)
        {
            filePath=data.getData();
            btnSelect.setText("Image Selected");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            showUpdateBannerDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.DELETE)){
            deleteBanner(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateBannerDialog(final String key, final Banner item) {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Edit Banner");
        alertDialog.setMessage("Please fill all the details");

        LayoutInflater inflater=this.getLayoutInflater();
        View edit_banner=inflater.inflate(R.layout.add_new_banner,null);

        edtName=edit_banner.findViewById(R.id.edtFoodName);
        edtFoodId=edit_banner.findViewById(R.id.edtFoodId);
        btnSelect=edit_banner.findViewById(R.id.btnSelect);
        btnUpload=edit_banner.findViewById(R.id.btnUpload);

        //Set default
        edtName.setText(item.getName());
        edtFoodId.setText(item.getId());


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(edit_banner);
        alertDialog.setIcon(R.drawable.ic_baseline_laptop_24);

        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                item.setName(edtName.getText().toString());
                item.setId(edtFoodId.getText().toString());

                Map<String,Object> update=new HashMap<>();
                update.put("id",item.getId());
                update.put("name",item.getName());
                update.put("image",item.getImage());

                banners.child(key)
                        .updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(rootLayout,"Updated",Snackbar.LENGTH_SHORT).show();
                                loadListBanner();
                            }
                        });
                loadListBanner();

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                loadListBanner();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Banner item) {
        if(filePath!=null){
            final ProgressDialog mDialog= new ProgressDialog(this);
            mDialog.setMessage("Uploading..");
            mDialog.show();

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder= storageReference.child("images/"+imageName);
            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded...("+progress+"%)");

                        }
                    });

        }
    }

    private void deleteBanner(String key) {
        banners.child(key).removeValue();
        Toast.makeText(BannerActivity.this, "Selected banner is deleted", Toast.LENGTH_SHORT).show();
    }

    private void uploadPicture() {
        if(filePath!=null){
            final ProgressDialog mDialog= new ProgressDialog(this);
            mDialog.setMessage("Uploading..");
            mDialog.show();

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder= storageReference.child("images/"+imageName);
            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newBanner=new Banner();
                                    newBanner.setName(edtName.getText().toString());
                                    newBanner.setId(edtFoodId.getText().toString());
                                    newBanner.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded...("+new DecimalFormat("##.#").format(progress)+"%)");

                        }
                    });

        }
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }
}