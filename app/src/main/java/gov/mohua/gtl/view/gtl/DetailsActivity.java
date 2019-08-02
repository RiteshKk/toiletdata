package gov.mohua.gtl.view.gtl;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import gov.mohua.gtl.R;
import gov.mohua.gtl.ToiletLocatorApp;
import gov.mohua.gtl.databinding.ActivityDetailsBinding;
import gov.mohua.gtl.model.C;
import gov.mohua.gtl.model.ToiletData;
import gov.mohua.gtl.util.Utils;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView1, imageView2, imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ToiletData toiletData = getIntent().getParcelableExtra(C.Companion.getDATA_TAG());
        SharedPreferences locator = getSharedPreferences(C.Companion.getPREF_NAME(), MODE_PRIVATE);

        toiletData.setStateName(locator.getString(C.Companion.getStateName(), ""));
        toiletData.setCityName(locator.getString(C.Companion.getCityName(), ""));


        ((TextView) findViewById(R.id.app_version)).setText(Utils.INSTANCE.getVersionName(this));

        if (toiletData.getOpenDays() != null) {
            ((TextView) findViewById(R.id.sunday)).setSelected(toiletData.getOpenDays().contains("Sunday"));
            ((TextView) findViewById(R.id.monday)).setSelected(toiletData.getOpenDays().contains("Monday"));
            ((TextView) findViewById(R.id.tuesday)).setSelected(toiletData.getOpenDays().contains("Tuesday"));
            ((TextView) findViewById(R.id.wednesday)).setSelected(toiletData.getOpenDays().contains("Wednesday"));
            ((TextView) findViewById(R.id.thursday)).setSelected(toiletData.getOpenDays().contains("Thursday"));
            ((TextView) findViewById(R.id.friday)).setSelected(toiletData.getOpenDays().contains("Friday"));
            ((TextView) findViewById(R.id.saturday)).setSelected(toiletData.getOpenDays().contains("Saturday"));
        }

        imageView1 = (ImageView) findViewById(R.id.details_image1);
        imageView2 = (ImageView) findViewById(R.id.details_image2);
        imageView3 = (ImageView) findViewById(R.id.details_image3);
        loadImage(toiletData);
        binding.setModelData(toiletData);
    }

    private void loadImage(ToiletData toiletData) {
        RequestQueue requestQueue = ToiletLocatorApp.Companion.getInstance().getRequestQueue();
        ImageRequest request1 = new ImageRequest(C.Companion.getIMAGE_BASE_URL() + toiletData.getImageUrl1(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView1.setImageBitmap(response);
            }
        }, 200, 200, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                    Toast.makeText(DetailsActivity.this, "Can't Connect right Now!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Log.e("volleyError",
                            "auth Error " + error.getMessage());
                } else if (error instanceof ServerError) {
                    Log.e("volleyError",
                            "server error " + error.getMessage());
                } else if (error instanceof NetworkError) {
                    Log.e("volleyError",
                            "network error " + error.getMessage());
                } else if (error instanceof ParseError) {
                    Log.e("volleyError",
                            "parsing error " + error.getMessage());
                }
            }
        });

        ImageRequest request2 = new ImageRequest(C.Companion.getIMAGE_BASE_URL() + toiletData.getImageUrl2(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView2.setImageBitmap(response);
            }
        }, 200, 200, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                    Toast.makeText(DetailsActivity.this, "Can't Connect right Now!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Log.e("volleyError",
                            "auth Error " + error.getMessage());
                } else if (error instanceof ServerError) {
                    Log.e("volleyError",
                            "server error " + error.getMessage());
                } else if (error instanceof NetworkError) {
                    Log.e("volleyError",
                            "network error " + error.getMessage());
                } else if (error instanceof ParseError) {
                    Log.e("volleyError",
                            "parsing error " + error.getMessage());
                }
            }
        });

        ImageRequest request3 = new ImageRequest(C.Companion.getIMAGE_BASE_URL() + toiletData.getImageUrl3(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView3.setImageBitmap(response);
            }
        }, 200, 200, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                    Toast.makeText(DetailsActivity.this, "Can't Connect right Now!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Log.e("volleyError",
                            "auth Error " + error.getMessage());
                } else if (error instanceof ServerError) {
                    Log.e("volleyError",
                            "server error " + error.getMessage());
                } else if (error instanceof NetworkError) {
                    Log.e("volleyError",
                            "network error " + error.getMessage());
                } else if (error instanceof ParseError) {
                    Log.e("volleyError",
                            "parsing error " + error.getMessage());
                }
            }
        });
        requestQueue.add(request1);
        requestQueue.add(request2);
        requestQueue.add(request3);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

