package com.example.listapp.ui

import com.example.listapp.Dao.ListDao
import com.example.listapp.Model.ChildItem
import com.example.listapp.Model.GrandchildItem
import com.example.listapp.Model.ParentItem
import com.example.listapp.Model.RowModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class Repository @Inject constructor(val listDao: ListDao) {

    fun createDatabase(){
        val grandchildItems: MutableList<GrandchildItem> = mutableListOf<GrandchildItem>().also {
            it.add(GrandchildItem("edittext grand child"))
        }
        val childItems  = ChildItem("drop down child", grandchildItems)
        GlobalScope.launch(Dispatchers.IO) {
            if(listDao.getAll().size == 0) {
                listDao.insert(RowModel(RowModel.COUNTRY, ParentItem("India", childItems), "Hindi"))
                listDao.insert(RowModel(RowModel.COUNTRY, ParentItem("USA", childItems), "English"))
            }

        }
    }

    fun getData(): MutableList<RowModel>{
        return listDao.getAll()
    }

    fun getLanguageData(): MutableList<String>{
        return listDao.getLanguages()
    }
}