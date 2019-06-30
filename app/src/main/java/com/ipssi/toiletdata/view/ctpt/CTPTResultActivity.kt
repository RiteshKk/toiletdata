package com.ipssi.toiletdata.view.ctpt

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.model.QuestionData
import com.ipssi.toiletdata.util.Utils
import kotlinx.android.synthetic.main.activity_ctptresult.*

class CTPTResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ctptresult)
        val data = intent.getParcelableArrayListExtra<QuestionData>("data")
        btn_done.setOnClickListener(View.OnClickListener { it -> finish() })

        var obtainedMandatory: Int = 0
        var obtainedEssential: Int = 0
        var obtainedDesirable: Int = 0
        var obtainedAdditional: Int = 0
        var mmMandatory: Int = 0
        var meEssential: Int = 0
        var mdDesirable: Int = 0
        var maAdditional: Int = 0
        for (question in data) {
            if (!TextUtils.isEmpty(question.question)) {
                val mark = if (question.obtainedmark >= 0) question.obtainedmark else 0
                if (question.questionType.startsWith("M", true)) {
                    mmMandatory += question.maxMarks
                    obtainedMandatory += mark
                } else if (question.questionType.startsWith("E", true)) {
                    meEssential += question.maxMarks
                    obtainedEssential += mark
                } else if (question.questionType.startsWith("D", true)) {
                    mdDesirable += question.maxMarks
                    obtainedDesirable += mark
                } else if (question.questionType.startsWith("A", true)) {
                    maAdditional += question.maxMarks
                    obtainedAdditional += mark
                }
            }
        }

        app_version.text = Utils.getVersionName(this)
        mandatory_result.text = "${obtainedMandatory}/${mmMandatory}"
        essential_result.text = "${obtainedEssential}/${meEssential}"
        desirable_result.text = "${obtainedDesirable}/${mdDesirable}"
        additional_result.text = "${obtainedAdditional}/${maAdditional}"

    }
}
