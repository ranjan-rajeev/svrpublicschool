package com.svrpublicschool.ui.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.svrpublicschool.ui.main.MainActivity;

public class SelctorBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = SelctorBottomSheetDialog.class.getSimpleName();
    BottomSheetOnCompletedListener bottomSheetOnCompletedListener;
    BottomSheetBehavior bottomSheetBehavior;
    Context context;
    int result = 0;
    LinearLayout llDocument, llCamera, llGallery;

    public SelctorBottomSheetDialog(Context context, BottomSheetOnCompletedListener bottomSheetOnCompletedListener) {
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
        View view = View.inflate(getContext(), R.layout.layout_bottom_sheet_selector, null);

        init_views(view);
        llCamera.setOnClickListener(this);
        llDocument.setOnClickListener(this);
        llGallery.setOnClickListener(this);
        //setting layout with bottom sheet
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
        llDocument = view.findViewById(R.id.llDocument);
        llGallery = view.findViewById(R.id.llGallery);
        llCamera = view.findViewById(R.id.llCamera);

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        bottomSheetOnCompletedListener.onComplete(result);
    }

    private void addClass() {
        /*DatabaseReference newRef = database.getReference(Constants.DB_CLASSES).push();
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassName("" + etClassName.getText().toString());
        classEntity.setfId(newRef.getKey());
        classEntity.setNoOfSubjects(10);
        newRef.setValue(classEntity);*/
        result = 1;
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCamera:
                if (context instanceof GroupChatActivity) {
                    ((GroupChatActivity) context).openCamera();
                }
                dismiss();
                break;
            case R.id.llDocument:
                if (context instanceof GroupChatActivity) {
                    ((GroupChatActivity) context).openPdfSelector();
                }
                dismiss();
                break;
            case R.id.llGallery:
                if (context instanceof GroupChatActivity) {
                    ((GroupChatActivity) context).openGallery();
                }
                dismiss();
                break;
        }
    }
}