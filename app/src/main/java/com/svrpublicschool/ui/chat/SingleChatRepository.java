package com.svrpublicschool.ui.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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
import com.svrpublicschool.models.UserEntity;

import java.util.ArrayList;
import java.util.List;

import static com.svrpublicschool.Util.Constants.PAGE_SIZE;

public class SingleChatRepository {

    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    SharedPrefManager sharedPrefManager;
    AppDatabase appDatabase;
    UserEntity senderEntity, receiverEntity;
    IDataSetChanged iDataSetChanged;

    String chatRefKey = "", lastAccessKey = "";
    boolean isRealTimeChatEnable = false, isUploadEnabled = false, isSendMsgAllowed = false;

    public SingleChatRepository(Context context, UserEntity receiverEntity, IDataSetChanged iDataSetChanged) {
        this.receiverEntity = receiverEntity;
        this.iDataSetChanged = iDataSetChanged;
        this.context = context;
        sharedPrefManager = SharedPrefManager.getInstance(context);
        appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        new GetLoginUser().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void getChatList(int page) {
        new LoadLocalChat(page).execute();
    }

    class GetLoginUser extends AsyncTask<Void, Void, UserEntity> {
        @Override
        protected UserEntity doInBackground(Void... voids) {
            String allString = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            Gson gson = new Gson();
            UserEntity senderEntity = gson.fromJson(allString, UserEntity.class);
            isSendMsgAllowed = true;
            if (Utility.getServerVersionExcel(Constants.SHD_PRF_REALTIME_ENABLED) == 1) {
                isRealTimeChatEnable = true;
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
                senderEntity = entity;
            }
        }
    }

    private void getChatFromFirebase(String lastKey) {
        if (chatRefKey.equals(""))
            chatRefKey = getSingleChatRef(senderEntity.getfId(), receiverEntity.getfId());
        Logger.d(chatRefKey);
        if (chatRefKey != null) {
            DatabaseReference classRef = database.getReference(Constants.DB_CHAT).child(chatRefKey);
            Query query;
            if (lastKey.equals("")) {
                query = classRef.orderByKey().limitToLast(PAGE_SIZE);
            } else {
                query = classRef.orderByKey().endAt(lastKey).limitToLast(PAGE_SIZE);
            }

            if (isRealTimeChatEnable) {
                query.addValueEventListener(valueEventListener);
            } else {
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<ChatEntity> list = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                try {
                    ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                    list.add(chatEntity);
                    Logger.d("" + chatEntity.getfId() + " - " + chatEntity.getMsg());
                } catch (Exception e) {
                    Logger.d("Unable to parse");
                }
            }
            if (list.size() % PAGE_SIZE != 0) {
                iDataSetChanged.lastPage(true);
            }
            if (list.size() > 0) {
                new StoreChatList(list).execute();
                iDataSetChanged.addListItem(list, true);
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            Logger.d(error.getMessage());
        }
    };

    public class LoadLocalChat extends AsyncTask<Void, Void, List<ChatEntity>> {
        int page = 0;

        LoadLocalChat(int page) {
            this.page = page;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<ChatEntity> doInBackground(Void... voids) {
            try {
                Logger.d("Fetching list from room db");
                return appDatabase.chatEntityDao().getSingleChatFirstPage(senderEntity.getfId(), receiverEntity.getfId(), page * PAGE_SIZE, PAGE_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ChatEntity> list) {
            super.onPostExecute(list);
            if (list != null) {
                Logger.d("Size  : " + list.size());
                if (list.size() < PAGE_SIZE) {
                    iDataSetChanged.lastPage(true);
                }
                iDataSetChanged.addListItem(list, true);
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
            sharedPrefManager.putIntegerValueForKey(chatRefKey, Utility.getServerVersionExcel(chatRefKey));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public interface IDataSetChanged {
        void addListItem(List<ChatEntity> list, boolean addItemBottom);

        void lastPage(boolean isLastPage);
    }

    private String getSingleChatRef(String uid1, String uid2) {
        if (uid1 == null || uid2 == null) {
            return null;
        }
        //Check if user1’s id is less than user2's
        if (uid1.compareTo(uid2) < 1) {
            return uid1 + uid2;
        } else {
            return uid2 + uid1;
        }
    }
}
