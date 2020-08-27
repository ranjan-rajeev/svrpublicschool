package com.svrpublicschool.ui.gallery;

import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;

import java.util.List;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.NavigationItem> {

    GalleryFragment mContex;
    List<String> mAppList;


    public GalleryAdapter(GalleryFragment context, List<String> list) {
        this.mContex = context;
        this.mAppList = list;
    }

    @Override
    public NavigationItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery, parent, false);
        return new NavigationItem(itemView);
    }

    @Override
    public void onBindViewHolder(NavigationItem holder, final int position) {

        if (holder instanceof NavigationItem) {
            final NavigationItem item = (NavigationItem) holder;
            item.ivIcon.setDrawingCacheEnabled(true);

            if (position < mAppList.size()) {
                Glide.with(mContex)
                        .load(mAppList.get(position))
                        //.placeholder(R.drawable.circle_placeholder)
                        //.transform(new CircleTransform(HomeActivity.this)) // applying the image transformer
                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
                        //.skipMemoryCache(true)
                        //.override(200, 200)
                        .into(item.ivIcon);
            }
            item.rlParent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    ActivityOptionsCompat activityOptionsCompat;
                    activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mContex.getActivity(), item.ivIcon, "imageMain");
                    Intent intent = new Intent(mContex.getActivity(), ZoomActivity.class);
                    intent.putExtra(Constants.INTENT_PARAM_URL, mAppList.get(position));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mContex.getActivity().startActivity(intent, activityOptionsCompat.toBundle());
                    } else {
                        mContex.getActivity().startActivity(intent);
                    }
                    // ((GalleryFragment)mContex).openZoomView(mAppList.get(position));
                   /* mContex.getActivity().startActivity(new Intent(mContex.getActivity(), ZoomActivity.class)
                            .putExtra("URL", mAppList.get(position)));*/
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return (mAppList.size());
    }

    public class NavigationItem extends RecyclerView.ViewHolder {

        ImageView ivIcon, ivIcon2;
        LinearLayout rlParent;

        public NavigationItem(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivIcon2 = itemView.findViewById(R.id.ivIcon2);
            rlParent = itemView.findViewById(R.id.rlParent);
        }

    }

}
