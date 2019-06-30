package com.ipssi.toiletdata.view.thirdParty.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat.invalidateOptionsMenu
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.ipssi.toiletdata.OnFragmentChangeListener
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.controller.CTPTDetailsAdapter
import com.ipssi.toiletdata.databinding.MandatoryFragmentBinding
import com.ipssi.toiletdata.events.OnRadioButtonClickListener
import com.ipssi.toiletdata.model.QuestionData

class MandatoryFragment : Fragment(), OnRadioButtonClickListener {
    var score: Int = 0
    var adapter: CTPTDetailsAdapter? = null
    override fun onRadioButtonClick() {
        score = 0
        for (question in this.list) {
            if (question.obtainedmark > 0)
                score = score + question.obtainedmark
        }

        invalidateOptionsMenu(activity)
    }

    private var list: ArrayList<QuestionData> = ArrayList<QuestionData>()

    companion object {
        fun newInstance() = MandatoryFragment()
    }


    private var mListener: OnFragmentChangeListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as OnFragmentChangeListener
    }

    private lateinit var binding: MandatoryFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<MandatoryFragmentBinding>(layoutInflater, R.layout.mandatory_fragment, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.ctpt_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.menu_next)
        if (score >= 70) {
            item?.setVisible(true)
        } else {
            item?.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_next) {
            val bundle = Bundle()
            bundle.putInt("mandatoryScore", score)
            mListener?.onFragmentChange(1, bundle)
        } else if (item?.itemId == R.id.menu_submit) {
            val bundle = Bundle()
            bundle.putInt("mandatoryScore", score)
            mListener?.onFragmentChange(4, bundle)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.header.setText("Mandatory")
        binding.detailsViewRecycler.layoutManager = LinearLayoutManager(activity)
        adapter = CTPTDetailsAdapter(this.list, this)
        binding.detailsViewRecycler.adapter = adapter
    }

    fun setList(list: ArrayList<QuestionData>) {
        this.list = list
        adapter?.setObject(this.list)
    }
}
