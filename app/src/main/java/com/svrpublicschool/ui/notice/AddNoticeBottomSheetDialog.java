package com.svrpublicschool.ui.notice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.svrpublicschool.R;
import com.svrpublicschool.Util.BottomSheetOnCompletedListener;
import com.svrpublicschool.Util.Constants;
import com.svrpublicschool.models.ClassEntity;
import com.svrpublicschool.models.NoticeEntity;

import java.util.Calendar;

public class AddNoticeBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = AddNoticeBottomSheetDialog.class.getSimpleName();
    NoticeAddedListener bottomSheetOnCompletedListener;
    BottomSheetBehavior bottomSheetBehavior;
    Context context;
    TextView tvLogin;
    EditText etUserName, etName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    NoticeEntity noticeEntity;

    public AddNoticeBottomSheetDialog(Context context, NoticeAddedListener bottomSheetOnCompletedListener) {
        this.bottomSheetOnCompletedListener = bottomSheetOnCompletedListener;
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
        View view = View.inflate(getContext(), R.layout.layout_bottom_sheet_add_notice, null);

        init_views(view);

        bottomSheet.setContentView(view);

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
        tvLogin = view.findViewById(R.id.tvLogin);
        etUserName = view.findViewById(R.id.etUserName);
        etName = view.findViewById(R.id.etName);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        bottomSheetOnCompletedListener.onComplete(noticeEntity);
    }

    private void addNotice() {
        DatabaseReference newRef = database.getReference(Constants.DB_NOTICE).push();
        noticeEntity = new NoticeEntity();
        noticeEntity.setCreatedAt(Calendar.getInstance().getTimeInMillis());
        noticeEntity.setTitle("" + etName.getText().toString());
        noticeEntity.setDesc("" + etUserName.getText().toString());
        noticeEntity.setFid(newRef.getKey());
        newRef.setValue(noticeEntity);
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLogin:
                addNotice();
                break;
        }
    }

    public interface NoticeAddedListener {
        void onComplete(NoticeEntity noticeEntity);
    }
}