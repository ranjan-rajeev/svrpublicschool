package com.svrpublicschool.ui.study;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.svrpublicschool.BaseFragment;
import com.svrpublicschool.ItemOffsetDecoration;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.models.UserEntity;
import com.svrpublicschool.ui.study.adapter.SubjectListAdapter;
import com.svrpublicschool.ui.main.MainActivity;
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.models.SubjectEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SubjectFragment extends BaseFragment implements View.OnClickListener {

    MainActivity activity;
    View view;
    RecyclerView rvClassList;
    FloatingActionButton fab;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPrefManager sharedPrefManager;
    List<SubjectEntity> subjectEntities = new ArrayList<>();
    SubjectListAdapter subjectListAdapter;
    ClassEntity classEntity;
    TextView tvTitle;
    UserEntity userEntity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.activity = (MainActivity) activity;
        } catch (Exception e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_subject, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(this.getActivity());

        initialise(view);
        try {
            classEntity = (ClassEntity) getArguments().getSerializable("CLASSENTITY");
            tvTitle.setText("Select Subject  - " + classEntity.getClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        subjectListAdapter = new SubjectListAdapter(this.getActivity(), subjectEntities, classEntity);

        hideShowAdminFeatures();
        setListener();
        if (Utility.shoulFetchFromServer(this.getActivity(), Constants.SHD_PRF_SUBJECT_VERSION, Constants.DB_SUBJECT)) {
            getSubjectList();
        } else {
            new LoadLocalClassList().execute();
        }
        return view;
    }

    private void getSubjectList() {
        Logger.d("Fetching class list from server");
        DatabaseReference classRef = database.getReference(Constants.DB_SUBJECT);//.child(classEntity.getfId());
        classRef.orderByChild("pos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectEntities.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        for (DataSnapshot subject : snapshot.getChildren()) {
                            SubjectEntity subjectEntity = subject.getValue(SubjectEntity.class);
                            subjectEntities.add(subjectEntity);
                            Logger.d("" + classEntity.getfId());
                        }
                    } catch (Exception e) {
                        Logger.d("Unable to parse");
                    }
                }
                new StoreClassList(subjectEntities).execute();
                new BindFilteredList(subjectEntities).execute();
                //subjectListAdapter.updateList(subjectEntities);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void initialise(View view) {
        tvTitle = view.findViewById(R.id.ivPreview);
        fab = view.findViewById(R.id.fab);
        rvClassList = view.findViewById(R.id.rvClassList);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.margin_8);
        rvClassList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvClassList.addItemDecoration(itemDecoration);
        rvClassList.setAdapter(subjectListAdapter);
        if (activity != null)
            userEntity = activity.getUser();
    }

    public void hideShowAdminFeatures() {
        if (userEntity == null) {
            fab.hide();
            return;
        }
        if (userEntity.getType().equalsIgnoreCase("admin")) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    private void setListener() {
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                openSubjectBottomSheet();
                break;
        }
    }

    public void openSubjectBottomSheet() {

        AddSubjectBottomSheetDialog addClassBottomSheetDialog = new AddSubjectBottomSheetDialog(classEntity, SubjectFragment.this.getActivity(), result -> {
            if (result == 1) { //success
                Toast.makeText(SubjectFragment.this.getActivity(), "Task Completed Success", Toast.LENGTH_SHORT).show();
            } else { // Failure
                Toast.makeText(SubjectFragment.this.getActivity(), "Task Failed", Toast.LENGTH_SHORT).show();
            }
        });
        addClassBottomSheetDialog.show(getFragmentManager(), "Opening add subject bottom sheet");
    }

    public class LoadLocalClassList extends AsyncTask<Void, Void, List<SubjectEntity>> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<SubjectEntity> doInBackground(Void... voids) {
            Gson gson = new Gson();
            String jsonArray;
            String prefData = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_SUBJECT_DATA, "");
            if (prefData.equals("")) {
                Logger.d("Binding list from default JSON!");
                jsonArray = Utility.readAssetsJsonFile(SubjectFragment.this.getActivity(), "subject.json");
            } else {
                Logger.d("Binding list from Preference!");
                jsonArray = prefData;
            }
            Type listType = new TypeToken<List<SubjectEntity>>() {
            }.getType();
            return gson.fromJson(jsonArray, listType);
        }

        @Override
        protected void onPostExecute(List<SubjectEntity> classEntityList) {
            super.onPostExecute(classEntityList);
            if (classEntityList != null) {
                new BindFilteredList(classEntityList).execute();
                /*subjectEntities = classEntityList;
                Logger.d("" + classEntityList.size());
                subjectListAdapter.updateList(subjectEntities);*/
            }
        }
    }

    private class StoreClassList extends AsyncTask<Void, Void, Void> {
        List<SubjectEntity> classEntities;

        StoreClassList(List<SubjectEntity> classEntities) {
            this.classEntities = classEntities;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Gson gson = new Gson();
            sharedPrefManager.putStringValueForKey(Constants.SHD_PRF_SUBJECT_DATA, gson.toJson(classEntities));
            sharedPrefManager.putIntegerValueForKey(Constants.SHD_PRF_SUBJECT_VERSION, Utility.getServerVersionExcel(Constants.DB_SUBJECT));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class BindFilteredList extends AsyncTask<Void, Void, List<SubjectEntity>> {

        List<SubjectEntity> subjectEntities;
        List<SubjectEntity> filteredList = new ArrayList<>();

        BindFilteredList(List<SubjectEntity> subjectEntities) {
            this.subjectEntities = subjectEntities;
        }

        @Override
        protected List<SubjectEntity> doInBackground(Void... voids) {
            for (SubjectEntity subjectEntity : subjectEntities) {
                if (subjectEntity.getParentId().equals(classEntity.getfId())) {
                    filteredList.add(subjectEntity);
                }
            }

            return filteredList;
        }

        @Override
        protected void onPostExecute(List<SubjectEntity> filteredList) {
            super.onPostExecute(filteredList);
            if (filteredList != null) {
                Logger.d("" + filteredList.size());
                rvClassList.setAdapter(new SubjectListAdapter(activity, filteredList, classEntity));
                //subjectListAdapter.updateList(filteredList);
            }
        }
    }

}
