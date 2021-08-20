package com.example.cobaa.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.cobaa.R;
import com.example.cobaa.models.BannerModel;
import com.example.cobaa.models.PulauModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PulauAdapter extends  RecyclerView.Adapter<PulauAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<PulauModel> list;
    private ProgressDialog progressDialog;

    public PulauAdapter(Context context, ArrayList<PulauModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView banner;
        private final TextView id_banner;
        private final TextView name_banner;

        private final Button btn_hapus;
        private final Button btn_edit;

        public ViewHolder(View v) {
            super(v);
            btn_edit = v.findViewById(R.id.btn_edit);
            btn_hapus = v.findViewById(R.id.btn_hapus);
            banner = v.findViewById(R.id.banner);
            id_banner = v.findViewById(R.id.id_banner);
            name_banner = v.findViewById(R.id.name_banner);
        }
    }

    @NonNull
    @Override
    public PulauAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_master_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PulauAdapter.ViewHolder holder, final int position) {
        Glide.with(context).load(list.get(position).getIcon())
                .placeholder(R.drawable.ic_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(0.5f)
                .into(holder.banner);

        holder.id_banner.setText(list.get(position).getId());
        holder.name_banner.setText(list.get(position).getName_pulau());

        holder.btn_hapus.setOnClickListener(v -> konfirDelete(holder));
        holder.btn_edit.setOnClickListener(v -> editData(holder));

    }

    private void editData(ViewHolder holder) {
        final String id = list.get(holder.getAdapterPosition()).getId();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_edit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        EditText txt_nama = dialog.findViewById(R.id.txt_nama);
        Button btnExit = dialog.findViewById(R.id.btnExit);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        txt_nama.setText(list.get(holder.getAdapterPosition()).getName_pulau());
        btnExit.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnSimpan.setOnClickListener(v -> {
            if (txt_nama.getText().toString().trim().equalsIgnoreCase("")){
                txt_nama.setError("Nama Banner Tidak boleh kosong");
            }else{
                saving(id, txt_nama.getText().toString().trim());
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void saving(String id, String name_banner) {
        if (id != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Menyimpan Data");
            progressDialog.show();
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("master pulau");
            try {
                ref.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ref.child(id).child("name_pulau").setValue(name_banner);

                        progressDialog.dismiss();
                        Toast.makeText(context,
                                "Edit Data successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Pastikan semua data sudah benar",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void konfirDelete(ViewHolder holder) {
        final String id = list.get(holder.getAdapterPosition()).getId();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Yakin ingin menghapus banner ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.cancel();
                    progressDialog = ProgressDialog.show(context, "Please wait...",
                            "Processing...", true);
                    progressDialog.show();
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("master pulau");
                    try {
                        ref.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Data Banner berhasil dihapus", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("BannerAdapter", "onCancelled", databaseError.toException());
                            }

                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
        //for positive side button
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
        //for negative side button
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

}