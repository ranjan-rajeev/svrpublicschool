package com.svrpublicschool.fragments.facility;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.R;
import com.svrpublicschool.models.FacilityDetailsEntity;

import java.util.ArrayList;
import java.util.List;

public class FacilityFragment extends BaseFragment {
    View view;
    FacilityAdapter facilityAdapter;
    RecyclerView rvFacility;
    List<FacilityDetailsEntity> facilityDetailsEntities;
    TextView tvAdmisson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_facility, container, false);
        initialise(view);

        //setListener();
        facilityDetailsEntities = getList();
        facilityAdapter = new FacilityAdapter(this, facilityDetailsEntities);
        rvFacility.setAdapter(facilityAdapter);
        //new MasterController(getActivity()).getConstants(this);
        return view;
    }

    private void initialise(View view) {
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setSelected(true);
        rvFacility = view.findViewById(R.id.rvFacility);
        //ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        rvFacility.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvFacility.addItemDecoration(itemDecoration);
    }

    public List<FacilityDetailsEntity> getList() {
        int i = 0;
        String baseUrl = "http://www.svrpublicschool.com/";
        List<FacilityDetailsEntity> list = new ArrayList<>();

        list.add(new FacilityDetailsEntity("Sports Facility", getResources().getString(R.string.sports)));
        list.add(new FacilityDetailsEntity("Transport Facility", getResources().getString(R.string.transport_facility)));
        list.add(new FacilityDetailsEntity("Library", getResources().getString(R.string.library)));
        list.add(new FacilityDetailsEntity("Computer Labs", getResources().getString(R.string.computer_lab)));
        list.add(new FacilityDetailsEntity("Smart Classes", getResources().getString(R.string.smart_classes)));
        //list.add(new FacilityDetailsEntity("Sports", ""));
        //list.add(new FacilityDetailsEntity("Clothing Items for Boarding", ""));

        return list;
    }

    public void refreshAdapter(List<FacilityDetailsEntity> list) {
        this.facilityDetailsEntities = list;
        facilityAdapter.notifyDataSetChanged();
    }
}
