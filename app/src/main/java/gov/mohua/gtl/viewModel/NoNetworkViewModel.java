package gov.mohua.gtl.viewModel;

import android.databinding.BaseObservable;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import gov.mohua.gtl.events.NoNetworkEvent;

public class NoNetworkViewModel extends BaseObservable {
        private String mClickedFrom;

        public NoNetworkViewModel(String clickedFrom) {
            this.mClickedFrom = clickedFrom;
        }

        public void onRetryClick(View view) {
            EventBus.getDefault().post(new NoNetworkEvent("retry", mClickedFrom));
        }


        public void setClickedFrom(String clickedFrom) {
            mClickedFrom = clickedFrom;
        }
    }
