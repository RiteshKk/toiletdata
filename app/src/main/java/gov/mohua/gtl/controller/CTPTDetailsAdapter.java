package gov.mohua.gtl.controller;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

import gov.mohua.gtl.R;
import gov.mohua.gtl.databinding.CtptDetailsItemViewBinding;
import gov.mohua.gtl.events.OnRadioButtonClickListener;
import gov.mohua.gtl.model.QuestionData;

public class CTPTDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<QuestionData> dataList;
    private String headerText = " ";
    private OnRadioButtonClickListener mListener;

    public CTPTDetailsAdapter(ArrayList<QuestionData> dataList, OnRadioButtonClickListener listener) {
        this.dataList = dataList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == 0) {
//            HeaderViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.header_view, parent, false);
//            return new HeaderViewHolder(binding);
//        } else {
        CtptDetailsItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ctpt_details_item_view, parent, false);
        return new ViewHolder(binding);
//        }
    }

//    private void addHeader() {
//        QuestionData data = new QuestionData();
//        data.setQuestionType("M0");
//        dataList.add(0, data);
//        char header = 'M';
//        ListIterator<QuestionData> questionDataListIterator = dataList.listIterator();
//        while (questionDataListIterator.hasNext()) {
//            QuestionData next = questionDataListIterator.next();
//            next.setObtainedmark(-1);
//            if (next.getQuestionType().charAt(0) != header) {
//                header = next.getQuestionType().charAt(0);
//                QuestionData d = new QuestionData();
//                d.setQuestionType(header + "0");
//                questionDataListIterator.add(d);
//            }
//        }
//
//    }

//    @Override
//    public int getItemViewType(int position) {
//        final QuestionData data = dataList.get(position);
//        if (TextUtils.isEmpty(data.getQuestion())) {
//            return 0;
//        }
//        return 1;
//    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final QuestionData data = dataList.get(position);

        ViewHolder holder1 = (ViewHolder) holder;
        holder1.mBinding.setViewModel(data);
        holder1.mBinding.questionCounter.setText(data.getQuestionType().substring(1) + ". ");
        holder1.mBinding.options.clearCheck();
        if (data.getObtainedmark() == data.getMarks1()) {
            holder1.mBinding.options1.setChecked(true);
        } else if (data.getObtainedmark() == data.getMarks2()) {
            holder1.mBinding.options2.setChecked(true);
        } else if (data.getObtainedmark() == data.getMarks3()) {
            holder1.mBinding.options3.setChecked(true);
        } else if (data.getObtainedmark() == data.getMarks4()) {
            holder1.mBinding.options4.setChecked(true);
        }
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void setObject(ArrayList<QuestionData> data) {
        dataList = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CtptDetailsItemViewBinding mBinding;

        public ViewHolder(CtptDetailsItemViewBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.options1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    QuestionData questionData = dataList.get(position);
                    questionData.setSelectedOptionNo(1);
                    questionData.setObtainedmark(questionData.getMarks1());
                    mListener.onRadioButtonClick();
                }
            });
            mBinding.options2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    QuestionData questionData = dataList.get(position);
                    questionData.setSelectedOptionNo(2);
                    questionData.setObtainedmark(questionData.getMarks2());
                    mListener.onRadioButtonClick();
                }
            });
            mBinding.options3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    QuestionData questionData = dataList.get(position);
                    questionData.setSelectedOptionNo(3);
                    questionData.setObtainedmark(questionData.getMarks3());
                    mListener.onRadioButtonClick();
                }
            });
            mBinding.options4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    QuestionData questionData = dataList.get(position);
                    questionData.setSelectedOptionNo(4);
                    questionData.setObtainedmark(questionData.getMarks4());
                    mListener.onRadioButtonClick();
                }
            });
        }
    }
}
