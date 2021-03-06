package com.example.android.mybooks.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mybooks.MainListActivity;
import com.example.android.mybooks.R;
import com.example.android.mybooks.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    private final int[] sampleBookImages = {
            R.drawable.ic_sample_book1,
            R.drawable.ic_sample_book2,
            R.drawable.ic_sample_book3,
            R.drawable.ic_sample_book4
    };

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        //Set views from item_list.xml
        TextView bookTitleView = view.findViewById(R.id.book_title);
        TextView authorView = view.findViewById(R.id.book_author);
        TextView priceView = view.findViewById(R.id.book_price);
        TextView quantityTextView = view.findViewById(R.id.book_quantity);
        ImageView thumnailView = view.findViewById(R.id.article_thumbnail);
        // get column index
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        final int bookIdColumnindex = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        // get variables from the Cursor for the current chosen book
        final int bookID = cursor.getInt(idColumnIndex);
        String bookTitle = cursor.getString(titleColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        bookTitleView.setText(bookTitle);
        authorView.setText(author);
        priceView.setText("$" + String.valueOf(price));
        //set the sample book image
        int imageIndex = bookIdColumnindex % 4;
        thumnailView.setImageResource(sampleBookImages[imageIndex]);
        quantityTextView.setText(" (Stock:" + String.valueOf(quantity) + ")");
        // sale image clickable
        ImageView imgView = view.findViewById(R.id.sale_btn);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainListActivity Activity = (MainListActivity) context;
                Activity.sellBook(bookID, quantity);
            }
        });
    }
}
