package com.ipssi.toiletdata.viewModel;

import android.databinding.BaseObservable;
import android.view.View;

import com.ipssi.toiletdata.events.NoNetworkEvent;

import org.greenrobot.eventbus.EventBus;

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
