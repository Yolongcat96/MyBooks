package com.example.android.mybooks;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mybooks.data.BookContract.BookEntry;

public class BookDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG_DEBUG = this.getClass().getSimpleName();
    private static final int EXISTING_PET_LOADER = 0;
    private Uri mCurrentBookUri;
    private long currentBookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        // get Data from the previous activity
        getDataFromPreviousActivity();
        // setTitle
        setTitle();
        // Kick off the loader
        getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
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
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // exit activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define projection
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
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleCI = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE); // Book title column index
            int authorCI = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME);
            int genreCI = cursor.getColumnIndex(BookEntry.COLUMN_GENRE);
            int quantityCI = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int priceCI = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int supplierNameCI = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneCI = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
            // Extract out the value from the Cursor for the given column index
            String currTitle = cursor.getString(titleCI);
            String currAuthor = cursor.getString(authorCI);
            int currGenre = cursor.getInt(genreCI);
            int currQuantity = cursor.getInt(quantityCI);
            double currPrice = cursor.getDouble(priceCI);
            String currSName = cursor.getString(supplierNameCI);
            String currSPhone = cursor.getString(supplierPhoneCI);
            // get the TextViews from the xml
            TextView titleTV = findViewById(R.id.book_title);
            TextView authorTV = findViewById(R.id.author);
            TextView genreTV = findViewById(R.id.genre);
            TextView quantityTV = findViewById(R.id.quantity);
            TextView priceTV = findViewById(R.id.price);
            TextView supplierNameTV = findViewById(R.id.supplier_name);
            TextView supplierPhoneTV = findViewById(R.id.supplier_phone);
            // Set text on EditText
            titleTV.setText(currTitle);
            authorTV.setText(currAuthor);
            genreTV.setText(getGenreWithIndex(currGenre));
            quantityTV.setText(String.valueOf(currQuantity));
            priceTV.setText(String.valueOf(currPrice));
            supplierNameTV.setText(currSName);
            supplierPhoneTV.setText(currSPhone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void getDataFromPreviousActivity() {
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        currentBookId = Long.valueOf(mCurrentBookUri.getLastPathSegment());
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

    private void setTitle() {
        getSupportActionBar().setTitle(getResources().getString(R.string.book_detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the delete.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the delete.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Delete the current book
    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void goToEditorBook() {
        // Create the new intent
        Intent intent = new Intent(BookDetailActivity.this, EditorActivity.class);
        // get the current uri
        Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentBookId);
        // Set the URI on the data field of the intent
        intent.setData(currentBookUri);
        // launch the bookDetailActivity to display the dta for the current book
        startActivity(intent);
    }

    // Go to MainList for Books
    private void goToMainList() {
        Intent _MainListActivity = new Intent(getApplicationContext(), MainListActivity.class);
        startActivity(_MainListActivity);
    }
}
