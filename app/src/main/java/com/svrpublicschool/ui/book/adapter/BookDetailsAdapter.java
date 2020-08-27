package com.svrpublicschool.ui.book.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.BooksEntity;

import java.util.List;


public class BookDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE = 1;
    List<BooksEntity> list;
    private Context mContext;
    private int height, width;

    public BookDetailsAdapter(Context context, List<BooksEntity> list) {
        this.mContext = context;
        this.list = list;
        width = (Utility.getDisplayWidth((Activity) mContext) / 2) - 30;
        height = width;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_details, parent, false);
            return new BookDetailsViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_details, parent, false);
            return new BookDetailsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BooksEntity item = list.get(position);
        switch (holder.getItemViewType()) {
            case ITEM_TYPE:
                BookDetailsViewHolder tilesHolder = (BookDetailsViewHolder) holder;
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
