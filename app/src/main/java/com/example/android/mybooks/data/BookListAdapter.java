package com.example.android.mybooks.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mybooks.R;
import com.example.android.mybooks.data.BookContract.BookEntry;

public class BookListAdapter extends CursorAdapter {

    private final int[] sampleBookImages = {
            R.drawable.ic_sample_book1,
            R.drawable.ic_sample_book2,
            R.drawable.ic_sample_book3,
            R.drawable.ic_sample_book4
    };

    public BookListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Set views from item_list.xml
        TextView bookTitleView = view.findViewById(R.id.book_title);
        TextView authorView = view.findViewById(R.id.book_author);
        TextView priceView = view.findViewById(R.id.book_price);
        ImageView thumnailView = view.findViewById(R.id.article_thumbnail);
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        final int bookIdColumnindex = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        String bookTitle = cursor.getString(titleColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        bookTitleView.setText(bookTitle);
        authorView.setText(author);
        priceView.setText("$" + String.valueOf(price));
        //set the sample book image
        int imageIndex = bookIdColumnindex % 4;
        thumnailView.setImageResource(sampleBookImages[imageIndex]);
    }
}
