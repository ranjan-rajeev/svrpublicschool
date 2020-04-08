package com.svrpublicschool.fragments.facility;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.svrpublicschool.R;
import com.svrpublicschool.Utility;
import com.svrpublicschool.models.FacilityDetailsEntity;


import java.util.List;


public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.NavigationItem> {

    FacilityFragment mContex;
    List<FacilityDetailsEntity> mAppList;


    public FacilityAdapter(FacilityFragment context, List<FacilityDetailsEntity> list) {
        this.mContex = context;
        this.mAppList = list;
    }

    @Override
    public NavigationItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_facility, parent, false);
        return new NavigationItem(itemView);
    }

    @Override
    public void onBindViewHolder(NavigationItem holder, final int position) {

        if (holder instanceof NavigationItem) {
            final NavigationItem item = (NavigationItem) holder;
            final FacilityDetailsEntity entity = mAppList.get(position);

            item.tvDesc.setText(entity.getDesc());
            item.tvHeading.setText(entity.getTitle());
            item.tvTitle.setText(entity.getTitle());
            Glide.with(mContex).load(entity.getUrl()).into(item.ivIcon);

            if (entity.isDescVisible()) {
                item.llChild.setVisibility(View.VISIBLE);
                item.tvHeading.setBackgroundColor(mContex.getActivity().getResources().getColor(R.color.blue_descent));

                item.tvHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                //item.tvHeading.setCompoundDrawables(null,null,mContex.getActivity().getDrawable(android.R.drawable.arrow_up_float),null);

            } else {
                item.llChild.setVisibility(View.GONE);
                item.tvHeading.setBackgroundColor(mContex.getActivity().getResources().getColor(Utility.getAccentColor(mContex.getActivity())));
                item.tvHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                //item.tvHeading.setCompoundDrawables(null,null,mContex.getActivity().getDrawable(android.R.drawable.arrow_down_float),null);
            }
            item.tvHeading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (entity.isDescVisible()) {
                        resetAll();
                        ((FacilityFragment) mContex).refreshAdapter(mAppList);
                    } else {
                        resetAll();
                        mAppList.get(position).setDescVisible(true);
                        ((FacilityFragment) mContex).refreshAdapter(mAppList);
                    }
                }
            });
        }

    }

    private void resetAll() {
        for (FacilityDetailsEntity facilityDetailsEntity : mAppList) {
            facilityDetailsEntity.setDescVisible(false);
        }
    }


    @Override
    public int getItemCount() {
        return (mAppList.size());
    }

    public class NavigationItem extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvHeading;
        ImageView ivIcon;
        LinearLayout rlParent, llChild;

        public NavigationItem(View itemView) {
            super(itemView);
            tvHeading = itemView.findViewById(R.id.tvHeading);
            llChild = itemView.findViewById(R.id.llChild);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            rlParent = itemView.findViewById(R.id.rlParent);
        }

    }

}
