package gov.mohua.gtl.controller

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import gov.mohua.gtl.R
import gov.mohua.gtl.model.C
import gov.mohua.gtl.model.ToiletData
import java.util.*

class ToiletDataAdapter() : RecyclerView.Adapter<ToiletDataAdapter.ToiletDataHolder>() {
    private var data: List<ToiletData> = ArrayList()

    constructor(data: List<ToiletData>?) : this() {
        if (data != null) {
            this.data = data
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToiletDataHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
        return ToiletDataHolder(view)
    }

    override fun onBindViewHolder(holder: ToiletDataHolder, position: Int) {
        val toiletData = data[position]
        holder.toiletId.text = Html.fromHtml("Toilet Id : <b>" + toiletData.toiletId + "</b>")
        holder.createdAt.text = Html.fromHtml("Created At  : <b>" + toiletData.createdAt + "</b>")
        holder.address.text = Html.fromHtml(C.address + " : <b>" + toiletData.toiletAddress + "</b>")
        holder.pinCode.text = Html.fromHtml(C.pinCode + " : <b>" + toiletData.pinCode + "</b>")
        val s = toiletData.imageUrl3
        if (s != null && s.length > 0) {
            Picasso.with(holder.itemView.context).load(C.IMAGE_BASE_URL + s).resize(100, 100).centerInside().placeholder(R.mipmap.icon).error(R.mipmap.icon).into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setToiletDataList(toiletDataList: List<ToiletData>) {
        data = toiletDataList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): ToiletData {
        return data[position]
    }


    inner class ToiletDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var toiletId: TextView
        var address: TextView
        var createdAt: TextView
        var pinCode: TextView
        var image: ImageView

        init {
            image = itemView.findViewById(R.id.show_preview)
            address = itemView.findViewById(R.id.recycler_view_address)
            toiletId = itemView.findViewById(R.id.recycler_view_toilet_id)
            createdAt = itemView.findViewById(R.id.recycler_view_created_at)
            pinCode = itemView.findViewById(R.id.recycler_view_pincode)
        }
    }
}
