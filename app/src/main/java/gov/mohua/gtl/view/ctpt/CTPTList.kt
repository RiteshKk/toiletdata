package gov.mohua.gtl.view.ctpt

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import gov.mohua.gtl.R
import gov.mohua.gtl.model.C
import gov.mohua.gtl.util.Utils
import kotlinx.android.synthetic.main.activity_ctptlist.*

class CTPTList : AppCompatActivity() {

    private lateinit var ctptListViewModel:gov.mohua.gtl.viewModel.CTPTListViewModel

    private lateinit var adapter:gov.mohua.gtl.controller.CTPTListAdapter

    private lateinit var binding:gov.mohua.gtl.databinding.ActivityCtptlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ctptlist)
        ctptListViewModel =gov.mohua.gtl.viewModel.CTPTListViewModel(application)
        binding.viewModel = ctptListViewModel
        if (intent.getStringExtra("type").equals(C.THIRD_PARTY, ignoreCase = true))
            supportActionBar?.title = "CTPT-Third-Party Validation Tool"

        observeData()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =gov.mohua.gtl.controller.CTPTListAdapter(applicationContext)
        binding.recyclerView.adapter = adapter

        app_version.text = Utils.getVersionName(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        ctptListViewModel.setLoadData(true)
        observeData()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_show)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)?.edit()?.putBoolean(C.IS_LOGGED_IN, false)?.apply()
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun observeData() =
            ctptListViewModel.observeData.observe(this, Observer<gov.mohua.gtl.utility.LiveDataWrapper<gov.mohua.gtl.model.CTPTResponseModel>> {
                val data = it?.data
                adapter.run {
                    setData(data?.data, data?.totalMarks.toString())
                }
            })

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
//            val id = data?.getStringExtra("id");
//            val marks = data?.getStringExtra("marks");
//            updateModel(id,marks);
//        }
//    }
}
