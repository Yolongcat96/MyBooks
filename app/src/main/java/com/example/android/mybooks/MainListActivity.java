package com.example.android.mybooks;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.mybooks.data.BookContract.BookEntry;
import com.example.android.mybooks.data.BookCursorAdapter;

public class MainListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    private BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        /* Set the title */
        setTitle();
        // Set the floating button
        setFloatingButton();
        // Set listView
        setListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_mainlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
            case android.R.id.home:
                goToLogin();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListView() {
        // Set the book list view
        ListView bookListView = findViewById(R.id.list_view);
        View emptyBoxView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyBoxView);
        // set cursor adapter
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create the new intent
                Intent intent = new Intent(MainListActivity.this, BookDetailActivity.class);
                // get the current uri
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);
                // launch the bookDetailActivity to display the dta for the current book
                startActivity(intent);
            }
        });
        // Kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    // Randomly insert book
    private void insertBook() {
        // create content values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "Wonder");
        values.put(BookEntry.COLUMN_AUTHOR_NAME, "Tom Bruce");
        values.put(BookEntry.COLUMN_GENRE, 0);
        values.put(BookEntry.COLUMN_QUANTITY, 1);
        values.put(BookEntry.COLUMN_PRICE, 3.99);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Nation Inc.");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "1234567899");
        getContentResolver().insert(BookEntry.CONTENT_URI, values);

    }

    private void setTitle() {
        getSupportActionBar().setTitle(getResources().getString(R.string.list_top_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void goToLogin() {
        Intent loginActivity = new Intent(MainListActivity.this, LoginActivity.class);
        startActivity(loginActivity);
    }

    private void goToEditorBook() {
        Intent editorActivity = new Intent(MainListActivity.this, EditorActivity.class);
        startActivity(editorActivity);
    }

    private void setFloatingButton() {
        FloatingActionButton floatingActionButton = findViewById(R.id.fab_add_book);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditorBook();
            }
        });
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_all_successfully),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_all_fail),
                    Toast.LENGTH_SHORT).show();
        }
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
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    public void sellBook(int bookId, int quantity) {
        if (quantity > 0) {
            // decrease the number of book
            int curQuantity = quantity - 1;
            // update the database and display
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, curQuantity);
            Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
            int rowsUpdated = getContentResolver().update(currentUri, values, null, null);
            if (rowsUpdated == 1) {
                Toast.makeText(this, R.string.sell_one_book, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, getString(R.string.no_more_book), Toast.LENGTH_SHORT).show();
        }
    }
}
