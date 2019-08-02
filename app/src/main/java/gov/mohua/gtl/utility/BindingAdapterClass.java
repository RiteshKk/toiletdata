package gov.mohua.gtl.utility;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import gov.mohua.gtl.model.C;

public class BindingAdapterClass {


    private static final String TAG = BindingAdapterClass.class.getSimpleName();

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String imageUrl) {
        Picasso.with(imageView.getContext()).load(C.Companion.getIMAGE_BASE_URL() + imageUrl).fit().into(imageView);
    }

}
