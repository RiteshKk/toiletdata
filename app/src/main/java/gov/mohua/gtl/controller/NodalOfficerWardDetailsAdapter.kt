package gov.mohua.gtl.controller

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import gov.mohua.gtl.R
import gov.mohua.gtl.model.C
import gov.mohua.gtl.view.nodalOfficer.NodalOfficerDetails
import java.text.SimpleDateFormat
import java.util.*

class NodalOfficerWardDetailsAdapter : RecyclerView.Adapter<NodalOfficerWardDetailsAdapter.ViewHolder>() {
    var mListener: gov.mohua.gtl.events.OnCaptureButtonClickedListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.nodal_officer_item_view, parent, false))
    }

    private val dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewData = data?.get(position)
        holder.updatedOn.text = viewData?.nodal_clicked_img ?: viewData?.created_at
        holder.officerImageTime.text = viewData?.nodal_clicked_img ?: ""
        holder.adminImageTime.text = viewData?.created_at
        holder.gvpId.text = viewData?.gvp_id
        holder.address.text = viewData?.address
        Picasso.with(holder.itemView.context)
                .load(C.IMAGE_BASE_URL + viewData?.image_path)
                .fit().into(holder.cityAdminImage)
        Picasso.with(holder.itemView.context)
                .load(C.IMAGE_BASE_URL + viewData?.nodal_img)
                .fit().placeholder(android.R.drawable.ic_menu_camera)
                .into(holder.nodalOfficerImage)
        holder.btnProceed.tag = viewData
        holder.btnCapture.tag = position
        holder.nodalOfficerImage.tag = position
        if (viewData?.nodalOfficerImage != null) {
            holder.run { nodalOfficerImage.setImageBitmap(viewData.nodalOfficerImage) }
        }

        if (viewData?.nodal_clicked_img != null && dateFormatter.format(Date()).equals(viewData.nodal_clicked_img.split(" ")[0])) {
            holder.todaysPic.setBackgroundResource(R.mipmap.check)
        } else {
            holder.todaysPic.setBackgroundResource(R.mipmap.check_grey)
        }

    }

    private var data: ArrayList<gov.mohua.gtl.model.ViewData>? = null

    fun setData(data: ArrayList<gov.mohua.gtl.model.ViewData>?) {
        this.data = data
        notifyDataSetChanged()
    }


    fun setListener(listener: gov.mohua.gtl.events.OnCaptureButtonClickedListener?) {
        mListener = listener
    }

    fun update(viewData: gov.mohua.gtl.model.ViewData?,position: Int) {
        data?.set(position, viewData!!)
        notifyDataSetChanged()

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cityAdminImage: ImageView
        var nodalOfficerImage: ImageView
        var address: TextView
        var adminImageTime: TextView
        var officerImageTime: TextView
        var gvpId: TextView
        var todaysPic: TextView
        var updatedOn: TextView
        var btnCapture: TextView
        var btnProceed: TextView

        init {
            cityAdminImage = itemView?.findViewById(R.id.image_admin_clicked)!!
            nodalOfficerImage = itemView.run { findViewById(R.id.latest_officer_clicked) }
            address = itemView.run { findViewById(R.id.address) }
            adminImageTime = itemView.run { findViewById(R.id.admin_image_date_time) }
            officerImageTime = itemView.run { findViewById(R.id.nodal_image_date_time) }
            gvpId = itemView.run { findViewById(R.id.gvp_id) }
            todaysPic = itemView.run { findViewById(R.id.todays_pic) }
            updatedOn = itemView.run { findViewById(R.id.tv_updated_on) }
            btnCapture = itemView.run { findViewById(R.id.btn_capture) }
            nodalOfficerImage.setOnClickListener { v ->
                val position = v?.getTag() as? Int
                mListener?.onCaptureButtomClicked(data?.get(position!!), position!!)
            }
            btnCapture.setOnClickListener { v ->
                val position = v?.getTag() as? Int
                mListener?.onCaptureButtomClicked(data?.get(position!!), position!!)
            }

            btnProceed = itemView.run { findViewById(R.id.btn_proceed) }
            btnProceed.setOnClickListener { p0 ->
                val viewData = p0?.getTag() as? gov.mohua.gtl.model.ViewData
                val intent = Intent(
                        itemView.context,
                        NodalOfficerDetails::class.java
                ).apply {
                    putExtra(C.DATA_TAG, viewData)
                }
                with(itemView) {
                    context.startActivity(intent)
                }
            }
        }
    }
}