package com.ipssi.toiletdata.events;

public class NoNetworkEvent {
    private String clickType;
    private String clickedFrom;

    public NoNetworkEvent(String clickType, String clickedFrom) {
        this.clickType = clickType;
        this.clickedFrom = clickedFrom;
    }

    public String getClickType() {
        return clickType;
    }

    public String getClickedFrom() {
        return clickedFrom;
    }
}

