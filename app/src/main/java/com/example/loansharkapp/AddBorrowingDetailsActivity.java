package com.example.loansharkapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddBorrowingDetailsActivity extends AppCompatActivity {

    private final String tag = "AddBorrowingDetailsActivity";
    private String profileNameSelected = "";
    private Spinner profileSpinner;
    private TextView titleChooseBorrowerNameTextView, fillBorrowingReasonTextView, borrowingAmountTextView;
    private EditText borrowingReasonEditText, borrowingAmountEditText;
    private String borrowingReason = "";
    private float borrowingAmount = 0;
    private static boolean targetProfilePreSelected = false;
    private static String targetedProfilePreSelectedName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_borrowing_details);
        initializeAddBorrowingDetailsActivity();
    }

    public void initializeAddBorrowingDetailsActivity() {
        //Declare views for hiding purpose during fragment switch
        titleChooseBorrowerNameTextView = findViewById(R.id.chooseBorrowerNameTextView);
        fillBorrowingReasonTextView = findViewById(R.id.borrowingReasonTextView);
        borrowingAmountTextView = findViewById(R.id.borrowingAmountTextView);
        borrowingReasonEditText = findViewById(R.id.borrowingReasonEditText);
        borrowingAmountEditText = findViewById(R.id.borrowingAmountEditText);
        //Initialize spinner with borrowers profile name
        ArrayList<String> borrowerProfileNameList = new ArrayList<String>();
        for (Borrower b : Borrower.getBorrowerList()) {
            borrowerProfileNameList.add(b.getName());
        }

        profileSpinner = findViewById(R.id.profileNameSpinner);
        if (borrowerProfileNameList.isEmpty()) {
            borrowerProfileNameList.add("No Profile");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, borrowerProfileNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        profileSpinner.setAdapter(adapter);

        if (targetProfilePreSelected) {
            //Fixed the targeted profile
            profileSpinner.setSelection(borrowerProfileNameList.indexOf(targetedProfilePreSelectedName));
            profileSpinner.setEnabled(false);
        }
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profileNameSelected = borrowerProfileNameList.get(position);
                Toast.makeText(getApplicationContext(), profileNameSelected, Toast.LENGTH_SHORT).show();
                //Set target profile pre select flag to false
                targetProfilePreSelected = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    public static void addToTargetedProfile(String name) {
        targetProfilePreSelected = true;
        targetedProfilePreSelectedName = name;
    }
    public void hideActivityViewForFragment() {
        profileSpinner.setVisibility(View.INVISIBLE);
        titleChooseBorrowerNameTextView.setVisibility(View.INVISIBLE);
        fillBorrowingReasonTextView.setVisibility(View.INVISIBLE);
        borrowingAmountTextView.setVisibility(View.INVISIBLE);
        borrowingReasonEditText.setVisibility(View.INVISIBLE);
        borrowingAmountEditText.setVisibility(View.INVISIBLE);
        Button button1 = findViewById(R.id.buttonAddBorrowingDetails2);
        Button button2 = findViewById(R.id.buttonCancelAddBorrowingDetails);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
    }

    public void addBorrowingDetails(View view) {
        if (Borrower.getBorrowerByName(profileNameSelected) != null) {
            if (borrowingReasonEditText.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),"Please Fill In Borrowing Reason", Toast.LENGTH_SHORT).show();
                return;
            }
            String reason = borrowingReasonEditText.getText().toString();
            if (borrowingAmountEditText.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),"Please Fill In Amount", Toast.LENGTH_SHORT).show();
                return;
            }
            float amount = Float.valueOf(borrowingAmountEditText.getText().toString());
            Borrower.getBorrowerByName(profileNameSelected).borrow(reason, (float) amount);

            //Go to fragment
            hideActivityViewForFragment();
            FragmentTransaction fragmentTransaction;
            FragmentManager fragmentManager = getSupportFragmentManager();
            ViewBorrowerProfileDetailsFragment viewBorrowerProfileDetailsFragment = new ViewBorrowerProfileDetailsFragment();
            ViewBorrowerProfileDetailsFragment.setLimitedAccess(true);
            Bundle profileChosed = new Bundle();
            profileChosed.putParcelable("Borrower Selected", Borrower.getBorrowerByName(profileNameSelected));
            viewBorrowerProfileDetailsFragment.setArguments(profileChosed);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout, viewBorrowerProfileDetailsFragment);
            fragmentTransaction.addToBackStack("ViewBorrowerActiity->ViewBorrowerProfileDetailsFragment");
            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (fragmentManager.getBackStackEntryCount() == 0) {
                        ViewBorrowerProfileDetailsFragment.setLimitedAccess(false);
                        finish();
                    }
                }
                });
            fragmentTransaction.commit();
        }
    }

    public void cancelAddBorrowingDetails(View view) {
        finish();
    }
}
