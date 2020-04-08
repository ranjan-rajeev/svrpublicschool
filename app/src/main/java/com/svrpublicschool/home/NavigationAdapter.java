package com.svrpublicschool.home;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.svrpublicschool.R;
import com.svrpublicschool.Util.Utility;

import java.util.List;


public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationItem> {

    Context mContex;
    List<NavigationItemEntity> mAppList;


    public NavigationAdapter(Context context, List<NavigationItemEntity> list) {
        this.mContex = context;
        this.mAppList = list;
    }

    @Override
    public NavigationItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bottom_navigation, parent, false);
        return new NavigationItem(itemView);
    }

    @Override
    public void onBindViewHolder(NavigationItem holder, int position) {

        if (holder instanceof NavigationItem) {
            final NavigationItem item = (NavigationItem) holder;
            final NavigationItemEntity entity = mAppList.get(position);

            item.tvText.setText(entity.getTitle());
            item.icon.setBackgroundResource(entity.getIcon());
            if (entity.isSelected) {
                item.icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContex, Utility.getPrimaryColor(mContex))));
                //item.icon.setColorFilter(ContextCompat.getColor(mContex, Utility.getPrimaryColor(mContex)), android.graphics.PorterDuff.Mode.MULTIPLY);
                //item.tvText.setTextColor(Utility.getPrimaryColor(mContex));

            } else {
                item.icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContex, Utility.getPrimaryColor(mContex))));
                //item.icon.setColorFilter(ContextCompat.getColor(mContex, Utility.getPrimaryColor(mContex)), android.graphics.PorterDuff.Mode.MULTIPLY);
                item.tvText.setTextColor(Utility.getPrimaryColor(mContex));
            }

            item.rlParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetAll();
                    entity.setSelected(true);
                }
            });

        }

    }

    private void resetAll() {
        for (NavigationItemEntity navigationItemEntity : mAppList) {
            navigationItemEntity.setSelected(false);
        }

    }

    @Override
    public int getItemCount() {
        return mAppList.size();
    }

    public class NavigationItem extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView tvText;
        RelativeLayout rlParent;

        public NavigationItem(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            tvText = itemView.findViewById(R.id.tvText);
            rlParent = itemView.findViewById(R.id.rlParent);
        }

    }

    @Override
    public long getItemId(int position) {
        return mAppList.get(position).getId();
    }

}
