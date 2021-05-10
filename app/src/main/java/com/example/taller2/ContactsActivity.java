package com.example.taller2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;

public class ContactsActivity extends AppCompatActivity {
    private ListView contactsListView;
    private final int CONTACTS_PERMISSION_CODE = 1;
    private final String RATIONALE = "Es necesario brindar permiso para visualizar los contactos";
    private Cursor cursor;
    private final String[] projection = {ContactsContract.Profile._ID,
            ContactsContract.Profile.DISPLAY_NAME_PRIMARY};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contactsListView = findViewById(R.id.contactsListView);
        updateUI();
        PermissionHelper.requestPermission(this,
                Manifest.permission.READ_CONTACTS, CONTACTS_PERMISSION_CODE, RATIONALE);
    }

    private void updateUI() {
        if (PermissionHelper.hasPermission(this, Manifest.permission.READ_CONTACTS)) {
            cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection,
                    null, null, null);
            contactsListView.setAdapter(new ContactsAdapter(this, cursor, 0));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACTS_PERMISSION_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateUI();
        }
    }
}