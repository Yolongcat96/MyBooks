package com.example.android.mybooks;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.mybooks.data.BookContract.BookEntry;
import com.example.android.mybooks.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the book title
     */
    private EditText mBookTitleEditText;

    /**
     * EditText field to enter the author name
     */
    private EditText mAuthorNameEditText;

    /**
     * EditText field to enter the book's genre
     */
    private Spinner mGenreSpinner;

    /**
     * EditText field to enter the quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the Price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the supplier name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the supplier phone
     */
    private EditText mSupplierPhoneEditText;

    // Strings received from the user
    private String bookTitleString;
    private String authorNameString;
    private String supplierNameString;
    private String supplierPhoneString;
    private String quantityString;
    private String priceString;
    private int quantity;
    private double price;

    // SQlite related variables
    private BookDbHelper mDbHelper;
    private int bookRowId;
    private String bookID;
    private Cursor mCursor;

    // This activity can be called from two different activities: MainList and BookDetail.
    // According to the calling activity, the additional setup is needed.
    private boolean hasPreviousInform;

    /**
     * Gender of the pet. The possible values are:
     * 0:novel, 1 for fantasy, 2 for historical fiction, 3 for non fiction, and 4 for romance
     */
    private int mGenre = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        // Set input values
        setInputValues();
        // Set drop down
        setupDropdown();
        // Get information from the previous activity
        getInformationFromPreviousActivity();
        // Set title
        setTitle();
    }

    // Get the information from the previous activity
    private void getInformationFromPreviousActivity() {
        if (getIntent() != null) {
            // get an item name
            String preActivityName = getIntent().getStringExtra(getString(R.string.callingAct));
            if (preActivityName.compareTo(getString(R.string.BookDetail)) == 0) {
                hasPreviousInform = true;
                if (getIntent() != null) {
                    // get an item name
                    bookRowId = getIntent().getIntExtra(getString(R.string.bookRowId), -1);
                }
                if (bookRowId == -1) {
                    Toast.makeText(this, getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
                } else {
                    setVariables();
                }
            } else {
                hasPreviousInform = false;
            }
        }
    }

    private void setInformation() {
        // Figure out the index of each column
        int IDCI = mCursor.getColumnIndex(BookEntry._ID);
        int titleCI = mCursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE); // Book title column index
        int authorCI = mCursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME);
        int genreCI = mCursor.getColumnIndex(BookEntry.COLUMN_GENRE);
        int quantityCI = mCursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int priceCI = mCursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int supplierNameCI = mCursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneCI = mCursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
        int currentRow = 0;
        // Finding the chosen book information and set it on the xml
        while (mCursor.moveToNext()) {
            // choose information
            if (currentRow == bookRowId) {
                // Use that index to extract the string or Int value of the word
                // at the current row the cursor is on.
                bookID = mCursor.getString(IDCI);
                String currTitle = mCursor.getString(titleCI);
                String currAuthor = mCursor.getString(authorCI);
                int currGenre = mCursor.getInt(genreCI);
                int currQuantity = mCursor.getInt(quantityCI);
                double currPrice = mCursor.getDouble(priceCI);
                String currSName = mCursor.getString(supplierNameCI);
                String currSPhone = mCursor.getString(supplierPhoneCI);
                mBookTitleEditText.setText(currTitle);
                mAuthorNameEditText.setText(currAuthor);
                mQuantityEditText.setText(String.valueOf(currQuantity));
                mPriceEditText.setText(String.valueOf(currPrice));
                mSupplierNameEditText.setText(currSName);
                mSupplierPhoneEditText.setText(currSPhone);
                // set the received genre info
                mGenreSpinner.setSelection(currGenre);
            }
            currentRow++;
        }
    }

    private void getCurrentCursor() {
        mDbHelper = new BookDbHelper(this);
        // create and open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_AUTHOR_NAME,
                BookEntry.COLUMN_GENRE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };
        mCursor = db.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
    }

    private void setVariables() {
        //1. get the current cursor
        getCurrentCursor();
        // 2. set information
        setInformation();
    }

    private void setInputValues() {
        mBookTitleEditText = findViewById(R.id.edit_book_title);
        mAuthorNameEditText = findViewById(R.id.edit_author_name);
        mGenreSpinner = findViewById(R.id.spinner_genre);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mPriceEditText = findViewById(R.id.edit_price);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
    }

    private void setTitle() {
        if (hasPreviousInform) {
            getSupportActionBar().setTitle(getResources().getString(R.string.editor_activity_title_edit_book));
        } else {
            getSupportActionBar().setTitle(getResources().getString(R.string.editor_activity_title_new_book));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupDropdown() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);
        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        mGenreSpinner.setAdapter(genderSpinnerAdapter);
        // Set the integer mSelected to the constant values
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_novel))) {
                        mGenre = 0;
                    } else if (selection.equals(getString(R.string.genre_fantasy))) {
                        mGenre = 1;
                    } else if (selection.equals(getString(R.string.genre_historical_fiction))) {
                        mGenre = 2;
                    } else if (selection.equals(getString(R.string.genre_non_fiction))) {
                        mGenre = 3;
                    } else {
                        mGenre = 0; // Default Genre
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = 0; // Novel
            }
        });
    }

    private ContentValues setValues() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, bookTitleString);
        values.put(BookEntry.COLUMN_AUTHOR_NAME, authorNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        values.put(BookEntry.COLUMN_GENRE, mGenre);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_PRICE, price);
        return values;
    }

    // Get user input from editor and save new pet into database.
    private void insertBook() {
        // get information from user
        getInfoFromUser();
        // Create database helper
        BookDbHelper mDbHelper = new BookDbHelper(this);
        // Get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = setValues();
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
        // show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, getString(R.string.saving_success) + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    // Get information from user-typing
    private void getInfoFromUser() {
        bookTitleString = mBookTitleEditText.getText().toString().trim();
        authorNameString = mAuthorNameEditText.getText().toString().trim();
        supplierNameString = mSupplierNameEditText.getText().toString().trim();
        supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        quantityString = mQuantityEditText.getText().toString().trim();
        quantity = Integer.parseInt(quantityString);
        priceString = mPriceEditText.getText().toString().trim();
        price = Double.parseDouble(priceString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                actionSave();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // exit activity
                goToMainList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        // 1. update the object from the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = setValues();
        db.update(BookEntry.TABLE_NAME, values, BookEntry._ID + "=?", new String[]{bookID});
        db.close();

    }

    private void actionSave() {
        if (checkAllInfoInserted()) {
            // Update the already exist row
            if (hasPreviousInform) {
                // Update the information of the chosen book and i to the database
                update();
                // Go to the MainList
                goToMainList();
            } else {
                // Insert the new book to the database
                insertBook();
                // Go to the MainList
                goToMainList();
            }
        }
    }

    // Check all info is correct before going back to the MainList
    private boolean checkAllInfoInserted() {
        // Get information from user
        getInfoFromUser();
        if (TextUtils.isEmpty(bookTitleString)) {
            Toast.makeText(this, getString(R.string.no_title), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(authorNameString)) {
            Toast.makeText(this, getString(R.string.no_author), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.no_quantity), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.no_price), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.no_supplier_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, getString(R.string.no_supplier_phone), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    // Go to MainList for Books
    private void goToMainList() {
        Intent _MainListActivity = new Intent(getApplicationContext(), MainListActivity.class);
        startActivity(_MainListActivity);
    }

}