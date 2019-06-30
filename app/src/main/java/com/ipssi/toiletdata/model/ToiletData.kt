package com.ipssi.toiletdata.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ToiletData : Parcelable {

    @SerializedName(value = "id")
    var toiletId: String? = null
    @SerializedName(value = "open_days")
    lateinit var openDays: String
    @SerializedName(value = "image_url")
    var imageUrl1: String? = null
    @SerializedName(value = "image_url_two")
    var imageUrl2: String? = null
    @SerializedName(value = "image_url_three")
    var imageUrl3: String? = null
    var category: String? = null
    @SerializedName(value = "state_code")
    var stateCode: Int = 0
    @SerializedName(value = "city_cencus_code")
    var cityCode: Int = 0
    @SerializedName(value = "state_id")
    var stateId: Int = 0
    @SerializedName(value = "city_id")
    var cityId: Int = 0
    var seats: Int = 0
    var fee: String? = null
    var cost: Int = 0
    var stateName: String? = null
    var cityName: String? = null
    @SerializedName(value = "assessor_name")
    var assessorName: String? = null
    @SerializedName(value = "assessor_phn_no")
    var assessorPhoneNo: String? = null
    var zone: String? = null
    var ward: String? = null
    var type = ""
    @SerializedName(value = "address")
    var toiletAddress: String? = null
    @SerializedName(value = "maintainance_authority")
    var authority: String? = null
    @SerializedName(value = "care_taker_name")
    var careTakerName: String? = null
    @SerializedName(value = "care_taker_phn_no")
    var careTakerPhoneNo: String? = null
    @SerializedName(value = "opening_time")
    var openingTime: String? = null
    @SerializedName(value = "closing_time")
    var closingTime: String? = null
    var gender: String? = null
    @SerializedName(value = "pincode")
    var pinCode: String? = null
    @SerializedName(value = "child_friendly")
    private var childFriendly: String? = null
    @SerializedName(value = "differently_abled_friendly")
    private var differentlyAbledFriendly: String? = null
    @SerializedName(value = "availability_of_sanitory_napkin_machine")
    private var napkinMachine: String? = null
    @SerializedName(value = "availability_of_incinerator")
    private var incinerator: String? = null
    @SerializedName(value = "availablity_of_water_atm")
    private var waterAtm: String? = null
    var openingDays = booleanArrayOf(true, true, true, true, true, true, true)
    @SerializedName(value = "latitude")
    var lat: Double = 0.toDouble()
        private set
    @SerializedName(value = "longitude")
    var lng: Double = 0.toDouble()
        private set
    @SerializedName(value = "created_at")
    var createdAt: String? = null
    @SerializedName(value = "owner_authority")
    var toiletLocatedAt: String? = null
    var otherLocation: String? = null

    var otherAuthority: String? = null


    val isChildFriendly: Boolean
        get() = childFriendly?.equals("yes", ignoreCase = true) ?: false

    val isDifferentlyAbledFriendly: Boolean
        get() = differentlyAbledFriendly?.equals("yes", ignoreCase = true) ?: false

    val isNapkinMachine: Boolean
        get() = napkinMachine?.equals("yes", ignoreCase = true) ?: false

    val isIncinerator: Boolean
        get() = incinerator?.equals("yes", ignoreCase = true) ?: false

    val isWaterAtm: Boolean
        get() = waterAtm?.equals("yes", ignoreCase = true) ?: false

    val openingDaysString: String
        get() {
            val daysName = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            val days = StringBuilder()
            for (i in daysName.indices) {
                if (openingDays[i]) {
                    days.append(if (days.length > 0) "," + daysName[i] else daysName[i])
                }
            }
            return days.toString()
        }

    fun setLat(lat: Long) {
        this.lat = lat.toDouble()
    }

    fun setLng(lng: Long) {
        this.lng = lng.toDouble()
    }

    fun setChildFriendly(childFriendly: String) {
        this.childFriendly = childFriendly
    }

    fun setDifferentlyAbledFriendly(differentlyAbledFriendly: String) {
        this.differentlyAbledFriendly = differentlyAbledFriendly
    }

    fun setNapkinMachine(napkinMachine: String) {
        this.napkinMachine = napkinMachine
    }

    fun setIncinerator(incinerator: String) {
        this.incinerator = incinerator
    }

    fun setWaterAtm(waterAtm: String) {
        this.waterAtm = waterAtm
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(toiletId)
        dest.writeDouble(lat)
        dest.writeDouble(lng)
        dest.writeInt(stateCode)
        dest.writeInt(cityCode)
        dest.writeInt(stateId)
        dest.writeInt(cityId)
        dest.writeInt(seats)
        dest.writeString(fee)
        dest.writeInt(cost)
        dest.writeString(openDays)
        dest.writeString(imageUrl1)
        dest.writeString(imageUrl2)
        dest.writeString(imageUrl3)

        dest.writeString(stateName)
        dest.writeString(cityName)
        dest.writeString(assessorName)
        dest.writeString(assessorPhoneNo)
        dest.writeString(zone)
        dest.writeString(ward)
        dest.writeString(type)
        dest.writeString(toiletAddress)
        dest.writeString(otherLocation)
        dest.writeString(authority)
        dest.writeString(careTakerName)
        dest.writeString(careTakerPhoneNo)
        dest.writeString(openingTime)
        dest.writeString(closingTime)
        dest.writeString(gender)
        dest.writeString(pinCode)
        dest.writeString(category)
        dest.writeBooleanArray(openingDays)
        dest.writeString(childFriendly)
        dest.writeString(differentlyAbledFriendly)
        dest.writeString(napkinMachine)
        dest.writeString(incinerator)
        dest.writeString(waterAtm)
        dest.writeString(createdAt)
        dest.writeString(toiletLocatedAt)
    }

    constructor() {
        openDays = ""
    }

    constructor(`in`: Parcel) : this() {
        this.toiletId = `in`.readString()
        this.lat = `in`.readDouble()
        this.lng = `in`.readDouble()
        this.stateCode = `in`.readInt()
        this.cityCode = `in`.readInt()
        this.stateId = `in`.readInt()
        this.cityId = `in`.readInt()
        this.seats = `in`.readInt()
        this.fee = `in`.readString()
        this.cost = `in`.readInt()
        this.openDays = `in`.readString()
        this.imageUrl1 = `in`.readString()
        this.imageUrl2 = `in`.readString()
        this.imageUrl3 = `in`.readString()

        this.stateName = `in`.readString()
        this.cityName = `in`.readString()
        this.assessorName = `in`.readString()
        this.assessorPhoneNo = `in`.readString()
        this.zone = `in`.readString()
        this.ward = `in`.readString()
        this.type = `in`.readString() ?: ""
        this.toiletAddress = `in`.readString()
        this.otherLocation = `in`.readString()
        this.authority = `in`.readString()
        this.careTakerName = `in`.readString()
        this.careTakerPhoneNo = `in`.readString()
        this.openingTime = `in`.readString()
        this.closingTime = `in`.readString()
        this.gender = `in`.readString()
        this.pinCode = `in`.readString()
        this.category = `in`.readString()
        `in`.readBooleanArray(this.openingDays)
        this.childFriendly = `in`.readString()
        this.differentlyAbledFriendly = `in`.readString()
        this.napkinMachine = `in`.readString()
        this.incinerator = `in`.readString()
        this.waterAtm = `in`.readString()
        this.createdAt = `in`.readString()
        this.toiletLocatedAt = `in`.readString()
    }

    companion object CREATOR : Parcelable.Creator<ToiletData> {
        override fun createFromParcel(`in`: Parcel): ToiletData {
            return ToiletData(`in`)
        }

        override fun newArray(size: Int): Array<ToiletData?> {
            return arrayOfNulls(size)
        }
    }
}
