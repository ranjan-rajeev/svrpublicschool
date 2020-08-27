package com.svrpublicschool.ui.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.svrpublicschool.BuildConfig;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.FileUtility;
import com.svrpublicschool.Util.FileUtils;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.Util.Utility;
import com.svrpublicschool.database.AppDatabase;
import com.svrpublicschool.database.DatabaseClient;
import com.svrpublicschool.firebase.FirebaseHelper;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.models.GroupDetailsEntity;
import com.svrpublicschool.models.SubjectEntity;
import com.svrpublicschool.models.UserEntity;
import com.svrpublicschool.ui.chat.adapter.ChatAdapter;
import com.svrpublicschool.ui.login.LoginBottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.svrpublicschool.Util.Constants.PAGE_SIZE;
import static com.svrpublicschool.Util.Constants.THRESHOLD;

public class GroupChatActivity extends BaseActivity implements View.OnClickListener, IChatListener {

    private static final int REQUEST_CODE_PERMISSIONS_FILE_ACCESS = 1002;
    public static final int PICK_IMAGE = 100;
    public static final int PICK_DOC = 101;
    public static final int PICK_CAMERA_IMAGE = 104;
    Uri camereUri;
    RecyclerView rvChat;
    LinearLayoutManager linearLayoutManager;
    ChatAdapter chatAdapter;
    List<ChatEntity> chatEntityList = new ArrayList<>();
    View inputView;
    AppCompatImageView ivAttach, ivCamera;
    LinearLayout llSend;
    EditText etMessage;
    GroupDetailsEntity groupDetailsEntity;
    GroupChatRepository groupChatRepository;
    UserEntity senderEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        senderEntity = getUser();
        init_views();
        hideShowAdminFeatures();
        getIntentExtra();
        groupChatRepository = new GroupChatRepository(this, groupDetailsEntity, this);
        setListener();
        if (!checkPermission()) {
            requestPermission();
        }
        showDialog();
        groupChatRepository.getInitialList();
        senderEntity = groupChatRepository.getLoggedinUser();
    }

    public void hideShowAdminFeatures() {
        if (senderEntity != null && senderEntity.getType().equalsIgnoreCase("admin")) {
            inputView.setVisibility(View.VISIBLE);
        } else {
            inputView.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        ivAttach.setOnClickListener(this);
        llSend.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
    }

    private void init_views() {
        inputView = findViewById(R.id.inputView);
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
            groupDetailsEntity = (GroupDetailsEntity) getIntent().getSerializableExtra(Constants.INTENT_PARAM_GROUP);
            getSupportActionBar().setTitle(groupDetailsEntity.getGpName());
            String from = getIntent().getStringExtra(Constants.INTENT_PARAM_FROM);
            if (from != null && from.equals("ASK")) {
                if (senderEntity == null) {
                    openLoginBottomSheet();
                } else {
                    inputView.setVisibility(View.VISIBLE);
                    if (!FirebaseHelper.getUploadEnable()) {
                        ivCamera.setVisibility(View.GONE);
                    }
                    ivAttach.setVisibility(View.GONE);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAttach:
                //openPdfSelector();
                openSelectorBottomSheet();
                break;
            case R.id.llSend:
                if (!etMessage.getText().toString().equals("")) {
                    new AddTextChatEntity(etMessage.getText().toString().trim()).execute();
                }
                break;
            case R.id.ivCamera:
                openCamera();
                break;
        }
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    public void openPdfSelector() {
        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, PICK_DOC);*/

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_DOC);
    }

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",
                photo));
        camereUri = Uri.fromFile(photo);
        startActivityForResult(intent, PICK_CAMERA_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                Logger.d("Image Selected URI :" + data.getData());
                new AddImageChatEntity(data.getData()).execute();
            }

        } else if (requestCode == PICK_DOC) {
            if (resultCode == RESULT_OK) {
                Logger.d("Document Selected URI : " + data.getData());
                new AddPdfChatEntity(data.getData()).execute();
            }

        } else if (requestCode == PICK_CAMERA_IMAGE) {
            if (resultCode == RESULT_OK) {
                Logger.d("Camera Selected URI :" + camereUri);
                new AddCameraChatEntity(camereUri).execute();
            }
        }
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
                String srcPath = FileUtils.getPath(GroupChatActivity.this, fileuri);
                String fileName = timeStamp + "-" + FileUtility.getFileName(fileuri, GroupChatActivity.this);
                fileName = fileName.replaceAll("\\s", "");
                String fileDir = FileUtility.getPdfDirectoryName();
                FileUtility.copyFile(srcPath, "" + fileDir + fileName);
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setPkId(timeStamp);
                chatEntity.setStatus(Constants.NOT_SENT);
                chatEntity.setSenName(senderEntity.getName());
                chatEntity.setChatType(ChatAdapter.TYPE_PDF_SENT);
                chatEntity.setCreatedAt(timeStamp);
                chatEntity.setSender(senderEntity.getfId());
                chatEntity.setReceiver(groupDetailsEntity.getFid());
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
                updateRecyclerView();
                groupChatRepository.storeChatLocalDb(chatEntity);
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
                Bitmap tempBitmap = FileUtility.getCorrectlyOrientedImage(GroupChatActivity.this, fileuri, filePath);
                long timeStamp = Calendar.getInstance().getTimeInMillis();
                String fileName = timeStamp + "-" + FileUtility.getFileName(fileuri, GroupChatActivity.this);
                fileName = fileName.replaceAll("\\s", "");
                String fileDir = FileUtility.getImageDirectoryName();
                FileUtility.storeBitmap(GroupChatActivity.this, tempBitmap, fileDir, fileName);
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setPkId(timeStamp);
                chatEntity.setStatus(Constants.NOT_SENT);
                chatEntity.setSenName(senderEntity.getName());
                chatEntity.setChatType(ChatAdapter.TYPE_IMG_SENT);
                chatEntity.setCreatedAt(Calendar.getInstance().getTimeInMillis());
                chatEntity.setSender(senderEntity.getfId());
                chatEntity.setReceiver(groupDetailsEntity.getFid());
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
                updateRecyclerView();
                groupChatRepository.storeChatLocalDb(chatEntity);
            }
        }
    }

    class AddCameraChatEntity extends AsyncTask<Void, Void, ChatEntity> {
        Uri fileuri;

        public AddCameraChatEntity(Uri filePath) {
            this.fileuri = filePath;
        }

        @Override
        protected ChatEntity doInBackground(Void... voids) {

            try {
                String filePath = fileuri.getEncodedPath();
                Bitmap tempBitmap = FileUtility.getCorrectlyOrientedImage(GroupChatActivity.this, fileuri, filePath);
                long timeStamp = Calendar.getInstance().getTimeInMillis();
                String fileName = timeStamp + "-" + FileUtility.getFileName(fileuri, GroupChatActivity.this);
                fileName = fileName.replaceAll("\\s", "");
                String fileDir = FileUtility.getImageDirectoryName();
                FileUtility.storeBitmap(GroupChatActivity.this, tempBitmap, fileDir, fileName);
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setPkId(timeStamp);
                chatEntity.setStatus(Constants.NOT_SENT);
                chatEntity.setSenName(senderEntity.getName());
                chatEntity.setChatType(ChatAdapter.TYPE_IMG_SENT);
                chatEntity.setCreatedAt(Calendar.getInstance().getTimeInMillis());
                chatEntity.setSender(senderEntity.getfId());
                chatEntity.setReceiver(groupDetailsEntity.getFid());
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
                updateRecyclerView();
                groupChatRepository.storeChatLocalDb(chatEntity);
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
                chatEntity.setChatType(ChatAdapter.TYPE_MSG_SENT);
                chatEntity.setCreatedAt(timeStamp);
                chatEntity.setMsg(msg);
                chatEntity.setStatus(Constants.NOT_SENT);
                chatEntity.setSenName(senderEntity.getName());
                chatEntity.setSender(senderEntity.getfId());
                chatEntity.setReceiver(groupDetailsEntity.getFid());
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
                groupChatRepository.storeChatLocalDb(chatEntity);
            }
        }
    }

    public String getPath(Uri uri) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {

        }
        return "";
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED /*&& ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(GroupChatActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/},
                REQUEST_CODE_PERMISSIONS_FILE_ACCESS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS_FILE_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    groupChatRepository.getInitialList();
                } else {
                    //requestPermission();
                }
                break;
        }
    }

    public void updateRecyclerView(int scrollPosition) {
        chatAdapter = new ChatAdapter(GroupChatActivity.this, chatEntityList, groupChatRepository.getLoggedinUser());
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
        chatAdapter = new ChatAdapter(GroupChatActivity.this, chatEntityList, groupChatRepository.getLoggedinUser());
        rvChat.setAdapter(chatAdapter);
    }

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
                if (!isLoading && !isLastPage) {
                    if (pastVisiblesItems == THRESHOLD) {
                        isLoading = true;
                        currentPage++;
                        Logger.d("Fetching more data from local" + currentPage);
                        if (!Utility.isListEmpty(chatEntityList)) {
                            String lastKey = chatEntityList.get(0).getfId();
                            groupChatRepository.getChatBeforeKeyFirebase(lastKey);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void addListItem(List<ChatEntity> list, boolean addItemBottom) {
        cancelDialog();
        if (addItemBottom) {
            chatEntityList.addAll(0, list);
            updateRecyclerView(getScrollPosition());
        } else {
            chatEntityList.addAll(list);
            updateRecyclerView();
        }

        isLoading = false;
    }

    @Override
    public void addItem(ChatEntity chatEntity) {
        chatEntityList.add(chatEntity);
        updateRecyclerView();
    }

    @Override
    public void lastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
        Logger.d("Last Page fetched !!!");
    }

    @Override
    public void onError(String message) {
        cancelDialog();
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public boolean pushChatToFireBase(ChatEntity chatEntity) {
        return groupChatRepository.pushChatToFirebAse(chatEntity);
    }

    public void setSenderEntity(UserEntity senderEntity) {
        this.senderEntity = senderEntity;
    }

    public void openLoginBottomSheet() {
        LoginBottomSheetDialog addClassBottomSheetDialog = new LoginBottomSheetDialog(this, true, result -> {
            if (result != null) { //success
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                setUserEntity(result);
                senderEntity = result;
                getIntentExtra();
            }
        });
        addClassBottomSheetDialog.show(getSupportFragmentManager(), "Opening login bottom sheet");
    }

    public void openSelectorBottomSheet() {
        SelctorBottomSheetDialog selctorBottomSheetDialog = new SelctorBottomSheetDialog(this, result -> {
            if (result == 1) { //success
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            } else { // Failure
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
        selctorBottomSheetDialog.show(getSupportFragmentManager(), "Opening login bottom sheet");

    }

    @Override
    protected void onDestroy() {
        groupChatRepository.removeAllListener();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //groupChatRepository.removeAllListener();
    }

    public void openDeletePopUp(ChatEntity chatEntity, int position) {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete ?")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groupChatRepository.deleteChatFirebase(chatEntity);
                        groupChatRepository.deleteChatLocalDb(chatEntity);
                        chatEntityList.remove(position);
                        updateRecyclerView(position);
                    }
                }).show();
    }


}
