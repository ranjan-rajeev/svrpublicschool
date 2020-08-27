package com.svrpublicschool.ui.study;

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
import com.svrpublicschool.models.SubjectEntity;

public class AddSubjectBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = AddSubjectBottomSheetDialog.class.getSimpleName();
    BottomSheetOnCompletedListener bottomSheetOnCompletedListener;
    BottomSheetBehavior bottomSheetBehavior;
    Context context;
    TextView tvAddClass;
    EditText etClassName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int result = 0;
    ClassEntity classEntity;

    public AddSubjectBottomSheetDialog(ClassEntity classEntity, Context context, BottomSheetOnCompletedListener bottomSheetOnCompletedListener) {
        this.classEntity = classEntity;
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
        View view = View.inflate(getContext(), R.layout.layout_bottom_sheet_add_subject, null);

        init_views(view);
        tvAddClass.setOnClickListener(this);
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
        etClassName = view.findViewById(R.id.etClassName);
        tvAddClass = view.findViewById(R.id.tvAddClass);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        bottomSheetOnCompletedListener.onComplete(result);
    }

    private void hideAppBar(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);
    }

    private void showView(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize() {
        final TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = (int) array.getDimension(0, 0);
        return size;
    }

    private void addClass() {
        DatabaseReference newRef = database.getReference(Constants.DB_SUBJECT).child(classEntity.getfId()).push();
        SubjectEntity subjectEntity = new SubjectEntity();
        subjectEntity.setName("" + etClassName.getText().toString());
        subjectEntity.setfId(newRef.getKey());
        subjectEntity.setParentId(classEntity.getfId());
        newRef.setValue(subjectEntity);
        result = 1;
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAddClass:
                addClass();
                break;
        }
    }
}