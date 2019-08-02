package gov.mohua.gtl.view.thirdParty.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import gov.mohua.gtl.R

class AdditionalFragment : Fragment(),gov.mohua.gtl.events.OnRadioButtonClickListener {

    var adapter: gov.mohua.gtl.controller.CTPTDetailsAdapter? = null
    var score: Int = 0
    override fun onRadioButtonClick() {
        score = 0
        for (question in this.list) {
            if (question.obtainedmark > 0)
                score = score + question.obtainedmark
        }

        ActivityCompat.invalidateOptionsMenu(activity)
    }


    private var mListener: gov.mohua.gtl.OnFragmentChangeListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as gov.mohua.gtl.OnFragmentChangeListener
    }


    companion object {
        fun newInstance() = AdditionalFragment()
    }

    private lateinit var binding:gov.mohua.gtl.databinding.MandatoryFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<gov.mohua.gtl.databinding.MandatoryFragmentBinding>(layoutInflater, R.layout.mandatory_fragment, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    private var list: ArrayList<gov.mohua.gtl.model.QuestionData> = ArrayList<gov.mohua.gtl.model.QuestionData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailsViewRecycler.layoutManager = LinearLayoutManager(activity)
        binding.header.setText("Additional")
//        list = ArrayList<QuestionData>()
        adapter =gov.mohua.gtl.controller.CTPTDetailsAdapter(list,this)
        binding.detailsViewRecycler.adapter = adapter
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.ctpt_menu, menu)
        menu?.findItem(R.id.menu_next)?.setVisible(false)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_next) {
            arguments?.putInt("additionalScore", score)
            mListener?.onFragmentChange(3, arguments)
        } else if (item?.itemId == R.id.menu_submit) {
            arguments?.putInt("additionalScore", score)
            mListener?.onFragmentChange(4, arguments)
        }
        return super.onOptionsItemSelected(item)
    }

    fun setList(list: ArrayList<gov.mohua.gtl.model.QuestionData>) {
        this.list = list
        adapter?.setObject(this.list)
    }
}
