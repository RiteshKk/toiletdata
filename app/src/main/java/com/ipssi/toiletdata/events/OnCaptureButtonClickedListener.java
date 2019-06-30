package com.ipssi.toiletdata.events;

import com.ipssi.toiletdata.model.ViewData;

public interface OnCaptureButtonClickedListener {
    void onCaptureButtomClicked(ViewData viewData,int position);

    void onUploadButtonClicked(ViewData data);
}
