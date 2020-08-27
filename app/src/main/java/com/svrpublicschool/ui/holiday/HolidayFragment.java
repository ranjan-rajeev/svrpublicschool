package com.svrpublicschool.ui.holiday;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.ui.study.AddClassBottomSheetDialog;
import com.svrpublicschool.ui.study.adapter.ClassListAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HolidayFragment extends BaseFragment implements View.OnClickListener {

    View view;
    RecyclerView rvClassList;
    FloatingActionButton fab;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPrefManager sharedPrefManager;
    List<ClassEntity> classEntities = new ArrayList<>();
    ClassListAdapter classListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_study, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(this.getActivity());
        classListAdapter = new ClassListAdapter(this.getActivity(), classEntities);
        initialise(view);
        hideShowAdminFeatures();
        setListener();
        if (Utility.shoulFetchFromServer(this.getActivity(), Constants.SHD_PRF_CLASSES_VERSION, Constants.DB_CLASSES)) {
            getClassesList();
        } else {
            new LoadLocalClassList().execute();
        }

        return view;
    }

    private void getClassesList() {
        Logger.d("Fetching class list from server");
        DatabaseReference classRef = database.getReference("classes");
        classRef.orderByChild("pos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classEntities.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ClassEntity classEntity = snapshot.getValue(ClassEntity.class);
                        classEntities.add(classEntity);
                        Logger.d("" + classEntity.getfId());
                    } catch (Exception e) {
                        Logger.d("Unable to parse");
                    }
                }
                new StoreClassList(classEntities).execute();
                classListAdapter.updateList(classEntities);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void initialise(View view) {
        fab = view.findViewById(R.id.fab);
        rvClassList = view.findViewById(R.id.rvClassList);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.margin_8);
        rvClassList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvClassList.addItemDecoration(itemDecoration);
        rvClassList.setAdapter(classListAdapter);
    }

    public void hideShowAdminFeatures() {
        if (Constants.USER_TYPE == 1) {
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
                openAddClassBottomSheet();
                break;
        }
    }

    public void openAddClassBottomSheet() {

        AddClassBottomSheetDialog addClassBottomSheetDialog = new AddClassBottomSheetDialog(HolidayFragment.this.getActivity(), result -> {
            if (result == 1) { //success
                Toast.makeText(HolidayFragment.this.getActivity(), "Task Completed Success", Toast.LENGTH_SHORT).show();
            } else { // Failure
                Toast.makeText(HolidayFragment.this.getActivity(), "Task Failed", Toast.LENGTH_SHORT).show();
            }
        });
        addClassBottomSheetDialog.show(getFragmentManager(), "Opening add class bottom sheet");
    }

    public class LoadLocalClassList extends AsyncTask<Void, Void, List<ClassEntity>> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<ClassEntity> doInBackground(Void... voids) {
            Gson gson = new Gson();
            String jsonArray;
            String prefData = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_CLASSES_DATA, "");
            if (prefData.equals("")) {
                Logger.d("Binding list from default JSON!");
                jsonArray = Utility.readAssetsJsonFile(HolidayFragment.this.getActivity(), "classes.json");
            } else {
                Logger.d("Binding list from Preference!");
                jsonArray = prefData;
            }
            Type listType = new TypeToken<List<ClassEntity>>() {
            }.getType();
            return gson.fromJson(jsonArray, listType);
        }

        @Override
        protected void onPostExecute(List<ClassEntity> classEntityList) {
            super.onPostExecute(classEntityList);
            if (classEntityList != null) {
                classEntities = classEntityList;
                Logger.d("" + classEntityList.size());
                classListAdapter.updateList(classEntities);
            }
        }
    }

    private class StoreClassList extends AsyncTask<Void, Void, Void> {
        List<ClassEntity> classEntities;

        StoreClassList(List<ClassEntity> classEntities) {
            this.classEntities = classEntities;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Gson gson = new Gson();
            sharedPrefManager.putStringValueForKey(Constants.SHD_PRF_CLASSES_DATA, gson.toJson(classEntities));
            sharedPrefManager.putIntegerValueForKey(Constants.SHD_PRF_CLASSES_VERSION, Utility.getServerVersionExcel( Constants.DB_CLASSES));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
