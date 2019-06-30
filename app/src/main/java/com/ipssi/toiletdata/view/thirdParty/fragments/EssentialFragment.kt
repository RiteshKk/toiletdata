package com.ipssi.toiletdata.view.thirdParty.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.ipssi.toiletdata.OnFragmentChangeListener
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.controller.CTPTDetailsAdapter
import com.ipssi.toiletdata.databinding.MandatoryFragmentBinding
import com.ipssi.toiletdata.events.OnRadioButtonClickListener
import com.ipssi.toiletdata.model.QuestionData

class EssentialFragment : Fragment(), OnRadioButtonClickListener {

    var adapter: CTPTDetailsAdapter? = null

    companion object {
        fun newInstance() = EssentialFragment()
    }

    var score: Int = 0
    override fun onRadioButtonClick() {
        score = 0
        for (question in this.list) {
            if (question.obtainedmark > 0)
                score = score + question.obtainedmark
        }

        ActivityCompat.invalidateOptionsMenu(activity)
    }

    private lateinit var binding: MandatoryFragmentBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<MandatoryFragmentBinding>(layoutInflater, R.layout.mandatory_fragment, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    private var list: ArrayList<QuestionData> = ArrayList<QuestionData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailsViewRecycler.layoutManager = LinearLayoutManager(activity)
        binding.header.setText("Essential")
        adapter = CTPTDetailsAdapter(list, this)
        binding.detailsViewRecycler.adapter = adapter
        binding.detailsViewRecycler.adapter = CTPTDetailsAdapter(list, this)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.ctpt_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.menu_next)
        val bundle = arguments
        val mandatoryScore = bundle?.getInt("mandatoryScore", 0) ?: 0
        if (mandatoryScore >= 70 && score >= 60) {
            item?.setVisible(true)
        } else {
            item?.setVisible(false)
        }
    }

    private var mListener: OnFragmentChangeListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as OnFragmentChangeListener
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_next) {
            arguments?.putInt("essentialScore", score)
            mListener?.onFragmentChange(2, arguments)
        } else if (item?.itemId == R.id.menu_submit) {
            arguments?.putInt("essentialScore", score)
            mListener?.onFragmentChange(4, arguments)
        }
        return super.onOptionsItemSelected(item)
    }


    fun setList(list: ArrayList<QuestionData>) {
        this.list = list
        adapter?.setObject(this.list)
    }
}
