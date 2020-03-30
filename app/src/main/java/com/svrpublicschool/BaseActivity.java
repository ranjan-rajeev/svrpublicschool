package com.svrpublicschool;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by Rajeev on 08/11/18.
 */
public class BaseActivity extends AppCompatActivity {

    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setTheme(R.style.AppTheme);
    }


    //region all neccessary functions
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void cancelDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected void cancelDialog(Context context) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected void showDialog() {
//        dialog = ProgressDialog.show(BaseActivity.this, "", "Loading...", true);
        showDialog("Loading...");
    }

    protected void showDialog(Context context) {
        // dialog = ProgressDialog.show(context, "", "Loading...", true);
        if (context != null)
            showDialog("Loading...", context);
    }

    protected void showDialog(String msg, Context context) {
        if (dialog == null)
            dialog = ProgressDialog.show(context, "", msg, true);
        else {
            if (!dialog.isShowing())
                dialog = ProgressDialog.show(context, "", msg, true);
        }
    }

    protected void showDialog(String msg) {
        if (dialog == null)
            dialog = ProgressDialog.show(this, "", msg, true);
        else {
            if (!dialog.isShowing())
                dialog = ProgressDialog.show(this, "", msg, true);
        }
    }
}