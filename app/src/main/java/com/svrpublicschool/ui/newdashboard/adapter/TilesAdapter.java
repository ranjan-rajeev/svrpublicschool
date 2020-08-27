package com.svrpublicschool.ui.newdashboard.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.WebViewActivity;
import com.svrpublicschool.models.DashBoardEntity;
import com.svrpublicschool.models.UserEntity;
import com.svrpublicschool.ui.book.BookFragment;
import com.svrpublicschool.ui.chat.ChatActivity;
import com.svrpublicschool.ui.chat.GroupChatActivity;
import com.svrpublicschool.ui.gallery.GalleryFragment;
import com.svrpublicschool.ui.homework.HomeworkFragment;
import com.svrpublicschool.ui.main.MainActivity;
import com.svrpublicschool.ui.newdashboard.DashboardFragment;
import com.svrpublicschool.ui.notice.NoticeFragment;
import com.svrpublicschool.ui.study.StudyFragment;

import java.util.List;


public class TilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE = 1;
    List<DashBoardEntity> list;
    private Context mContext;
    private int height, width;

    public TilesAdapter(Context context, List<DashBoardEntity> list) {
        this.mContext = context;
        this.list = list;
        width = (Utility.getDisplayWidth((Activity) mContext) / 3) - 30;
        height = width;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_dashboard_square, parent, false);
            return new TilesHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_dashboard, parent, false);
            return new TilesHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DashBoardEntity item = list.get(position);
        switch (holder.getItemViewType()) {
            case ITEM_TYPE:
                TilesHolder tilesHolder = (TilesHolder) holder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //tilesHolder.rlIcon.setBackgroundTintList(ColorStateList.valueOf(Utility.getRandomColor(position, mContext)));
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                tilesHolder.cvParent.setUseCompatPadding(true);
                tilesHolder.cvParent.setLayoutParams(layoutParams);
                tilesHolder.tvName.setText(item.getTitle());
                tilesHolder.rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item.getRedurl().equalsIgnoreCase("WebViewActivity")) {
                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra(Constants.INTENT_PARAM_URL, item.getUrl());
                            intent.putExtra(Constants.INTENT_PARAM_TITLE, item.getTitle());
                            mContext.startActivity(intent);
                            //new GetLoginUser().execute();

                        } else {
                            redirect(item.getRedurl());
                        }
                    }
                });

                tilesHolder.ivIcon.setImageResource(getIcon(item.getRedurl()));
                new ShowBlink(item, tilesHolder.ivDot).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                /*if (!item.getUrl().equals("")) {
                    GenericImageLoader.loadImage(mContext, tilesHolder.ivIcon, item.getUrl(), 0);
                } else {
                    tilesHolder.ivIcon.setImageResource(getIcon(item.getId()));
                }*/
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


    public class TilesHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        RelativeLayout rl, rlIcon;
        CardView cvParent;
        AppCompatImageView ivDot;

        public TilesHolder(View view) {
            super(view);
            ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            tvName = (TextView) view.findViewById(R.id.tvName);
            rl = view.findViewById(R.id.rl);
            rlIcon = view.findViewById(R.id.rlIcon);
            cvParent = view.findViewById(R.id.cvParent);
            ivDot = view.findViewById(R.id.ivDot);
        }


    }

    class ShowBlink extends AsyncTask<Void, Void, Boolean> {
        DashBoardEntity dashBoardEntity;
        AppCompatImageView ivDot;

        ShowBlink(DashBoardEntity dashBoardEntity, AppCompatImageView ivDot) {
            this.dashBoardEntity = dashBoardEntity;
            this.ivDot = ivDot;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            String redUrl = dashBoardEntity.getRedurl();
            if (redUrl.equalsIgnoreCase("BooksFragment")) {
                return Utility.shoulFetchFromServer(mContext, Constants.SHD_PRF_BOOKS_VERSION, Constants.SHD_PRF_BOOKS_VERSION);
            } else if (redUrl.equalsIgnoreCase("StudyFragment")) {
                return Utility.shoulFetchFromServer(mContext, Constants.SHD_PRF_CLASSES_VERSION, Constants.DB_CLASSES);
            } else if (redUrl.equalsIgnoreCase("HomeWorkFragment")) {
                return Utility.shoulFetchFromServer(mContext, Constants.SHD_PRF_CLASSES_VERSION, Constants.DB_CLASSES);
            } else if (redUrl.equalsIgnoreCase("NoticeFragment")) {
                return Utility.shoulFetchFromServer(mContext, Constants.SHD_PRF_NOTICE_VERSION, Constants.SHD_PRF_NOTICE_VERSION);
            } else if (redUrl.equalsIgnoreCase("GalleryFragment")) {
                return false;
            } else if (redUrl.equalsIgnoreCase("GroupChat")) {
                return false;
            }
            return false;

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

    void addBlinkingAnimation(View view) {
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(1000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        view.startAnimation(animation);
    }

    public int getIcon(String redUrl) {

        if (redUrl.equalsIgnoreCase("BooksFragment")) {
            return R.drawable.book_icon;
        } else if (redUrl.equalsIgnoreCase("StudyFragment")) {
            return R.drawable.study;
        } else if (redUrl.equalsIgnoreCase("HomeWorkFragment")) {
            return R.drawable.homework;
        } else if (redUrl.equalsIgnoreCase("NoticeFragment")) {
            return R.drawable.notice;
        } else if (redUrl.equalsIgnoreCase("GalleryFragment")) {
            return R.drawable.gallery;
        } else if (redUrl.equalsIgnoreCase("GroupChat")) {
            return R.drawable.ask;
        } else {
            return R.drawable.icon_round;
        }
    }

    public void redirect(String redUrl) {
        MainActivity mainActivity = ((MainActivity) mContext);
        if (redUrl.equalsIgnoreCase("BooksFragment")) {
            mainActivity.addFragment(new BookFragment(), "books");
        } else if (redUrl.equalsIgnoreCase("StudyFragment")) {
            mainActivity.addFragment(new StudyFragment(), "books");
        } else if (redUrl.equalsIgnoreCase("HomeWorkFragment")) {
            mainActivity.addFragment(new HomeworkFragment(), "homework");
        } else if (redUrl.equalsIgnoreCase("NoticeFragment")) {
            mainActivity.addFragment(new NoticeFragment(), "notice");
        } else if (redUrl.equalsIgnoreCase("GalleryFragment")) {
            mainActivity.addFragment(new GalleryFragment(), "books");
        } else if (redUrl.equalsIgnoreCase("GroupChat")) {
            Intent intent = new Intent(mContext, GroupChatActivity.class);
            intent.putExtra(Constants.INTENT_PARAM_GROUP, Utility.getGroupDetails());
            intent.putExtra(Constants.INTENT_PARAM_FROM, "ASK");
            mContext.startActivity(intent);
        }
    }


    class GetLoginUser extends AsyncTask<Void, Void, UserEntity> {
        @Override
        protected UserEntity doInBackground(Void... voids) {
            String allString = SharedPrefManager.getInstance(mContext).getStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            Gson gson = new Gson();
            UserEntity userEntity = gson.fromJson(allString, UserEntity.class);
            return userEntity;
        }

        @Override
        protected void onPostExecute(UserEntity userEntity) {
            super.onPostExecute(userEntity);
            if (userEntity != null) {
                if (userEntity.getfId().equals("-M8B4qZlYyg5Ww9sf9Z4")) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(Constants.INTENT_CHAT_TYPE, "single");
                    intent.putExtra(Constants.INTENT_PARAM_USER, getRajeev());
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(Constants.INTENT_CHAT_TYPE, "single");
                    intent.putExtra(Constants.INTENT_PARAM_USER, getNisha());
                    mContext.startActivity(intent);
                }
            }
        }
    }


    public UserEntity getNisha() {
        UserEntity userEntity = new UserEntity();
        userEntity.setfId("-M8B4qZlYyg5Ww9sf9Z4");
        userEntity.setName("Nisha Bharti");
        userEntity.setType("student");
        userEntity.setUderId("nisha");
        userEntity.setPass("bharti");
        return userEntity;
    }

    public UserEntity getRajeev() {
        UserEntity userEntity = new UserEntity();
        userEntity.setfId("-M8Aw5y4dNoBJo4uMg-3");
        userEntity.setName("Rajeev Ranjan");
        userEntity.setType("admin");
        userEntity.setUderId("rajeev");
        userEntity.setPass("rajeev");
        return userEntity;
    }
}
