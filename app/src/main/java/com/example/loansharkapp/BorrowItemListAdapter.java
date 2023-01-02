package com.example.loansharkapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BorrowItemListAdapter extends BaseAdapter {
    Context context;
    ArrayList<BorrowedItem> borrowItemList;
    LayoutInflater inflter;

    public BorrowItemListAdapter(Context applicationContext, ArrayList<BorrowedItem> borrowItemList) {
        this.context = applicationContext;
        this.borrowItemList = borrowItemList;
        this.inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return borrowItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.adapter_borrow_item_list, null);
        initializeBorrowerListScrollingView(convertView, position);
        return convertView;
    }

    public void initializeBorrowerListScrollingView(View view, int position) {
        //Set List Index
        TextView noTextView = (TextView) view.findViewById(R.id.no);
        noTextView.setText(Integer.toString(borrowItemList.get(position).index));
        //Set List Item Name
        TextView itemTextView = (TextView) view.findViewById(R.id.item);
        itemTextView.setText(borrowItemList.get(position).item);
        //Set List Amount
        TextView amountTextView = (TextView) view.findViewById(R.id.amount);
        amountTextView.setText(Borrower.toCurrencyFormatRM(borrowItemList.get(position).amount));
        //Set List Date
        TextView dateTextView = (TextView) view.findViewById(R.id.date);
        dateTextView.setText(borrowItemList.get(position).date);
    }
}
