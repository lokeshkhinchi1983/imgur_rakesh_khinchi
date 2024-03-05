package com.rakeshdemo.imgurgallary


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.grid_item_view.view.*
import kotlinx.android.synthetic.main.linear_item_view.view.*
import java.util.*


class GallaryAdapter(private val context:Context,private val TYPE_LINEAR: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable {

    private var list=ArrayList<ImageResponse>()
    private var filterList=ArrayList<ImageResponse>()

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (TYPE_LINEAR==1){
            val root = LayoutInflater.from(context).inflate(R.layout.grid_item_view, null)
            GridHolder(root)
        }else{
            val root = LayoutInflater.from(context).inflate(R.layout.linear_item_view, null)
            LinearHolder(root)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Glide to Image load Progress bar
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.animation_roatate)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform()

        when (TYPE_LINEAR) {
            1 -> {
                val gHolder=holder as GridHolder
                gHolder.title.text=filterList[position].title
                gHolder.moreImage.text=filterList[position].imagesCount.toString()
                // Date convert DD/MM/YYYY
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = filterList[position].datetime!!.toLong()
                gHolder.date.text=calendar.time.convertString("dd/MM/YY hh:mm a")
                //Glide to Image Load
                if (filterList[position].imagesCount.toString().toInt()>0){
                    val gif= filterList[position].images
                    Glide.with(context).load(gif[0].link).apply(options).into(gHolder.profile)
                }
            }
            2 -> {
                val lHolder=holder as LinearHolder
                lHolder.title.text=filterList[position].title
                lHolder.moreImage.text=filterList[position].imagesCount.toString()
                // Date convert DD/MM/YYYY
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = filterList[position].datetime!!.toLong()
                lHolder.date.text=calendar.time.convertString("dd/MM/YY hh:mm a")

                if (filterList[position].imagesCount.toString().toInt()>0){
                    val gif= filterList[position].images
                    // Glide Image Load
                    Glide.with(context).load(gif[0].link).apply(options).into(lHolder.profile)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filterList.count()
    }

    /** Set New List on Adapter **/
    @SuppressLint("NotifyDataSetChanged")
    fun setReload(arrayList: ArrayList<ImageResponse>) {
        list.clear()
        list.addAll(arrayList)
        filterList=list
        notifyDataSetChanged()
    }

    inner class GridHolder(root: View) : RecyclerView.ViewHolder(root) {
        var profile=root.gridImage as ImageView
        var title=root.txtGridTitle as TextView
        var date=root.txtGridDate as TextView
        var moreImage=root.txtGridMoreImage as TextView
        //var progress=root.progress_grid as ProgressBar
    }

    inner class LinearHolder(root: View) : RecyclerView.ViewHolder(root) {
        var profile=root.linearImage as ImageView
        var title=root.txtLinearTitle as TextView
        var date=root.txtLinearDate as TextView
        var moreImage=root.txtLinearMoreImage as TextView
    }

    /*
    * Filter List on Text
    * */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val searchString = charSequence.toString().trim()
                filterList = if (searchString.isEmpty()){
                    list
                }else{
                    // search text is not blank
                    list.filter {
                        it.title!!.contains(searchString)
                    } as ArrayList<ImageResponse>
                }

                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filterList = filterResults?.values as ArrayList<ImageResponse>
                notifyDataSetChanged()
            }
        }
    }
}