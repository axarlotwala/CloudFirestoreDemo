package `in`.apparray.mylibrary.classes

import `in`.apparray.mylibrary.R
import `in`.apparray.mylibrary.utils.MyUtils
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnCancelListener
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatSpinner
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

import java.util.HashMap

class SelectSpinner : AppCompatSpinner, OnCancelListener {
    private var items: List<KeyPairBoolData>? = null
    private var list: ArrayList<HashMap<String, Any>>? = null
    private var optionsList: ArrayList<String>? = null
    private var multiSelect: Boolean = false
    private var fsCollection: String = ""
    private var fsSearchKey: String = ""
    private var fsDisplayKey: String = ""

    public var defaultText: String? = ""
    private var spinnerTitle: String? = ""
    private var listener: SpinnerListener? = null
    private var limit = -1
    private var selected = 0
    private var limitListener: LimitExceedListener? = null
    internal var adapter: MyAdapter? = null

    private var listView: ListView? = null
    private var imgClear: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var editText: EditText? = null
    private var empty: TextView? = null
    private var addNewTxt = "Add New"
    private var addOptionFlag = false

    private var displayVal = ""
    private var dispClear = true

    private var addNewListener: OnClickListener? = null

    var validate: Boolean = false
    var text: String = ""

    fun getValue(): Any? {
        if (list == null) return text
        else {
            if (multiSelect) return getItems()
            else return getItem()
        }
    }

    fun getList(): ArrayList<HashMap<String, Any>>? {
        return list
    }

    val selectedItems: List<KeyPairBoolData>
        get() {
            val selectedItems = ArrayList<KeyPairBoolData>()
            for (item in items!!) {
                if (item.isSelected) {
                    selectedItems.add(item)
                }
            }
            return selectedItems
        }

    val selectedIds: List<Long>
        get() {
            val selectedItemsIds = ArrayList<Long>()
            for (item in items!!) {
                if (item.isSelected) {
                    selectedItemsIds.add(item.id)
                }
            }
            return selectedItemsIds
        }

    fun setTitle(title: String, multipleSelection: Boolean = false) {
        spinnerTitle = title
        defaultText = title
        multiSelect = multipleSelection

    }

    constructor(context: Context) : super(context) {}

    constructor(arg0: Context, arg1: AttributeSet) : super(arg0, arg1) {
        val a = arg0.obtainStyledAttributes(arg1, R.styleable.multi_spinner)
        for (i in 0 until a.indexCount) {
            val attr = a.getIndex(i)
            when (attr) {
                R.styleable.multi_spinner_clearFlag ->{
                    dispClear = a.getBoolean(attr,true)
                }
                R.styleable.multi_spinner_display -> {
                    displayVal = a.getString(attr)
                }
                R.styleable.multi_spinner_hint_text -> {
                    spinnerTitle = a.getString(attr)
                    defaultText = spinnerTitle
                }
                R.styleable.multi_spinner_multi_select -> {
                    multiSelect = a.getBoolean(attr, false)
                }
                R.styleable.multi_spinner_fs_collection -> {
                    fsCollection = a.getString(attr)
                }
                R.styleable.multi_spinner_fs_searchkey -> {
                    fsSearchKey = a.getString(attr)
                }
                R.styleable.multi_spinner_fs_displaykey -> {
                    fsDisplayKey = a.getString(attr)
                }
            }
        }
        Log.i(TAG, "spinnerTitle: " + spinnerTitle!!)
        a.recycle()
    }

    constructor(arg0: Context, arg1: AttributeSet, arg2: Int) : super(arg0, arg1, arg2) {}

    fun addNewListener(onClickListener: OnClickListener, addNewTxt: String = "Add New") {
        addNewListener = onClickListener
        this.addNewTxt = addNewTxt
    }

    private fun addOption(option: String) {
        if (!addOptionFlag || option.equals("")) return
        optionsList!!.add(option)
        setArrayItems(optionsList!!, "", listener, true)
        selectItem(option)
    }

    fun setHint(hintText: String) {
        spinnerTitle = hintText
        defaultText = hintText
    }

    fun setMultiSelect(selectFlag: Boolean) {
        multiSelect = selectFlag
    }

