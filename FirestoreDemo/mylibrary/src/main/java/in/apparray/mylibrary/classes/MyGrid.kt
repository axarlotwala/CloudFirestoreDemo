package `in`.apparray.mylibrary.classes

import `in`.apparray.mylibrary.R
import `in`.apparray.mylibrary.utils.MyUtils
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.zhouchaoyuan.excelpanel.ExcelPanel
import com.afollestad.materialdialogs.MaterialDialog
import es.dmoral.toasty.Toasty
import hk.ids.gws.android.sclick.SClick


class MyGrid : LinearLayout, View.OnClickListener {

    lateinit var ivAdd: TextView
    lateinit var tvTitle: TextView
    lateinit var layoutTitle: MaterialFancyLayout
    lateinit var ivShare: ImageView

    private var funKey = ""

    private lateinit var adapter: ExcelPanelAdapter
    lateinit var excelPanel: ExcelPanel

    private lateinit var formBuilder: FormBuilder

    private lateinit var topTitle: MutableList<RowTitle>
    private lateinit var formObjects: ArrayList<FormObject>
    private lateinit var itemList: ArrayList<HashMap<String, Any>>


    private lateinit var gridListener: GridListener

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        li.inflate(R.layout.my_grid_element, this, true)

        itemList = ArrayList()

        tvTitle = findViewById(R.id.tvTitle)
        ivAdd = findViewById(R.id.ivAdd)
        layoutTitle = findViewById(R.id.layoutTitle)
        excelPanel = findViewById(R.id.excelPanel)
        ivShare = findViewById(R.id.ivShare)

        adapter = ExcelPanelAdapter(context, cellClickListener, null)
        excelPanel.setAdapter(adapter)

        ivAdd.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        layoutTitle.setOnClickListener(this)

        ivShare.visibility = View.GONE

        MyUtils.fawiTextViewIcon(context, ivAdd, R.string.fawi_plus)

    }

    private val cellClickListener = OnClickListener { view ->
        try {
            val cell = view.tag as Cell
            val item = itemList.get(cell.rowIndex)

            if (formObjects.size > 0) dialogForm("Modify", item)
            else if (gridListener != null) gridListener.onItemSelected(item)

        } catch (e: Exception) {
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivShare -> { adapter.shareExcel(tvTitle.text.toString(), "", "report") }
            R.id.layoutTitle -> {
                if (ivAdd.visibility == View.VISIBLE) //display add Dialog if only add button functionality is on
                    dialogForm("Add")
            }
            R.id.ivAdd -> {
                dialogForm("Add")
            }
            else -> {
            }
        }

    }

    fun initGrid(
        title: String, formObjects: ArrayList<FormObject>, topTitle: MutableList<RowTitle>? = null, key: String = "", itemList: ArrayList<HashMap<String, Any>> = ArrayList()
    ) {
        funKey = key
        tvTitle.text = title
        this.formObjects = formObjects
        this.itemList = itemList

        if (topTitle != null) this.topTitle = topTitle
        else {
            this.topTitle = ArrayList()
            formObjects.forEach { formObject: FormObject ->
                if (formObject is FormElement) {
                    val fObject = formObject as FormElement
                    this.topTitle.add(RowTitle(fObject.getTag().toString(), fObject.getTitle().toString()))
                }
            }
        }
    }


    fun initGrid(
        title: String, topTitle: MutableList<RowTitle>, gridListener: GridListener? = null, itemList: ArrayList<HashMap<String, Any>> = ArrayList()
    ) {
        tvTitle.text = title
        this.topTitle = topTitle
        if (gridListener != null) this.gridListener = gridListener
        this.itemList = itemList

        formObjects = ArrayList()
        ivAdd.visibility = View.GONE

        refreshGrid()
    }


    fun dialogForm(title: String, item: HashMap<String, Any> = HashMap()) {

        var builder = MaterialDialog.Builder(context).customView(R.layout.dialog_form, true).positiveText(android.R.string.ok).negativeText(android.R.string.cancel).onNegative { dialog, which -> dialog.cancel() }.autoDismiss(false).cancelable(false)
            .title(title)

        if (item.size > 0) {
            builder.neutralText("Remove").onNeutral { dialog, which ->
                itemList.remove(item)
                adapter.setAllData(itemList, topTitle, gridPaddingWidth = 20)
                dialog.cancel()
            }
        }

        val materialDialog = builder.show()
        val view = materialDialog.getCustomView()
        val linearLayout: LinearLayout = view!!.findViewById(R.id.linearLayout)

        formBuilder = FormBuilder(context, linearLayout)
        formBuilder.build(formObjects)

        formBuilder.setFormData(item)

        when (funKey) {

        }


        builder.onPositive { dialog, which ->
            if (!SClick.check(SClick.BUTTON_CLICK)) return@onPositive

            if (!formBuilder.validate()) return@onPositive

            //get hashMap doc of form elements and store as row in itemList
            //updat itemList and continue

            val formDoc = formBuilder.getFormData()

            itemList.remove(item)
            itemList.add(formDoc)
            adapter.setAllData(itemList, topTitle, gridPaddingWidth = 20)
            dialog.cancel()

            Toasty.success(context, "Entry Successful").show()

        }


    }

    fun refreshGrid() {
        adapter.setAllData(itemList, topTitle, gridPaddingWidth = 20)
    }

    fun addItem(item: HashMap<String, Any>, refreshFlag: Boolean = true) {
        itemList.add(item)
        if (refreshFlag) refreshGrid()
    }

    fun addItems(items: ArrayList<HashMap<String, Any>>, refreshFlag: Boolean = true, resetItemsFlag: Boolean = false) {
        if (resetItemsFlag) itemList = items
        else itemList.addAll(items)
        if (refreshFlag || resetItemsFlag) refreshGrid()
    }


    fun removeItem(item: HashMap<String, Any>, refreshFlag: Boolean = true) {
        itemList.remove(item)
        if (refreshFlag) refreshGrid()
    }

    fun getItemList(): ArrayList<HashMap<String, Any>> {
        return itemList
    }

    fun getItemSize(): Int {
        return itemList.size
    }

}