package com.svrpublicschool.ui.newdashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.svrpublicschool.R;
import com.svrpublicschool.models.DashBoardEntity;

import java.util.List;

public class DashBoardAdapter extends RecyclerView.Adapter {

    public static final int TYPE_CAROUSAL = 1;
    public static final int TYPE_WEBVIEW = 2;
    public static final int TYPE_VIEW_PAGER = 3;
    public static final int TYPE_HEADER = 4;
    public static final int TYPE_CALCULATOR_LIST = 5;
    public static final int TYPE_SPACE = 6;
    public static final int TYPE_ADVIEW = 7;
    private List<DashBoardEntity> list;
    Context mContext;

    public DashBoardAdapter(List<DashBoardEntity> data, Context context) {
        this.list = data;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            /*case TYPE_CAROUSAL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
                return new CarouselViewHolder(view);
            case TYPE_WEBVIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_webview, parent, false);
                return new DashboardWebViewViewHolder(view);
            case TYPE_VIEW_PAGER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
                return new ViewPagerViewHolder(view);
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
                return new HeaderViewHolder(view);
            case TYPE_CALCULATOR_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calculator, parent, false);
                return new CalculatorViewHolder(view);
            case TYPE_SPACE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_space, parent, false);
                return new SpaceViewHolder(view);
            case TYPE_ADVIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adview, parent, false);
                return new AdViewViewHolder(view);*/

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return list != null && list.get(position) != null ? list.get(position).getId() : -1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        DashBoardEntity dashBoardEntity = list.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_CAROUSAL:
               // ((TilesViewHolder) holder).setData(mContext, dashBoardEntity);
                break;
           /* case TYPE_CALCULATOR_LIST:
                ((CalculatorViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
            case TYPE_WEBVIEW:
                ((DashboardWebViewViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
            case TYPE_VIEW_PAGER:
                ((ViewPagerViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;

            case TYPE_SPACE:
                ((SpaceViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
            case TYPE_ADVIEW:
                ((AdViewViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;*/
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void updateList(List<DashBoardEntity> dashBoardEntities) {
        this.list = dashBoardEntities;
        notifyDataSetChanged();
    }
}