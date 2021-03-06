package com.example.android.mybooks;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mybooks.data.BookContract.BookEntry;

public class BookDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PET_LOADER = 0;
    private Uri mCurrentBookUri;
    private long currentBookId;

    private TextView mQuantityTV;
    private TextView mSupplierPhoneTV;

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
        // set clickable imageViews
        setClickableImageViews();
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
            // Respond to a click on the "Left" arrow button in the app bar
            case android.R.id.home:
                // exit activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                mCurrentBookUri,
                null,
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
            TextView mTitleTV = findViewById(R.id.book_title);
            TextView mAuthorTV = findViewById(R.id.author);
            TextView mGenreTV = findViewById(R.id.genre);
            mQuantityTV = findViewById(R.id.quantity);
            TextView mPriceTV = findViewById(R.id.price);
            TextView mSupplierNameTV = findViewById(R.id.supplier_name);
            mSupplierPhoneTV = findViewById(R.id.supplier_phone);
            // Set text on EditText
            mTitleTV.setText(currTitle);
            mAuthorTV.setText(currAuthor);
            mGenreTV.setText(getGenreWithIndex(currGenre));
            mQuantityTV.setText(String.valueOf(currQuantity));
            mPriceTV.setText(String.valueOf(currPrice));
            mSupplierNameTV.setText(currSName);
            mSupplierPhoneTV.setText(currSPhone);
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
        finish(); // Finish the current activity
    }

    // set setClickableImageViews for increase, decrease and order buttons
    private void setClickableImageViews() {
        // decrease ImageView
        final ImageView decreaseIV = findViewById(R.id.decreaseImageView);
        decreaseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityTV.getText().toString().trim();
                String stringValue = quantityString.matches("") ? "0" : quantityString;
                int quantity = Integer.parseInt(stringValue);
                updateQuantity(quantity, false);
            }
        });
        // Increase ImageView
        ImageView increaseIV = findViewById(R.id.increaseImageView);
        increaseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityTV.getText().toString().trim();
                String stringValue = quantityString.matches("") ? "0" : quantityString;
                int quantity = Integer.parseInt(stringValue);
                updateQuantity(quantity, true);
            }
        });
        // Order (Call) ImageView
        ImageView orderIV = findViewById(R.id.orderImageView);
        orderIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call phone app API
                String phone = mSupplierPhoneTV.getText().toString().trim();
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                PackageManager packageManager = getBaseContext().getPackageManager();
                if (phoneIntent.resolveActivity(packageManager) == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_available), Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(phoneIntent);
                }
            }
        });

    }

    private void updateQuantity(int quantity, boolean isIncreasing) {
        int currValue;
        if (isIncreasing) { // increase by 1
            currValue = quantity + 1;
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, currValue);
            getContentResolver().update(mCurrentBookUri, values, null, null);
        } else { // decrease by 1
            if (quantity == 0) {
                Toast.makeText(getApplicationContext(), R.string.no_negative, Toast.LENGTH_SHORT).show();
            } else {
                currValue = quantity - 1;
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_QUANTITY, currValue);
                getContentResolver().update(mCurrentBookUri, values, null, null);
            }
        }
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
}