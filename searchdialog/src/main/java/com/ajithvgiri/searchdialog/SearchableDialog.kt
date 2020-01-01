package com.ajithvgiri.searchdialog

import android.app.Activity
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

/**
 * Created by ajithvgiri on 06/11/17.
 */
class SearchableDialog {
    var searchListItems: ArrayList<SearchListItem>
    var activity: Activity
    private var dialogTitle: String = ""
    private lateinit var onSearchItemSelected: OnSearchItemSelected
    lateinit var alertDialog: AlertDialog
    private var position = 0
    var style = 0
    private lateinit var searchListItem: SearchListItem
    lateinit var adapter: SearchListAdapter
    lateinit var listView: ListView

    constructor(activity: Activity, searchListItems: ArrayList<SearchListItem>, dialogTitle: String) {
        this.searchListItems = searchListItems
        this.activity = activity
        this.dialogTitle = dialogTitle
    }

    constructor(activity: Activity, searchListItems: ArrayList<SearchListItem>, dialogTitle: String, style: Int) {
        this.searchListItems = searchListItems
        this.activity = activity
        this.dialogTitle = dialogTitle
        this.style = style
    }

    /***
     *
     * @param searchItemSelected
     * return selected position & item
     */
    fun setOnItemSelected(searchItemSelected: OnSearchItemSelected) {
        onSearchItemSelected = searchItemSelected
    }

    /***
     *
     * show the seachable dialog
     */
    fun show() {
        val adb = AlertDialog.Builder(activity)
        val view = activity.layoutInflater.inflate(R.layout.search_dialog_layout, null)
        val rippleViewClose = view.findViewById<View>(R.id.close) as TextView
        val title = view.findViewById<View>(R.id.spinerTitle) as TextView
        title.text = dialogTitle
        listView = view.findViewById<View>(R.id.list) as ListView
        val searchBox = view.findViewById<View>(R.id.searchBox) as EditText
        adapter = SearchListAdapter(activity, R.layout.items_view_layout, R.id.text1, searchListItems)
        listView.adapter = adapter
        adb.setView(view)
        alertDialog = adb.create()
        alertDialog.window?.attributes?.windowAnimations = style //R.style.DialogAnimations_SmileWindow;
        alertDialog.setCancelable(false)
        listView.onItemClickListener = OnItemClickListener { _, view, _, _ ->
            val t = view.findViewById<TextView>(R.id.text1)
            for (j in searchListItems.indices) {
                if (t.text.toString().equals(searchListItems[j].toString(), ignoreCase = true)) {
                    position = j
                    searchListItem = searchListItems[position]
                }
            }
            try {
                onSearchItemSelected
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
            alertDialog.dismiss()
        }
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val filteredValues = ArrayList<SearchListItem>()
                for (i in searchListItems.indices) {
                    val item = searchListItems[i]
                    if (item.title.toLowerCase().trim { it <= ' ' }.contains(searchBox.text.toString().toLowerCase().trim { it <= ' ' })) {
                        filteredValues.add(item)
                    }
                }
                adapter = SearchListAdapter(activity, R.layout.items_view_layout, R.id.text1, filteredValues)
                listView.adapter = adapter
            }
        })
        rippleViewClose.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    /***
     *
     * clear the list
     */
    fun clear() {
        searchListItems.clear()
    }

    /***
     *
     * refresh the adapter (notifyDataSetChanged)
     */
    fun refresh() {
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "SearchableDialog"
    }
}