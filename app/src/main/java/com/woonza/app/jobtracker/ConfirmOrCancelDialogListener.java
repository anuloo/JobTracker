package com.woonza.app.jobtracker;

import android.os.Parcelable;

/**
 * Created by user on 12/2/2016.
 */

public interface ConfirmOrCancelDialogListener extends Parcelable {
    void onConfirmButtonPressed();
    void onCancelButtonPressed();
}
