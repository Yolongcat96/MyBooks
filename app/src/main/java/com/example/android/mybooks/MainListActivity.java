package com.example.android.mybooks;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.mybooks.data.BookContract.BookEntry;
import com.example.android.mybooks.data.BookDbHelper;
import com.example.android.mybooks.data.BookListAdapter;

public class MainListActivity extends AppCompatActivity {

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        // Set the title
        setTitle();
        // Set the floating button
        setFloatingButton();
        // Set listView
        setListView();
    }

    private void setListView() {
        ListView bookListView = findViewById(R.id.list_view);
        View emptyBoxView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyBoxView);
        // get the cursor
        getCurrentCursor();
        BookListAdapter mCurrentAdapter = new BookListAdapter(this, mCursor);
        bookListView.setAdapter(mCurrentAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainListActivity.this, BookDetailActivity.class);
                intent.putExtra("bookRowId", position);
                startActivity(intent);
            }
        });

    }

    // Temporary helper method to display information in the on screen TextView about the state of the pets database
    private void getCurrentCursor() {
        // To access our database, we instantiate our subclass of SQLiteOpenHolder
        // and pass the context, which is the current activity
        BookDbHelper mDbHelper = new BookDbHelper(this);
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

    private void setTitle() {
        getSupportActionBar().setTitle(getResources().getString(R.string.list_top_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Related to icons on the toolbar: menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            goToLogin();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent loginActivity = new Intent(MainListActivity.this, LoginActivity.class);
        startActivity(loginActivity);
    }

    private void goToEditorBook() {
        Intent editorActivity = new Intent(MainListActivity.this, EditorActivity.class);
        editorActivity.putExtra(getString(R.string.callingAct), getString(R.string.MainList));
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
}
