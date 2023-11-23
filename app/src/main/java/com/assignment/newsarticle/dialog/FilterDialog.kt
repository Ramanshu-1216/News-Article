package com.assignment.newsarticle.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import com.assignment.newsarticle.R

class FilterDialog(context: Context, private val onSortSelected: (Int) -> Unit) {
    private var selectedSortOption: Int = 0
    private val dialogView: View = LayoutInflater.from(context).inflate(R.layout.filter_dialog, null)
    private val featuredButton : RadioButton = dialogView.findViewById(R.id.featured)
    private val ascending: RadioButton = dialogView.findViewById(R.id.ascending)
    private val descending: RadioButton = dialogView.findViewById(R.id.descending)
    private val radioGroup: RadioGroup = dialogView.findViewById(R.id.radio_group)

    init {
        featuredButton.isChecked = true
        radioGroup.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.featured -> selectedSortOption = 0
                R.id.ascending -> selectedSortOption = 1
                R.id.descending -> selectedSortOption = 2
            }
        }
    }
    private val alertDialog: AlertDialog = AlertDialog.Builder(context)
        .setTitle("Sort Options")
        .setView(dialogView)
        .setPositiveButton("Ok"){_, _ ->
            onSortSelected(selectedSortOption)
        }
        .setNegativeButton("Cancel", null)
        .create()

    fun show(){
        alertDialog.show()
    }
}