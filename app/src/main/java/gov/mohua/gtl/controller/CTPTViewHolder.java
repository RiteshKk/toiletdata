package gov.mohua.gtl.controller;

import android.support.v7.widget.RecyclerView;

import gov.mohua.gtl.databinding.CtptItemViewBinding;
import gov.mohua.gtl.model.CTPTDataModel;
import gov.mohua.gtl.viewModel.CTPTListItemViewModel;

class CTPTViewHolder extends RecyclerView.ViewHolder {
    CtptItemViewBinding mBinding;
    private CTPTListItemViewModel mItemViewModel;

    public CTPTViewHolder(CtptItemViewBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }


    public void onBind(CTPTDataModel obj, int position) {
        if (mItemViewModel != null) {
            mItemViewModel.setObject(obj, position);
        } else {
            mItemViewModel = new CTPTListItemViewModel(mBinding.getRoot().getContext(), obj);
            mBinding.setViewModel(mItemViewModel);
        }
    }
}
