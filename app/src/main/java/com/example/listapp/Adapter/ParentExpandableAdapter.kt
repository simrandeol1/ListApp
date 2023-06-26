package com.example.listapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.MyApplication
import com.example.listapp.R
import com.example.listapp.ViewHolder.DropDownViewHolder
import com.example.listapp.ViewHolder.HeaderViewHolder
import com.example.listapp.ViewHolder.ImageViewHolder
import com.example.listapp.ViewHolder.NestedViewHolder
import com.example.listapp.ViewHolder.VideoViewHolder
import com.example.listapp.ViewModel.ListItemModel
import com.example.listapp.ViewModel.ListViewModel
import com.example.listapp.utils.StandardParserFactory
import com.google.android.material.snackbar.Snackbar
/**
 * adapter for the expandable recycler view in main activity
 */
class ParentExpandableAdapter(mCtx: Context, listViewModel: ListViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private var actionLock = false
    private val context = mCtx
    private var listVM = listViewModel
    private var languageModels: MutableList<String> = mutableListOf()
    val parserFactory = StandardParserFactory()
    init {
        listViewModel.getLanguageData()
        listViewModel.languagemodel.observe(mCtx as LifecycleOwner){
            languageModels = it
        }
    }

    /**
     * Factory Design pattern to identify the type of header for different view in recycler view
     */

    private var listItemModel: MutableList<ListItemModel> = mutableListOf(
        ListItemModel(ListItemModel.DHEADER, parserFactory.createFromType(ListItemModel.DHEADER), false),
        ListItemModel(ListItemModel.NHEADER, parserFactory.createFromType(ListItemModel.NHEADER), false),
        ListItemModel(ListItemModel.IHEADER, parserFactory.createFromType(ListItemModel.IHEADER), false),
        ListItemModel(ListItemModel.VHEADER, parserFactory.createFromType(ListItemModel.VHEADER), false)
    )

    companion object {
        private const val VIEW_TYPE_DHEADER = 11
        private const val VIEW_TYPE_DROPDOWN = 1
        private const val VIEW_TYPE_NHEADER = 22
        private const val VIEW_TYPE_NESTED = 2
        private const val VIEW_TYPE_IHEADER = 33
        private const val VIEW_TYPE_IMAGE = 3
        private const val VIEW_TYPE_VHEADER = 44
        private const val VIEW_TYPE_VIDEO = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            /**
             * view for drop down list
             */
            VIEW_TYPE_DROPDOWN -> {
                val dropDownViewHolder = inflater.inflate(R.layout.dropdown_layout, parent, false)
                DropDownViewHolder(dropDownViewHolder)
            }
            /**
             * view for nested list with radio button, drop down list, edittext
             */
            VIEW_TYPE_NESTED -> {
                val nestedViewHolder = inflater.inflate(R.layout.nested_layout, parent, false)
                NestedViewHolder(nestedViewHolder)
            }
            /**
             * view for showing images clicked
             */
            VIEW_TYPE_IMAGE -> {
                val imageViewHolder = inflater.inflate(R.layout.image_layout, parent, false)
                ImageViewHolder(imageViewHolder)
            }
            /**
             * view for showing the recorded video
             */
            VIEW_TYPE_VIDEO -> {
                val videoViewHolder = inflater.inflate(R.layout.video_item_layout, parent, false)
                VideoViewHolder(videoViewHolder)
            }
            /**
             * view for headers
             */
            else -> {
                val headerViewHolder = inflater.inflate(R.layout.header_layout, parent, false)
                HeaderViewHolder(headerViewHolder)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItemModel.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = listItemModel[position]

        when(listItemModel[position].headerParser?.type() ?: listItemModel[position].type){

            VIEW_TYPE_DROPDOWN -> {
                listVM.getLanguageData()
                listVM.languagemodel.observe(context as LifecycleOwner){
                    languageModels = it
                    (holder as DropDownViewHolder).spinner.adapter = ArrayAdapter(context,
                        R.layout.spinner_item_layout, languageModels)
                }

                (holder as DropDownViewHolder).spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedText = languageModels[position]
                        listVM.setDropDown(selectedText)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }

            VIEW_TYPE_NESTED ->{
                val recyclerView = (holder as NestedViewHolder).recyclerView
                val layoutManager = LinearLayoutManager(context)
                recyclerView.layoutManager = layoutManager
                listVM.getListData()
                val adapter = ExpandableAdapter(context, listVM)
                recyclerView.adapter = adapter
            }

            VIEW_TYPE_IMAGE -> {
                if(MyApplication.instance.getImageFile().size == 0) {
                    (holder as ImageViewHolder).viewPager.visibility = View.GONE
                    holder.title.text = "No Images"
                }
                else {

                    val adapter = GalleryAdapter(context, MyApplication.instance.getImageFile(), "IMAGE")
                    (holder as ImageViewHolder).viewPager.adapter = adapter
                    holder.viewPager.adapter?.notifyDataSetChanged()
                }
            }

            VIEW_TYPE_VIDEO -> {
                if (MyApplication.instance.isVideoFileEmpty()) {
                    Toast.makeText(context,"Please record a video",Toast.LENGTH_SHORT).show()
                    return
                }
                (holder as VideoViewHolder).videoView.setVideoURI(MyApplication.instance.getVideoFile())
                val mediaController = MediaController(context)
                holder.videoView.setMediaController(mediaController)
                mediaController.setAnchorView(holder.frameLayout)
                holder.videoView.requestFocus()
                holder.videoView.visibility = View.VISIBLE
                holder.videoView.start()
            }

            else -> {
                (holder as HeaderViewHolder).titleTextView.text = parserFactory.getFromType(listItemModel[position].headerParser!!.type()).getHeader()
                holder.itemView.setOnClickListener {
                    if (!actionLock) {
                        actionLock = true
                        row.isExpanded = !row.isExpanded
                        if (row.isExpanded) {
                            holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_expand))
                            collapse(position)
                        } else {
                            holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_collapse))
                            expand(position)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listItemModel[position].headerParser?.type() ?: listItemModel[position].type
    }
    /**
     * function to handle expand operation for the expandable recycler view
     */
    private fun expand(position: Int) {
        var nextPosition = position
                /**
                 * add element just below of clicked row
                 */
        listItemModel.add(++nextPosition, parserFactory.getFromType(listItemModel[position].headerParser!!.type()).addItemModel())
        notifyItemRangeChanged(nextPosition, listItemModel.size)
        actionLock = false
    }
    /**
     * function to handle collapse operation for the expandable recycler view
     */
    private fun collapse(position: Int) {
        val row = listItemModel[position]
        val nextPosition = position + 1

        when (row.type) {

            VIEW_TYPE_DHEADER -> {

                /**
                 * remove element from below until it ends or find another node of same type
                 */
                outerloop@ while (true) {
                    if (nextPosition == listItemModel.size || listItemModel.get(nextPosition).type === VIEW_TYPE_DHEADER || listItemModel.get(nextPosition).type === VIEW_TYPE_NHEADER || listItemModel.get(nextPosition).type === VIEW_TYPE_IHEADER || listItemModel.get(nextPosition).type === VIEW_TYPE_VHEADER) {
                        break@outerloop
                    }
                    listItemModel.removeAt(nextPosition)
                }

                notifyItemRangeChanged(nextPosition, listItemModel.size)
            }
            VIEW_TYPE_NHEADER -> {

                /**
                 * remove element from below until it ends or find another node of same type
                 */
                outerloop@ while (true) {
                    if (nextPosition == listItemModel.size || listItemModel.get(nextPosition).type === VIEW_TYPE_NHEADER || listItemModel.get(nextPosition).type === VIEW_TYPE_IHEADER || listItemModel.get(nextPosition).type === VIEW_TYPE_VHEADER) {
                        break@outerloop
                    }

                    listItemModel.removeAt(nextPosition)
                }

                notifyItemRangeChanged(nextPosition, listItemModel.size)

            }
            VIEW_TYPE_IHEADER -> {

                /**
                 * remove element from below until it ends or find another node of same type
                 */
                outerloop@ while (true) {
                    if (nextPosition == listItemModel.size || listItemModel.get(nextPosition).type === VIEW_TYPE_IHEADER || listItemModel.get(nextPosition).type === VIEW_TYPE_VHEADER) {
                        break@outerloop
                    }

                    listItemModel.removeAt(nextPosition)
                }

                notifyItemRangeChanged(nextPosition, listItemModel.size)

            }
            VIEW_TYPE_VHEADER -> {

                /**
                 * remove element from below until it ends or find another node of same type
                 */
                outerloop@ while (true) {
                    if (nextPosition == listItemModel.size || listItemModel.get(nextPosition).type === VIEW_TYPE_VHEADER) {
                        break@outerloop
                    }

                    listItemModel.removeAt(nextPosition)
                }

                notifyItemRangeChanged(nextPosition, listItemModel.size)
            }
        }
        actionLock = false
    }
}