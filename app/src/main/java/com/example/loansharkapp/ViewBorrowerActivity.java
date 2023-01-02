package com.example.loansharkapp;

import static com.example.loansharkapp.ProfileContainer.state.EMPTY;
import static com.example.loansharkapp.ProfileContainer.state.FILLED;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Objects;

public class ViewBorrowerActivity extends AppCompatActivity {
    private final String tag = "ViewBorrowerActivity";
    private Button addBorrowerProfileButton, prevPageButton, nextPageButton;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    ArrayList<Borrower> borrowersInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_borrower);
        initializeViewBorrowerActivity();
        initializeFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void initializeViewBorrowerActivity() {
        Log.i(tag, "Initializing View com.example.loansharkapp.Borrower Activity");
        if (ProfileContainer.profileContainer.size() < ProfileContainerPage.getMaxProfileContainerInPage()) {
            new ProfileContainer(R.id.imageViewProfile1, R.id.textViewProfile1);
            new ProfileContainer(R.id.imageViewProfile2, R.id.textViewProfile2);
            new ProfileContainer(R.id.imageViewProfile3, R.id.textViewProfile3);
            new ProfileContainer(R.id.imageViewProfile4, R.id.textViewProfile4);
        }
        for (ProfileContainer p : ProfileContainer.profileContainer) {
            p.markProfileEmptied();
            p.profilePicView = findViewById(p.profilePicViewRID);
            p.profilePicView.setImageResource(R.drawable.default_profile_pic);
            p.profileNameView = findViewById(p.profileNameViewRID);
            p.profileNameView.setText("Unassigned");
            //Profile Picture Button Setup
            p.profilePicView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!p.isProfileContainerEmpty()) {
                        //Open View For Profile Details
                        //Toast.makeText(getApplicationContext(),"Selected Profile " + p.profileNameView.getText(), Toast.LENGTH_SHORT).show();
                        ViewBorrowerProfileDetailsFragment viewBorrowerProfileDetailsFragment = new ViewBorrowerProfileDetailsFragment();
                        Bundle profileChosed = new Bundle();
                        profileChosed.putParcelable("Borrower Selected", p.borrowerStored);
                        viewBorrowerProfileDetailsFragment.setArguments(profileChosed);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.layoutTop, viewBorrowerProfileDetailsFragment);
                        fragmentTransaction.addToBackStack("ViewBorrowerActiity->ViewBorrowerProfileDetailsFragment");
                        fragmentTransaction.commit();
                        Log.i(tag, "Starting View Borrower Profile's Detail Fragment");
                    } else {
                        Toast.makeText(getApplicationContext(), "No Profile Assigned", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            p.profilePicView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //Prevent Creating Dialog If Profile is Empty
                    if (p.isProfileContainerEmpty()) {
                        return false;
                    }
                    DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!p.isProfileContainerEmpty()) {
                                Borrower.removeBorrower(p.borrowerStored.getName());
                                resetBorrowerDataInPage();
                            }
                        }
                    };
                    DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    };

                    DeleteProfileDialogFragment deleteProfileDialogFragment = new DeleteProfileDialogFragment(okButton, cancelButton);
                    deleteProfileDialogFragment.show(fragmentManager, "Delete Profile");
                    //Return true here to indicate that the event had been handled, prevent other handling process (Short click)
                    return true;
                }
            });
        }
        addBorrowerProfileButton = findViewById(R.id.buttonAddBorrowerProfile);
        addBorrowerProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create Fragment For Add com.example.loansharkapp.Borrower Profile
                AddBorrowerProfileFragment addBorrowerProfileFragment = new AddBorrowerProfileFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layoutTop, addBorrowerProfileFragment);
                fragmentTransaction.addToBackStack("ViewBorrowerActiity->AddBorrowerProfileFragment");
                fragmentTransaction.commit();
                Log.i(tag, "Starting Add Borrower Profile Fragment");
            }
        });

        initializeBorrowerData();

    }

    public void hideViewBorrowerActivityWhenGoingToFragment(boolean hide) {
        if (hide) {
            Borrower.resetBorrowerProfilePictureBufferPreventLag();
        }
        for (ProfileContainer p : ProfileContainer.profileContainer) {
            p.profilePicView = findViewById(p.profilePicViewRID);
            p.profilePicView.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
            p.profileNameView = findViewById(p.profileNameViewRID);
            p.profileNameView.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        }

        addBorrowerProfileButton.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        prevPageButton.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        nextPageButton.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        Log.i(tag, hide ? "Hide" : "Unhide" + " Views Visibility In com.example.loansharkapp.Borrower Activity");
    }

    public void initializeBorrowerData() {
        setBorrowerProfile();
        ProfileContainerPage.setLastPage(borrowersInfo.size());
        if (ProfileContainerPage.getCurrentPage() == 0) {
            ProfileContainerPage.setCurrentPage(1);
        }
        setButtonNextAndPreviousAvailability();
    }

    public void setBorrowerProfile() {
        borrowersInfo = Borrower.getBorrowerList();
        if (borrowersInfo.size() != 0) {
            setProfilePictureAndNameView(borrowersInfo);
            //Toast.makeText(getApplicationContext(), "Borrower List Contains " + borrowersInfo.size() + " Data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Borrower List Empty", Toast.LENGTH_SHORT).show();
        }
    }

    //Use This Method To Update UI For Future Delete/Edit Function
    public void resetBorrowerDataInPage() {
        ProfileContainerPage.setLastPage(borrowersInfo.size());
        for (ProfileContainer p : ProfileContainer.profileContainer) {
            p.markProfileEmptied();
            p.profilePicView = findViewById(p.profilePicViewRID);
            p.profilePicView.setImageResource(R.drawable.default_profile_pic);
            p.profileNameView = findViewById(p.profileNameViewRID);
            p.profileNameView.setText("Unassigned");
        }
        setBorrowerProfile();
        setButtonNextAndPreviousAvailability();
    }

    public void setProfilePictureAndNameView(ArrayList<Borrower> borrowersInfo) {
        //Counter to track starting profile of the page
        int firstProfileThisPage = ProfileContainerPage.getFirstProfileIndexInThisPage();
        for (int i = 0; i < borrowersInfo.size(); i++) {
            Borrower b = borrowersInfo.get(i);
            if (i < firstProfileThisPage) {
                continue;
            }
            Log.i(tag, "Borrower Info = " + b.getName());

            for (ProfileContainer p : ProfileContainer.profileContainer) {
                Log.i(tag, "Profile Container Size " + ProfileContainer.profileContainer.size());
                if (p.isProfileContainerEmpty()) {
                    //Set Profile Pic
                    p.profilePicView = findViewById(p.profilePicViewRID);
                    if (Objects.equals(b.photo, "default")) {
                        p.profilePicView.setImageResource(R.drawable.occupied_profile_pic3);
                    } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Uri uri = Uri.parse(b.photo);
                                    Handler mHandler = new Handler(Looper.getMainLooper());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Set Profile Pic from URI
                                            b.profilePictureBuffer = ImageProcessor.convertUriToBitMap(getContentResolver(), uri);
                                            p.profilePicView.setImageBitmap(b.profilePictureBuffer);
                                        }
                                    });
                                }
                            }).start();
                    }

                    //Set Profile Name
                    p.profileNameView = findViewById(p.profileNameViewRID);
                    p.profileNameView.setText(b.getName());
                    p.borrowerStored = b;
                    p.markProfileFilled();
                    break;
                }
            }
        }
    }

    public void initializeFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                //Hide this activity view if fragments still exist
                hideViewBorrowerActivityWhenGoingToFragment(fragmentManager.getBackStackEntryCount() != 0);
                //Function run when returning from fragments
                if (fragmentManager.getBackStackEntryCount() == 0) {
                    initializeViewBorrowerActivity();
                    //Reset borrower data in upon return to activity
                    resetBorrowerDataInPage();
                }
                try {
                    if (getIntent().hasExtra("returnFromFragment")) {
                        Log.i(tag, "Fragment pop back stack");
                        final String value = getIntent().getStringExtra("returnFromFragment");
                        if (value == "AddBorrowerProfileFragmentCancel") {
                            Toast.makeText(getApplicationContext(), "Action Canceled", Toast.LENGTH_SHORT).show();
                        }
                        if (value == "AddBorrowerProfileFragmentSubmitted") {
                            Toast.makeText(getApplicationContext(), "Action Submitted", Toast.LENGTH_SHORT).show();
                        }
                        getIntent().removeExtra("returnFromFragment");
                    }
                    if (getIntent().hasExtra("returnFromViewBorrowerDetailsFragment")) {
                        Log.i(tag, "Fragment pop back stack");
                        final String value = getIntent().getStringExtra("returnFromViewBorrowerDetailsFragment");
                        if (value == "ResetFragment") {
                            Bundle b = getIntent().getBundleExtra("Borrower Selected");
                            Borrower borrowerInfo = b.getParcelable("borrowerInfo");
                            ViewBorrowerProfileDetailsFragment viewBorrowerProfileDetailsFragment = new ViewBorrowerProfileDetailsFragment();
                            Bundle profileChosed = new Bundle();
                            profileChosed.putParcelable("Borrower Selected", borrowerInfo);
                            viewBorrowerProfileDetailsFragment.setArguments(profileChosed);
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.layoutTop, viewBorrowerProfileDetailsFragment);
                            fragmentTransaction.addToBackStack("ViewBorrowerActiity->ViewBorrowerProfileDetailsFragment");
                            fragmentTransaction.commit();
                            Log.i(tag, "Starting View Borrower Profile's Detail Fragment");
                        }
                        getIntent().removeExtra("returnFromViewBorrowerDetailsFragment");
                    }
                } catch (Exception e) {
                    Log.i(tag, "No Intent Received");
                }
            }
        });
    }

    public void setButtonNextAndPreviousAvailability() {
        int currentPage = ProfileContainerPage.getCurrentPage();
        nextPageButton = findViewById(R.id.buttonNextPage);
        prevPageButton = findViewById(R.id.buttonPreviousPage);
        if (currentPage == 1) {
            if (currentPage == ProfileContainerPage.getLastPage()) {
                //If first page is the only page
                nextPageButton.setEnabled(false);
                prevPageButton.setEnabled(false);
            } else {
                //In first page, disable previous button
                nextPageButton.setEnabled(true);
                prevPageButton.setEnabled(false);
            }
        } else if (currentPage == ProfileContainerPage.getLastPage()) {
            //In last page, disable next page button
            nextPageButton.setEnabled(false);
            prevPageButton.setEnabled(true);
        } else {
            //In middle pages, enable next and previous page button
            nextPageButton.setEnabled(true);
            prevPageButton.setEnabled(true);
        }
    }

    public void toggleNextProfile(View view) {
        Log.i(tag, "Next Page");
        int currentPage = ProfileContainerPage.getCurrentPage();
        ProfileContainerPage.setCurrentPage(++currentPage);
        resetBorrowerDataInPage();
    }

    public void togglePrevProfile(View view) {
        Log.i(tag, "Previous Page");
        int currentPage = ProfileContainerPage.getCurrentPage();
        ProfileContainerPage.setCurrentPage(--currentPage);
        resetBorrowerDataInPage();
    }
}

