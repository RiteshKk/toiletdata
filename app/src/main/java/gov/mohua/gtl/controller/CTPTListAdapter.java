package gov.mohua.gtl.controller;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import gov.mohua.gtl.R;
import gov.mohua.gtl.databinding.CtptItemViewBinding;
import gov.mohua.gtl.model.CTPTDataModel;

public class CTPTListAdapter extends RecyclerView.Adapter<CTPTViewHolder> {
//    private final Context mContext;

    private ArrayList<CTPTDataModel> modelList;
//    private String totalMark;

    public CTPTListAdapter(Context ctx) {
        /*mContext = ctx;*/
    }

    @NonNull
    @Override
    public CTPTViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CtptItemViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.ctpt_item_view, parent, false);
        return new CTPTViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CTPTViewHolder holder, final int position) {
        CTPTDataModel model = modelList.get(position);
        holder.onBind(model, position);
    }

    @Override
    public int getItemCount() {
        return modelList == null ? 0 : modelList.size();
    }

    public void setData(ArrayList<CTPTDataModel> model, String totalMark) {
        modelList = model;
        notifyDataSetChanged();
    }
}
