package com.svrpublicschool.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.svrpublicschool.PrefManager.SharedPrefManager;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.Util.Logger;
import com.svrpublicschool.models.UserEntity;

public class LoginBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = LoginBottomSheetDialog.class.getSimpleName();
    LoginCompletedListener bottomSheetOnCompletedListener;
    BottomSheetBehavior bottomSheetBehavior;
    Context context;
    TextView tvLogin, tvSignUp;
    EditText etUserName, etPass, etName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    UserEntity result;
    TextInputLayout tilName;
    SharedPrefManager sharedPrefManager;
    boolean enableSignUpView = false;

    public LoginBottomSheetDialog(Context context, boolean enableSignUpView, LoginCompletedListener bottomSheetOnCompletedListener) {
        this.bottomSheetOnCompletedListener = bottomSheetOnCompletedListener;
        this.enableSignUpView = enableSignUpView;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        //inflating layout
        View view = View.inflate(getContext(), R.layout.layout_bottom_sheet_login, null);
        sharedPrefManager = SharedPrefManager.getInstance(context);
        init_views(view);
        tvLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        //setting layout with bottom sheet
        bottomSheet.setContentView(view);
        setCancelable(true);
        enablSignUpView(enableSignUpView);

        new GetLoginUser().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                Log.d("BOTTOM_STATE", "" + i);
                switch (i) {

                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        dismiss();
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        //(appBarLayout);
        return bottomSheet;
    }

    private void init_views(View view) {
        tvSignUp = view.findViewById(R.id.tvSignUp);
        tvLogin = view.findViewById(R.id.tvLogin);
        etUserName = view.findViewById(R.id.etUserName);
        etName = view.findViewById(R.id.etName);
        etPass = view.findViewById(R.id.etPass);
        tilName = view.findViewById(R.id.tilName);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        bottomSheetOnCompletedListener.onComplete(result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSignUp:
                if (isValid(true))
                    addUser();
                break;
            case R.id.tvLogin:
                if (isValid(false))
                    getUser();
                break;
        }
    }

    public boolean isValid(boolean isSignUp) {
        if (isSignUp) {
            if (etName.getText().toString().trim().equals("")) {
                etName.requestFocus();
                etName.setError("Enter Name");
                return false;
            }
        }
        if (etUserName.getText().toString().trim().equals("")) {
            etUserName.requestFocus();
            etUserName.setError("Enter user name");
            return false;
        }
        if (etPass.getText().toString().trim().equals("")) {
            etPass.requestFocus();
            etPass.setError("Enter pass");
            return false;
        }

        return true;
    }

    private void getUser() {
        DatabaseReference userRef = database.getReference(Constants.DB_USER);
        userRef.orderByChild("uderId").equalTo(etUserName.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            UserEntity userEntity = snapshot.getValue(UserEntity.class);
                            Logger.d("User Id Exhist");
                            if (userEntity.getPass().equals(etPass.getText().toString().trim())) {
                                new StoreUser(userEntity).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                result = userEntity;
                                dismiss();
                            } else {
                                Toast.makeText(context, "Enter correct password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Logger.d("Unable to parse");
                        }
                    }

                } else {
                    Toast.makeText(context, "Enter valid user name", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Try after some time", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUser() {
        DatabaseReference userRef = database.getReference(Constants.DB_USER).push();
        UserEntity userEntity = new UserEntity();
        userEntity.setName("" + etName.getText().toString().trim());
        userEntity.setPass("" + etPass.getText().toString().trim());
        userEntity.setUderId("" + etUserName.getText().toString().trim());
        userEntity.setType("student");

        userEntity.setfId(userRef.getKey());
        userRef.setValue(userEntity);
        result = userEntity;
        new StoreUser(result).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        dismiss();
    }

    public void enablSignUpView(boolean b) {
        if (b) {
            tilName.setVisibility(View.VISIBLE);
            tvSignUp.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.GONE);
        } else {
            tilName.setVisibility(View.GONE);
            tvSignUp.setVisibility(View.GONE);
            tvLogin.setVisibility(View.VISIBLE);
        }
    }

    public interface LoginCompletedListener {
        void onComplete(UserEntity userEntity);
    }

    class GetLoginUser extends AsyncTask<Void, Void, UserEntity> {
        @Override
        protected UserEntity doInBackground(Void... voids) {
            String result = "";
            String allString = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            Gson gson = new Gson();
            UserEntity userEntity = gson.fromJson(allString, UserEntity.class);
            return userEntity;
        }

        @Override
        protected void onPostExecute(UserEntity userEntity) {
            super.onPostExecute(userEntity);
            if (userEntity != null) {
                if (userEntity.getType().equalsIgnoreCase("admin")) {
                    enablSignUpView(true);
                }
            }
        }
    }

    class StoreUser extends AsyncTask<Void, Void, Void> {
        UserEntity userEntity;

        StoreUser(UserEntity userEntity) {
            this.userEntity = userEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sharedPrefManager.putStringValueForKey(Constants.SHD_PRF_USER_DETAILS, new Gson().toJson(userEntity));
            return null;
        }
    }
}