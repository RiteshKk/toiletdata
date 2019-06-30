package com.ipssi.toiletdata.utility;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.ipssi.toiletdata.model.C;
import com.squareup.picasso.Picasso;

public class BindingAdapterClass {


    private static final String TAG = BindingAdapterClass.class.getSimpleName();

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String imageUrl) {
        Picasso.with(imageView.getContext()).load(C.Companion.getIMAGE_BASE_URL() + imageUrl).fit().into(imageView);
    }

}
