package com.ipssi.toiletdata.controller

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.events.OnCaptureButtonClickedListener
import com.ipssi.toiletdata.model.C
import com.ipssi.toiletdata.model.ViewData
import com.ipssi.toiletdata.view.nodalOfficer.NodalOfficerDetails
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class NodalOfficerWardDetailsAdapter : RecyclerView.Adapter<NodalOfficerWardDetailsAdapter.ViewHolder>() {
    var mListener: OnCaptureButtonClickedListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.nodal_officer_item_view, parent, false))
    }

    private val dateFormatter: SimpleDateFormat;

    init {
        dateFormatter = SimpleDateFormat("yyyy-MM-dd")
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewData = data?.get(position)
        holder.updatedOn.text = viewData?.nodal_clicked_img ?: viewData?.created_at
        holder.gvpId.text = viewData?.gvp_id
        holder.address.text = viewData?.address
        Picasso.with(holder.itemView.context).load(C.IMAGE_BASE_URL + viewData?.image_path).fit().into(holder.cityAdminImage)
        Picasso.with(holder.itemView.context).load(C.IMAGE_BASE_URL + viewData?.nodal_img).fit().placeholder(android.R.drawable.ic_menu_camera).into(holder.nodalOfficerImage)
        holder.btnProceed.setTag(viewData)
        holder.btnCapture.setTag(position)
        if (viewData?.nodalOfficerImage != null) {
            holder.nodalOfficerImage.setImageBitmap(viewData?.nodalOfficerImage)
        }

        if (viewData?.nodal_clicked_img != null && dateFormatter.format(Date()).equals(viewData?.nodal_clicked_img.split(" ")[0])) {
            holder.todaysPic.setBackgroundResource(R.mipmap.check)
        } else {
            holder.todaysPic.setBackgroundResource(R.mipmap.check_grey)
        }

    }

    private var data: ArrayList<ViewData>? = null

    fun setData(data: ArrayList<ViewData>?) {
        this.data = data;
        notifyDataSetChanged()
    }


    fun setListener(listener: OnCaptureButtonClickedListener?) {
        mListener = listener
    }

    fun update(viewData: ViewData?, position: Int) {
        data?.set(position, viewData!!);
        notifyDataSetChanged()

    }


    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var cityAdminImage: ImageView
        var nodalOfficerImage: ImageView
        var address: TextView
        var gvpId: TextView
        var todaysPic: TextView
        var updatedOn: TextView
        var btnCapture: TextView
        var btnProceed: TextView

        init {
            cityAdminImage = itemView?.findViewById<ImageView>(R.id.image_admin_clicked)!!
            nodalOfficerImage = itemView?.findViewById<ImageView>(R.id.latest_officer_clicked)
            address = itemView?.findViewById<TextView>(R.id.address)
            gvpId = itemView?.findViewById<TextView>(R.id.gvp_id)
            todaysPic = itemView?.findViewById<TextView>(R.id.todays_pic)
            updatedOn = itemView?.findViewById<TextView>(R.id.tv_updated_on)
            btnCapture = itemView?.findViewById<TextView>(R.id.btn_capture)
//            nodalOfficerImage.setOnClickListener(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//
//                }
//            })
            btnCapture?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    val position = v?.getTag() as? Int
                    mListener?.onCaptureButtomClicked(data?.get(position!!), position!!)

//                    val viewData = v?.getTag() as? ViewData
//                    mListener?.onUploadButtonClicked(viewData)
                }
            })

            btnProceed = itemView?.findViewById<TextView>(R.id.btn_proceed)
            btnProceed?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val viewData = p0?.getTag() as? ViewData
                    val intent = Intent(itemView?.context, NodalOfficerDetails::class.java)
                    intent.putExtra(C.DATA_TAG, viewData)
                    itemView?.context.startActivity(intent)
                }

            })
        }
    }
}