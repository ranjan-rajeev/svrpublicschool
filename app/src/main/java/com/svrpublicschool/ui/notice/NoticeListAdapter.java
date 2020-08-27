package com.svrpublicschool.ui.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.NoticeEntity;

import java.util.List;

public class NoticeListAdapter extends RecyclerView.Adapter {

    public static final int TYPE_CLASS_ITEM = 1;
    private List<NoticeEntity> list;
    Context mContext;

    public NoticeListAdapter(Context context, List<NoticeEntity> data) {
        this.list = data;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case TYPE_CLASS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
                return new NoticeViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_CLASS_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        NoticeEntity noticeEntity = list.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_CLASS_ITEM:
                ((NoticeViewHolder) holder).setData(position, noticeEntity);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void updateList(List<NoticeEntity> classEntities) {
        this.list = classEntities;
        notifyDataSetChanged();
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        CardView llParent;
        View view;
        TextView tvTitle, tvDesc, tvTime;
        AppCompatImageView ivLogo, ivDot;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.llParent = itemView.findViewById(R.id.llParent);
            this.tvDesc = itemView.findViewById(R.id.tvDesc);
            this.tvTime = itemView.findViewById(R.id.tvTime);
            this.ivLogo = itemView.findViewById(R.id.ivLogo);

            this.ivDot = itemView.findViewById(R.id.ivDot);
            this.view = itemView.findViewById(R.id.view);
        }

        public void setData(int position, NoticeEntity noticeEntity) {
            if (!noticeEntity.getTitle().equalsIgnoreCase("")) {
                tvTitle.setText(noticeEntity.getTitle());
            }
            tvDesc.setText(noticeEntity.getDesc());
            tvTime.setText(Utility.getFormattedDate(noticeEntity.getCreatedAt(), false));
            view.setBackgroundColor(Utility.getRandomColor(position, mContext));
            addBlinkingAnimation(ivDot);
        }

    }

    void addBlinkingAnimation(View view) {
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(1000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        view.startAnimation(animation);
    }

}