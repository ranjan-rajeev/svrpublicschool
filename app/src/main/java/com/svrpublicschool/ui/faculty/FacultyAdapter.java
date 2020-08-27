package com.svrpublicschool.ui.faculty;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.GenericImageLoader;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.FacultyDetailsEntity;
import com.svrpublicschool.ui.gallery.ZoomActivity;

import java.util.List;


public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.NavigationItem> {

    FacultyFragment mContex;
    List<FacultyDetailsEntity> mAppList;


    public FacultyAdapter(FacultyFragment context, List<FacultyDetailsEntity> list) {
        this.mContex = context;
        this.mAppList = list;
    }

    @Override
    public NavigationItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faculty, parent, false);
        return new NavigationItem(itemView);
    }

    @Override
    public void onBindViewHolder(NavigationItem holder, final int position) {

        if (holder instanceof NavigationItem) {
            final NavigationItem item = (NavigationItem) holder;
            FacultyDetailsEntity entity = mAppList.get(position);

            if (position < mAppList.size()) {

                Glide.with(mContex).asBitmap().load(entity.getProfilePhoto()).into(new BitmapImageViewTarget(item.ivIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContex.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        item.ivIcon.setImageDrawable(circularBitmapDrawable);
                    }
                });
                item.rlParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityOptionsCompat activityOptionsCompat;
                        activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mContex.getActivity(), item.ivIcon, "imageMain");
                        Intent intent = new Intent(mContex.getActivity(), ZoomActivity.class);
                        intent.putExtra(Constants.INTENT_PARAM_TITLE, entity.getName());
                        intent.putExtra(Constants.INTENT_PARAM_URL, entity.getProfilePhoto());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mContex.getActivity().startActivity(intent, activityOptionsCompat.toBundle());
                        } else {
                            mContex.getActivity().startActivity(intent);
                        }
                    }
                });

            }
            item.tvTitle.setText(entity.getName());
            item.tvDesc.setText(entity.getDesg());
        }

    }


    @Override
    public int getItemCount() {
        return (mAppList.size());
    }

    public class NavigationItem extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        ImageView ivIcon;
        LinearLayout rlParent;

        public NavigationItem(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.ivPreview);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            rlParent = itemView.findViewById(R.id.rlParent);
        }

    }

}
