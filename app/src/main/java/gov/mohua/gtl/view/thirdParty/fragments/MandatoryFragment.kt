package gov.mohua.gtl.view.thirdParty.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat.invalidateOptionsMenu
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import gov.mohua.gtl.R

class MandatoryFragment : Fragment(),gov.mohua.gtl.events.OnRadioButtonClickListener {
    var score: Int = 0
    var adapter: gov.mohua.gtl.controller.CTPTDetailsAdapter? = null
    override fun onRadioButtonClick() {
        score = 0
        for (question in this.list) {
            if (question.obtainedmark > 0)
                score = score + question.obtainedmark
        }

        invalidateOptionsMenu(activity)
    }

    private var list: ArrayList<gov.mohua.gtl.model.QuestionData> = ArrayList<gov.mohua.gtl.model.QuestionData>()

    companion object {
        fun newInstance() = MandatoryFragment()
    }


    private var mListener: gov.mohua.gtl.OnFragmentChangeListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as gov.mohua.gtl.OnFragmentChangeListener
    }

    private lateinit var binding:gov.mohua.gtl.databinding.MandatoryFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<gov.mohua.gtl.databinding.MandatoryFragmentBinding>(layoutInflater, R.layout.mandatory_fragment, container, false)
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
        adapter =gov.mohua.gtl.controller.CTPTDetailsAdapter(this.list,this)
        binding.detailsViewRecycler.adapter = adapter
    }

    fun setList(list: ArrayList<gov.mohua.gtl.model.QuestionData>) {
        this.list = list
        adapter?.setObject(this.list)
    }
}
