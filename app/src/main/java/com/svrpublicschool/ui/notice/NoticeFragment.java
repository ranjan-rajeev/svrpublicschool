package com.svrpublicschool.ui.notice;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import com.svrpublicschool.database.DatabaseClient;
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.models.NoticeEntity;
import com.svrpublicschool.models.UserEntity;
import com.svrpublicschool.ui.main.MainActivity;
import com.svrpublicschool.ui.study.AddClassBottomSheetDialog;
import com.svrpublicschool.ui.study.adapter.ClassListAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NoticeFragment extends BaseFragment implements View.OnClickListener {

    View view;
    RecyclerView rvNoticeList;
    FloatingActionButton fab;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPrefManager sharedPrefManager;
    List<NoticeEntity> noticeEntities = new ArrayList<>();
    NoticeListAdapter noticeListAdapter;
    UserEntity userEntity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notice, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(this.getActivity());
        noticeListAdapter = new NoticeListAdapter(this.getActivity(), noticeEntities);
        initialise(view);
        new GetLoginUser().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        hideShowAdminFeatures();
        setListener();
        getNoticeList();
        return view;
    }

    private void getNoticeList() {
        Logger.d("Fetching notice list from server");
        showDialog();
        DatabaseReference classRef = database.getReference(Constants.DB_NOTICE);
        classRef.orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cancelDialog();
                noticeEntities.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        NoticeEntity noticeEntity = snapshot.getValue(NoticeEntity.class);
                        noticeEntities.add(0, noticeEntity);
                        Logger.d("" + noticeEntity.getFid());
                    } catch (Exception e) {
                        Logger.d("Unable to parse");
                    }
                }
                new StoreNoticeList(noticeEntities).execute();
                noticeListAdapter.updateList(noticeEntities);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                cancelDialog();
                Logger.d(error.getMessage());
            }
        });
    }

    private void initialise(View view) {
        fab = view.findViewById(R.id.fab);
        rvNoticeList = view.findViewById(R.id.rvNoticeList);
        rvNoticeList.setLayoutManager(new LinearLayoutManager(this.getActivity(), RecyclerView.VERTICAL, false));
        rvNoticeList.setAdapter(noticeListAdapter);
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
                openAddNoticeBottomSheet();
                break;
        }
    }

    public void openAddNoticeBottomSheet() {

        AddNoticeBottomSheetDialog addNoticeBottomSheetDialog = new AddNoticeBottomSheetDialog(NoticeFragment.this.getActivity(), result -> {

            if (result != null) { //success
                Toast.makeText(NoticeFragment.this.getActivity(), "Notice Added Success", Toast.LENGTH_SHORT).show();
                noticeEntities.add(result);
                noticeListAdapter.updateList(noticeEntities);
                new StoreNotice(result).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else { // Failure
                Toast.makeText(NoticeFragment.this.getActivity(), "Task Failed", Toast.LENGTH_SHORT).show();
            }
        });
        addNoticeBottomSheetDialog.show(getFragmentManager(), "Opening add class bottom sheet");
    }

    class StoreNoticeList extends AsyncTask<Void, Void, Void> {
        List<NoticeEntity> list;

        StoreNoticeList(List<NoticeEntity> list) {
            this.list = list;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Logger.d("Storing user list to local database" + list.size());
            DatabaseClient.getInstance(NoticeFragment.this.getActivity()).getAppDatabase().noticeDao().insertList(list);
            sharedPrefManager.putIntegerValueForKey(Constants.SHD_PRF_NOTICE_VERSION, Utility.getServerVersionExcel(Constants.SHD_PRF_NOTICE_VERSION));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    class StoreNotice extends AsyncTask<Void, Void, Void> {
        NoticeEntity entity;

        StoreNotice(NoticeEntity list) {
            this.entity = list;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Logger.d("Storing Notice to local database");
            DatabaseClient.getInstance(NoticeFragment.this.getActivity()).getAppDatabase().noticeDao().insert(entity);
            //sharedPrefManager.putIntegerValueForKey(Constants.SHD_PRF_NOTICE_VERSION, Utility.getServerVersionExcel(Constants.SHD_PRF_NOTICE_VERSION));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    class FetchNoticeList extends AsyncTask<Void, Void, List<NoticeEntity>> {

        @Override
        protected List<NoticeEntity> doInBackground(Void... voids) {
            Logger.d("Fetching notice list from local database");
            return DatabaseClient.getInstance(NoticeFragment.this.getActivity()).getAppDatabase().noticeDao().getAll();
        }

        @Override
        protected void onPostExecute(List<NoticeEntity> list) {
            super.onPostExecute(list);
            if (list != null) {
                noticeEntities.addAll(list);
                Logger.d("" + list.size());
                noticeListAdapter.updateList(noticeEntities);
            }
        }
    }

    class GetLoginUser extends AsyncTask<Void, Void, UserEntity> {
        @Override
        protected UserEntity doInBackground(Void... voids) {
            String allString = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            Gson gson = new Gson();
            UserEntity userEntity = gson.fromJson(allString, UserEntity.class);
            return userEntity;
        }

        @Override
        protected void onPostExecute(UserEntity entity) {
            super.onPostExecute(userEntity);
            if (entity != null) {
                userEntity = entity;
                hideShowAdminFeatures();
            }
        }
    }
}