class ProfileContainerPage {
    private static final String tag = "ProfileContainerPage";
    static private final int MAX_PROFILE_IN_A_PAGE = 4;
    static private int currentPage = 0; //Current page = 0 meaning uninitialized
    static private int lastPage = 1;

    static public int getMaxProfileContainerInPage() {
        return MAX_PROFILE_IN_A_PAGE;
    }

    static public int getCurrentPage() {
        return currentPage;
    }

    static public int getLastPage() {
        return lastPage;
    }

    static public void setCurrentPage(int pg) {
        if (pg > 0 && pg <= lastPage) {
            currentPage = pg;
        } else {
            //Default to page 1
            currentPage = 1;
        }
    }

    static public void setLastPage(int totalProfiles) {
        if (totalProfiles == 0) {
            //Empty page
            lastPage = 1;
            return;
        }
        //Base on total profiles, calculate the total pages needed to contain each profile
        lastPage = totalProfiles / MAX_PROFILE_IN_A_PAGE;
        if (totalProfiles % MAX_PROFILE_IN_A_PAGE != 0) {
            lastPage++;
        }
        Log.i(tag, "Total " + totalProfiles + " profiles in " + lastPage + " pages ");
    }

    static public int getFirstProfileIndexInThisPage() {
        if (currentPage == 1) {
            //1st item in 1st page
            return 0;
        }
        //1st item in other page
        int index = ((currentPage - 1) * MAX_PROFILE_IN_A_PAGE);
        return index;
    }
}

class ProfileContainer {
    static ArrayList<ProfileContainer> profileContainer = new ArrayList<ProfileContainer>();

    enum state {EMPTY, FILLED};
    public ImageView profilePicView;
    public TextView profileNameView;
    public final int profilePicViewRID;
    public final int profileNameViewRID;
    public state containerState = EMPTY;
    public Borrower borrowerStored;

    public ProfileContainer(final int profilePicViewRID, final int profileNameViewRID) {
        this.profilePicViewRID = profilePicViewRID;
        this.profileNameViewRID = profileNameViewRID;
        profileContainer.add(this);
    }

    public void markProfileFilled() {
        containerState = FILLED;
    }

    public void markProfileEmptied() {
        containerState = EMPTY;
    }

    public boolean isProfileContainerEmpty() {
        if (containerState == EMPTY) {
            return true;
        } else {
            return false;
        }
    }
}