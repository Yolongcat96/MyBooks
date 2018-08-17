package com.example.android.mybooks.data;

import android.provider.BaseColumns;

public class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor
    private BookContract() {
    }

    // Inner class that defines constant values for the books database table
    // Each entry in the table represents a single book
    public static final class BookEntry implements BaseColumns {

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

        // possible for the genre
        public static final int GENRE_NOVEL = 0;
        public static final int GENRE_FANTASY = 1;
        public static final int GENRE_HISTORICAL_FICTION = 2;
        public static final int GENRE_NON_FICTION = 3;
        public static final int GENRE_ROMANCE = 4;

        // Book Quantity: Integer
        public final static String COLUMN_QUANTITY = "quantity";

        // Book Price:  Integer
        public final static String COLUMN_PRICE = "price";

        // Supplier Name:  TEXT
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        // Supplier Phone:  TEXT
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";

    }
}
