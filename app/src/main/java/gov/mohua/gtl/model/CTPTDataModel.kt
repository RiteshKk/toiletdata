package gov.mohua.gtl.model

import android.os.Parcel
import android.os.Parcelable

class CTPTDataModel() : Parcelable {
    var id: Int = 0
    var state_id: Int = 0
    var city_id: Int = 0
    var published_toilet: Int = 0
    var toilet_status: String? = null
    var toilet_id: String? = null
    var cat_code: String? = null
    var city_toilet_id: String? = null
    var category: String? = null
    var radius: String? = null
    var google_store_code: String? = null
    var place_id: String? = null
    var rating: String? = null
    var sms_rating: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var review_url: String? = null
    var open_days: String? = null
    var open_close_time: String? = null
    var primary_phn_no: String? = null
    var address: String? = null
    var postal_code: String? = null
    var locality: String? = null
    var toilet_data_auto_key: String? = null
    var updated_at: String? = null
    var answer: String? = ""
    var ct_pt_rating:String? = ""
    var third_party_ct_pt_rating:String? = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        state_id = parcel.readInt()
        city_id = parcel.readInt()
        published_toilet = parcel.readInt()
        toilet_status = parcel.readString()
        toilet_id = parcel.readString()
        cat_code = parcel.readString()
        city_toilet_id = parcel.readString()
        category = parcel.readString()
        radius = parcel.readString()
        google_store_code = parcel.readString()
        place_id = parcel.readString()
        rating = parcel.readString()
        sms_rating = parcel.readString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        review_url = parcel.readString()
        open_days = parcel.readString()
        open_close_time = parcel.readString()
        primary_phn_no = parcel.readString()
        address = parcel.readString()
        postal_code = parcel.readString()
        locality = parcel.readString()
        toilet_data_auto_key = parcel.readString()
        updated_at = parcel.readString()
        answer = parcel.readString()
        ct_pt_rating = parcel.readString()
        third_party_ct_pt_rating = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(state_id)
        parcel.writeInt(city_id)
        parcel.writeInt(published_toilet)
        parcel.writeString(toilet_status)
        parcel.writeString(toilet_id)
        parcel.writeString(cat_code)
        parcel.writeString(city_toilet_id)
        parcel.writeString(category)
        parcel.writeString(radius)
        parcel.writeString(google_store_code)
        parcel.writeString(place_id)
        parcel.writeString(rating)
        parcel.writeString(sms_rating)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(review_url)
        parcel.writeString(open_days)
        parcel.writeString(open_close_time)
        parcel.writeString(primary_phn_no)
        parcel.writeString(address)
        parcel.writeString(postal_code)
        parcel.writeString(locality)
        parcel.writeString(toilet_data_auto_key)
        parcel.writeString(updated_at)
        parcel.writeString(answer)
        parcel.writeString(ct_pt_rating)
        parcel.writeString(third_party_ct_pt_rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CTPTDataModel> {
        override fun createFromParcel(parcel: Parcel): CTPTDataModel {
            return CTPTDataModel(parcel)
        }

        override fun newArray(size: Int): Array<CTPTDataModel?> {
            return arrayOfNulls(size)
        }
    }
}
