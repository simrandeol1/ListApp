package com.example.listapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.Model.RowModel
import com.example.listapp.R
import com.example.listapp.ViewHolder.ChildItemViewHolder
import com.example.listapp.ViewHolder.GrandchildItemViewHolder
import com.example.listapp.ViewHolder.ParentItemViewHolder
import com.example.listapp.ViewModel.ListViewModel


class ExpandableAdapter(mCtx: Context, listViewModel: ListViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var actionLock = false
    private var rowModels: MutableList<RowModel> = mutableListOf()
    private var languageModels: MutableList<String> = mutableListOf()
    init {
        listViewModel.rowmodel.observe(mCtx as LifecycleOwner){
            rowModels = it
            notifyDataSetChanged()
        }
        listViewModel.languagemodel.observe(mCtx as LifecycleOwner){
            languageModels = it
        }
    }
    private val context: Context = mCtx
    companion object {
        private const val VIEW_TYPE_PARENT = 0
        private const val VIEW_TYPE_CHILD = 1
        private const val VIEW_TYPE_GRANDCHILD = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_PARENT -> {
                val parentItemView = inflater.inflate(R.layout.parent_item_layout, parent, false)
                ParentItemViewHolder(parentItemView)
            }
            VIEW_TYPE_CHILD -> {
                val childItemView = inflater.inflate(R.layout.child_item_layout, parent, false)
                ChildItemViewHolder(childItemView)
            }
            VIEW_TYPE_GRANDCHILD -> {
                val grandchildItemView = inflater.inflate(R.layout.grandchild_item_layout, parent, false)
                GrandchildItemViewHolder(grandchildItemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = rowModels.get(position)

        when(row.type){
            RowModel.COUNTRY ->  {
                (holder as ParentItemViewHolder).titleTextView.text = row.country.title
                holder.radioBtn.setOnClickListener{
                    holder.itemView.callOnClick()
                }
                holder.itemView.setOnClickListener {
                    if (!actionLock) {
                        actionLock = true
                        holder.radioBtn.isChecked = row.isExpanded
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
            RowModel.STATE -> {
                (holder as ChildItemViewHolder).spinner.adapter = ArrayAdapter(context, R.layout.spinner_item_layout, languageModels)
                holder.itemView.setOnClickListener {
                    if (!actionLock) {
                        actionLock = true
                        row.isExpanded = !row.isExpanded
                        if (row.isExpanded) {
                            (holder as ChildItemViewHolder).imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_expand))
                            collapse(position)
                        } else {
                            (holder as ChildItemViewHolder).imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_collapse))
                            expand(position)

                        }
                    }
                }
            }
            RowModel.CITY -> {
//                (holder as GrandchildItemViewHolder).nameTextView.setText(row.city.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return rowModels.size
    }

    override fun getItemViewType(position: Int): Int {
        return rowModels.get(position).type
    }

    fun expand(position: Int) {
        var nextPosition = position

        val row = rowModels.get(position)

        when (row.type) {

            RowModel.COUNTRY -> {

                /**
                 * add element just below of clicked row
                 */
                rowModels.add(++nextPosition, RowModel(RowModel.STATE, row.country.childItems))

                notifyItemRangeChanged(nextPosition, rowModels.size)
            }

            RowModel.STATE -> {

                /**
                 * add element just below of clicked row
                 */
                for (city in row.state.grandchildItems) {
                    rowModels.add(++nextPosition, RowModel(RowModel.CITY, city))
                }

                notifyItemRangeChanged(nextPosition, rowModels.size)

            }
        }
        actionLock = false
    }

    fun collapse(position: Int) {
        val row = rowModels.get(position)
        val nextPosition = position + 1

        when (row.type) {

            RowModel.COUNTRY -> {

                /**
                 * remove element from below until it ends or find another node of same type
                 */
                outerloop@ while (true) {
                    if (nextPosition == rowModels.size || rowModels.get(nextPosition).type === RowModel.COUNTRY) {
                        break@outerloop
                    }

                    rowModels?.removeAt(nextPosition)
                }

                notifyItemRangeChanged(nextPosition, rowModels.size)
            }

            RowModel.STATE -> {

                /**
                 * remove element from below until it ends or find another node of same type or find another parent node
                 */
                outerloop@ while (true) {
                    if (nextPosition == rowModels.size || rowModels.get(nextPosition).type === RowModel.STATE || rowModels.get(nextPosition).type === RowModel.COUNTRY) {
                        break@outerloop
                    }
                    rowModels.removeAt(nextPosition)
                }

                notifyItemRangeChanged(nextPosition, rowModels.size)
            }
        }

        actionLock = false
    }
}
