package gov.mohua.gtl.view.gtl

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import gov.mohua.gtl.R
import gov.mohua.gtl.events.OnFragmentInteractionListener
import gov.mohua.gtl.model.C
import gov.mohua.gtl.model.ToiletData
import gov.mohua.gtl.util.Utils

class LocalDetailsFragment : Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private var mListener: OnFragmentInteractionListener? = null

    private var seats: TextInputLayout? = null
    private var otherLocation: TextInputLayout? = null
    private var otherMaintainingAuthority: TextInputLayout? = null
    private var careTakerName: TextInputLayout? = null
    private var careTakerPhone: TextInputLayout? = null
    private var cost: TextInputLayout? = null
    private var openingTime: Spinner? = null
    private var closingTime: Spinner? = null
    private var childFriedly: CheckBox? = null
    private var diffAbledFriedly: CheckBox? = null
    private var waterAtm: CheckBox? = null
    private var napkin: CheckBox? = null
    private var incinerator: CheckBox? = null
    private var sunday: TextView? = null
    private var monday: TextView? = null
    private var tuesday: TextView? = null
    private var wednesday: TextView? = null
    private var thursday: TextView? = null
    private var friday: TextView? = null
    private var saturday: TextView? = null
    //    private var isOpening: Boolean = false
    private var gender: Spinner? = null
    private var fee: Spinner? = null
    private var spinToiletLocation: Spinner? = null
    private var maintenanceAuthority: Spinner? = null
    private var modelData: ToiletData? = null
    private lateinit var openingText: TextView
    private lateinit var closingText: TextView

