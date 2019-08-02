package gov.mohua.gtl.events;

import gov.mohua.gtl.model.ViewData;

public interface OnCaptureButtonClickedListener {
    void onCaptureButtomClicked(ViewData viewData,int position);

    void onUploadButtonClicked(ViewData data);
}
