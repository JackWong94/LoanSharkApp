package com.example.loansharkapp;

import static com.example.loansharkapp.Borrower.tag;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Handler;

public class Borrower implements Parcelable {
    static private Context context = null;
    static String tag = "Borrower";
    static ArrayList<Borrower> borrowerList = new ArrayList<>();
    ArrayList<BorrowedItem> borrowedItemsList = new ArrayList<>();
    String name = "";
    String photo = "default";
    Float totalBorrowingAmount = Float.valueOf(0);
    public Bitmap profilePictureBuffer = null;

    public Borrower(String _name) {
        name = _name;
        //Store this borrower into database
        boolean isSaved = saveBorrowerProfileToDatabase();
        if (isSaved) {
            borrowerList.add(this);
        }
    }

    public Borrower(String _name, String _photo, Float _totalBorrowingAmount) {
        //For creating borrower from database
        name = _name;
        photo = _photo;
        totalBorrowingAmount = _totalBorrowingAmount;
        borrowerList.add(this);
    }

    protected Borrower(Parcel in) {
        name = in.readString();
        photo = in.readString();
        if (in.readByte() == 0) {
            totalBorrowingAmount = null;
        } else {
            totalBorrowingAmount = in.readFloat();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(photo);
        if (totalBorrowingAmount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(totalBorrowingAmount);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Borrower> CREATOR = new Creator<Borrower>() {
        @Override
        public Borrower createFromParcel(Parcel in) {
            return new Borrower(in);
        }

        @Override
        public Borrower[] newArray(int size) {
            return new Borrower[size];
        }
    };

    public static ArrayList<Borrower> getBorrowerList() {
        return borrowerList;
    }

    public static void addNewBorrower(String _name) {
        //Check if borrower with this name exist
        for (Borrower b : borrowerList) {
            if (b.getName().equals(_name)) {
                Toast.makeText(context, "Profile already exist", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        new Borrower(_name);
    }

    public static void addNewBorrowerProfilePic(String _name, Bitmap _photo) {
        //Check if borrower with this name exist
        for (Borrower b : borrowerList) {
            if (b.getName().equals(_name)) {
                b.photo = "referDatabase";
                b.modifyBorrowerProfileValueInDatabase(b.photo, null);
                b.saveProfilePhotoInDatabase(_photo);
                b.profilePictureBuffer = _photo;
                return;
            }
        }
    }

    public static void removeBorrower(String _name) {
        DatabaseHelper myDb = new DatabaseHelper(context);
        int rowDeleted = myDb.deleteData(_name);
        if (rowDeleted == 1) {
            //One row of data is deleted
            if (borrowerList.contains(Borrower.getBorrowerByName(_name))) {
                int indexToDelete = borrowerList.indexOf(Borrower.getBorrowerByName(_name));
                borrowerList.remove(indexToDelete);
            }
            //Delete borrower details database
            BorrowedItem.deleteDBFile(_name);
            //Delete borrower profile picture if exist
            ImageProcessor.deleteProfilePicFromInternalStorage(context.getApplicationContext(), _name);
        } else {
            //If row deleted is 0 means that the data not found
            //If row deleted is more than 1 means more than one line of data is deleted
            Log.e(tag, "Something wrong with the database, please check");
        }
    }

    public static Borrower getBorrowerByName(String _name) {
        for (Borrower b : borrowerList) {
            if (b.getName().equals(_name)) {
                return b;
            }
        }
        Log.i(tag,"Not Found Borrower With This Name " + _name);
        return null;
    }

    public static void initializeContext(Context _context) {
        //Context from base activity required for Toast and Database
        context = _context;
    }

    public static void refreshBorrowerList() {
        //To prevent multi intializing borrower list
        borrowerList.clear();
    }

    public String getName() {
        return name;
    }

    public void saveProfilePhotoInDatabase(Bitmap bitmap) {
        Borrower borrower = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String profilePicPath = ImageProcessor.saveToInternalStorage(bitmap, context.getApplicationContext(), borrower.getName());
                Log.i(tag, profilePicPath);
                borrower.photo = String.valueOf(Uri.fromFile(new File(profilePicPath)));
                borrower.modifyBorrowerProfileValueInDatabase(borrower.photo, null);
            }
        }).start();
    }

    public static void resetBorrowerProfilePictureBufferPreventLag() {
        for (Borrower b : borrowerList) {
            if (b.profilePictureBuffer != null) {
                b.profilePictureBuffer.recycle();
            }
        }
    }

    public static String toCurrencyFormatRM(float _amount) {
        String inStringRM = "";
        if (_amount < 0) {
            //make string - RM amount
            inStringRM = "-RM" + String.format("%.2f", _amount*-1);
        } else {
            //make string RM amount
            inStringRM = "RM" + String.format("%.2f", _amount);
        }
        return inStringRM;
    }

    public static String toDateFormatShort(String _date) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd MMM yyyy").parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            String formattedDate = new SimpleDateFormat("dd/MM/yy").format(date);
            return formattedDate;
        } else {
            return "null";
        }
    }

    public void borrow(String _item, float _amount) {
        int newItemIndex =  borrowedItemsList.size() + 1;
        totalBorrowingAmount += _amount;
        //Modify database value
        modifyBorrowerProfileValueInDatabase(null, totalBorrowingAmount);
        BorrowedItem borrowedItem = BorrowedItem.addNewBorrowedItem (newItemIndex,_item, _amount, this.getName());
        borrowedItemsList.add(borrowedItem);
    }

    public void cancelBorrow(BorrowedItem _borrowedItem) {
        int rowDeleted = 0;
        rowDeleted = BorrowedItem.cancelBorrowedItem(_borrowedItem, this.getName());
        Log.i("JACK", String.valueOf(_borrowedItem.index));
        Log.i("JACK", this.getName());
        if (rowDeleted == 1) {
            borrowedItemsList.remove(_borrowedItem.index-1);
            totalBorrowingAmount -= _borrowedItem.amount;
            //Modify database value
            modifyBorrowerProfileValueInDatabase(null, totalBorrowingAmount);
            //Update BorrowedItemListIndex
            for (int i=0; i<borrowedItemsList.size(); i++) {
                boolean success = borrowedItemsList.get(i).refreshIndexNo(this.getName(),i+1);
                if (!success) {
                    Log.i("JACK", "Fail and retrying");
                    i--;
                } else {
                    Log.i("JACK", "Updated Index");
                    Log.i("JACK", Integer.toString(i + 1));
                }
            }
        } else {
            Log.e(tag, "Something wrong with the database, please check");
        }
    }

    public void cancelAllBorrowings() {
        for (int i=0; i<borrowedItemsList.size(); i++) {
            cancelBorrow(borrowedItemsList.get(i));
        }
    }

    public boolean saveBorrowerProfileToDatabase() {
        if (context == null) {
            Log.i(tag, "No Context Initialized");
            return false;
        }
        DatabaseHelper myDb;
        myDb = new DatabaseHelper(context);
        boolean isInserted = myDb.insertData(this.name, this.photo, String.valueOf(this.totalBorrowingAmount));
        if (isInserted) {
            Toast.makeText(context, "Stored Profile Succesfully",Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "FAIL TO CREATE AND STORE PROFILE",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void modifyBorrowerProfileValueInDatabase(String photo, Float totalBorrowingAmount) {
        if (context == null) {
            Log.i(tag, "No Context Initialized");
            return;
        }
        if (photo == null) {
            photo = this.photo;
        }
        if (totalBorrowingAmount == null) {
            totalBorrowingAmount = this.totalBorrowingAmount;
        }
        DatabaseHelper myDb;
        myDb = new DatabaseHelper(context);
        myDb.updateData(this.name, photo, totalBorrowingAmount.toString());
    }

    public static void loadBorrowersData() {
        if (context == null) {
            Log.i(tag, "No Context Initialized");
            return;
        }
        DatabaseHelper myDb;
        myDb = new DatabaseHelper(context);
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            //No data found in database
            Toast.makeText(context, "No Data Stored", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuffer buffer = new StringBuffer();

        //Assign data into borrower's object and store to buffer for logging purpose
        while (res.moveToNext()) {
            Borrower borrower = new Borrower(res.getString(1), res.getString(2), Float.valueOf(res.getString(3)));
            buffer.append("ID : " + res.getString(0) + "\n");
            buffer.append("NAME : " + res.getString(1) + "\n");
            buffer.append("PHOTO : " + res.getString(2) + "\n");
            buffer.append("TOTAL BORROWING AMOUNT : " + res.getString(3) + "\n\n");
            BorrowedItem.loadBorrowedItemData(res.getString(1), borrower.borrowedItemsList);
        }

        //Show database profile list
        Log.i(tag, buffer.toString());
    }
}

class BorrowedItem {
    static private Context context;
    int index = 0;
    String item = "No Borrowings Yet";
    String date = " - ";
    float amount = 0;

    public BorrowedItem(int _index, String _item, float _amount, String _date) {
        this.index = _index;
        this.item = _item;
        this.amount = _amount;
        this.date = _date;
    }

    public boolean refreshIndexNo(String _borrowerName, int _index) {
        int originalIndex = this.index;
        this.index = _index;
        DatabaseHelperForBorrowingDetails myDb;
        myDb = new DatabaseHelperForBorrowingDetails(context, getDatabaseName(_borrowerName));
        return myDb.updateData( String.valueOf(originalIndex), String.valueOf(_index), this.item, this.date, String.valueOf(this.amount));
    }

    public static BorrowedItem addNewBorrowedItem(int _newItemIndex, String _item, float _amount, String _borrowerName) {
        if (context == null) {
            Log.i(tag, "No Context Initialized");
            return null;
        }
        //Generate timestamp
        Date d = new Date();
        String _date = DateFormat.format("d MMMM yyyy", d.getTime()).toString();
        //Store to database
        DatabaseHelperForBorrowingDetails myDb;
        myDb = new DatabaseHelperForBorrowingDetails(context, getDatabaseName(_borrowerName));
        boolean isInserted = myDb.insertData(String.valueOf(_newItemIndex), _item, _date, String.valueOf(_amount));
        if (isInserted) {
            Toast.makeText(context, "Stored Details Successfully",Toast.LENGTH_SHORT).show();
            BorrowedItem borrowedItem = new BorrowedItem (_newItemIndex, _item, _amount, _date);
            return borrowedItem;
        } else {
            Toast.makeText(context, "FAIL TO CREATE AND STORE DETAILS",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static int cancelBorrowedItem(BorrowedItem _borrowedItem, String _borrowerName) {
        DatabaseHelperForBorrowingDetails myDb;
        myDb = new DatabaseHelperForBorrowingDetails(context, getDatabaseName(_borrowerName));
        int rowDeleted = myDb.deleteData(Integer.toString(_borrowedItem.index));
        return rowDeleted;
    }

    public static void initializeContext(Context _context) {
        //Context from base activity required for Toast and Database
        context = _context;
    }

    public static void loadBorrowedItemData(String _name, ArrayList<BorrowedItem> borrowedItemsList) {
        if (context == null) {
            Log.i(tag, "No Context Initialized");
            return;
        }
        DatabaseHelperForBorrowingDetails myDb;
        myDb = new DatabaseHelperForBorrowingDetails(context, getDatabaseName(_name));
        Cursor res = myDb.getAllData();
        StringBuffer buffer = new StringBuffer();

        //Assign data into borrower's object and store to buffer for logging purpose
        while (res.moveToNext()) {
            BorrowedItem borrowedItem = new BorrowedItem(Integer.valueOf(res.getString(1)),res.getString(2), Float.valueOf(res.getString(4)), res.getString(3));
            buffer.append("Name " + _name + "\n");
            buffer.append("NO : " + res.getString(1) + "\t");
            buffer.append("ITEM : " + res.getString(2) + "\t");
            buffer.append("AMOUNT : " + res.getString(4) + "\t");
            buffer.append("DATE : " + res.getString(3) + "\n\n");
            borrowedItemsList.add(borrowedItem);
        }

        //Show database profile list
        Log.i(tag, buffer.toString());
    }

    public static void deleteDBFile(String _borrowerName) {
        DatabaseHelperForBorrowingDetails myDb;
        myDb = new DatabaseHelperForBorrowingDetails(context, getDatabaseName(_borrowerName));
        myDb.deleteDatabase(getDatabaseName(_borrowerName));
    }

    public static String getDatabaseName(String borrowerName) {
        return borrowerName + ".db";
    }

    public StringBuffer borrowItemListToStringBuffer() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.index + ". ");
        stringBuffer.append("\t");
        stringBuffer.append(Borrower.toDateFormatShort(this.date));
        stringBuffer.append("\t\t");
        stringBuffer.append(this.item);
        stringBuffer.append("\t\t");
        stringBuffer.append(Borrower.toCurrencyFormatRM(this.amount));
        stringBuffer.append("\n");
        return stringBuffer;
    }

}