//    internal var onTimeSetListener: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//        var time = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
//        time = if (minute < 10) "$time:0$minute" else "$time:$minute"
//        if (isOpening) {
//            openingTime?.editText?.setText(time)
//        } else {
//            closingTime?.editText?.setText(time)
//        }
//    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_local_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modelData = arguments?.getParcelable(C.DATA_TAG)
        otherLocation = view.findViewById(R.id.edt_located_at_other)
        maintenanceAuthority = view.findViewById(R.id.spinner_toilet_maintenance_auth)
        otherMaintainingAuthority = view.findViewById(R.id.edt_maintaining_auth_other)
        careTakerName = view.findViewById(R.id.edt_caretaker_name)
        careTakerPhone = view.findViewById(R.id.edt_caretaker_phone)
        seats = view.findViewById(R.id.edt_seat)
        seats?.hint = Html.fromHtml(getString(R.string.seat))
        careTakerName?.hint = Html.fromHtml(getString(R.string.caretaker_name))
        careTakerPhone?.hint = Html.fromHtml(getString(R.string.caretaker_phone))
        otherLocation?.hint = Html.fromHtml(getString(R.string.other))
        cost = view.findViewById(R.id.edt_cost)
        openingText = view.findViewById(R.id.txt_opening_time)
        closingText = view.findViewById(R.id.txt_closing_time)
        openingText.text = Html.fromHtml(getString(R.string.opening_time))
        closingText.text = Html.fromHtml(getString(R.string.closing_time))



        (view.findViewById<View>(R.id.app_version) as TextView).text = Utils.getVersionName(activity)


        sunday = view.findViewById(R.id.sunday)
        sunday?.setOnClickListener(this)
        sunday?.isSelected = true
        monday = view.findViewById(R.id.monday)
        monday?.setOnClickListener(this)
        monday?.isSelected = true
        tuesday = view.findViewById(R.id.tuesday)
        tuesday?.setOnClickListener(this)
        tuesday?.isSelected = true
        wednesday = view.findViewById(R.id.wednesday)
        wednesday?.setOnClickListener(this)
        wednesday?.isSelected = true
        thursday = view.findViewById(R.id.thursday)
        thursday?.setOnClickListener(this)
        thursday?.isSelected = true
        friday = view.findViewById(R.id.friday)
        friday?.setOnClickListener(this)
        friday?.isSelected = true
        saturday = view.findViewById(R.id.saturday)
        saturday?.setOnClickListener(this)
        saturday?.isSelected = true

        openingTime = view.findViewById(R.id.spinner_opening_time)
        openingTime?.onItemSelectedListener = this
        closingTime = view.findViewById(R.id.spinner_closing_time)
        closingTime?.onItemSelectedListener = this

        gender = view.findViewById(R.id.gender)
        fee = view.findViewById(R.id.fee)
        spinToiletLocation = view.findViewById(R.id.spinner_toilet_location)
        gender?.onItemSelectedListener = this
        fee?.onItemSelectedListener = this
        spinToiletLocation?.onItemSelectedListener = this
        maintenanceAuthority?.onItemSelectedListener = this

        childFriedly = view.findViewById(R.id.child_friendly)
        diffAbledFriedly = view.findViewById(R.id.differently_abled_friendly)
        waterAtm = view.findViewById(R.id.availablity_of_water_atm)
        napkin = view.findViewById(R.id.availability_of_sanitory_napkin_machine)
        incinerator = view.findViewById(R.id.availability_of_incinerator)

        setDataToViews()
        (view.findViewById<View>(R.id.btn_next) as ImageView).setOnClickListener(this)
        (view.findViewById<View>(R.id.btn_back) as ImageView).setOnClickListener(this)
    }

    private fun setDataToViews() {
        careTakerName?.editText?.setText(modelData?.careTakerName)
        careTakerPhone?.editText?.setText(modelData?.careTakerPhoneNo)
        seats?.editText?.setText(if (modelData?.seats == 0) "" else modelData?.seats.toString())
        sunday?.isSelected = modelData!!.openingDays[0]
        monday?.isSelected = modelData!!.openingDays[1]
        tuesday?.isSelected = modelData!!.openingDays[2]
        wednesday?.isSelected = modelData!!.openingDays[3]
        thursday?.isSelected = modelData!!.openingDays[4]
        friday?.isSelected = modelData!!.openingDays[5]
        saturday?.isSelected = modelData!!.openingDays[6]
        openingTime?.setSelection(getTimeSlotPosition(modelData?.openingTime))
        closingTime?.setSelection(getTimeSlotPosition(modelData?.closingTime))

        val genderArray = resources.getStringArray(R.array.gender)
        for (i in genderArray.indices) {
            if (genderArray[i].equals(modelData?.gender, ignoreCase = true)) {
                gender?.setSelection(i)
                break
            }
        }
        val toiletLocationArray = resources.getStringArray(R.array.toilet_location)
        for (i in toiletLocationArray.indices) {
            if (toiletLocationArray[i].equals(modelData?.toiletLocatedAt, ignoreCase = true)) {
                spinToiletLocation?.setSelection(i)
                break
            }
        }
        otherLocation?.editText?.setText(modelData?.otherLocation)

        val responsibleAuthority = resources.getStringArray(R.array.responsible_org)
        for (i in responsibleAuthority.indices) {
            if (responsibleAuthority[i].equals(modelData?.authority, ignoreCase = true)) {
                maintenanceAuthority?.setSelection(i)
                break
            }
        }
        otherMaintainingAuthority?.editText?.setText(modelData?.otherAuthority)
        childFriedly?.isChecked = modelData!!.isChildFriendly
        diffAbledFriedly?.isChecked = modelData!!.isDifferentlyAbledFriendly
        waterAtm?.isChecked = modelData!!.isWaterAtm
        napkin?.isChecked = modelData!!.isNapkinMachine
        incinerator?.isChecked = modelData!!.isIncinerator

        val feeArray = resources.getStringArray(R.array.fee)
        if (feeArray[1].equals(modelData?.fee, ignoreCase = true)) {
            fee?.setSelection(1)
        } else {
            fee?.setSelection(0)
        }
        cost?.editText?.setText(modelData?.cost.toString())
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context?.toString() + " must implement OnFragmentInteractionListener")
        }
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(C.ULB_NAME, otherLocation?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.MAINTENANCE, otherMaintainingAuthority?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.CARE_TAKER, careTakerName?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.CARE_TAKER_PHONE, careTakerPhone?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.SEAT, seats?.editText?.text.toString().trim { it <= ' ' })
        outState.putInt(C.OPENING_TIME, openingTime?.selectedItemPosition!!)
        outState.putInt(C.CLOSING_TIME, closingTime?.selectedItemPosition!!)
        outState.putString(C.COST, cost?.editText?.text.toString().trim { it <= ' ' })

        outState.putBoolean(C.SUNDAY, sunday!!.isSelected)
        outState.putBoolean(C.MONDAY, monday!!.isSelected)
        outState.putBoolean(C.TUESDAY, tuesday!!.isSelected)
        outState.putBoolean(C.THURSDAY, wednesday!!.isSelected)
        outState.putBoolean(C.FRIDAY, thursday!!.isSelected)
        outState.putBoolean(C.SATURDAY, friday!!.isSelected)
        outState.putBoolean(C.WEDNESDAY, saturday!!.isSelected)
        outState.putInt(C.GENDER, gender!!.selectedItemPosition)
        outState.putInt(C.FEE, fee!!.selectedItemPosition)


        outState.putBoolean(C.CHILD, childFriedly!!.isChecked)
        outState.putBoolean(C.DISABLED, diffAbledFriedly!!.isChecked)
        outState.putBoolean(C.WATER_ATM, waterAtm!!.isChecked)
        outState.putBoolean(C.NAPKIN, napkin!!.isChecked)
        outState.putBoolean(C.INCINERATOR, incinerator!!.isChecked)

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            otherLocation?.editText?.setText(savedInstanceState.getString(C.ULB_NAME))
            otherMaintainingAuthority?.editText?.setText(savedInstanceState.getString(C.MAINTENANCE))
            careTakerName?.editText?.setText(savedInstanceState.getString(C.CARE_TAKER))
            careTakerPhone?.editText?.setText(savedInstanceState.getString(C.CARE_TAKER_PHONE))
            seats?.editText?.setText(savedInstanceState.getString(C.SEAT))
            sunday?.isSelected = savedInstanceState.getBoolean(C.SUNDAY)
            monday?.isSelected = savedInstanceState.getBoolean(C.MONDAY)
            tuesday?.isSelected = savedInstanceState.getBoolean(C.TUESDAY)
            wednesday?.isSelected = savedInstanceState.getBoolean(C.WEDNESDAY)
            thursday?.isSelected = savedInstanceState.getBoolean(C.THURSDAY)
            friday?.isSelected = savedInstanceState.getBoolean(C.FRIDAY)
            saturday?.isSelected = savedInstanceState.getBoolean(C.SATURDAY)
            openingTime?.setSelection(savedInstanceState.getInt(C.OPENING_TIME))
            closingTime?.setSelection(savedInstanceState.getInt(C.CLOSING_TIME))
            gender?.setSelection(savedInstanceState.getInt(C.GENDER))

            childFriedly?.isChecked = savedInstanceState.getBoolean(C.CHILD)
            diffAbledFriedly?.isChecked = savedInstanceState.getBoolean(C.DISABLED)
            waterAtm?.isChecked = savedInstanceState.getBoolean(C.WATER_ATM)
            napkin?.isChecked = savedInstanceState.getBoolean(C.NAPKIN)
            incinerator?.isChecked = savedInstanceState.getBoolean(C.INCINERATOR)

            fee?.setSelection(savedInstanceState.getInt(C.FEE))
            cost?.editText?.setText(savedInstanceState.getString(C.COST))
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        when (parent.id) {
            R.id.gender -> if (position == 2 || position == 3 || position == 4) {
                napkin?.visibility = View.VISIBLE
                incinerator?.visibility = View.VISIBLE
            } else {
                napkin?.visibility = View.GONE
                napkin?.isChecked = false
                incinerator?.visibility = View.GONE
                incinerator?.isChecked = false
            }
            R.id.fee -> cost?.visibility = if (position == 1) View.VISIBLE else View.GONE
            R.id.spinner_toilet_location -> otherLocation?.visibility = if (position == 10) View.VISIBLE else View.GONE
            R.id.spinner_toilet_maintenance_auth -> otherMaintainingAuthority?.visibility = if (position == 2) View.VISIBLE else View.GONE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    override fun onClick(v: View) {
        val windowToken = view?.windowToken
        when (v.id) {
            R.id.sunday -> v.isSelected = !v.isSelected
            R.id.monday -> v.isSelected = !v.isSelected
            R.id.tuesday -> v.isSelected = !v.isSelected
            R.id.wednesday -> v.isSelected = !v.isSelected
            R.id.thursday -> v.isSelected = !v.isSelected
            R.id.friday -> v.isSelected = !v.isSelected
            R.id.saturday -> v.isSelected = !v.isSelected
            R.id.btn_back -> {
                try {
                    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (windowToken != null) {
                        imm.hideSoftInputFromWindow(windowToken, 0)
                        view?.clearFocus()
                    }
                } catch (e: NullPointerException) {
                    Log.e("Local Details", "btnBack NullPointer")
                }

                createModel()
                mListener?.onFragmentInteraction("1")
            }
            R.id.btn_next -> {
                try {
                    val imm1 = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (windowToken != null) {
                        imm1.hideSoftInputFromWindow(windowToken, 0)
                        view?.clearFocus()
                    }
                } catch (e: NullPointerException) {
                    Log.e("Local Details", "btnNext NullPointer")
                }

                val careTakername = careTakerName?.editText?.text.toString().trim()
                val seatNo = seats?.editText?.text.toString().trim()
                val mobileNumber = careTakerPhone?.editText?.text.toString().trim { it <= ' ' }
                val openTime = openingTime?.selectedItemPosition
                val closeTime = closingTime?.selectedItemPosition
                val sex = gender?.selectedItemPosition

                if (careTakername.isEmpty()) {
                    careTakerName?.error = "Enter Care Taker name"
                    return
                } else {
                    careTakerName?.error = null
                }
                if (mobileNumber.isEmpty()) {
                    careTakerPhone?.error = "Enter Mobile Number"
                } else if (mobileNumber.length in 1..9) {
                    careTakerPhone?.error = "Invalid care taker Mobile Number"
                    return
                } else {
                    careTakerPhone?.error = null
                }
                if (seatNo.isEmpty()) {
                    seats?.error = "Enter Seat No"
                    return
                } else {
                    seats?.error = null
                }

                if (openTime == null || closeTime == null || openTime >= closeTime || (openTime == 0 && closeTime == 48)) {
                    Snackbar.make(view!!, "Closing Time must be greater than Opening Time", Snackbar.LENGTH_LONG).show()
                    return
                }

                if (sex == null || sex == 0) {
                    Snackbar.make(view!!, "Select Gender", Snackbar.LENGTH_LONG).show()
                    return
                }

                createModel()
            }
        }

    }

    private fun createModel() {
        modelData?.otherAuthority = otherMaintainingAuthority?.editText?.text.toString().trim { it <= ' ' }
        modelData?.careTakerName = careTakerName?.editText?.text.toString().trim { it <= ' ' }
        modelData?.careTakerPhoneNo = careTakerPhone?.editText?.text.toString().trim { it <= ' ' }
        modelData!!.openingDays[0] = sunday?.isSelected ?: false
        modelData!!.openingDays[1] = monday?.isSelected ?: false
        modelData!!.openingDays[2] = tuesday?.isSelected ?: false
        modelData!!.openingDays[3] = wednesday?.isSelected ?: false
        modelData!!.openingDays[4] = thursday?.isSelected ?: false
        modelData!!.openingDays[5] = friday?.isSelected ?: false
        modelData!!.openingDays[6] = saturday?.isSelected ?: false
        if (modelData?.openingDaysString!!.trim { it <= ' ' }.length == 0) {
            Snackbar.make(view!!, "Please Select Opening Days", Snackbar.LENGTH_LONG).show()
            return
        }
        val seatText = seats?.editText?.text.toString().trim { it <= ' ' }
        modelData?.seats = if (seatText.equals("", ignoreCase = true)) 0 else Integer.parseInt(seatText)
        modelData?.openingTime = getTimeSlotString(openingTime?.selectedItemPosition)
        modelData?.closingTime = getTimeSlotString(closingTime?.selectedItemPosition)
        modelData?.careTakerPhoneNo = careTakerPhone?.editText?.text.toString().trim { it <= ' ' }
        val genderValue = if (gender?.selectedItemPosition == 0) "" else resources.getStringArray(R.array.gender)[gender!!.selectedItemPosition]
        modelData?.gender = genderValue
        modelData?.setChildFriendly(if (childFriedly!!.isChecked) "yes" else "No")
        modelData?.setDifferentlyAbledFriendly(if (diffAbledFriedly!!.isChecked) "yes" else "No")
        modelData?.setWaterAtm(if (waterAtm!!.isChecked) "yes" else "No")
        modelData?.setNapkinMachine(if (napkin!!.isChecked) "yes" else "No")
        modelData?.setIncinerator(if (incinerator!!.isChecked) "yes" else "No")
        modelData?.fee = fee?.selectedItem as String
        val costText = cost?.editText?.text.toString().trim { it <= ' ' }
        modelData?.cost = if (costText.length > 0) Integer.parseInt(costText) else 0
        modelData?.toiletLocatedAt = resources.getStringArray(R.array.toilet_location)[spinToiletLocation!!.selectedItemPosition]
        modelData?.otherLocation = otherLocation?.editText?.text.toString().trim { it <= ' ' }

        modelData?.authority = resources.getStringArray(R.array.responsible_org)[maintenanceAuthority!!.selectedItemPosition]
        modelData?.otherAuthority = otherMaintainingAuthority?.editText?.text.toString().trim { it <= ' ' }
        mListener?.onFragmentInteraction("3")
    }

    private fun getTimeSlotPosition(timeSlot: String?): Int {
        val timeArray = resources.getStringArray(R.array.time_list)
        for (i in timeArray.indices) {
            if (timeArray[i].equals(timeSlot, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun getTimeSlotString(position: Int?): String {
        val timeArray = resources.getStringArray(R.array.time_list)
        return timeArray[position!!]
    }
}
