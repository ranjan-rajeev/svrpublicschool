package com.svrpublicschool.ui.faculty;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.ItemOffsetDecoration;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.firebase.FirebaseHelper;
import com.svrpublicschool.models.FacultyDetailsEntity;

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
        new BindFaculty().execute();
        return view;
    }

    private void initialise(View view) {
        tvAdmisson = view.findViewById(R.id.tvAdmisson);
        tvAdmisson.setText(FirebaseHelper.getScrollMessage());
        tvAdmisson.setSelected(true);
        rvFaculty = view.findViewById(R.id.rvFaculty);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        rvFaculty.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvFaculty.addItemDecoration(itemDecoration);
    }

    public class BindFaculty extends AsyncTask<Void, Void, List<FacultyDetailsEntity>> {

        @Override
        protected List<FacultyDetailsEntity> doInBackground(Void... voids) {

            try {
                Gson gson = new Gson();
                List<FacultyDetailsEntity> facultyDetailsEntities = gson.fromJson(FirebaseHelper.getFacultyList(), new TypeToken<List<FacultyDetailsEntity>>() {
                }.getType());
                return facultyDetailsEntities;
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(List<FacultyDetailsEntity> list) {
            super.onPostExecute(list);
            if (!Utility.isListEmpty(list)) {
                facultyAdapter = new FacultyAdapter(FacultyFragment.this, list);
                rvFaculty.setAdapter(facultyAdapter);
            }
        }
    }
}
