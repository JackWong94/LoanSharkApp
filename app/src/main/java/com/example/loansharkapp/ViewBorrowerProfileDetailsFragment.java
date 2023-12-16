package com.example.loansharkapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewBorrowerProfileDetailsFragment extends Fragment {

    private static boolean limitedAccess = false;

    public ViewBorrowerProfileDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_view_borrower_profile_details, container, false);

        initializeBorrowerProfileDetails(fragmentView);

        return fragmentView;
    }

    public void initializeBorrowerProfileDetails(View view) {
        Borrower borrowerInfo = getArguments().getParcelable("Borrower Selected");
        //Borrower's borrowed item lists

        TextView titleTextView = view.findViewById(R.id.profileName);
        titleTextView.setText(borrowerInfo.name + "'s Profile");

        ListView borrowItemList;
        borrowItemList = (ListView) view.findViewById(R.id.borrowItemListView);
        if(!limitedAccess) {
            borrowItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Delete Item Options");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("Deleting " + borrowerInfo.borrowedItemsList.get(position).item + " from " + borrowerInfo.getName() + "\n");
                            stringBuffer.append("Delete is not reversible.\nAre you sure?");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage(stringBuffer);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BorrowedItem borrowedItem = borrowerInfo.borrowedItemsList.get(position);
                                    borrowerInfo.cancelBorrow(borrowedItem);
                                    refreshThisFragmentCalledFromButton(borrowerInfo);
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Receipts");
                    alertDialog.show();
                }
            });
        }

        BorrowItemListAdapter borrowItemListAdapter = new BorrowItemListAdapter(getContext(), borrowerInfo.borrowedItemsList);
        borrowItemList.setAdapter(borrowItemListAdapter);

        TextView totalAmountTextView = view.findViewById(R.id.totalAmount);
        totalAmountTextView.setText(Borrower.toCurrencyFormatRM(borrowerInfo.totalBorrowingAmount));

        //Setup Button Add More Borrowing Details
        Button buttonAddMoreBorrowingDetails = view.findViewById(R.id.buttonAddMoreBorrowingDetails);
        buttonAddMoreBorrowingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addBorrowingDetailsActivityIntent = new Intent(getContext(), AddBorrowingDetailsActivity.class);
                startActivity(addBorrowingDetailsActivityIntent);
                AddBorrowingDetailsActivity.addToTargetedProfile(borrowerInfo.getName());
                refreshThisFragmentCalledFromButton(borrowerInfo);
            }
        });

        //Setup Button Ask For Payment
        if (!limitedAccess) {
            Button buttonAskForPayment = view.findViewById(R.id.buttonAskForPayment);
            buttonAskForPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(borrowerInfo.getName());
                    stringBuffer.append("\nTotal Payment: ");
                    stringBuffer.append(Borrower.toCurrencyFormatRM(borrowerInfo.totalBorrowingAmount));
                    stringBuffer.append("\n\n");
                    for (int i = 0; i < borrowerInfo.borrowedItemsList.size(); i++) {
                        stringBuffer.append(borrowerInfo.borrowedItemsList.get(i).borrowItemListToStringBuffer());
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(stringBuffer);

                    //Add view here to prevent sliding button views
                    //Refer this website
                    //https://ghostcode.in/2016/11/05/how-to-create-a-custom-alert-dialog-in-android-about-feedback-dialog/
                    builder.setNegativeButton("Paid & Delete All Records", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Deleting all records.\nDelete is not reversible.\nAre you sure?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    borrowerInfo.cancelAllBorrowings();
                                    refreshThisFragmentCalledFromButton(borrowerInfo);
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                    builder.setPositiveButton("COPY TO CLIPBOARD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("receipt", stringBuffer);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(getContext(), "Receipt Copied", Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Receipts");
                    alertDialog.show();
                }
            });
        }else {
            //Limited acess to this fragment, hide some button
            Button buttonAskForPayment = view.findViewById(R.id.buttonAskForPayment);
            buttonAskForPayment.setVisibility(View.INVISIBLE);
        }
    }

    public void refreshThisFragmentCalledFromButton(Borrower _borrowerInfo) {
        Intent popBackStackAcknowlegement = new Intent(getActivity().getBaseContext(), getActivity().getClass());
        Bundle profileChosed = new Bundle();
        profileChosed.putParcelable("borrowerInfo", _borrowerInfo);
        popBackStackAcknowlegement.putExtra("Borrower Selected", profileChosed);
        popBackStackAcknowlegement.putExtra("returnFromViewBorrowerDetailsFragment", "ResetFragment");
        getActivity().setIntent(popBackStackAcknowlegement);
        getParentFragmentManager().popBackStack();
    }

    public static void setLimitedAccess(boolean _limitedAccess) {
        /*  When this fragment is visited not through ViewBorrowerProfileDetailsFragment
            disable some feature such as ASK FOR PAYMENT and DELETE ITEM
         */
        limitedAccess = _limitedAccess;
    }
}