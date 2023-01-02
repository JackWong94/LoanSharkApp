package com.example.loansharkapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.sql.Blob;

public class AddBorrowerProfileFragment extends Fragment {

    private final String tag = "AddBorrowerProfileFrag";
    private Button addButton, cancelButton, addProfilePhotoButton;
    private TextView addBorrowerNameTextView;
    private ImageView profileImageView;
    private Bitmap imageUploaded = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(tag,"Creating View For AddBorrowerProfile");
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_add_borrower_profile, container, false);
        initializeAddBorrowerProfileFragment(fragmentView);
        return fragmentView;
    }

    public void initializeAddBorrowerProfileFragment(View view) {
        addBorrowerNameTextView = view.findViewById(R.id.textViewAddBorrowerName);
        addButton = view.findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag,"Add button pressed");
                //Get borrower info name from TextView
                String name = addBorrowerNameTextView.getText().toString();
                Log.i(tag,"Name = " + name);
                if (succesfullyAddNewBorrower(name)) {
                    Intent popBackStackAcknowlegement = new Intent(getActivity().getBaseContext(),getActivity().getClass());
                    popBackStackAcknowlegement.putExtra("returnFromFragment", "AddBorrowerProfileFragmentSubmitted");
                    getActivity().setIntent(popBackStackAcknowlegement);
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getActivity(), "Name must not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton = view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag,"Cancel button pressed");
                Intent popBackStackAcknowlegement = new Intent(getActivity().getBaseContext(),getActivity().getClass());
                popBackStackAcknowlegement.putExtra("returnFromFragment", "AddBorrowerProfileFragmentCancel");
                getActivity().setIntent(popBackStackAcknowlegement);
                getParentFragmentManager().popBackStack();
            }
        });
        addProfilePhotoButton = view.findViewById(R.id.buttonAddProfilePhoto);
        addProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallery();
            }
        });
    }

    public boolean succesfullyAddNewBorrower(String name) {
        if (!name.isEmpty()) {
            Borrower.addNewBorrower(name);
            //Add photo blob into sqlite if photo is being attached
            if (imageUploaded != null) {
                Borrower.addNewBorrowerProfilePic(name, imageUploaded);
            }
            return true;
        } else {
            return false;
        }
    }

    public void choosePhotoFromGallery() {
        Toast.makeText(getContext(),"Choose Photo From Gallery",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        try {
            launchChosePhotoActivity.launch(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(tag, "choosePhotoFromGallery: Activity Not Found");
        }
    }

    ActivityResultLauncher<Intent> launchChosePhotoActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode()== Activity.RESULT_OK) {
            Intent data = result.getData();
            // do your operation from here....
            if (data != null
                    && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap selectedImageBitmap = ImageProcessor.convertUriToBitMap(getActivity().getContentResolver(), selectedImageUri);
                profileImageView = getView().findViewById(R.id.imageViewProfileImage);
                profileImageView.setImageBitmap(selectedImageBitmap);
                imageUploaded = selectedImageBitmap;
            } else {
                imageUploaded = null;
            }
        }
    });
}