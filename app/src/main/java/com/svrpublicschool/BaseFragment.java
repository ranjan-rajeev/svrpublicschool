package com.svrpublicschool;

import android.app.ProgressDialog;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    ProgressDialog dialog;

    public BaseFragment() {

    }

    public void cancelDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void showDialog() {
        showDialog("Loading...");
    }

    public void showDialog(String msg) {
        if (dialog == null)
            dialog = ProgressDialog.show(getActivity(), "", msg, true);
        else {
            if (!dialog.isShowing())
                dialog = ProgressDialog.show(getActivity(), "", msg, true);
        }

    }


}
