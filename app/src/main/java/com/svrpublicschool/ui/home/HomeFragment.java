package com.svrpublicschool.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.R;
import com.svrpublicschool.firebase.FirebaseHelper;
import com.svrpublicschool.models.HomeDescEntity;
import com.svrpublicschool.ui.main.MainActivity;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment //implements
        /*YouTubePlayer.OnInitializedListener*/ {
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
    private MainActivity myContext;

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof MainActivity) {
            myContext = (MainActivity) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_home, container, false);
        initialise(view);
        //setCorousalView();

        bindRecyclerView();
        setListener();
        //updateBanner();
        //updateText();
        //initYoutubeView();
        //galleryAdapter = new GalleryAdapter(this, getImageList());
        //rvGallery.setAdapter(galleryAdapter);
        //new MasterController(getActivity()).getConstants(this);
        return view;
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
        tvAdmisson.setText(FirebaseHelper.getScrollMessage());
        tvAdmisson.setSelected(true);
        ivSchool = view.findViewById(R.id.ivSchool);
        Glide.with(this).load("https://c1.staticflickr.com/5/4851/45746110222_f877bdfa5e_o.jpg").into(ivSchool);
        rvFacility = view.findViewById(R.id.rvFacility);
        rvFacility.setLayoutManager(new LinearLayoutManager(getActivity()));

        rvRule = view.findViewById(R.id.rvRule);
        rvRule.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
