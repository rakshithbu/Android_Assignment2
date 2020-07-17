package info.rakshith.lambton.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import info.rakshith.lambton.database.model.Contact;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contacts_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contact.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Contact.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertContact(Contact contact) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Contact.COLUMN_FIRST_NAME, contact.getFirstName());
        values.put(Contact.COLUMN_LAST_NAME, contact.getLastName());
        values.put(Contact.COLUMN_EMAIL, contact.getEmail());
        values.put(Contact.COLUMN_PHONE_NUMBER, contact.getPhoneNumber());
        values.put(Contact.COLUMN_ADDRESS, contact.getAddress());

        // insert row
        long id = db.insert(Contact.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Contact getContact(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Contact.TABLE_NAME,
                new String[]{Contact.COLUMN_ID, Contact.COLUMN_FIRST_NAME,
                        Contact.COLUMN_LAST_NAME,Contact.COLUMN_EMAIL,Contact.COLUMN_PHONE_NUMBER,Contact.COLUMN_ADDRESS},
                Contact.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Contact contact = new Contact();

        contact.setFirstName(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_FIRST_NAME)));
        contact.setLastName(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_LAST_NAME)));
        contact.setId(cursor.getColumnIndex(Contact.COLUMN_ID));
        contact.setEmail(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_EMAIL)));
        contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_PHONE_NUMBER)));
        contact.setAddress(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_ADDRESS)));

        // close the db connection
        cursor.close();

        return contact;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Contact.TABLE_NAME + " ORDER BY " +
                Contact.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(Contact.COLUMN_ID)));
                contact.setFirstName(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_FIRST_NAME)));
                contact.setLastName(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_LAST_NAME)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_EMAIL)));
                contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_PHONE_NUMBER)));
                contacts.add(contact);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return contacts;
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + Contact.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contact.COLUMN_FIRST_NAME, contact.getFirstName());
        values.put(Contact.COLUMN_LAST_NAME, contact.getLastName());
        values.put(Contact.COLUMN_EMAIL, contact.getEmail());
        values.put(Contact.COLUMN_PHONE_NUMBER, contact.getPhoneNumber());
        values.put(Contact.COLUMN_ADDRESS, contact.getAddress());

        System.out.println("contact.getLastName()==>"+contact.getLastName());
        System.out.println("columnid ====>"+contact.getId());
        // updating row
        return db.update(Contact.TABLE_NAME, values, Contact.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    public void deleteContact(Contact note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Contact.TABLE_NAME, Contact.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
