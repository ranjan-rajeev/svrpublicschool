package com.svrpublicschool.fragments.gallery;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.ItemOffsetDecoration;
import com.svrpublicschool.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends BaseFragment {
    View view;
    GalleryAdapter galleryAdapter;
    RecyclerView rvGallery;

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
        list.add("https://upload.wikimedia.org/wikipedia/commons/5/5f/Larkmead_School%2C_Abingdon%2C_Oxfordshire.png");
        list.add("https://upload.wikimedia.org/wikipedia/commons/5/5f/Larkmead_School%2C_Abingdon%2C_Oxfordshire.png");
        list.add("https://upload.wikimedia.org/wikipedia/commons/5/5f/Larkmead_School%2C_Abingdon%2C_Oxfordshire.png");
        list.add("https://upload.wikimedia.org/wikipedia/commons/5/5f/Larkmead_School%2C_Abingdon%2C_Oxfordshire.png");
        list.add("https://upload.wikimedia.org/wikipedia/commons/5/5f/Larkmead_School%2C_Abingdon%2C_Oxfordshire.png");
        list.add("https://upload.wikimedia.org/wikipedia/commons/5/5f/Larkmead_School%2C_Abingdon%2C_Oxfordshire.png");


        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("https://upload.wikimedia.org/wikipedia/commons/5/5f/Larkmead_School%2C_Abingdon%2C_Oxfordshire.png");

        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("http://www.svrpublicschool.com/images/name.png");
        list.add("https://lh6.googleusercontent.com/rTlJik5uNVq_9DIG1_J2V7e5J0Fbo3a2Lruuq7l_92u4OXwBJ0srV3i6b50pz-92ClqnLex81Ywkg8k=w1366-h662");
        return list;
    }
}
