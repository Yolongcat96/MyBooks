package com.example.android.mybooks.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.mybooks.R;
import com.example.android.mybooks.data.BookContract.BookEntry;
import android.util.Log;

public class BookProvider extends ContentProvider {

    // Tag for the log messages
    private static final String LOG_TAG = BookProvider.class.getSimpleName();

    // URI matcher code for the content URI for the books table
    private static final int BOOKS = 100;

    // URI matcher code for the content URI for a single book in the books table
    private static final int BOOK_ID = 101;

    // URI matcher object
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, "books", BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, "books/#", BOOK_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    // Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match) {

            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.mybooks/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                // Cursor containing that row of the table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.not_supported) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.not_supported) + uri);

        }

    }

    // Insert a single book to the database
    private Uri insertBook(Uri uri, ContentValues values) {

        // 1. Book Title
        String name = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
        if (name == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.book_require_title));
        }

        // 2. Author Name
        String authorName = values.getAsString(BookEntry.COLUMN_AUTHOR_NAME);
        if (authorName == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.book_require_authorname));
        }

        // 3. Book Genre
        String genreType = values.getAsString(BookEntry.COLUMN_GENRE);
        if (genreType == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.book_require_genre));
        }

        // 4. Quantity: number of a book
        String quantityString = values.getAsString(BookEntry.COLUMN_QUANTITY);
        int quantity = Integer.parseInt(quantityString);

        // 5. Price
        String priceString = values.getAsString(BookEntry.COLUMN_PRICE);
        double price = Double.parseDouble(priceString);

        // 6. Supplier Name
        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.book_require_supplier));
        }

        // 7. Supplier contact information: phone number
        String supplierPhoneString = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, getContext().getString(R.string.fail_insertion_for) + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch(match) {
            case BOOKS:
                //return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                //return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.deletion_not_supported_for) +uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch(match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.update_not_supported_for) + uri);
        }

    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // 1. Book title
        if (values.containsKey(BookEntry.COLUMN_BOOK_TITLE)) {
            String booktitle = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
            if (booktitle == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.book_require_title));
            }
        }

        // 2. Author name
        if (values.containsKey(BookEntry.COLUMN_AUTHOR_NAME)) {
            String authorName = values.getAsString(BookEntry.COLUMN_AUTHOR_NAME);
            if (authorName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.book_require_authorname));
            }
        }

        // 3. Book Genre
        if (values.containsKey(BookEntry.COLUMN_GENRE)) {
            String genreType = values.getAsString(BookEntry.COLUMN_GENRE);
            if (genreType == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.book_require_genre));
            }
        }

        // 4. Quantity: number of a book
        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            String quantityString = values.getAsString(BookEntry.COLUMN_QUANTITY);
            int quantity = Integer.parseInt(quantityString);
        }

        // 5. Price
        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            String priceString = values.getAsString(BookEntry.COLUMN_PRICE);
            double price = Double.parseDouble(priceString);
        }

        // 6. Supplier Name
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.book_require_supplier));
            }
        }

        // 7. Supplier contact information: phone number
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierPhoneString = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;

    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(getContext().getString(R.string.unknown_uri) + uri + getContext().getString(R.string.with_match) + match);
        }
    }
}
