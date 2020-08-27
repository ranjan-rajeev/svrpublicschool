package com.svrpublicschool.ui.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.svrpublicschool.BaseActivity;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.FileUtility;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.database.AppDatabase;
import com.svrpublicschool.database.DatabaseClient;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.models.UserEntity;
import com.svrpublicschool.models.SubjectEntity;
import com.svrpublicschool.ui.chat.adapter.ChatAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.svrpublicschool.Util.Constants.PAGE_SIZE;
import static com.svrpublicschool.Util.Constants.THRESHOLD;

public class ChatActivity extends BaseActivity implements View.OnClickListener, SingleChatRepository.IDataSetChanged {

    private static final int REQUEST_CODE_PERMISSIONS_FILE_ACCESS = 1002;
    public static final int PICK_IMAGE = 1000;
    public static final int PICK_DOC = 1001;
    ClassEntity classEntity;
    SubjectEntity subjectEntity;
    RecyclerView rvChat;
    LinearLayoutManager linearLayoutManager;
    ChatAdapter chatAdapter;
    List<ChatEntity> chatEntityList = new ArrayList<>();
    AppCompatImageView ivAttach, ivCamera, ivDummy;
    LinearLayout llSend;
    EditText etMessage;
    SharedPrefManager sharedPrefManager;
    AppDatabase appDatabase;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Bitmap tempBitmap;
    UploadTask pdfUploadTask, imageUploadTask, videoUploadTask;

