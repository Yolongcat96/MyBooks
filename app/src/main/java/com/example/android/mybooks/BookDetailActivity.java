package com.example.android.mybooks;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mybooks.data.BookContract.BookEntry;
import com.example.android.mybooks.data.BookDbHelper;

public class BookDetailActivity extends AppCompatActivity {

    private final String TAG_DEBUG = this.getClass().getSimpleName();

    private BookDbHelper mDbHelper;
    private Cursor mCursor;
    private int bookRowId;
    private String bookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        // get the bookID
        getBookId();
        // get the cursor based on bookID
        getCurrentCursor();
        // get the information for the chosen book
        setInformation();
        // setTitle
        setTitle();
    }

    private String getGenreWithIndex(int index) {
        switch (index) {
            case 0:
                return getString(R.string.genre_novel);
            case 1:
                return getString(R.string.genre_fantasy);
            case 2:
                return getString(R.string.genre_historical_fiction);
            case 3:
                return getString(R.string.genre_non_fiction);
            default:
                return getString(R.string.not_decided);

        }
    }

    private void setInformation() {
        Log.d(TAG_DEBUG, "mCursor count: " + mCursor.getCount() + " current row id: " + bookRowId);
        // get the TextViews from the xml
        TextView titleTV = findViewById(R.id.book_title);
        TextView authorTV = findViewById(R.id.author);
        TextView genreTV = findViewById(R.id.genre);
        TextView quantityTV = findViewById(R.id.quantity);
        TextView priceTV = findViewById(R.id.price);
        TextView supplierNameTV = findViewById(R.id.supplier_name);
        TextView supplierPhoneTV = findViewById(R.id.supplier_phone);
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
                titleTV.setText(currTitle);
                authorTV.setText(currAuthor);
                genreTV.setText(getGenreWithIndex(currGenre));
                quantityTV.setText(String.valueOf(currQuantity));
                priceTV.setText(String.valueOf(currPrice));
                supplierNameTV.setText(currSName);
                supplierPhoneTV.setText(currSPhone);

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

    private void getBookId() {
        if (getIntent() != null) {
            // get an item name
            bookRowId = getIntent().getIntExtra(getString(R.string.bookRowId), -1);
        }
        if (bookRowId == -1) {
            Toast.makeText(this, getString(R.string.error_fetching), Toast.LENGTH_SHORT).show();
        }
    }

    private void setTitle() {
        getSupportActionBar().setTitle(getResources().getString(R.string.book_detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Edit" menu option
            case R.id.action_edit:
                goToEditorBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                actDelete();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // exit activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle delete function
    private void actDelete() {
        // 1. remove the book (BookEntry) from the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(BookEntry.TABLE_NAME, BookEntry._ID + "=?", new String[]{bookID});
        db.close();
        // 2. go back to MainList
        goToMainList();
    }

    private void goToEditorBook() {
        Intent editorActivity = new Intent(BookDetailActivity.this, EditorActivity.class);
        editorActivity.putExtra(getString(R.string.callingAct), getString(R.string.BookDetail));
        editorActivity.putExtra(getString(R.string.bookRowId), bookRowId);
        startActivity(editorActivity);
    }

    // Go to MainList for Books
    private void goToMainList() {
        Intent _MainListActivity = new Intent(getApplicationContext(), MainListActivity.class);
        startActivity(_MainListActivity);
    }
}
