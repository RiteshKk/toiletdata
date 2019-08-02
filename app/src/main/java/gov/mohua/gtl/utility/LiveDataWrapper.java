package gov.mohua.gtl.utility;


import android.support.annotation.Nullable;

import static gov.mohua.gtl.utility.LiveDataWrapper.Status.FAILURE;
import static gov.mohua.gtl.utility.LiveDataWrapper.Status.LOADING;
import static gov.mohua.gtl.utility.LiveDataWrapper.Status.OFFLINE;
import static gov.mohua.gtl.utility.LiveDataWrapper.Status.SUCCESS;

public class LiveDataWrapper<T> {

    public Status status;
    public T data;
    public String msg;
    public Throwable throwable;

     LiveDataWrapper( Status status, T data, String msg, Throwable throwable) {
        this.status = status;
        this.data = data;
        this.msg = msg;
        this.throwable = throwable;
    }



    public static <T> LiveDataWrapper<T> success(@Nullable T data) {
        return new LiveDataWrapper<>( SUCCESS, data, null, null);
    }

    // When Api is failed and onFaliure() is called
    public static <T> LiveDataWrapper<T> failure(Throwable throwable) {
        return new LiveDataWrapper<>( FAILURE, null, null, throwable);
    }

    //Useful to Show cached Data from ROOM DB When Api Response failure occurs from server
    public static <T> LiveDataWrapper<T> failure(Throwable throwable, @Nullable T data) {
        return new LiveDataWrapper<>( FAILURE, data, null, throwable);
    }

    public static <T> LiveDataWrapper<T> loading(@Nullable T data) {
        return new LiveDataWrapper<>( LOADING, data, null, null);
    }

    //Useful to Show Cached Data from ROOM DB When user is Offline
    public static <T> LiveDataWrapper<T> offline(String msg, @Nullable T data) {
        return new LiveDataWrapper<>( OFFLINE, data, msg, null);
    }

    public enum Status{
        SUCCESS,
        ERROR,
        FAILURE,
        OFFLINE,
        LOADING
    }
}
