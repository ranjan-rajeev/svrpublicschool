package com.svrpublicschool.ui.home;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.svrpublicschool.R;
import com.svrpublicschool.models.HomeDescEntity;

import java.util.List;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.NavigationItem> {

    HomeFragment mContex;
    List<HomeDescEntity> mAppList;


    public HomeAdapter(HomeFragment context, List<HomeDescEntity> list) {
        this.mContex = context;
        this.mAppList = list;
    }

    @Override
    public NavigationItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_desc, parent, false);
        return new NavigationItem(itemView);
    }

    @Override
    public void onBindViewHolder(NavigationItem holder, final int position) {

        if (holder instanceof NavigationItem) {
            final NavigationItem item = (NavigationItem) holder;
            HomeDescEntity homeDescEntity = mAppList.get(position);
            item.tvTitle.setText(homeDescEntity.getTitle());
            item.tvDesc.setText(homeDescEntity.getDesc());
        }

    }


    @Override
    public int getItemCount() {
        return (mAppList.size());
    }

    public class NavigationItem extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        RelativeLayout rlParent;

        public NavigationItem(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.ivPreview);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            rlParent = itemView.findViewById(R.id.rlParent);
        }

    }

}
