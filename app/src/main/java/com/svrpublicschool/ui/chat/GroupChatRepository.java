package com.svrpublicschool.ui.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.database.AppDatabase;
import com.svrpublicschool.database.DatabaseClient;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.GroupDetailsEntity;
import com.svrpublicschool.models.UserEntity;

import java.util.ArrayList;
import java.util.List;

import static com.svrpublicschool.Util.Constants.FIREBASE_PAGE_SIZE;
import static com.svrpublicschool.Util.Constants.PAGE_SIZE;

public class GroupChatRepository {

    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private SharedPrefManager sharedPrefManager;
    private AppDatabase appDatabase;
    private GroupDetailsEntity groupDetailsEntity;
    private IChatListener iChatListener;
    private UserEntity loggedinUser;

    private boolean isRealTimeChatEnable = false, isUploadEnabled = false;

    public static Query realTimeQuery, initialQuery, lastQuery, beforeQuery;

    public GroupChatRepository(Context context, GroupDetailsEntity groupDetailsEntity, IChatListener iChatListener) {
        this.iChatListener = iChatListener;
        this.context = context;
        this.groupDetailsEntity = groupDetailsEntity;
        sharedPrefManager = SharedPrefManager.getInstance(context);
        appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        new GetLoginUser().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void getInitialList() {

        //new LoadLocalChat(0).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        //new GetLatestChatKey(0).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        /*if (Utility.shoulFetchFromServer(context, groupDetailsEntity.getFid(), groupDetailsEntity.getFid())) {

        }*/
        getInitailChatFirebase();
    }

    public void getChatList(int page) {
        new LoadLocalChat(page).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void storeChatLocalDb(ChatEntity chatEntity) {
        new StoreChat(chatEntity).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void getAllChatAfterKeyFirebase(String lastKey) {
        Logger.d("Load chat from firebase After :" + lastKey);
        if (lastQuery == null) {
            DatabaseReference lastRef = database.getReference(Constants.DB_GROUP_CHAT).child(groupDetailsEntity.getFid());
            lastQuery = lastRef.orderByKey().startAt(lastKey);
            lastQuery.addListenerForSingleValueEvent(lastKeyValueEventListener);
        }

    }

    ValueEventListener lastKeyValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<ChatEntity> list = new ArrayList<>();
            int temp = 0;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                try {
                    if (temp == 0) {
                        temp++;
                        continue;
                    }
                    ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                    list.add(chatEntity);
                    Logger.d("After key fetch  :" + chatEntity.toString());
                } catch (Exception e) {
                    Logger.d("Unable to parse");
                }
            }
            if (list.size() > 0) {
                if (list.size() < PAGE_SIZE) {
                    iChatListener.lastPage(true);
                }
                new StoreChatList(list).execute();
                iChatListener.addListItem(list, false);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            iChatListener.onError(databaseError.getMessage());
            Logger.d(databaseError.getMessage());
        }
    };

