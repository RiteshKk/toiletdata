package gov.mohua.gtl.view.ctpt

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import gov.mohua.gtl.R
import gov.mohua.gtl.util.Utils
import kotlinx.android.synthetic.main.activity_ctptresult.*

class CTPTResultActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ctptresult)
        val data = intent.getParcelableArrayListExtra<gov.mohua.gtl.model.QuestionData>("data")
        btn_done.setOnClickListener { finish() }

        var obtainedMandatory = 0
        var obtainedEssential = 0
        var obtainedDesirable = 0
        var obtainedAdditional = 0
        var mmMandatory = 0
        var meEssential = 0
        var mdDesirable = 0
        var maAdditional = 0
        for (question in data) {
            if (!TextUtils.isEmpty(question.question)) {
                val mark = if (question.obtainedmark >= 0) question.obtainedmark else 0
                when {
                    question.questionType.startsWith("M", true) -> {
                        mmMandatory += question.maxMarks
                        obtainedMandatory += mark
                    }
                    question.questionType.startsWith("E", true) -> {
                        meEssential += question.maxMarks
                        obtainedEssential += mark
                    }
                    question.questionType.startsWith("D", true) -> {
                        mdDesirable += question.maxMarks
                        obtainedDesirable += mark
                    }
                    question.questionType.startsWith("A", true) -> {
                        maAdditional += question.maxMarks
                        obtainedAdditional += mark
                    }
                }
            }
        }

        app_version.text = Utils.getVersionName(this)
        mandatory_result.text = "$obtainedMandatory/$mmMandatory"
        essential_result.text = "$obtainedEssential/$meEssential"
        desirable_result.text = "$obtainedDesirable/$mdDesirable"
        additional_result.text = "$obtainedAdditional/$maAdditional"

    }
}