    fun setLimit(limit: Int, listener: LimitExceedListener) {
        this.limit = limit
        this.limitListener = listener
    }

    fun getItem(): HashMap<String, Any>? {
        var selItem = HashMap<String, Any>();
        if (list == null) return selItem;
        for (item in items!!) {
            if (item.isSelected) {
                return list!![item.id.toInt()]
            }
        }
        return selItem;
    }

    fun getItems(): List<HashMap<String, Any>> {
        val selItems = ArrayList<HashMap<String, Any>>()
        if (list == null) return selItems;
        for (item in items!!) {
            if (item.isSelected) {
                selItems.add(list!![item.id.toInt()])
            }
        }
        return selItems
    }

    fun getArrayItems(): ArrayList<String> {
        val selItems = ArrayList<String>()
        for (item in items!!) {
            if (item.isSelected) {
                selItems.add(item.name)
            }
        }
        return selItems
    }

    fun getHashMap(nameClm: String, trueOnly: Boolean = true): HashMap<String, Any> {
        val selItems = HashMap<String, Any>()
        if (list == null) return selItems;
        for (item in items!!) {
            if (item.isSelected) {
                var i = list!![item.id.toInt()]
                if (trueOnly) selItems.put(i[nameClm].toString(), true)
                else selItems.put(i[nameClm].toString(), i)
            }
        }
        return selItems
    }

    override fun onCancel(dialog: DialogInterface?) {
        // refresh text on spinner

        val spinnerBuffer = StringBuilder()

        val selItems: ArrayList<KeyPairBoolData> = ArrayList<KeyPairBoolData>()
        for (i in items!!.indices) {
            if (items!![i].isSelected) {
                spinnerBuffer.append(items!![i].name)
                spinnerBuffer.append(", ")
                selItems.add(items!![i])
            }
        }
        validate = selItems.size > 0

        var spinnerText: String? = spinnerBuffer.toString()
        if (spinnerText!!.length > 2) {
            spinnerText = spinnerText.substring(0, spinnerText.length - 2)
            text = spinnerText
        } else {
            spinnerText = defaultText
            text = ""
        }

//        val adapterSpinner = if(displayVal.equals("center", true)) ArrayAdapter(context, R.layout.selectspinner_textview_center, arrayOf<String>(spinnerText!!))
//        else ArrayAdapter(context, R.layout.selectspinner_textview, arrayOf<String>(spinnerText!!))

        val adapterSpinner = when (displayVal) {
            "blank" -> ArrayAdapter(context, R.layout.selectspinner_textview_blank, arrayOf<String>(spinnerText!!))
            "center" -> ArrayAdapter(context, R.layout.selectspinner_textview_center, arrayOf<String>(spinnerText!!))
            else -> ArrayAdapter(context, R.layout.selectspinner_textview, arrayOf<String>(spinnerText!!))
        }


        //val adapterSpinner = ArrayAdapter(context, R.layout.selectspinner_textview, arrayOf<String>(spinnerText!!))
        setAdapter(adapterSpinner)

        if (adapter != null) adapter!!.notifyDataSetChanged()

        if (listener != null) {
            val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            listener!!.onItemsSelected(selItems)
        }

    }