    GetLoginUser getLoginUserTask;
    UserEntity senderEntity, receiverEntity;
    String chatType = "";
    boolean isRealTimeChatEnable = false, isUploadEnabled = false, isSendMsgAllowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntentExtra();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        getLoginUserTask = new GetLoginUser();
        getLoginUserTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        appDatabase = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        init_views();
        setListener();
        if (!checkPermission()) {
            requestPermission();
        }

    }

    private void fetchChatList() {
        if (chatType.equalsIgnoreCase("single")) {

            if (isRealTimeChatEnable) {
                getSinleRealTimeChatFromFirebase();
            } else {
                getSinleOneTimeChatFromFirebase("");
            }

        } else {
            if (Utility.shoulFetchFromServer(this, subjectEntity.getfId(), subjectEntity.getfId())) {
                getChatListFromFirebase();
            } else {
                new LoadGroupChatWithPage(0).execute();
            }
        }
    }

    private void getSinleRealTimeChatFromFirebase() {
        Logger.d("Fetching single chat list from firebase real time  ");
        String ref = getSingleChatRef(senderEntity.getfId(), receiverEntity.getfId());
        Logger.d(ref);
        if (ref != null) {
            DatabaseReference classRef = database.getReference(Constants.DB_CHAT).child(ref);
            classRef.orderByKey().limitToLast(PAGE_SIZE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                            chatEntityList.add(chatEntity);
                            Logger.d("" + chatEntity.getfId());
                        } catch (Exception e) {
                            Logger.d("Unable to parse");
                        }
                    }
                    if (chatEntityList.size() > 0) {
                        new StoreChatList(chatEntityList).execute();
                        updateRecyclerView();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

    private void setListener() {
        ivAttach.setOnClickListener(this);
        llSend.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
    }

    private void init_views() {
        ivDummy = findViewById(R.id.ivDummy);
        ivAttach = findViewById(R.id.ivAttach);
        ivCamera = findViewById(R.id.ivCamera);
        etMessage = findViewById(R.id.etMessage);
        llSend = findViewById(R.id.llSend);
        rvChat = findViewById(R.id.rvChat);

        rvChat.addOnScrollListener(recyclerViewOnScrollListener);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setAdapter(chatAdapter);

    }

    private void getIntentExtra() {
        try {
            chatType = getIntent().getStringExtra(Constants.INTENT_CHAT_TYPE);
            if (chatType.equalsIgnoreCase("single")) {
                receiverEntity = (UserEntity) getIntent().getSerializableExtra(Constants.INTENT_PARAM_USER);
                getSupportActionBar().setTitle(receiverEntity.getName());
            } else {
                classEntity = (ClassEntity) getIntent().getSerializableExtra("CLASS");
                subjectEntity = (SubjectEntity) getIntent().getSerializableExtra("SUBJECT");
                getSupportActionBar().setTitle(subjectEntity.getName() + " - " + classEntity.getClassName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAttach:
                openPdfSelector();
                break;
            case R.id.llSend:
                if (!etMessage.getText().toString().equals("")) {
                    new AddTextChatEntity(etMessage.getText().toString().trim()).execute();
                }
                break;
            case R.id.ivCamera:
                openGallery();
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);

        /*Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);*/
    }

    private void openPdfSelector() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        //intent.setType("pdf");
        startActivityForResult(intent, PICK_DOC);

//        Intent intent;
//        if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
//            intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
//            intent.putExtra("CONTENT_TYPE", "*/*");
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//        } else {
//
//            String[] mimeTypes =
//                    {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
//                            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
//                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
//                            "text/plain",
//                            "application/pdf",
//                            "application/zip", "application/vnd.android.package-archive"};
//            String[] newmimeTypes = {"application/msword", "text/plain", "application/pdf"};
//
//            intent = new Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT
//            intent.setType("*/*");
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, newmimeTypes);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//            startActivityForResult(intent, PICK_DOC);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                Logger.d("Image Selected URI :" + data.getData());
                Logger.d("Name " + data.getData().getLastPathSegment());
                new AddImageChatEntity(data.getData()).execute();
            }

        } else if (requestCode == PICK_DOC) {
            if (resultCode == RESULT_OK) {
                Logger.d("Document Selected URI : " + data.getData());
                Logger.d("Name " + data.getData().getLastPathSegment());
                new AddPdfChatEntity(data.getData()).execute();
            }

        }
    }


    @Override
    public void addListItem(List<ChatEntity> list, boolean addItemBottom) {
        if (addItemBottom) {
            chatEntityList.addAll(0, list);
        } else {
            chatEntityList.addAll(list);
        }
        updateRecyclerView(getScrollPosition());
        isLoading = false;
    }

    @Override
    public void lastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    class AddPdfChatEntity extends AsyncTask<Void, Void, ChatEntity> {
        Uri fileuri;

        public AddPdfChatEntity(Uri filePath) {
            this.fileuri = filePath;
        }

        @Override
        protected ChatEntity doInBackground(Void... voids) {

            try {
                Logger.d("Adding PDF chat ");
                long timeStamp = Calendar.getInstance().getTimeInMillis();
                String srcPath = getPath(fileuri);
                String fileName = timeStamp + "-" + fileuri.getLastPathSegment();
                fileName = fileName.replaceAll("\\s", "");
                String fileDir = FileUtility.getPdfDirectoryName();
                FileUtility.copyFile(srcPath, "" + fileDir + fileName);
                //FileUtility.storePdf(ChatActivity.this, fileDir, fileName);

                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setPkId(timeStamp);
                chatEntity.setChatType(ChatAdapter.TYPE_PDF_RECEIVED);
                chatEntity.setCreatedAt(timeStamp);
                chatEntity.setSender(subjectEntity.getfId());
                chatEntity.setFileName(fileName);
                return chatEntity;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ChatEntity chatEntity) {
            super.onPostExecute(chatEntity);
            if (chatEntity != null) {
                chatEntityList.add(chatEntity);
                Logger.d("Pdf chat added to list " + chatEntity.toString());
                new StoreChat(chatEntity).execute();
            }
        }
    }

    class AddImageChatEntity extends AsyncTask<Void, Void, ChatEntity> {
        Uri fileuri;

        public AddImageChatEntity(Uri filePath) {
            this.fileuri = filePath;
        }

        @Override
        protected ChatEntity doInBackground(Void... voids) {

            try {
                String filePath = getPath(fileuri);
                tempBitmap = FileUtility.getCorrectlyOrientedImage(ChatActivity.this, fileuri, filePath);
                long timeStamp = Calendar.getInstance().getTimeInMillis();
                String fileName = timeStamp + "-" + fileuri.getLastPathSegment();
                fileName = fileName.replaceAll("\\s", "");
                String fileDir = FileUtility.getImageDirectoryName();
                FileUtility.storeBitmap(ChatActivity.this, tempBitmap, fileDir, fileName);
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setPkId(timeStamp);
                chatEntity.setChatType(ChatAdapter.TYPE_IMG_SENT);
                chatEntity.setCreatedAt(Calendar.getInstance().getTimeInMillis());
                chatEntity.setSender(subjectEntity.getfId());
                chatEntity.setFileName(fileName);
                return chatEntity;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ChatEntity chatEntity) {
            super.onPostExecute(chatEntity);
            if (chatEntity != null) {
                chatEntityList.add(chatEntity);
                new StoreChat(chatEntity).execute();
            }
        }
    }

    class AddTextChatEntity extends AsyncTask<Void, Void, ChatEntity> {
        String msg;

        public AddTextChatEntity(String msg) {
            this.msg = msg;
        }

        @Override
        protected ChatEntity doInBackground(Void... voids) {

            try {
                long timeStamp = Calendar.getInstance().getTimeInMillis();
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setPkId(timeStamp);
                chatEntity.setSenName(senderEntity.getName());
                chatEntity.setChatType(ChatAdapter.TYPE_MSG_SENT);
                chatEntity.setCreatedAt(timeStamp);
                chatEntity.setMsg(msg);
                chatEntity.setSender(senderEntity.getfId());
                if (chatType.equals("single"))
                    chatEntity.setReceiver(receiverEntity.getfId());
                return chatEntity;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ChatEntity chatEntity) {
            super.onPostExecute(chatEntity);
            if (chatEntity != null) {
                etMessage.setText("");
                chatEntityList.add(chatEntity);
                updateRecyclerView();
                new StoreChat(chatEntity).execute();
                updateChatEntitytoFirebAse(chatEntity);

            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED /*&& ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ChatActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/},
                REQUEST_CODE_PERMISSIONS_FILE_ACCESS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS_FILE_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Utility.shoulFetchFromServer(this, subjectEntity.getfId(), subjectEntity.getfId())) {
                        getChatListFromFirebase();
                    } else {
                        new LoadGroupChatWithPage(0).execute();
                    }
                } else {
                    //requestPermission();
                }
                break;
        }
    }

    public void updateChatEntitytoFirebAse(ChatEntity chatEntity) {

        /*if (chatType.equalsIgnoreCase("single")) {
            Logger.d("Updating chat to firebase server" + chatEntity.toString());
            DatabaseReference newRef = database.getReference(Constants.DB_CHAT).child(getSingleChatRef(chatEntity.getSender(), chatEntity.getReceiver())).push();
            chatEntity.setfId(newRef.getKey());
            newRef.setValue(chatEntity);
        } else {
            Logger.d("Updating chat to firebase server" + chatEntity.toString());
            DatabaseReference newRef = database.getReference(Constants.DB_GROUP_CHAT).child(chatEntity.getSender()).push();
            chatEntity.setfId(newRef.getKey());
            newRef.setValue(chatEntity);
        }*/

    }

    private void getChatListFromFirebase() {
        Logger.d("Fetching chat list from server");
        DatabaseReference classRef = database.getReference(Constants.DB_CHAT).child(subjectEntity.getfId());
        classRef.orderByChild("pos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatEntityList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                        chatEntityList.add(chatEntity);
                        Logger.d("" + chatEntity.getfId());
                    } catch (Exception e) {
                        Logger.d("Unable to parse");
                    }
                }
                new StoreChatList(chatEntityList).execute();
                updateRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
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
            if (!chatType.equalsIgnoreCase("single"))
                sharedPrefManager.putIntegerValueForKey(subjectEntity.getfId(), Utility.getServerVersionExcel(subjectEntity.getfId()));
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
            Logger.d("Adding chat  to list , local database " + chatEntity.toString());
            appDatabase.chatEntityDao().insertChat(chatEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRecyclerView();
        }
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

    class GetLoginUser extends AsyncTask<Void, Void, UserEntity> {
        @Override
        protected UserEntity doInBackground(Void... voids) {
            String allString = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            Gson gson = new Gson();
            UserEntity senderEntity = gson.fromJson(allString, UserEntity.class);
            if (chatType.equalsIgnoreCase("single") || senderEntity.getType().equalsIgnoreCase("admin")) {
                isSendMsgAllowed = true;
            }
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
                //fetchChatList();
                fetchLocalList();
            }
        }
    }

    public void uploadPdfFileToServer(Uri uri) {
        StorageReference storageRef = storage.getReference().child("pdf/" + uri.getLastPathSegment());
        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("application/pdf")
                .build();
        imageUploadTask = storageRef.putFile(uri, metadata);

        imageUploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Logger.d("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Logger.d("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Logger.d("" + exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Logger.d("Upload is Finished" + storageRef.getDownloadUrl());
            }
        });


    }

    public void downloadFileFromServer(String url) {
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl("gs://svr-public-school-4064b.appspot.com/pdf/Gratuity Form F nomination.PDF");
        final long ONE_MEGABYTE = 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void updateRecyclerView(int scrollPosition) {
        chatAdapter = new ChatAdapter(ChatActivity.this, chatEntityList, senderEntity);
        rvChat.setAdapter(chatAdapter);
        rvChat.scrollToPosition(scrollPosition);

    }

    public int getScrollPosition() {
        if (currentPage == 0) {
            return PAGE_SIZE - 1;
        } else {
            if (chatEntityList.size() % PAGE_SIZE == 0) {
                return PAGE_SIZE + THRESHOLD + linearLayoutManager.getChildCount() - 2;
            } else {
                return (chatEntityList.size() % PAGE_SIZE) + (linearLayoutManager.getChildCount() - 2);
            }
        }
    }

    public void updateRecyclerView() {
        chatAdapter = new ChatAdapter(ChatActivity.this, chatEntityList, senderEntity);
        rvChat.setAdapter(chatAdapter);
    }

    String lastAccessKey = "";
    boolean isLastPage = false;
    private boolean isLoading = false;
    private int currentPage = 0;

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
            if (dy < 0) { //check for scroll down
                Logger.d("SCROLLING DOWN - >" + " visibleItemCount: " + visibleItemCount + " totalItemCount : " + totalItemCount + " pastVisiblesItems : " + pastVisiblesItems);

                if (!isLoading && !isLastPage) {
                    if (pastVisiblesItems == THRESHOLD) {
                        isLoading = true;
                        Logger.d("Fetching more data" + lastAccessKey);
                        currentPage++;
                        new LoadSingleChatWithPage(currentPage).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                }
            } else { //check for scroll up
                Logger.d("SCROLLING UP - >" + " visibleItemCount: " + visibleItemCount + " totalItemCount : " + totalItemCount + " pastVisiblesItems : " + pastVisiblesItems);

            }


           /* int visibleItemCount = linearLayoutManager.getChildCount(); // number of item on screen
            int totalItemCount = linearLayoutManager.getItemCount();  // total number of items
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition(); // top item position

            Logger.d("visibleItemCount : " + visibleItemCount + " totalItemCount : " + totalItemCount + " firstVisibleItemPosition : " + firstVisibleItemPosition);
            if (!isLastPage && !isLoading) {

                if (firstVisibleItemPosition % PAGE_SIZE == Constants.FETCH_NEW_PAGE  && totalItemCount >= PAGE_SIZE) {
                    isLoading = true;
                    Logger.d("Fetching more data" + lastAccessKey);
                    getSinleOneTimeChatFromFirebase(lastAccessKey);
                }
              */
           /*
              if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition % PAGE_SIZE == Constants.FETCH_NEW_PAGE
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    Logger.d("Fetching more data" + lastAccessKey);
                    getSinleOneTimeChatFromFirebase(lastAccessKey);
                }*//*
            }*/
        }
    };

    private void getSinleOneTimeChatFromFirebase(String lastKey) {
        Logger.d("Fetching single chat list from firebase one time ");
        String ref = getSingleChatRef(senderEntity.getfId(), receiverEntity.getfId());
        Logger.d(ref);
        if (ref != null) {
            DatabaseReference classRef = database.getReference(Constants.DB_CHAT).child(ref);
            Query query;
            if (lastKey.equals("")) {
                // first page data query
                query = classRef;
            } else {
                query = classRef.orderByKey().endAt(lastKey).limitToLast(PAGE_SIZE);
            }

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int length = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            if (length == 0) {
                                lastAccessKey = snapshot.getKey();
                            }
                            ChatEntity chatEntity = snapshot.getValue(ChatEntity.class);
                            chatEntityList.add(chatEntity);
                            length++;
                            Logger.d("" + chatEntity.getfId() + " - " + chatEntity.getMsg());
                        } catch (Exception e) {
                            Logger.d("Unable to parse");
                        }
                    }
                    if (length % PAGE_SIZE != 0) {
                        isLastPage = true;
                    }
                    isLoading = false;
                    if (chatEntityList.size() > 0) {
                        new StoreChatList(chatEntityList).execute();
                        updateRecyclerView();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

    //region loading list from local

    private void fetchLocalList() {
        if (chatType.equalsIgnoreCase("single")) {
            new LoadSingleChatWithPage(0).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            new LoadGroupChatWithPage(0).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }

    }

    public class LoadGroupChatWithPage extends AsyncTask<Void, Void, List<ChatEntity>> {

        int page = 0;

        LoadGroupChatWithPage(int page) {
            this.page = page;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<ChatEntity> doInBackground(Void... voids) {
            try {
                Logger.d("Fetching list from room db");
                return appDatabase.chatEntityDao().getAllChat(subjectEntity.getfId(), page * PAGE_SIZE, PAGE_SIZE);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ChatEntity> chatEntities) {
            super.onPostExecute(chatEntities);
            if (chatEntities != null) {
                chatEntityList.addAll(chatEntities);
                Logger.d("Size  : " + chatEntities.size());
                updateRecyclerView();
            } else {

            }
        }
    }

    public class LoadSingleChatWithPage extends AsyncTask<Void, Void, List<ChatEntity>> {

        int page = 0;

        LoadSingleChatWithPage(int page) {
            this.page = page;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<ChatEntity> doInBackground(Void... voids) {
            try {
                Logger.d("Fetching list from room db");
                return appDatabase.chatEntityDao().getSingleChatFirstPage(senderEntity.getfId(), receiverEntity.getfId(), page * PAGE_SIZE, PAGE_SIZE);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ChatEntity> chatEntities) {
            super.onPostExecute(chatEntities);
            if (chatEntities != null) {
                Logger.d("Size  : " + chatEntities.size());
                if (chatEntities.size() < PAGE_SIZE) {
                    isLastPage = true;
                }
                chatEntityList.addAll(0, chatEntities);
                int scroll = getScrollPosition();
                Logger.d("Scrollpos" + scroll);
                updateRecyclerView(scroll);
                isLoading = false;
            }
        }
    }
    //endregion

}
