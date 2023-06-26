package com.example.listapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listapp.Model.RowModel
import com.example.listapp.MyApplication
import com.example.listapp.ui.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * life cycle aware viewholder for expandable recycler view
 */


class ListViewModel: ViewModel() {

    @Inject
    lateinit var repository: Repository

    init{
        MyApplication.instance.applicationComponent.inject(this)
        repository.createDatabase()
    }

    private var dropDownSelection = MutableLiveData<String>()
    private val rowModel = MutableLiveData<MutableList<RowModel>>()
    val dropDownString : LiveData<String> get() = dropDownSelection
    val rowmodel : LiveData<MutableList<RowModel>> get() = rowModel

    private val languageModel = MutableLiveData<MutableList<String>>()
    val languagemodel : LiveData<MutableList<String>> get() = languageModel

    fun getListData(){
        viewModelScope.launch(Dispatchers.IO) {
            rowModel.postValue(repository.getData())
        }
    }

    fun getLanguageData(){
        viewModelScope.launch(Dispatchers.IO) {
            languageModel.postValue(repository.getLanguageData())
        }
    }

    fun setDropDown(input:String){
        dropDownSelection.value = input
    }

}