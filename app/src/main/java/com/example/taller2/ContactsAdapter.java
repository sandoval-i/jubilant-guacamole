package com.example.taller2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ContactsAdapter extends CursorAdapter {
    private final int CONTACT_ID_INDEX = 0;
    private final int CONTACT_NAME_INDEX = 1;
    public ContactsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contactIdTextView = view.findViewById(R.id.contactIdTextView);
        TextView contactNameTextView = view.findViewById(R.id.contactNameTextView);
        int contactId = cursor.getInt(CONTACT_ID_INDEX);
        String contactName = cursor.getString(CONTACT_NAME_INDEX);
        contactIdTextView.setText(String.valueOf(contactId));
        contactNameTextView.setText(contactName);
    }
}
