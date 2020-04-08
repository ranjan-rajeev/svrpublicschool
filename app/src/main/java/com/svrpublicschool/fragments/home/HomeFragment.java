package com.svrpublicschool.fragments.home;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.SVRApplication;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.BannerModel;
import com.svrpublicschool.models.HomeDescEntity;
import com.svrpublicschool.models.KeyValueModel;
import com.svrpublicschool.services.HttpService;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends BaseFragment {
    View view;
    CarouselView carouselView;
    String[] sampleImages = {"https://c1.staticflickr.com/5/4851/45746110222_f877bdfa5e_o.jpg",
            "https://c1.staticflickr.com/5/4816/45746112072_23c3e157a4_o.jpg"
            , "https://c1.staticflickr.com/5/4806/45746111412_486e1a26b5_o.jpg",
            "https://c1.staticflickr.com/5/4889/45746110872_5c96e1df01_o.jpg"};
    TextView tvAdmisson;
    ImageView ivSchool;
    RecyclerView rvFacility, rvRule;
    HomeAdapter homeAdapter, ruleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_home, container, false);
        initialise(view);
        setCorousalView();

        bindRecyclerView();
        setListener();
        updateBanner();
        updateText();
        //galleryAdapter = new GalleryAdapter(this, getImageList());
        //rvGallery.setAdapter(galleryAdapter);
        //new MasterController(getActivity()).getConstants(this);
        return view;
    }

    private void updateBanner() {
        Observable<BannerModel> userModelObservable = HttpService.getInstance().getBannerList(Constants.BANNER_URL);
        userModelObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BannerModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BannerModel bannerModel) {
                        Logger.d("TAGEER", bannerModel.getBanner().get(0).getUrl());
                        sampleImages = Utility.getBannerList();
                        setCorousalView();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void updateText() {

        Observable<KeyValueModel> userModelObservable = HttpService.getInstance().getStringList(Constants.KEY_VALUE_STRING_URL);
        userModelObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<KeyValueModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(KeyValueModel keyValueModel) {
                        Logger.d("TAGEER", keyValueModel.getKeyValue().get(0).getKey());
                        tvAdmisson.setText(Utility.getStringValue("admission"));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void bindRecyclerView() {
        homeAdapter = new HomeAdapter(this, getLIst());
        rvFacility.setAdapter(homeAdapter);

        ruleAdapter = new HomeAdapter(this, getRuleLIst());
        rvRule.setAdapter(ruleAdapter);
    }

    public List<HomeDescEntity> getLIst() {
        List<HomeDescEntity> list = new ArrayList<>();

        list.add(new HomeDescEntity("Sports Facility", getResources().getString(R.string.sports)));
        list.add(new HomeDescEntity("Transport Facility", getResources().getString(R.string.transport_facility)));
        //list.add(new HomeDescEntity("Medical Facility", ""));
        list.add(new HomeDescEntity("Library", getResources().getString(R.string.library)));
        list.add(new HomeDescEntity("Computer Labs", getResources().getString(R.string.computer_lab)));
        list.add(new HomeDescEntity("Smart Classes", getResources().getString(R.string.smart_classes)));
        return list;
    }

    public List<HomeDescEntity> getRuleLIst() {
        List<HomeDescEntity> list = new ArrayList<>();

        list.add(new HomeDescEntity("School Discipline", getResources().getString(R.string.discpline)));
        list.add(new HomeDescEntity("Guardian’s Role", getResources().getString(R.string.parent_role)));
        /*list.add(new HomeDescEntity("Medical Facility", ""));
        list.add(new HomeDescEntity("Well Maintained School", ""));
        list.add(new HomeDescEntity("Recreation", ""));
        list.add(new HomeDescEntity("Sports", ""));
        list.add(new HomeDescEntity("Clothing Items for Boarding", ""));*/
        return list;
    }


    private void setListener() {
    }

    private void setCorousalView() {


        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {

                Glide.with(getActivity())
                        .load(sampleImages[position])
                        //.placeholder(R.drawable.circle_placeholder)
                        //.transform(new CircleTransform(HomeActivity.this)) // applying the image transformer
                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
                        //.skipMemoryCache(true)
                        //.override(200, 200)
                        .into(imageView);
                //imageView.setImageResource(sampleImages[position]);
            }
        };

        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);


    }

    private void initialise(View view) {
        carouselView = view.findViewById(R.id.carouselView);
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setSelected(true);
        ivSchool = view.findViewById(R.id.ivSchool);
        Glide.with(this).load("https://c1.staticflickr.com/5/4851/45746110222_f877bdfa5e_o.jpg").into(ivSchool);
        rvFacility = view.findViewById(R.id.rvFacility);
        rvFacility.setLayoutManager(new LinearLayoutManager(getActivity()));

        rvRule = view.findViewById(R.id.rvRule);
        rvRule.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
