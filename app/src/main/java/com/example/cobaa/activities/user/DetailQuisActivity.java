package com.example.cobaa.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobaa.R;
import com.example.cobaa.models.SoalModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DetailQuisActivity extends AppCompatActivity {

    private TextView tvScore;
    private ImageView btnStart;
    private ImageView btnStop;
    private TextView tvQuestion;
    private Button btnAnswerA;
    private Button btnAnswerB;
    private Button btnAnswerC;
    private Button btnAnswerD;

    private int no = 1 ;
    private String jawab = "" ;
    private String lagu = "" ;
    private int hasil_jawaban = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_quis);

        insial();
        if (getIntent()!=null){
            String jenis_soal = getIntent().getStringExtra("jenis_soal");
            String map = getIntent().getStringExtra("map");
            TextView tv_title = findViewById(R.id.tv_title);
            if (map.equalsIgnoreCase("")){
                tv_title.setText(getString(R.string.name)+" "+getString(R.string.name_map) );
            }else {
                tv_title.setText(getString(R.string.name)+" "+getString(R.string.name_acak) );
            }

            getSoal(jenis_soal, map);
        }

        btnAnswerA.setOnClickListener(view -> {
            progressDialog.show();
            if (btnAnswerA.getText().toString().equals(jawab)) {
                popUPJawabanBenarsalah("benar");
                no++;
                hasil_jawaban++;
            }else {
                popUPJawabanBenarsalah("salah");
                no++;
            }
        });

        btnAnswerB.setOnClickListener(view -> {
            progressDialog.show();
            if (btnAnswerB.getText().toString().equals(jawab)) {
                popUPJawabanBenarsalah("benar");
                no++;
                hasil_jawaban++;
            }else {
                popUPJawabanBenarsalah("salah");
                no++;
            }
        });

        btnAnswerC.setOnClickListener(view -> {
            progressDialog.show();
            if (btnAnswerC.getText().toString().equals(jawab)) {
                popUPJawabanBenarsalah("benar");
                no++;
                hasil_jawaban++;
            }else {
                popUPJawabanBenarsalah("salah");
                no++;
            }
        });

        btnAnswerD.setOnClickListener(view -> {
            progressDialog.show();
            if (btnAnswerD.getText().toString().equals(jawab)) {
                popUPJawabanBenarsalah("benar");
                no++;
                hasil_jawaban++;
            }else {
                popUPJawabanBenarsalah("salah");
                no++;
            }
        });

        btnStart.setOnClickListener(v -> {
            if (!isPlaying) {
                play_audio(lagu);
                isPlaying = true;
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
            }
        });
        btnStop.setOnClickListener(v -> stopAudio());

        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> onBackPressed());
    }

    private void popUPJawabanBenarsalah( String jawaban_dipliih) {
        stopAudio();
        final Dialog dialog = new Dialog(DetailQuisActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_info);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvInfo = dialog.findViewById(R.id.tvInfo);
        Button btnExit = dialog.findViewById(R.id.btnExit);
        Button btnNewGame = dialog.findViewById(R.id.btnNewGame);
        ImageView ivIndicator = dialog.findViewById(R.id.ivIndicator);

        btnNewGame.setVisibility(View.GONE);
        btnExit.setText("OK");
        btnExit.setBackground(getDrawable(R.drawable.bg_button_save));
        btnExit.setTextColor(getResources().getColor(R.color.ColorWhite));

        if (jawaban_dipliih.equalsIgnoreCase("benar")) {
            tvInfo.setText("Horeee.. Jawaban kamu benar.");
            ivIndicator.setImageResource(R.drawable.ic_smile);
        }else if (jawaban_dipliih.equalsIgnoreCase("salah")){
            ivIndicator.setImageResource(R.drawable.ic_sad);
            tvInfo.setText("Yaaaah.. Jawaban kamu salah.");
        }
        btnExit.setOnClickListener(v -> {
            updateQuestion(list.size());
            tvScore.setText("Soal : "+no + " / 10" );
            dialog.dismiss();
            stopAudio();
        });
        btnNewGame.setOnClickListener(v -> {
            dialog.dismiss();
            stopAudio();
        });

        dialog.show();
    }

    private boolean isPlaying = false;
    private void play_audio(final String audio) {
        try {
            mp.setDataSource(audio);
            mp.prepareAsync();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.stop();
                mediaPlayer.reset();
                isPlaying = false;
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<SoalModel> list = new ArrayList<>();
    private void getSoal(String jenis_soal, String map) {
        list.clear();
        Log.e("getSoal", jenis_soal+ " || " +map);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("soal");
        reference.orderByChild("jenis_soal").equalTo(jenis_soal).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    SoalModel p = dataSnapshot1.getValue(SoalModel.class);
                    if (p.getMap().toLowerCase().equalsIgnoreCase(map)) {
                        list.add(p);
                    }
                }

                Log.e("list.size()", "" + (list.size()));
                if (list.size()<10){
                    munculPopup();
                }else {
                    updateQuestion(list.size());
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailQuisActivity.this,
                        "Opsss.... Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void munculPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Mohon maaf soal ini belum bisa dipilih ?")
                .setCancelable(false)
                .setPositiveButton("Keluar",
                        (dialog, id) -> {
                    dialog.dismiss();
                    stopAudio();
                    finish();
                });
        AlertDialog alert = builder.create();
        alert.show();

        //for positive side button
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void updateQuestion(int num) {
        if (no==10){
            SoalBerakhir();
            TextView nilai = findViewById(R.id.nilai);
            nilai.setText("Nilai : "+hasil_jawaban);
            progressDialog.dismiss();
        }else {
            stopAudio();
            int minimum = 1;
            int range = num-minimum+1;
            Random random = new Random();
            int randomAngka = random.nextInt(range)+minimum;
            tvQuestion.setText(list.get(randomAngka-1).getSoal().trim());
            btnAnswerA.setText(list.get(randomAngka-1).getPilihan1().trim());
            btnAnswerB.setText(list.get(randomAngka-1).getPilihan2().trim());
            btnAnswerC.setText(list.get(randomAngka-1).getPilihan3().trim());
            btnAnswerD.setText(list.get(randomAngka-1).getPilihan4().trim());
            jawab = list.get(randomAngka-1).getJawaban().trim();


            if (!lagu.contains("https://firebasestorage.googleapis.com")){
                final String id_lagu = list.get(randomAngka-1).getLagu();

                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Lagu");
                try {
                    ref.orderByChild("id").equalTo(id_lagu).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // dataSnapshot is the "issue" node with all children with id 0
                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                    lagu = childSnapshot.child("url").getValue().toString();
                                    Log.e("lagu", lagu);
                                }
                            }else{
                                lagu = list.get(randomAngka-1).getLagu();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                lagu = list.get(randomAngka-1).getLagu();
            }
            Log.e("int", "randomAngka " + (randomAngka-1));
            Log.e("jawab", "jawab " + jawab);
            progressDialog.dismiss();
        }
    }


    private ProgressDialog progressDialog;
    private void insial() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyiapkan Data");
        progressDialog.show();

        mp = new MediaPlayer();

        tvScore = findViewById(R.id.tvScore);
        btnStart = findViewById(R.id.btnStart);
        btnStop  = findViewById(R.id.btnStop);
        tvQuestion = findViewById(R.id.tvQuestion);

        btnAnswerA = findViewById(R.id.btnAnswerA);
        btnAnswerB = findViewById(R.id.btnAnswerB);
        btnAnswerC = findViewById(R.id.btnAnswerC);
        btnAnswerD = findViewById(R.id.btnAnswerD);
    }

    public void SoalBerakhir() {
        stopAudio();
        final Dialog dialog = new Dialog(DetailQuisActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_info);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvInfo = dialog.findViewById(R.id.tvInfo);
        Button btnExit = dialog.findViewById(R.id.btnExit);
        Button btnNewGame = dialog.findViewById(R.id.btnNewGame);
        ImageView ivIndicator = dialog.findViewById(R.id.ivIndicator);

        if (hasil_jawaban>7 && hasil_jawaban<=10) {
            tvInfo.setText("Selesai !, Kamu mendapat " + hasil_jawaban+".");
            ivIndicator.setImageResource(R.drawable.ic_smile);
        } else if (hasil_jawaban>=4 && hasil_jawaban<=7){
            ivIndicator.setImageResource(R.drawable.ic_confused);
            tvInfo.setText("Permainan selesai !, Poin kamu " + hasil_jawaban+".");
        }else if (hasil_jawaban<4){
            ivIndicator.setImageResource(R.drawable.ic_sad);
            tvInfo.setText("Yaaaah... Poin kamu " + hasil_jawaban+".");
        }
        btnExit.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
            stopAudio();
        });
        btnNewGame.setOnClickListener(v -> {
            dialog.dismiss();
            recreate();
        });

        dialog.show();

    }

    private MediaPlayer mp;
    private void stopAudio() {
        if (isPlaying) {
            mp.stop();
            mp.reset();
            isPlaying = false;
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        stopAudio();

    }
}