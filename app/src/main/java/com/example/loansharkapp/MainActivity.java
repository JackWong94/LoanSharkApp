package com.example.loansharkapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private final String tag = "MainActivity";
    private Button buttonAddBorrowingDetails, buttonViewBorrowerDetails, buttonExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(tag, "Created Main Activity Layout" );
        appInitialization();

    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonAddBorrowingDetails.setEnabled(true);
        buttonViewBorrowerDetails.setEnabled(true);
    }

    //Initialize Apps
    public void appInitialization() {
        //Apps Data Initialize
        Borrower.refreshBorrowerList();
        Borrower.initializeContext(getApplicationContext());
        BorrowedItem.initializeContext(getApplicationContext());
        Borrower.loadBorrowersData();

        //Developing Purpose
        developerModeDataInit();

        //Apps UI Initialize
        buttonAddBorrowingDetails = findViewById(R.id.buttonAddBorrowingDetails);
        buttonAddBorrowingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start Choose com.example.loansharkapp.Borrower Fragment
                Log.i(tag, "buttonAddBorrowingDetails clicked");
                buttonAddBorrowingDetails.setEnabled(false);

                Intent addBorrowingDetailsActivityIntent = new Intent(MainActivity.this, AddBorrowingDetailsActivity.class);
                startActivity(addBorrowingDetailsActivityIntent);
                buttonViewBorrowerDetails.setEnabled(false);
            }
        });

        buttonViewBorrowerDetails = findViewById(R.id.buttonViewBorrowerDetails);
        buttonViewBorrowerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start View com.example.loansharkapp.Borrower Activity
                Log.i(tag, "buttonViewBorrowerDetails clicked");

                Intent viewBorrowerActivityIntent = new Intent(MainActivity.this, ViewBorrowerActivity.class);
                startActivity(viewBorrowerActivityIntent);
                buttonViewBorrowerDetails.setEnabled(false);
            }
        });

        buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Exit applications
                Log.i(tag, "buttonExit clicked");
                finish();
            }
        });
    }

    public void developerModeDataInit() {
        //Initialize Data For Development Testing
        /*if (Borrower.getBorrowerList().isEmpty()) {
            Borrower.addNewBorrower("Jack Wong");
            if (Borrower.getBorrowerByName("Jack Wong") != null) {
                for (int i=0; i<20; i++){
                    Borrower.getBorrowerByName("Jack Wong").borrow("Nasi Lemak", (float) 10);
                    Borrower.getBorrowerByName("Jack Wong").borrow("Teh Oh", (float) 1.6);
                    Borrower.getBorrowerByName("Jack Wong").borrow("Mee Goreng", (float) 8.95);
                }
            }
            Borrower.addNewBorrower("Huat Lee");
            if (Borrower.getBorrowerByName("Huat Lee") != null) {
                for (int i=0; i<100; i++){
                    Borrower.getBorrowerByName("Huat Lee").borrow("a", (float) 1.555);
                    Borrower.getBorrowerByName("Huat Lee").borrow("b", (float) 9.226);
                }
            }
            Borrower.addNewBorrower("Moon");
            if (Borrower.getBorrowerByName("Moon") != null) {
            }
            Borrower.addNewBorrower("Kang");
            if (Borrower.getBorrowerByName("Kang") != null) {
                for (int i=0; i<2; i++){
                    Borrower.getBorrowerByName("Kang").borrow("KANG", (float) 1);
                }
            }
            Borrower.addNewBorrower("Hao");
            if (Borrower.getBorrowerByName("Hao") != null) {
                for (int i=0; i<2; i++){
                    Borrower.getBorrowerByName("Hao").borrow("KANG", (float) 1);
                }
            }
            Borrower.addNewBorrower("Zhun");
            if (Borrower.getBorrowerByName("Zhun") != null) {
                for (int i=0; i<2; i++){
                    Borrower.getBorrowerByName("Zhun").borrow("Zhun", (float) 1);
                }
            }
            for (int i=0; i<20; i++) {
                Borrower.addNewBorrower("YEAHO" + i*100);
            }
        }*/
    }

}