    override fun performClick(): Boolean {

        if (!this.isEnabled) return false

        if (this.items == null) return false;

//        if(this.items!!.size <= 0){
//            Toasty.info(context, "Not Found!", Toast.LENGTH_LONG).show()
//            return false
//        }

        var builder = MaterialDialog.Builder(context).customView(R.layout.alert_dialog_listview_search, false).title(spinnerTitle.toString()).theme(Theme.LIGHT)

//        if (multiSelect)
//            builder.positiveText(android.R.string.ok).onPositive { dialog, which -> dialog.cancel() }

        if (multiSelect) builder.positiveText(android.R.string.ok).onPositive { dialog, which -> dialog.cancel() }
        else builder.positiveText("Close").onPositive { dialog, which -> dialog.cancel() }

        if (addNewListener != null) {
            builder.neutralText(addNewTxt).onNeutral { dialog, which -> addNewListener!!.onClick(null) }
        } else if (addOptionFlag) builder.neutralText("Search").onNeutral { dialog, which ->
            text = editText!!.text.toString()
            addOption(text)
            if (listener != null) listener!!.onItemsSelected(ArrayList<KeyPairBoolData>())
            //selectItem(searchText)
        }

        if (text != "" && dispClear) builder.negativeText("Clear").onNegative { dialog, which ->
            selectItem("")
            dialog.cancel()
        }

        materialDialog = builder.show()
        materialDialog.setOnCancelListener(this)

        val view = materialDialog.getCustomView()

        listView = view!!.findViewById<View>(R.id.alertSearchListView) as ListView
        listView!!.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView!!.isFastScrollEnabled = false
        adapter = MyAdapter(context, this.items!!)
        listView!!.adapter = adapter

        val emptyText = view.findViewById<View>(R.id.empty) as TextView
        listView!!.emptyView = emptyText

        imgClear = view.findViewById<View>(R.id.imgClear) as ImageView
        imgClear!!.setOnClickListener { view ->
            editText!!.setText("")
        }

        editText = view.findViewById<View>(R.id.alertSearchEditText) as EditText
        if (fsCollection.equals("") && this.items!!.size < 10) {
            editText!!.visibility = View.GONE
            imgClear!!.visibility = View.GONE
        } else {
            editText!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    adapter!!.filter.filter(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun afterTextChanged(s: Editable) {}
            })

            if (!fsCollection.equals("")) {
                val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }

        }


        progressBar = view.findViewById<View>(R.id.progressBar) as ProgressBar
        empty = view.findViewById<View>(R.id.empty) as TextView

        if (this.items!!.size <= 0) {
            empty!!.text = "Not Found!"
            //Toasty.info(context, "Not Found!", Toast.LENGTH_LONG).show()
//            return false
        }

