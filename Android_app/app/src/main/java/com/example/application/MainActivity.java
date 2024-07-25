package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Switch sw1,hengio;
    EditText giocd, phutcd;
    Button bt1;
    TextView tv, time1;
    TextClock textClock;
    int check = 0;
    String st1;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sw1 = findViewById(R.id.sw);
        hengio = findViewById(R.id.check);
        giocd = findViewById(R.id.giocd);
        phutcd = findViewById(R.id.phutcd);
        bt1 = findViewById(R.id.bt1);
        tv = findViewById(R.id.tv);
        textClock = findViewById(R.id.textclock);
        time1 = findViewById(R.id.time1);
        String formatdate = "E, d-M-yyyy k:m:sa";
        textClock.setFormat12Hour(formatdate);

        /*lay thoi gian luu vao bien date*/
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        date = simpleDateFormat.format(calendar.getTime());


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        DatabaseReference check_hengio = database.getReference("data/hengio/check");
        DatabaseReference gio_hengio = database.getReference("data/hengio/gio");
        DatabaseReference phut_hengio = database.getReference("data/hengio/phut");
        DatabaseReference status = database.getReference("data/status");
        DatabaseReference time_change = database.getReference("data/time");

        myRef.setValue("Hello, World!4564");

        /*lấy dư liệu trường status từ firebase*/
        status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int st1 = snapshot.getValue(int.class);
                    if(st1 == 1) {
                        sw1.setChecked(true);
                        Toast.makeText(MainActivity.this, "chay duoc",Toast.LENGTH_SHORT).show();
                    }else{
                        sw1.setChecked(false);
                    }

                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "khong chay",Toast.LENGTH_SHORT).show();
                    sw1.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*event switch 1: status on off*/
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int data;
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*neu bat switch status thi ghi gia tri len firebase: 1*/
                if(b){  /* b == true*/
                    data = 1;
                    check++;
                    myRef.setValue("Hello check + "+check);
                    status.setValue(data);
                    Toast.makeText(MainActivity.this, "cho cá ăn ",Toast.LENGTH_SHORT).show();
                    /*hien thi ngay gio hien tai lan khoi dong truoc do*/
                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
                    date = simpleDateFormat.format(calendar.getTime());
                    time1.setText("thời gian trước:"+date);
                }else{
                    /*neu tat switch status thi ghi gia tri len firebase: 0*/
                    data = 0;
                    myRef.setValue("Hello check + "+check);
                    status.setValue(data);
                    Toast.makeText(MainActivity.this, "không cho cá ăn",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*click button finsh:*/
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gio_st = giocd.getText().toString();
                String phut_st = phutcd.getText().toString();

                int gio = Integer.parseInt(gio_st);
                int phut = Integer.parseInt(phut_st);
                //luu lai du lieu 1 file xml
                SharedPreferences share = getSharedPreferences("account", 0);
                SharedPreferences.Editor edit = share.edit();
                edit.putInt("gio", gio);
                edit.putInt("phut", phut);
                edit.commit();  //ghi vao file

                //lay du lieu trong shared....
//                SharedPreferences sh = getSharedPreferences("account", 0);
//                gio = sh.getInt("gio",gio);
//                phut = sh.getInt("phut",phut);

                check++;
                myRef.setValue("Hello + "+check);

                tv.setText(gio+" giờ "+phut+" phút");



                /*switch hen gio onclick*/
                hengio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        /*neu switch hen gio bat thi day du lieu len firebase: hen gio*/
                        if(b){  /* b == true*/
                            check++;
                            phut_hengio.setValue(phut);
                            gio_hengio.setValue(gio);
                            Toast.makeText(MainActivity.this, "đã hẹn giờ: "+ gio +" giờ "+ phut +" phút",Toast.LENGTH_SHORT).show();
                            /*neu bien kiem tra > 100 thi gan ve bat dau tu 1*/
                            if(check > 101){
                                check = 1;
                            }
                            check_hengio.setValue(check);
                        }else{
                            /*neu switch hen gio bat thi day du lieu len firebase: huy hen gio*/
                            Toast.makeText(MainActivity.this, "hủy hẹn giờ",Toast.LENGTH_SHORT).show();
                            check++;
                            int gio = 0;
                            int phut = 0;
                            phut_hengio.setValue(phut);
                            gio_hengio.setValue(gio);
                        }

                    }
                });

            }
        });


    }
}