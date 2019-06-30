package com.ipssi.toiletdata.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipssi.toiletdata.R;
import com.ipssi.toiletdata.events.OnCaptureButtonClickedListener;
import com.ipssi.toiletdata.events.OnRadioButtonClickListener;
import com.ipssi.toiletdata.model.C;
import com.ipssi.toiletdata.model.ViewData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;

public class CityAdminShowDataAdmin extends RecyclerView.Adapter<CityAdminShowDataAdmin.ViewHolder> {
    private final OnCaptureButtonClickedListener mListener;
    private ArrayList<ViewData> data = new ArrayList<>();
    private JSONObject categoryJSON;
    private JSONObject wardJSON;
    private String baseUrl = "http://sbmtoilet.org/backend/web/";
    private String city;
    private String state;


    public CityAdminShowDataAdmin(OnCaptureButtonClickedListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_admin_show_data, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewData viewData = data.get(position);
        holder.city.setText(city);
        holder.state.setText(state);
        String image_path = viewData.getImage_path();
        if (image_path != null && image_path.length() > 0) {
            Picasso.Builder builder = new Picasso.Builder(holder.itemView.getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Log.e("Picasso Error", "Failed to load image: " + exception);
                }
            });
            Picasso pic = builder.build();
            pic.load((baseUrl + image_path).trim()).resize(250, 150).placeholder(R.mipmap.icon).error(R.mipmap.icon).into(holder.image);
        }
        holder.checkBox.setTag(viewData);
        holder.checkBox.setChecked(viewData.getDeletion_request() == 1);
        holder.address.setText(viewData.getAddress());
        holder.comment.setText(viewData.getComment());
        holder.category.setText(categoryJSON.optString(viewData.getCategory() + "") + "");
        holder.ward.setText(wardJSON.optString(viewData.getWard_id() + "") + "");
    }

    @Override
    public int getItemCount() {
        return data.size();

    }

    public void setData(@Nullable ArrayList<ViewData> data) {
        if (data != null && data.size() > 0) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    public void setCategoryJSON(@NotNull JSONObject categoryData) {
        this.categoryJSON = categoryData;
    }

    public void setWardJSON(@NotNull JSONObject wardData) {
        this.wardJSON = wardData;
    }

    public void setCity(@Nullable String cityName) {
        this.city = cityName;
    }

    public void setState(@Nullable String stateName) {
        this.state = stateName;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView address, ward, category, comment, city, state;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.view_image);
            address = itemView.findViewById(R.id.view_address);
            category = itemView.findViewById(R.id.view_category);
            ward = itemView.findViewById(R.id.view_ward);
            state = itemView.findViewById(R.id.view_state);
            city = itemView.findViewById(R.id.view_city);
            comment = itemView.findViewById(R.id.view_comment);
            checkBox = itemView.findViewById(R.id.cb_admin_delete);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setMessage("Do you want to mark it as deleted?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ViewData data = (ViewData) v.getTag();
                                data.setDeletion_request(1);
                                dialog.dismiss();
                                mListener.onUploadButtonClicked(data);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ViewData data = (ViewData) v.getTag();
                                data.setDeletion_request(0);
                                checkBox.setChecked(false);
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
            });
        }
    }
}
