package com.example.loansharkapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GnssAntennaInfo;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class DeleteProfileDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener okButton, cancelButton;
    public DeleteProfileDialogFragment(DialogInterface.OnClickListener _okButton, DialogInterface.OnClickListener _cancelButton) {
        this.okButton = _okButton;
        this.cancelButton = _cancelButton;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.are_you_sure)
                .setTitle(R.string.delete_this_profile)
                .setPositiveButton(R.string.ok, okButton)
                .setNegativeButton(R.string.cancel, cancelButton);
        return builder.create();
    }
}
