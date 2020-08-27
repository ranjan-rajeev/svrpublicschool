package com.svrpublicschool.ui.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.svrpublicschool.R;
import com.svrpublicschool.Util.GenericImageLoader;

import java.util.ArrayList;


public class ProductZoomThumbnailAdapter extends RecyclerView.Adapter<ProductZoomThumbnailAdapter.ViewHolder> {
    private ArrayList<String> thumbImages;
    private Context mContext;
    private CallbacksProductThumbnail callbacksProductThumbnail;

    private int row_index = 0;

    private int transparentColor;

    public ProductZoomThumbnailAdapter(Context vContext, ArrayList<String> thumbImages) {
        mContext = vContext;
        transparentColor = ContextCompat.getColor(mContext, android.R.color.transparent);
        this.thumbImages = thumbImages;
    }

    public void setCallback(CallbacksProductThumbnail vCallbackPromoBannerFour) {
        callbacksProductThumbnail = vCallbackPromoBannerFour;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.pager_gallery_pdpzoom_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (thumbImages.get(position) != null) {
            if (thumbImages.get(position).startsWith("Video")) {
                //GenericImageLoader.loadImage(mContext, holder.iv_product_detail_zoom, thumbImages.get(0), true, 0);
                holder.iv_product_detail_zoom_thumbnail_play.setVisibility(View.VISIBLE);
            } else
                //GenericImageLoader.loadImage(mContext, holder.iv_product_detail_zoom, thumbImages.get(position), true, 0);
                holder.iv_product_detail_zoom_thumbnail_play.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                if (callbacksProductThumbnail != null) {
                    callbacksProductThumbnail.ThumbnailClick(holder.getAdapterPosition());
                }
                //notifyDataSetChanged();
            }
        });

        if (row_index == position) {

            // holder.itemView.setBackgroundColor(Color.parseColor("#20000000"));
            holder.pdpzoom_border.setVisibility(View.VISIBLE);
        } else {
            //  holder.itemView.setBackgroundColor(transparentColor);
            holder.pdpzoom_border.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return thumbImages != null ? thumbImages.size() : 0;
    }

    public void addData(ArrayList<String> vThumbImages) {
        thumbImages = vThumbImages;
        notifyDataSetChanged();
    }

    public void selectThumbnail(int position, int lastpos) {
        row_index = position;
        notifyItemChanged(position);
        notifyItemChanged(lastpos);
    }

    public interface CallbacksProductThumbnail {
        void ThumbnailClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_product_detail_zoom, iv_product_detail_zoom_thumbnail_play, pdpzoom_border;

        ViewHolder(View itemView) {
            super(itemView);
            iv_product_detail_zoom = itemView.findViewById(R.id.iv_product_detail_zoom);
            iv_product_detail_zoom_thumbnail_play = itemView.findViewById(R.id.iv_product_detail_zoom_thumbnail_play);
            pdpzoom_border = itemView.findViewById(R.id.pdpzoom_border);
        }
    }
}
