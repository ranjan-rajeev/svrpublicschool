package com.svrpublicschool.ui.chat.viewholder;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.GenericImageLoader;
import com.svrpublicschool.database.DatabaseClient;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.UserEntity;

public class VideoReceivedViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout rlParent;
    TextView tvMsg, tvTime;
    AppCompatImageView ivPreview;

    public VideoReceivedViewHolder(View itemView) {
        super(itemView);
        this.ivPreview = itemView.findViewById(R.id.ivPreview);
        this.rlParent = itemView.findViewById(R.id.rlParent);
        this.tvTime = itemView.findViewById(R.id.tvTime);
    }

    public void setData(Context context, ChatEntity chatEntity) {
        tvTime.setText("" + chatEntity.getCreatedAt());
        GenericImageLoader.loadImage(context, ivPreview, chatEntity.getUrl(), R.drawable.avtar);
        rlParent.setOnClickListener(v -> {
        });
    }
}