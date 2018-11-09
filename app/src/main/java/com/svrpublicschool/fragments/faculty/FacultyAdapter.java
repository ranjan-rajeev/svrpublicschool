package com.svrpublicschool.fragments.faculty;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.svrpublicschool.R;
import com.svrpublicschool.fragments.home.HomeFragment;
import com.svrpublicschool.models.FacultyDetailsEntity;
import com.svrpublicschool.models.HomeDescEntity;

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

                Glide.with(mContex).load(entity.getProfilePhoto()).asBitmap().centerCrop().into(new BitmapImageViewTarget(item.ivIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContex.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        item.ivIcon.setImageDrawable(circularBitmapDrawable);
                    }
                });

                /*Glide.with(mContex)
                        .load(entity.getProfilePhoto())
                        .apply(RequestOptions.circleCropTransform())
                        //.placeholder(R.drawable.circle_placeholder)
                        //.transform(new CircleTransform(HomeActivity.this)) // applying the image transformer
                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
                        //.skipMemoryCache(true)
                        //.override(200, 200)
                        .into(item.ivIcon);*/
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
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            rlParent = itemView.findViewById(R.id.rlParent);
        }

    }

}