        return true
    }

    fun setSpinnerListener(listener: SpinnerListener) {
        this.listener = listener
    }

    fun setArrayItems(list: Array<String>, selText: String = "", listener: SpinnerListener? = null) {
        //this.list = list;
        val items = ArrayList<KeyPairBoolData>()

        //var position = -1
        var positions: ArrayList<Int> = ArrayList<Int>()
        for (i in list.indices) {
            val text = list[i]
            val h = KeyPairBoolData()
            h.id = i.toLong() // + 1
            h.name = text
            h.isSelected = false
            items.add(h)
            if (selText != "" && selText.equals(text, ignoreCase = true)) positions.add(i)
        }
        setItems(items, positions, listener)
    }

    fun setHashMapItems(map: HashMap<String, Any>, selText: String = "", listener: SpinnerListener? = null) {
        val list = ArrayList<HashMap<String, Any>>()
        map.forEach { entry ->
            val eh: HashMap<String, Any> = HashMap()
            eh.put("key", entry.key)
            eh.put("value", entry.value)
            list.add(eh)
        }
        setItems(list, "value", selText, listener)
    }

    fun setArrayItems(list: ArrayList<String>, selText: String = "", listener: SpinnerListener? = null, addManualOptionFlag: Boolean = false) {
        //this.list = list;
        optionsList = list
        val items = ArrayList<KeyPairBoolData>()
        addOptionFlag = addManualOptionFlag

        //var position = -1
        var positions: ArrayList<Int> = ArrayList<Int>()
        for (i in list.indices) {
            val text = list[i]
            val h = KeyPairBoolData()
            h.id = i.toLong() // + 1
            h.name = text
            h.isSelected = false
            items.add(h)
            if (selText != "" && selText.equals(text, ignoreCase = true)) positions.add(i)
        }
        setItems(items, positions, listener)
    }

    fun selectIndex(index: Int) {
        try {
            items!!.forEach { t: KeyPairBoolData? ->
                t!!.isSelected = false
            }
            if (items!!.size > index) {
                items!!.get(index).isSelected = true
            }
            onCancel(null)
        } catch (e: Exception) {
        }
    }

    fun selectItem(selText: String) {
        try {
            items!!.forEach { t: KeyPairBoolData? ->
                t!!.isSelected = t!!.name.equals(selText, true)
            }
            onCancel(null)
        } catch (e: Exception) {
        }
    }

    fun selectItems(selText: HashMap<String, Any>) {
        try {
            items!!.forEach { item: KeyPairBoolData? ->
                item!!.isSelected = false
            }
            selText.forEach { entry ->
                items!!.forEach { item: KeyPairBoolData? ->
                    if (item!!.name.equals(entry.key, true)) item!!.isSelected = true
                }
            }
            onCancel(null)
        } catch (e: Exception) {
        }
    }

    fun selectItems(selText: ArrayList<HashMap<String, Any>>, nameKey: String = "key") {
        try {
            items!!.forEach { item: KeyPairBoolData? ->
                item!!.isSelected = false
            }

            selText.forEach { entry ->
                items!!.forEach { item: KeyPairBoolData? ->
                    if (item!!.name.equals(entry[nameKey].toString(), true)) item!!.isSelected = true
                }
            }
            onCancel(null)
        } catch (e: Exception) {
        }
    }

    fun selectValue(selVal: String, nameKey: String = "key") {
        try {
            if (list == null) {
                selectItem(selVal)
                return
            }
            items!!.forEach { item: KeyPairBoolData? ->
                item!!.isSelected = false
            }

            list!!.forEachIndexed { index, item ->
                if (item[nameKey].toString().equals(selVal, true)) items!!.get(index).isSelected = true
            }
            onCancel(null)
        } catch (e: Exception) {
        }
    }

    fun selectValue(selVal: ArrayList<String>, nameKey: String = "key") {
        try {
            if (list == null) {
                selectArrayItems(selVal)
                return
            }

            items!!.forEach { item: KeyPairBoolData? ->
                item!!.isSelected = false
            }

            list!!.forEachIndexed { index, item ->
                if (selVal.contains(item[nameKey].toString())) items!!.get(index).isSelected = true
            }
            onCancel(null)
        } catch (e: Exception) {
        }
    }

    fun selectArrayItems(selText: ArrayList<String>) {
        try {
            items!!.forEach { item: KeyPairBoolData? ->
                item!!.isSelected = false
            }

            selText.forEach { entry ->
                items!!.forEach { item: KeyPairBoolData? ->
                    if (item!!.name.equals(entry, true)) item!!.isSelected = true
                }
            }
            onCancel(null)
        } catch (e: Exception) {
        }
    }

    fun setItems(iClient: HashMap<String, Any>? = null, listener: SpinnerListener? = null) {
        list = ArrayList()
        items = ArrayList()
        val positions: ArrayList<Int> = ArrayList<Int>()

        if (iClient == null) setItems(items as ArrayList<KeyPairBoolData>, positions, listener)
        else {
            positions.add(0)
            list!!.add(iClient)
            setItems(list!!, "clientName", iClient["clientName"].toString(), listener)
            //setItems(items as ArrayList<KeyPairBoolData>, positions, listener)
        }
    }

    fun setItems(list: List<HashMap<String, Any>>, nameKey: String, selTexts: String = "", listener: SpinnerListener? = null, valueKey: String = "") {
        this.list = list as ArrayList<HashMap<String, Any>>
        val items = ArrayList<KeyPairBoolData>()

        var positions: ArrayList<Int> = ArrayList<Int>()
        for (i in list.indices) {
            var text = ""
            val arrK = nameKey.split(",")
            arrK.forEach { key: String? ->
                if (text.equals("")) text = MyUtils.hasMapString(list[i][key]) //list[i][key].toString()
                else {
                    val t = MyUtils.hasMapString(list[i][key])
                    if (t != "") text += ", $t" //hasMapString(list[i][key]) //list[i][key].toString()
                }

            }

//            if(nameKey.contains(",")){
//                val arrK = nameKey.split(",")
//            }else
//                text = list[i][nameKey].toString()

            val h = KeyPairBoolData()
            h.id = i.toLong() // + 1
            h.name = text
            h.isSelected = false
            items.add(h)
            if (selTexts != "") {
                var valueT = text
                if (!valueKey.equals("")) valueT = list[i][valueKey].toString()

                val selTextss = ",$selTexts,"
                if (selTextss.contains(",$valueT,")) positions.add(i)
            }
        }
        setItems(items, positions, listener)
    }

    fun setItems(items: List<KeyPairBoolData>, positions: ArrayList<Int>, listener: SpinnerListener? = null) {

        this.items = items
        this.listener = listener

        val spinnerBuffer = StringBuilder()

        for (i in items.indices) {
            if (items[i].isSelected) {
                spinnerBuffer.append(items[i].name)
                spinnerBuffer.append(", ")
            }
        }
        if (spinnerBuffer.length > 2) defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length - 2)

        //val adapterSpinner = ArrayAdapter(context, R.layout.selectspinner_textview, arrayOf<String>(this.defaultText!!))