    ValueEventListener beforelastKeyValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<ChatEntity> list = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                try {
                    ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                    list.add(chatEntity);
                    Logger.d("Before key fetch  :" + chatEntity.toString());
                } catch (Exception e) {
                    Logger.d("Unable to parse");
                }
            }
            if (list.size() > 0) {
                list.remove(list.size() - 1);
                if (list.size() < PAGE_SIZE) {
                    iChatListener.lastPage(true);
                }
                new StoreChatList(list).execute();
                iChatListener.addListItem(list, true);
            }else {
                iChatListener.onError("Empty List");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            iChatListener.onError(databaseError.getMessage());
            Logger.d(databaseError.getMessage());
        }
    };

    private void getInitailChatFirebase() {
        Logger.d("Load initial list from firebase max: " + FIREBASE_PAGE_SIZE);
        //if (initialQuery == null) {
            DatabaseReference initialRef = database.getReference(Constants.DB_GROUP_CHAT).child(groupDetailsEntity.getFid());
            initialQuery = initialRef.orderByKey().limitToLast(FIREBASE_PAGE_SIZE);
            initialQuery.addValueEventListener(initialValueEventListener);
        //}
    }

    public void getChatBeforeKeyFirebase(String key) {
        if (key == null)
            return;
        Logger.d("Load chat from firebase Before :" + key);
        //if (beforeQuery == null) {
            DatabaseReference lastRef = database.getReference(Constants.DB_GROUP_CHAT).child(groupDetailsEntity.getFid());
            beforeQuery = lastRef.orderByKey().endAt(key).limitToLast(FIREBASE_PAGE_SIZE);
            beforeQuery.addListenerForSingleValueEvent(beforelastKeyValueEventListener);
        //}
    }

    ValueEventListener initialValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Logger.d("Initial chat callback firebase ");
            List<ChatEntity> list = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                try {
                    ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                    list.add(chatEntity);
                    Logger.d("initial chat  : " + chatEntity.toString());
                } catch (Exception e) {
                    Logger.d("Unable to parse");
                }
            }
            if (list.size() > 0) {
                new StoreChatList(list).execute();
                iChatListener.addListItem(list, true);
            }else {
                iChatListener.onError("Empty List");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            iChatListener.onError(databaseError.getMessage());
            Logger.d(databaseError.getMessage());
        }
    };

    private void setRealTimeChatListener() {
        if (isRealTimeChatEnable) {
            Logger.d("Adding realtime fetch for one element ");
            if (realTimeQuery == null) {
                DatabaseReference realTimeRef = database.getReference(Constants.DB_GROUP_CHAT);
                realTimeQuery = realTimeRef.child(groupDetailsEntity.getFid()).orderByKey().limitToLast(1);
                realTimeQuery.addChildEventListener(realTimeChildEventListener);
            }
        } else {
            if (realTimeQuery != null) {
                realTimeQuery.removeEventListener(realTimeChildEventListener);
            }
        }
    }

    ChildEventListener realTimeChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Logger.d("Realtime one data fetch called");
            try {
                ChatEntity chatEntity = dataSnapshot.getValue(ChatEntity.class);
                Logger.d("Child Fetched Realtime  : " + chatEntity.toString());
                new CheckStoreChat(chatEntity).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } catch (Exception e) {
                Logger.d("Unable to parse");
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public boolean pushChatToFirebAse(ChatEntity chatEntity) {
        if (!Utility.isNetworkConnected(context)) {
            return false;
        }
        Logger.d("Updating chat to firebase server" + chatEntity.toString());

        DatabaseReference pushRef = database.getReference(Constants.DB_GROUP_CHAT).child(chatEntity.getReceiver()).push();
        chatEntity.setStatus(Constants.READ);
        chatEntity.setfId(pushRef.getKey());
        pushRef.setValue(chatEntity);
        new StoreChat(chatEntity).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        return true;
    }

    public boolean deleteChatFirebase(ChatEntity chatEntity) {
        database.getReference(Constants.DB_GROUP_CHAT).
                child(chatEntity.getReceiver()).
                child(chatEntity.getfId()).
                removeValue();
        return true;
    }

    public void deleteChatLocalDb(ChatEntity chatEntity) {
        new DeleteChat(chatEntity).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    class GetLoginUser extends AsyncTask<Void, Void, UserEntity> {
        @Override
        protected UserEntity doInBackground(Void... voids) {
            String allString = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            Gson gson = new Gson();
            UserEntity senderEntity = gson.fromJson(allString, UserEntity.class);
            if (Utility.getServerVersionExcel(Constants.SHD_PRF_REALTIME_ENABLED) == 1) {
                isRealTimeChatEnable = true;
                //setRealTimeChatListener();
            }
            if (Utility.getServerVersionExcel(Constants.SHD_PRF_UPLOAD_ENABLED) == 1) {
                isUploadEnabled = true;
            }
            return senderEntity;
        }

        @Override
        protected void onPostExecute(UserEntity entity) {
            super.onPostExecute(entity);
            if (entity != null) {
                loggedinUser = entity;
                ((GroupChatActivity) context).setSenderEntity(entity);
            }
        }
    }

    ValueEventListener addToBottom = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<ChatEntity> list = new ArrayList<>();
            int temp = 0;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                try {
                    if (temp == 0) {
                        temp++;
                        continue;
                    }
                    ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                    list.add(chatEntity);
                    //Logger.d(chatEntity.toString());
                } catch (Exception e) {
                    Logger.d("Unable to parse");
                }
            }
            /*if (list.size() % PAGE_SIZE != 0) {
                iChatListener.lastPage(true);
            }*/
            if (list.size() > 0) {
                new StoreChatList(list).execute();
                iChatListener.addListItem(list, false);
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            iChatListener.onError(error.getMessage());
            Logger.d(error.getMessage());
        }
    };

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<ChatEntity> list = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                try {
                    ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                    list.add(chatEntity);
                    // Logger.d(chatEntity.toString());
                } catch (Exception e) {
                    Logger.d("Unable to parse");
                }
            }
            /*if (list.size() % PAGE_SIZE != 0) {
                iChatListener.lastPage(true);
            }*/
            if (list.size() > 0) {
                new StoreChatList(list).execute();
                iChatListener.addListItem(list, true);
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            iChatListener.onError(error.getMessage());
            Logger.d(error.getMessage());
        }
    };

    private class GetLatestChatKey extends AsyncTask<Void, Void, List<ChatEntity>> {
        int page = 0;

        GetLatestChatKey(int page) {
            this.page = page;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<ChatEntity> doInBackground(Void... voids) {
            try {
                Logger.d("Fetching list from room db");
                return appDatabase.chatEntityDao().getGroupChatWithPage(groupDetailsEntity.getFid(), page * PAGE_SIZE, PAGE_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ChatEntity> list) {
            super.onPostExecute(list);
            if (list != null && list.size() > 0) {
                getAllChatAfterKeyFirebase(list.get(list.size() - 1).getfId());
            } else {
                if (page == 0) {
                    getInitailChatFirebase();
                }
            }
        }
    }

    private class LoadLocalChat extends AsyncTask<Void, Void, List<ChatEntity>> {
        int page = 0;

        LoadLocalChat(int page) {
            this.page = page;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<ChatEntity> doInBackground(Void... voids) {
            try {
                Logger.d("Fetching list from room db");
                return appDatabase.chatEntityDao().getGroupChatWithPage(groupDetailsEntity.getFid(), page * PAGE_SIZE, PAGE_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ChatEntity> list) {
            super.onPostExecute(list);
            if (!Utility.isListEmpty(list)) {
                Logger.d("Size  : " + list.size());
                if (list.size() < PAGE_SIZE) {
                    iChatListener.lastPage(true);
                }
                iChatListener.addListItem(list, true);
                if (list.size() > 0) {
                    getAllChatAfterKeyFirebase(list.get(list.size() - 1).getfId());
                }
            } else {
                iChatListener.onError("No chat found !");
                getInitailChatFirebase();
            }
        }
    }

    private class StoreChatList extends AsyncTask<Void, Void, Void> {
        List<ChatEntity> chatEntities;

        StoreChatList(List<ChatEntity> chatEntities) {
            this.chatEntities = chatEntities;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Logger.d("Storing chat list to local database" + chatEntities.size());
            appDatabase.chatEntityDao().insertChat(chatEntities);
            sharedPrefManager.putIntegerValueForKey(groupDetailsEntity.getFid(), Utility.getServerVersionExcel(groupDetailsEntity.getFid()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class StoreChat extends AsyncTask<Void, Void, Void> {
        ChatEntity chatEntity;

        StoreChat(ChatEntity chatEntity) {
            this.chatEntity = chatEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Logger.d("Storing chat : " + chatEntity.toString());
            appDatabase.chatEntityDao().insertChat(chatEntity);
            sharedPrefManager.putIntegerValueForKey(groupDetailsEntity.getFid(), Utility.getServerVersionExcel(groupDetailsEntity.getFid()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    class DeleteChat extends AsyncTask<Void, Void, Void> {
        ChatEntity chatEntity;

        DeleteChat(ChatEntity chatEntity) {
            this.chatEntity = chatEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.chatEntityDao().deleteChat(chatEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void entity) {
            super.onPostExecute(entity);
        }
    }

    private class CheckStoreChat extends AsyncTask<Void, Void, Integer> {
        ChatEntity chatEntity;

        CheckStoreChat(ChatEntity chatEntity) {
            this.chatEntity = chatEntity;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            if (appDatabase.chatEntityDao().getChat(chatEntity.getPkId()) == null) {
                Logger.d("Storing chat : " + chatEntity.toString());
                appDatabase.chatEntityDao().insertChat(chatEntity);
                sharedPrefManager.putIntegerValueForKey(groupDetailsEntity.getFid(), Utility.getServerVersionExcel(groupDetailsEntity.getFid()));
                return 1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid == 1) {
                iChatListener.addItem(chatEntity);
            }
        }
    }

    public UserEntity getLoggedinUser() {
        return loggedinUser;
    }

    public boolean isRealTimeChatEnable() {
        return isRealTimeChatEnable;
    }

    public boolean isUploadEnabled() {
        return isUploadEnabled;
    }

    public void removeAllListener() {
        if (initialQuery != null) {
            initialQuery.removeEventListener(initialValueEventListener);
            initialValueEventListener = null;
            initialQuery = null;
        }

        if (realTimeQuery != null) {
            realTimeQuery.removeEventListener(realTimeChildEventListener);
            realTimeQuery = null;
            realTimeChildEventListener = null;
        }

        if (lastQuery != null) {
            lastQuery.removeEventListener(lastKeyValueEventListener);
            lastQuery = null;
            lastKeyValueEventListener = null;
        }
        if (beforeQuery != null) {
            beforeQuery.removeEventListener(beforelastKeyValueEventListener);
            beforeQuery = null;
            beforelastKeyValueEventListener = null;
        }
        database = null;
    }
}
