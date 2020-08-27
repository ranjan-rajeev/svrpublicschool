package com.svrpublicschool.ui.study.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.ui.homework.HomeworkFragment;
import com.svrpublicschool.ui.study.SubjectFragment;
import com.svrpublicschool.ui.main.MainActivity;
import com.svrpublicschool.models.ClassEntity;

import java.util.List;

public class ClassListAdapter extends RecyclerView.Adapter {

    public static final int TYPE_CLASS_ITEM = 1;
    private List<ClassEntity> list;
    Context mContext;
    int width, height;
    HomeworkFragment homeworkFragment;

    public ClassListAdapter(Context context, List<ClassEntity> data) {
        this.list = data;
        this.mContext = context;
        width = ((Utility.getDisplayWidth((Activity) mContext) / 3) - 30);
        height = width;
    }

    public ClassListAdapter(HomeworkFragment homeworkFragment, List<ClassEntity> data) {
        this.list = data;
        this.homeworkFragment = homeworkFragment;
        this.mContext = homeworkFragment.getActivity();
        width = ((Utility.getDisplayWidth((Activity) mContext) / 3) - 30);
        height = width;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case TYPE_CLASS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_list, parent, false);
                return new ClassItemViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_CLASS_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ClassEntity classEntity = list.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_CLASS_ITEM:
                ((ClassItemViewHolder) holder).setData(mContext, classEntity);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void updateList(List<ClassEntity> classEntities) {
        this.list = classEntities;
        notifyDataSetChanged();
    }

    public class ClassItemViewHolder extends RecyclerView.ViewHolder {
        CardView llParent;
        TextView tvTitle;
        AppCompatImageView ivDot;

        public ClassItemViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.ivPreview);
            this.llParent = itemView.findViewById(R.id.llParent);
            this.ivDot = itemView.findViewById(R.id.ivDot);
        }

        public void setData(Context context, ClassEntity classEntity) {
            llParent.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            tvTitle.setText(classEntity.getClassName());
            llParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (homeworkFragment != null) {
                        homeworkFragment.redirectToGroupChat(classEntity);
                    } else {
                        ((MainActivity) context).addSubjectFragment(new SubjectFragment(), classEntity);
                    }

                }
            });
        }
    }
}