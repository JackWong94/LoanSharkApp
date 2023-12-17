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

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
                    builder.setMessage("You can edit the selected item");
                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(),"Editing . . . ", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Edit Item Details");
                            LinearLayout linearLayout = new LinearLayout(getContext());
                            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ));
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.setGravity(Gravity.CENTER);
                            // Set margins (adjust as needed)
                            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            editParams.setMargins(80, 20, 80, 10); // left, top, right, bottom
                            //Edit Item Name
                            final TextView itemName = new TextView(getContext());
                            itemName.setLayoutParams(editParams);
                            itemName.setText("Change Item Name");
                            linearLayout.addView(itemName);
                            final EditText itemInput = new EditText(getContext());
                            itemInput.setLayoutParams(editParams);
                            itemInput.setText(borrowerInfo.borrowedItemsList.get(position).item);
                            linearLayout.addView(itemInput);
                            builder.setView(linearLayout);
                            //Edit Item Amount
                            final TextView itemAmount = new TextView(getContext());
                            itemAmount.setLayoutParams(editParams);
                            itemAmount.setText("Change Item Amount (RM)");
                            linearLayout.addView(itemAmount);
                            final EditText itemAmountInput = new EditText(getContext());
                            itemAmountInput.setLayoutParams(editParams);
                            itemAmountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                            String formattedValue = String.format("%.2f", borrowerInfo.borrowedItemsList.get(position).amount);
                            itemAmountInput.setText(formattedValue);
                            linearLayout.addView(itemAmountInput);
                            builder.setView(linearLayout);
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    BorrowedItem borrowedItem = borrowerInfo.borrowedItemsList.get(position);
                                    if (itemInput.getText().toString().equals("")) {
                                        Toast.makeText(getContext(),"Please Fill In Borrowing Reason", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (itemAmountInput.getText().toString().equals("")) {
                                        Toast.makeText(getContext(),"Please Fill In Amount", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    borrowerInfo.editingBorrow(borrowedItem, itemInput.getText().toString(), Float.valueOf(itemAmountInput.getText().toString()));
                                    borrowedItem.item = itemInput.getText().toString();
                                    borrowedItem.amount = Float.valueOf(itemAmountInput.getText().toString());
                                    refreshThisFragmentCalledFromButton(borrowerInfo);
                                }
                            });
                            builder.show();
                        }
                    });
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("Deleting " + borrowerInfo.borrowedItemsList.get(position).item + " from " + borrowerInfo.getName() + "\n");
                            stringBuffer.append("Delete is not reversible.\nAre you sure?");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage(stringBuffer);
                            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BorrowedItem borrowedItem = borrowerInfo.borrowedItemsList.get(position);
                                    borrowerInfo.cancelBorrow(borrowedItem);
                                    refreshThisFragmentCalledFromButton(borrowerInfo);
                                }
                            });
                            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Editing Options");
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
                //Fix this bug as the add more item will cause the previous not updated screen to appear
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
            //Limited access to this fragment, hide some button
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