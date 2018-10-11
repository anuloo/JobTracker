package com.woonza.app.jobtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by user on 11/8/2016.
 */

public class CustomDialogFragment extends DialogFragment {

    public static CustomDialogFragment newInstance(String title, String message) {
        CustomDialogFragment frag = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, null);
        return builder.create();
    }
}
