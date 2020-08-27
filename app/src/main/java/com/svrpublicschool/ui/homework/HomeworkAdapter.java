package com.svrpublicschool.ui.homework;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.models.DashBoardEntity;
import com.svrpublicschool.ui.main.MainActivity;
import com.svrpublicschool.ui.study.SubjectFragment;

import java.util.List;

public class HomeworkAdapter extends RecyclerView.Adapter {

    public static final int TYPE_CLASS_ITEM = 1;
    private List<ClassEntity> list;
    Context mContext;
    int width, height;
    HomeworkFragment homeworkFragment;

    public HomeworkAdapter(HomeworkFragment homeworkFragment, List<ClassEntity> data) {
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_work, parent, false);
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
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
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
            //new ShowBlink(classEntity, ivDot).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
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

    class ShowBlink extends AsyncTask<Void, Void, Boolean> {
        ClassEntity classEntity;
        AppCompatImageView ivDot;

        ShowBlink(ClassEntity classEntity, AppCompatImageView ivDot) {
            this.classEntity = classEntity;
            this.ivDot = ivDot;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return Utility.shoulFetchFromServer(mContext, classEntity.getfId(), classEntity.getfId());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                ivDot.setVisibility(View.VISIBLE);
                addBlinkingAnimation(ivDot);
            } else {
                ivDot.setVisibility(View.GONE);
            }
        }
    }
}