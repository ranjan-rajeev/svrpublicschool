package com.svrpublicschool.ui.newdashboard;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.firebase.FirebaseHelper;
import com.svrpublicschool.models.BannerEntity;
import com.svrpublicschool.models.DashBoardEntity;
import com.svrpublicschool.ui.main.MainActivity;
import com.svrpublicschool.ui.newdashboard.adapter.TilesAdapter;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends BaseFragment {
    View view;
    CarouselView carouselView;
    TextView tvAdmisson;
    Context context;
    RecyclerView rvTiles;

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof MainActivity) {
            context = (MainActivity) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initialise(view);
        new BindCarouselList().execute();
        new BindRecyclerlList().execute();
        return view;
    }

    private void initialise(View view) {
        rvTiles = view.findViewById(R.id.rvTiles);
        carouselView = view.findViewById(R.id.carouselView);
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setText(FirebaseHelper.getScrollMessage());
        tvAdmisson.setSelected(true);
    }

    public class BindCarouselList extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> banners = new ArrayList<>();
            try {

                Gson gson = new Gson();
                List<BannerEntity> bannerEntities = gson.fromJson(FirebaseHelper.getBannerList(), new TypeToken<List<BannerEntity>>() {
                }.getType());
                if (!Utility.isListEmpty(bannerEntities)) {
                    for (BannerEntity bannerEntity : bannerEntities) {
                        banners.add(bannerEntity.getUrl());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return banners;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            super.onPostExecute(list);
            if (!Utility.isListEmpty(list)) {
                setCorousalView(list);
            }
        }
    }

    public class BindRecyclerlList extends AsyncTask<Void, Void, List<DashBoardEntity>> {

        @Override
        protected List<DashBoardEntity> doInBackground(Void... voids) {
            List<DashBoardEntity> dashBoardEntities = new ArrayList<>();
            try {
                Gson gson = new Gson();
                List<DashBoardEntity> distinctDash = gson.fromJson(FirebaseHelper.getDashboardList(), new TypeToken<List<DashBoardEntity>>() {
                }.getType());
                for (DashBoardEntity dashBoardEntity : distinctDash) {
                    if (dashBoardEntity.isIsActive()) {
                        dashBoardEntities.add(dashBoardEntity);
                    }
                }
                return dashBoardEntities;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<DashBoardEntity> list) {
            super.onPostExecute(list);
            if (list != null) {
                TilesAdapter tilesAdapter = new TilesAdapter(context, list);
                rvTiles.setLayoutManager(new GridLayoutManager(context, 3));
                rvTiles.setAdapter(tilesAdapter);
            }
        }
    }

    private void setCorousalView(List<String> banners) {

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {

                Glide.with(context)
                        .load(banners.get(position))
                        //.placeholder(R.drawable.circle_placeholder)
                        //.transform(new CircleTransform(HomeActivity.this)) // applying the image transformer
                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
                        //.skipMemoryCache(true)
                        //.override(200, 200)
                        .into(imageView);
                //imageView.setImageResource(sampleImages[position]);
            }
        };
        carouselView.setImageListener(imageListener);

        carouselView.setPageCount(banners.size());
    }
}
