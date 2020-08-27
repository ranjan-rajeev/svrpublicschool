package com.svrpublicschool.ui.aboutus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.R;
import com.svrpublicschool.firebase.FirebaseHelper;

public class AboutUsFragment extends BaseFragment {

    TextView tvAdmisson;
    ImageView ivIcon;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_about_us, container, false);
        initialise(view);
        //setListener();
        return view;
    }

    private void initialise(View view) {
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setText(FirebaseHelper.getScrollMessage());
        tvAdmisson.setSelected(true);
        ivIcon = view.findViewById(R.id.ivIcon);
        Glide.with(this).load("http://www.svrpublicschool.com/images/building.jpg").into(ivIcon);
    }

}
