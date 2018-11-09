package com.svrpublicschool.fragments.faculty;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.ItemOffsetDecoration;
import com.svrpublicschool.R;
import com.svrpublicschool.models.FacultyDetailsEntity;

import java.util.ArrayList;
import java.util.List;

public class FacultyFragment extends BaseFragment {

    View view;
    FacultyAdapter facultyAdapter;
    RecyclerView rvFaculty;
    TextView tvAdmisson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_faculty, container, false);
        initialise(view);

        //setListener();

        facultyAdapter = new FacultyAdapter(this, getImageList());
        rvFaculty.setAdapter(facultyAdapter);
        //new MasterController(getActivity()).getConstants(this);
        return view;
    }

    private void initialise(View view) {
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setSelected(true);
        rvFaculty = view.findViewById(R.id.rvFaculty);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        rvFaculty.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvFaculty.addItemDecoration(itemDecoration);
    }

    public List<FacultyDetailsEntity> getImageList() {
        int i = 0;
        String baseUrl = "http://www.svrpublicschool.com/images/team/";
        List<FacultyDetailsEntity> list = new ArrayList<>();
        list.add(new FacultyDetailsEntity(++i, "Rajesh Roushan", "Director", baseUrl + "rajesh-kumar.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Anupma Sinha", "Principle", baseUrl + "anupma-sinha.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Ajay Devgan", "Teacher(Maths/Hindi)", baseUrl + "ajay-devgan.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Usha Devi", "Supervisor", baseUrl + "usha-devi.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Madan Prasad", "Teacher(Maths)", baseUrl + "madan-prasad.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Monali Majunder", "Teacher(S.S.T)", baseUrl + "monali-majunder.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Prabhatsen Guptak", "Teacher", baseUrl + "prabhatsen-gupta.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Rakesh Ranjan", "Teacher(English)", baseUrl + "rakesh-ranjan.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Shashi Ranjan", "Teacher", baseUrl + "shashi-ranjan.jpg"));

        list.add(new FacultyDetailsEntity(++i, "Abhay Prasad", "Teacher(Comp)", "https://c1.staticflickr.com/5/4839/43979530940_d13e19b053_o.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Poonam Kumari", "Teacher", "https://c1.staticflickr.com/5/4837/45746627852_7973b585ee_o.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Pratima Kumari", "Teacher", "https://c1.staticflickr.com/5/4871/43979531120_96b720ea95_o.jpg"));


        list.add(new FacultyDetailsEntity(++i, "Sanju Kumari", "Teacher", baseUrl + "smita-kumari.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Smita Yadav", "Teacher", baseUrl + "smita-yadav.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Subir Chatterjee", "Accountant", baseUrl + "sudhir-chatterjee.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Sudhir Kumar Singh", "Teacher(Comp)", baseUrl + "sudhir-kr-singh.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Geeta Sinha", "Teacher", baseUrl + "geeta-sinha.jpg"));

        list.add(new FacultyDetailsEntity(++i, "Manoj Sharma", "Teacher(English)", baseUrl + "manoj-sharma.jpg"));
        list.add(new FacultyDetailsEntity(++i, "Subhrato Kumar Ghosh", "Teacher(Science)", baseUrl + "subhrato-ghosh.jpg"));


        return list;
    }
}
