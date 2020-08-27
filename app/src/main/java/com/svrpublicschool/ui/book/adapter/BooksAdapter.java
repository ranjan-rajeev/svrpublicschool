package com.svrpublicschool.ui.book.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.GenericImageLoader;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.BooksEntity;
import com.svrpublicschool.ui.book.BookDetailsFragment;
import com.svrpublicschool.ui.main.MainActivity;

import java.util.List;


public class BooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE = 1;
    List<BooksEntity> list;
    private Context mContext;
    private int height, width;

    public BooksAdapter(Context context, List<BooksEntity> list) {
        this.mContext = context;
        this.list = list;
        width = (Utility.getDisplayWidth((Activity) mContext) / 2) - 30;
        height = width;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_home, parent, false);
            return new BookViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_home, parent, false);
            return new BookViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BooksEntity item = list.get(position);
        switch (holder.getItemViewType()) {
            case ITEM_TYPE:
                BookViewHolder tilesHolder = (BookViewHolder) holder;
                tilesHolder.setData(mContext, item, width);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


}
