package com.svrpublicschool.ui.study.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.models.GroupDetailsEntity;
import com.svrpublicschool.models.SubjectEntity;
import com.svrpublicschool.ui.chat.GroupChatActivity;

import java.util.Calendar;
import java.util.List;

public class SubjectListAdapter extends RecyclerView.Adapter {

    public static final int TYPE_CLASS_ITEM = 1;
    private List<SubjectEntity> list;
    Context mContext;
    int width, height;
    ClassEntity classEntity;

    public SubjectListAdapter(Context context, List<SubjectEntity> data, ClassEntity classEntity) {
        this.list = data;
        this.mContext = context;
        width = ((Utility.getDisplayWidth((Activity) mContext) / 3) - 30);
        height = width;
        this.classEntity = classEntity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case TYPE_CLASS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_list, parent, false);
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

        SubjectEntity subjectEntity = list.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_CLASS_ITEM:
                ((ClassItemViewHolder) holder).setData(mContext, subjectEntity);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void updateList(List<SubjectEntity> subjectEntities) {
        list.clear();
        list.addAll(subjectEntities);
        this.notifyDataSetChanged();
    }

    public class ClassItemViewHolder extends RecyclerView.ViewHolder {
        CardView llParent;
        TextView tvTitle, tvClass;

        public ClassItemViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.ivPreview);
            this.llParent = itemView.findViewById(R.id.llParent);
            this.tvClass = itemView.findViewById(R.id.tvClass);
        }

        public void setData(Context context, SubjectEntity subjectEntity) {
            llParent.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            tvTitle.setText(subjectEntity.getName());
            tvClass.setText(classEntity.getClassName());
            llParent.setOnClickListener(v -> {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra(Constants.INTENT_PARAM_GROUP, getGroupDetails(subjectEntity));
                context.startActivity(intent);
            });
        }

        public GroupDetailsEntity getGroupDetails(SubjectEntity subjectEntity) {
            GroupDetailsEntity groupDetailsEntity = new GroupDetailsEntity();
            groupDetailsEntity.setCreatedAt(Calendar.getInstance().getTimeInMillis());
            groupDetailsEntity.setFid(subjectEntity.getfId());
            groupDetailsEntity.setGpName("" + subjectEntity.getName() + " - " + classEntity.getClassName());
            return groupDetailsEntity;
        }
    }
}