//        val adapterSpinner = if(displayVal.equals("center", true)) ArrayAdapter(context, R.layout.selectspinner_textview_center, arrayOf<String>(this.defaultText!!))
//        else ArrayAdapter(context, R.layout.selectspinner_textview, arrayOf<String>(this.defaultText!!))

        val adapterSpinner = when (displayVal) {
            "blank" -> ArrayAdapter(context, R.layout.selectspinner_textview_blank, arrayOf<String>(this.defaultText!!))
            "center" -> ArrayAdapter(context, R.layout.selectspinner_textview_center, arrayOf<String>(this.defaultText!!))
            else -> ArrayAdapter(context, R.layout.selectspinner_textview, arrayOf<String>(this.defaultText!!))
        }


        setAdapter(adapterSpinner)

        positions.forEach { i: Int ->
            items[i].isSelected = true
            onCancel(null)
        }
//        if (position != -1) {
//            items[position].setSelected(true)
//            //listener.onItemsSelected(items);
//            onCancel(null)
//        }
    }

    interface LimitExceedListener {
        fun onLimitListener(data: KeyPairBoolData)
    }

    //Adapter Class
    inner class MyAdapter(context: Context, internal var arrayList: List<KeyPairBoolData>) : BaseAdapter(), Filterable {
        internal var mOriginalValues: List<KeyPairBoolData>? = null // Original Values
        internal var inflater: LayoutInflater

        init {
            inflater = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return arrayList.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        private inner class ViewHolder {
            internal var textView: TextView? = null
            internal var checkBox: CheckBox? = null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            Log.i(TAG, "getView() enter")
            val holder: ViewHolder

            if (convertView == null) {
                holder = ViewHolder()
                if (multiSelect) {
                    convertView = inflater.inflate(R.layout.selectspinner_listview_multiple_item, parent, false)
                    holder.checkBox = convertView.findViewById<View>(R.id.alertCheckbox) as CheckBox
                } else convertView = inflater.inflate(R.layout.selectspinner_listview_single_item, parent, false)

                holder.textView = convertView!!.findViewById<View>(R.id.alertTextView) as TextView

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }

            val data = arrayList[position]

            holder.textView!!.text = data.name
            holder.textView!!.setTypeface(null, Typeface.NORMAL)

            if (multiSelect) holder.checkBox!!.isChecked = data.isSelected

            convertView.setOnClickListener(OnClickListener { v ->
                if (multiSelect) {
                    if (data.isSelected) { // deselect
                        selected--
                    } else if (selected == limit) { // select with limit
                        if (limitListener != null) limitListener!!.onLimitListener(data)
                        return@OnClickListener
                    } else { // selected
                        selected++
                    }

                    val temp = v.tag as ViewHolder
                    temp.checkBox!!.isChecked = !temp.checkBox!!.isChecked

                    data.isSelected = !data.isSelected
                    Log.i(TAG, "On Click Selected Item : " + data.name + " : " + data.isSelected)
                    notifyDataSetChanged()

                } else {

                    items!!.forEach { t: KeyPairBoolData? ->
                        t!!.isSelected = false
                    }

                    val len = arrayList.size
                    for (i in 0 until len) {
                        arrayList[i].isSelected = false
                        if (i == position) {
                            arrayList[i].isSelected = true
                            Log.i(TAG, "On Click Selected Item : " + arrayList[i].name + " : " + arrayList[i].isSelected)
                        }
                    }
                    materialDialog.dismiss()
                    this@SelectSpinner.onCancel(materialDialog)
                }
            })
            if (data.isSelected) {
                holder.textView!!.setTypeface(null, Typeface.BOLD)
                // convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_selected));
            }
            if (multiSelect) holder.checkBox!!.tag = holder

            return convertView
        }

        @SuppressLint("DefaultLocale")
        override fun getFilter(): Filter {
            return object : Filter() {

                override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                    var constraint = constraint
                    val results = Filter.FilterResults()        // Holds the results of a filtering operation in values
                    val FilteredArrList = ArrayList<KeyPairBoolData>()

                    if (mOriginalValues == null) {
                        mOriginalValues = ArrayList(arrayList) // saves the original data in mOriginalValues
                    }

                    if (fsCollection.equals("")) {
                        /********
                         *
                         * If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                         * else does the Filtering and returns FilteredArrList(Filtered)
                         *
                         */
                        if (constraint == null || constraint.length == 0) {

                            // set the Original result to return
                            results.count = mOriginalValues!!.size
                            results.values = mOriginalValues
                        } else {
                            constraint = constraint.toString().toLowerCase()
                            for (i in mOriginalValues!!.indices) {
                                Log.i(TAG, "Filter : " + mOriginalValues!![i].name + " -> " + mOriginalValues!![i].isSelected)
                                val data = mOriginalValues!![i].name
                                if (data.toLowerCase().contains(constraint.toString())) {
                                    FilteredArrList.add(mOriginalValues!![i])
                                }
                            }
                            // set the Filtered result to return
                            results.count = FilteredArrList.size
                            results.values = FilteredArrList
                        }

                    } else {
                        /*
                        remote data fetch function here and set it to get data from server...
                        */
                        if (constraint != null && constraint.length > 1) {


                            val activity: Activity = context as Activity
                            activity.runOnUiThread(java.lang.Runnable {
                                empty!!.setText("Loading...")
                                listView!!.visibility = View.GONE
                                progressBar!!.visibility = View.VISIBLE
                            })

                            queryFSCollection(constraint as String)
                            results.count = items!!.size
                            results.values = items
                        }

                    }


                    return results
                }

                override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                    empty!!.setText("Not Found!")
                    listView!!.visibility = View.VISIBLE
                    progressBar!!.visibility = View.GONE

//                    if (results != null && results.count > 0) {
//                        arrayList = results.values as List<KeyPairBoolData> // has the filtered values
//                        notifyDataSetChanged()  // notifies the data with new filtered values
//                    } else {
//                        notifyDataSetInvalidated()
//                    }

                    if (results != null) {
                        try {
                            arrayList = results.values as List<KeyPairBoolData> // has the filtered values
                            notifyDataSetChanged()  // notifies the data with new filtered values
                        } catch (e: Exception) {
                        }
                    } else {
                        notifyDataSetInvalidated()
                    }

                }

            }
        }


        internal fun queryFSCollection(query1: String) {
            val db = FirebaseFirestore.getInstance()

            var query = query1[0].toUpperCase() + query1.substring(1, query1.length)
            val tmTask = db.collection(fsCollection).whereGreaterThanOrEqualTo(fsSearchKey, query).whereLessThanOrEqualTo(fsSearchKey, query + '\uf8ff').get()
            val querySnapshot = Tasks.await(tmTask) as QuerySnapshot

            val itemsT: List<KeyPairBoolData> = ArrayList()
            val listT: ArrayList<HashMap<String, Any>> = ArrayList()
            var i = 0
            querySnapshot.forEach { queryDocumentSnapshot: QueryDocumentSnapshot? ->
                var data = queryDocumentSnapshot!!.data as HashMap<String, Any>
                // val tmN = document.getData() as HashMap<String, Any>
                try {
                    listT!!.add(data)

                    val text = data[fsDisplayKey].toString()
                    val h = KeyPairBoolData()
                    h.id = i.toLong() // + 1
                    h.name = text
                    h.isSelected = false
                    (itemsT as ArrayList<KeyPairBoolData>).add(h)
                    i++
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }

            list = ArrayList(listT)
            items = ArrayList(itemsT)


        }

    }

    companion object {
        private val TAG = SelectSpinner::class.java.simpleName
        //lateinit var builder: MaterialDialog.Builder
        lateinit var materialDialog: MaterialDialog


    }


}