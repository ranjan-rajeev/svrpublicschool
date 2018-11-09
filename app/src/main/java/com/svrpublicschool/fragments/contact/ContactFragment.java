package com.svrpublicschool.fragments.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.R;

public class ContactFragment extends BaseFragment implements View.OnClickListener {
    View view;

    TextView tvAdmisson, tvSubmit;
    EditText etName, etEmail, etMobile, etMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_contact, container, false);
        initialise(view);
        //setListener();
        return view;
    }

    private void initialise(View view) {
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setSelected(true);
        tvSubmit = view.findViewById(R.id.tvSubmit);
        tvSubmit.setOnClickListener(this);

        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        etMessage = view.findViewById(R.id.etMessage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSubmit:
                Toast.makeText(getActivity(), "Thank You for contacting US!!!", Toast.LENGTH_SHORT).show();
                etName.setText("");
                etEmail.setText("");
                etMobile.setText("");
                etMessage.setText("");
                break;
        }
    }
}
