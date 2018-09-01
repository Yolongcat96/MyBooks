package com.example.android.mybooks.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    // set several constant variables
    public static final String CONTENT_AUTHORITY = "com.example.android.mybooks";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_BOOKS = "books";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor
    private BookContract() {
    }

    // Inner class that defines constant values for the books database table
    // Each entry in the table represents a single book
    public static final class BookEntry implements BaseColumns {

        // set the CONTENT_URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        // Name of database table for books
        public final static String TABLE_NAME = "books";

        // Unique ID number for the book (only for use in the database table)
        // Type: Integer
        public final static String _ID = BaseColumns._ID;

        // Book Title: TEXT
        public final static String COLUMN_BOOK_TITLE = "book_title";

        // Author Name: TEXT
        public final static String COLUMN_AUTHOR_NAME = "author_name";

        // Book Genre:TEXT
        public final static String COLUMN_GENRE = "genre";

        // Book Quantity: Integer
        public final static String COLUMN_QUANTITY = "quantity";

        // Book Price:  Integer
        public final static String COLUMN_PRICE = "price";

        // Supplier Name:  TEXT
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        // Supplier Phone number:  TEXT
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    }
}
