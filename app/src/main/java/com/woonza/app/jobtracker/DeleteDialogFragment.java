package com.woonza.app.jobtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * Created by user on 11/8/2016.
 */

public class DeleteDialogFragment extends DialogFragment {


    public static DeleteDialogFragment newInstance(String title, String message, ConfirmOrCancelDialogListener listener) {
        DeleteDialogFragment frag = new DeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putParcelable("listener", listener);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        final ConfirmOrCancelDialogListener mListener = getArguments().getParcelable("listener");
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.button_delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialog, int button) {
                        mListener.onConfirmButtonPressed();

                    } // end method onClick
                } // end anonymous inner class
        ); // end call to method setPositiveButton

        builder.setNegativeButton(R.string.button_cancel, null);
        return builder.create();
    }
}
