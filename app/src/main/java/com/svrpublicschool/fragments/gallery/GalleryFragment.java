package com.svrpublicschool.fragments.gallery;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.ItemOffsetDecoration;
import com.svrpublicschool.R;
import com.svrpublicschool.customview.TouchImageView;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends BaseFragment {
    View view;
    GalleryAdapter galleryAdapter;
    RecyclerView rvGallery;
    TouchImageView ivZoom;
    TextView tvAdmisson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        initialise(view);

        //setListener();

        galleryAdapter = new GalleryAdapter(this, getImageList());
        rvGallery.setAdapter(galleryAdapter);
        //new MasterController(getActivity()).getConstants(this);
        return view;
    }

    private void initialise(View view) {
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setSelected(true);
        ivZoom = (TouchImageView) view.findViewById(R.id.ivZoom);
        rvGallery = view.findViewById(R.id.rvGallery);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        rvGallery.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvGallery.addItemDecoration(itemDecoration);
    }

    public List<String> getImageList() {
        List<String> list = new ArrayList<>();
        /*list.add("https://drive.google.com/open?id=0BwKIYpAWxdc0UVVaY1R2N1FwMFU");
        list.add("https://photos.google.com/photo/AF1QipO3FXrqY_sH6ibVSO7JPmY_uaBcUvLTa6DZcQoZ");
        list.add("https://www.google.co.in/maps/place/S+V+R+Public+School/@25.5753024,85.1180396,3a,75y,90t/data=!3m8!1e2!3m6!1sAF1QipPIXOHQUAF22Xj8WUU3eaA4-ZvlvJQ3O4Z_jA_f!2e10!3e12!6shttps:%2F%2Flh5.googleusercontent.com%2Fp%2FAF1QipPIXOHQUAF22Xj8WUU3eaA4-ZvlvJQ3O4Z_jA_f%3Dw203-h152-k-no!7i3264!8i2448!4m5!3m4!1s0x0:0x56bc28c8235dffcd!8m2!3d25.5753024!4d85.1180396#");
*/
        list.add("https://c1.staticflickr.com/5/4879/31924625408_4245740bb5_o.jpg");
        list.add("https://c1.staticflickr.com/5/4851/45746110222_f877bdfa5e_o.jpg");
        list.add("https://c1.staticflickr.com/5/4820/31924625998_38e30ce73f_o.jpg");
        list.add("https://c1.staticflickr.com/5/4883/45071375624_e6e2354906_o.jpg");
        list.add("https://c1.staticflickr.com/5/4908/45746110682_c1408ef20d_o.jpg");
        list.add("https://c1.staticflickr.com/5/4900/31924626508_88af66ea85_o.jpg");


        list.add("https://c1.staticflickr.com/5/4860/45071375934_11a47fa9bc_o.jpg");
        list.add("https://c1.staticflickr.com/5/4889/45746110872_5c96e1df01_o.jpg");


        list.add("https://c1.staticflickr.com/5/4863/31924626698_60a5d925e0_o.jpg");
        list.add("https://c1.staticflickr.com/5/4843/45746109982_62ee0c25be_o.jpg");
        list.add("https://c1.staticflickr.com/5/4823/45746110982_a0712df6de_o.jpg");
        list.add("https://c1.staticflickr.com/5/4883/45071376364_510cf15ddc_o.jpg");
        list.add("https://c1.staticflickr.com/5/4912/45746111092_e609fa312f_o.jpg");
        list.add("https://c1.staticflickr.com/5/4816/31924626998_61ae252dc2_o.jpg");
        list.add("https://c1.staticflickr.com/5/4828/45071376644_75eb8c7211_o.jpg");
        list.add("https://c1.staticflickr.com/5/4893/31924627228_8b81a2621e_o.jpg");
        list.add("https://c1.staticflickr.com/5/4846/45746111272_88b4b19f5e_o.jpg");

        list.add("https://c1.staticflickr.com/5/4851/45071376904_2d8e4e6c0a_o.jpg");
        list.add("https://c1.staticflickr.com/5/4846/31924625618_63055c8bc3_o.jpg");
        list.add("https://c1.staticflickr.com/5/4806/45746111412_486e1a26b5_o.jpg");
        list.add("https://c1.staticflickr.com/5/4904/31924627598_f7575835ca_o.jpg");
        list.add("https://c1.staticflickr.com/5/4861/45071377124_e18669490f_o.jpg");
        list.add("https://c1.staticflickr.com/5/4876/45746110302_0540526df1_o.jpg");
        list.add("https://c1.staticflickr.com/5/4821/45746111552_1d427b7709_o.jpg");
        list.add("https://c1.staticflickr.com/5/4823/31924627678_d5e2d0cfa9_b.jpg");
        list.add("https://c1.staticflickr.com/5/4858/45071377614_b3159ab411_o.jpg");
        list.add("https://c1.staticflickr.com/5/4880/45071377744_37c06cae23_o.jpg");

        list.add("https://c1.staticflickr.com/5/4803/45071377904_13032537fb_o.jpg");
        list.add("https://c1.staticflickr.com/5/4816/45746112072_dfc26fdbbf_b.jpg");
        list.add("https://c1.staticflickr.com/5/4884/45071378434_190b7570ed_o.jpg");

        return list;
    }

    public void openZoomView(String s) {
        rvGallery.setVisibility(View.GONE);
        ivZoom.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(s)
                //.placeholder(R.drawable.circle_placeholder)
                //.transform(new CircleTransform(HomeActivity.this)) // applying the image transformer
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                //.override(200, 200)
                .into(ivZoom);
    }
}
