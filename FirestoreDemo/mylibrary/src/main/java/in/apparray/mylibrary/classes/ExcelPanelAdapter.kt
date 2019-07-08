package `in`.apparray.mylibrary.classes

import `in`.apparray.mylibrary.R
import `in`.apparray.mylibrary.utils.MyUtils
import `in`.apparray.utis.classes.HashMapComparator
import android.Manifest
import android.content.Context
import android.os.AsyncTask
import android.os.StrictMode
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.zhouchaoyuan.excelpanel.BaseExcelPanelAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.kotlinpermissions.KotlinPermissions


import dmax.dialog.SpotsDialog
import es.dmoral.toasty.Toasty
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ExcelPanelAdapter(val context: Context, private val clickListener: View.OnClickListener? = null, private val longClickListener: View.OnLongClickListener? = null) : BaseExcelPanelAdapter<RowTitle, ColTitle, Cell>(context) {
    public var cellsList: MutableList<List<Cell>> = ArrayList()
    public var rptList: ArrayList<HashMap<String, Any>> = ArrayList()
    private var allList: ArrayList<HashMap<String, Any>> = ArrayList()

    private lateinit var topTitles: MutableList<RowTitle>

    fun setAllData(rptList: ArrayList<HashMap<String, Any>>, clmTitles: MutableList<RowTitle>? = null, filterFlag: Boolean = false, gridPaddingWidth: Int = 0): Boolean {

        this.rptList = rptList
        if (!filterFlag) {
            allList = ArrayList()
            allList.addAll(rptList)
        }

        val leftTitles: MutableList<ColTitle> = ArrayList() //add SrNo at present
        topTitles = ArrayList() //hashMap key as title
        //val cells: MutableList<List<Cell>> = ArrayList() //hasMap value as text
        cellsList = ArrayList()

        if (clmTitles != null) {
            topTitles = clmTitles
        } else {
            var colKeys: HashMap<String, Any> = HashMap()
            colKeys = rptList.get(0)
            for (key in colKeys.keys) {
                topTitles.add(RowTitle(key, key.toUpperCase()))
            }
        }

        // val cellWidth = 70
        val width = PxToDp(context.getResources().getDisplayMetrics().widthPixels) - 35
        var tWidth = 0
        for (clm in topTitles) {
            var cW = clm.colLength
            //if (cW == 0) cW = cellWidth
            tWidth += cW
        }

        if (tWidth < width) {
            val c = (width - gridPaddingWidth) / (topTitles.size)
            //val cW = Math.round(width / (topTitles.size + 1))
            for (clm in topTitles) {
                clm.colLength = c
            }
        }





        for (rowIndex in 0 until rptList.size) {
            leftTitles.add(ColTitle((rowIndex + 1).toString())) //equal to no of keys in hashMap

            val data = rptList.get(rowIndex)
            val kCells: ArrayList<Cell> = ArrayList()
            var clmIndex = 0
            for (clm in topTitles) {
                var bgColor = R.color.excelpanel_normal_cell_bg
                if (clm.bgColor > 0) bgColor = clm.bgColor
                //bgColor = cellBgColor(data, clm.key, bgColor)
                kCells.add(Cell(clmIndex, clm.key, rowIndex, MyUtils.hasMapString(data[clm.key]), "", clm.colLength, bgColor, icon = clm.icon, gravity = clm.gravity, cellFormat = clm.cellFormat)) //equal to no of keys in hashMap
                clmIndex += 1
            }
            cellsList.add(kCells)
        }

        if (rptList.size <= 0) {
            leftTitles.add(ColTitle("-")) //equal to no of keys in hashMap

            //val data = rptList.get(rowIndex)
            val kCells: ArrayList<Cell> = ArrayList()
            var clmIndex = 0
            for (clm in topTitles) {
                var clmText = "Not Found!!"
                if (clmIndex != 0) clmText = ""
                var bgColor = R.color.excelpanel_normal_cell_bg
                if (clm.bgColor > 0) bgColor = clm.bgColor
                kCells.add(Cell(clmIndex, clm.key, 0, clmText, "", clm.colLength, bgColor)) //, icon = clm.icon //equal to no of keys in hashMap
                clmIndex += 1
            }
            cellsList.add(kCells)
        }

        setAllData(leftTitles, topTitles, cellsList)
        return true
    }

    fun createHtmTable(title: String, subTitle: String, tableWidth: String = "100%"): String {
        val htmlBuilder = StringBuilder()
        htmlBuilder.append("<table width='$tableWidth' border='1' cellspacing='0' cellpadding='2' style='border: solid #333' ><tr><th>#</th>")
        topTitles.forEach { clm: RowTitle? ->
            htmlBuilder.append("<th align='center'>${clm!!.text}</th>")
        }
        htmlBuilder.append("</tr>")

        var srNo = 1
        rptList.forEach { row: HashMap<String, Any>? ->
            htmlBuilder.append("<tr><td align='center'>$srNo</td>")
            topTitles.forEach { clm: RowTitle? ->
                var align = ""
                when (clm!!.gravity) {
                    Gravity.RIGHT -> {
                        align = " align='right' "
                    }
                    Gravity.LEFT -> {
                        align = " align='left' "
                    }
                    else -> {
                        align = " align='center' "
                    }
                }
                htmlBuilder.append("<td $align>${MyUtils.hasMapString(row!![clm!!.key])}</td>")
            }
            htmlBuilder.append("</tr>")
            srNo += 1
        }

        htmlBuilder.append("</table>")
        return htmlBuilder.toString()

    }

    fun shareExcel(title: String, subTitle: String = "", fileName: String = "report") {
        try {
            KotlinPermissions.with(context as androidx.fragment.app.FragmentActivity) // where this is an FragmentActivity instance
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).onAccepted { permissions ->
                    //List of accepted permissions
                    ExcelReport().execute("share", title, subTitle, fileName)
                }.onDenied { permissions ->
                    //List of denied permissions
                    Toasty.warning(context, "Storage Read/Write Permission denied.").show()
                }.onForeverDenied { permissions ->
                    //List of forever denied permissions
                    Toasty.error(context, "Storage Read/Write Permission denied forever.").show()
                }.ask()
        } catch (e: Exception) {
            ExcelReport().execute("share", title, subTitle, fileName)
        }
    }

    private inner class ExcelReport : AsyncTask<Any, Void, File?>() {

        var loaderDialog: SpotsDialog = SpotsDialog(context)

        var type = ""
        var title = ""
        var subTitle = ""
        var fileName = "report"

        var recipient = ""
        var subject = ""
        var body = ""
        var recipientCC = ""
        var recipientBCC = ""

        override fun onPreExecute() {
            super.onPreExecute()
            try {
                loaderDialog.show()
            } catch (e: Exception) {
            }
        }

        protected override fun doInBackground(vararg params: Any): File? {
            type = params.get(0) as String
            title = params.get(1) as String
            subTitle = params.get(2) as String
            fileName = params.get(3) as String

            if (type.equals("backgroundMail")) {
                recipient = params.get(4) as String
                subject = params.get(5) as String
                body = params.get(6) as String
                recipientCC = params.get(7) as String
                recipientBCC = params.get(8) as String
            }

            if (params.size > 3) fileName = params.get(3) as String

            return try {
                MyUtils.createExcel(rptList, topTitles, title, subTitle, fileName)
            } catch (e: Exception) {
                null
            }

            //return "Executed"
        }

        override fun onPostExecute(outFile: File?) {
            super.onPostExecute(outFile)

            try {
                loaderDialog.dismiss()
            } catch (e: Exception) {
            }

            if (outFile == null) {
                Toasty.info(context, "Excel Share failed.").show()
                return
            }
            when (type) {
                "share" -> {
                    try {
                        val builder = StrictMode.VmPolicy.Builder()
                        StrictMode.setVmPolicy(builder.build())
                        MyUtils.share(outFile, context)
                    } catch (e: Exception) {
                    }
                }
//                "backgroundMail" -> {
//                    val bm = BackgroundMail(context, MyUtils.eBName, MyUtils.eUserName, MyUtils.ePwd, subject, body, true, recipient, recipientCC, recipientBCC)
//                    bm.setAttachment(outFile.absolutePath)
//                    bm.send()
//                }
            }
        }
    }


    private fun getCellStyle(workbook: SXSSFWorkbook, fontHeight: Short = 9, bold: Boolean = false, allign: String = "center"): CellStyle? {

        val titleStyle = workbook.createCellStyle()
        when (allign) {
            "center" -> {
                titleStyle.alignment = XSSFCellStyle.ALIGN_CENTER
                titleStyle.verticalAlignment = XSSFCellStyle.VERTICAL_CENTER
            }
            "right" -> {
                titleStyle.alignment = XSSFCellStyle.ALIGN_RIGHT
                titleStyle.verticalAlignment = XSSFCellStyle.VERTICAL_CENTER
            }
            else -> {
                titleStyle.alignment = XSSFCellStyle.ALIGN_LEFT
                titleStyle.verticalAlignment = XSSFCellStyle.VERTICAL_CENTER
            }

        }

        titleStyle.wrapText = true

        titleStyle.setBorderTop(XSSFCellStyle.BORDER_THIN)
        titleStyle.setTopBorderColor(IndexedColors.GREY_80_PERCENT.getIndex())

        titleStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN)
        titleStyle.setBottomBorderColor(IndexedColors.GREY_80_PERCENT.getIndex())

        titleStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN)
        titleStyle.setLeftBorderColor(IndexedColors.GREY_80_PERCENT.getIndex())

        titleStyle.setBorderRight(XSSFCellStyle.BORDER_THIN)
        titleStyle.setRightBorderColor(IndexedColors.GREY_80_PERCENT.getIndex())

        val font = workbook.createFont()
        font.fontHeightInPoints = fontHeight
        font.bold = bold

        titleStyle.setFont(font)
        return titleStyle
    }


    fun selectRows(bgColor: Int = 0) {
        var index = 0
        cellsList.forEach { row: List<Cell> ->
            selectRow(row, index, bgColor)
            index += 1
        }
    }

    fun selectRow(index: Int, bgColor: Int = 0) {
        val row = cellsList[index]
        selectRow(row, index, bgColor)
//        var bgColor = bgColor
//        if (bgColor == 0)
//            bgColor = R.color.material_lightblue300
//
//        var selected = false
//        for (cell in row) {
//            if (cell.selected) {
//                cell.bgColor = R.color.normal_cell_bg
//                cell.selected = false
//            } else {
//                cell.bgColor = bgColor
//                cell.selected = true
//            }
//            selected = cell.selected
//        }
//        val rptRow = rptList.get(index)
//        rptRow["selected"] = selected
    }

    fun selectRow(row: List<Cell>, index: Int, bgColor: Int) {
        var bgColor = bgColor
        if (bgColor == 0) bgColor = R.color.material_grey300

        var selected = false
        for (cell in row) {
            if (cell.selected) {
                cell.bgColor = R.color.excelpanel_normal_cell_bg
                cell.selected = false
            } else {
                cell.bgColor = bgColor
                cell.selected = true
            }
            selected = cell.selected
        }
        val rptRow = rptList.get(index)
        rptRow["selected"] = selected
    }

    fun rowBgColor(index: Int, bgColor: Int) {
        val row = cellsList[index]
        for (cell in row) {
            cell.bgColor = bgColor
        }
    }

    fun cellBgColor(data: HashMap<String, Any>, key: String, bgColor: Int): Int {
        var bgColor = bgColor
        if (key.equals("city")) bgColor = R.color.material_amber200

        return bgColor
    }

    fun dpToPx(dp: Int): Int {
        val density = context.getResources().getDisplayMetrics().density
        return Math.round(dp.toFloat() * density)
    }

    fun PxToDp(px: Int): Int {
        val density = context.getResources().getDisplayMetrics().density
        return Math.round(px.toFloat() / density)
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder? {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.excelpanel_normal_cell, parent, false)
        return CellHolder(layout)
    }

    override fun onBindCellViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, verticalPosition: Int, horizontalPosition: Int) {
        val cell = getMajorItem(verticalPosition, horizontalPosition)
        if (null == holder || holder !is CellHolder || cell == null) {
            return
        }

        // val viewHolder: CellHolder = holder
        holder.cellContainer.tag = cell
        holder.cellContainer.setOnClickListener(clickListener)
        holder.cellContainer.setOnLongClickListener(longClickListener)

        if (cell.icon > 0) holder.tvText.setCompoundDrawablesWithIntrinsicBounds(cell.icon, 0, 0, 0);
        else holder.tvText.text = cell.text

        if (!cell.subText.equals("")) {
            holder.tvSubText.text = cell.subText
            holder.tvSubText.visibility = View.VISIBLE
        }

        if (cell.bgColor != 0) holder.cellContainer.setBackgroundColor(ContextCompat.getColor(context, cell.bgColor))

        if (cell.colLength > 0) holder.rlCell.getLayoutParams().width = dpToPx(cell.colLength)

        if (cell.gravity > 0) holder.tvText.gravity = cell.gravity

        when (cell.cellFormat) {
            "number" -> {
                val cV = MyUtils.hasMapDouble(cell.text)
                //if (cV > 0) holder.tvText.text = MyUtils.indianNumberWithDecimal(cV)
                holder.tvText.text = MyUtils.indianNumberWithDecimal(cV)
            }
        }
    }

    internal class CellHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val tvText: TextView
        val tvSubText: TextView
        val cellContainer: LinearLayout
        val rlCell: RelativeLayout

        init {
            tvText = itemView.findViewById(R.id.tvText) as TextView
            tvSubText = itemView.findViewById(R.id.tvSubText) as TextView
            cellContainer = itemView.findViewById(R.id.pms_cell_container) as LinearLayout
            rlCell = itemView.findViewById(R.id.rlCell) as RelativeLayout
        }
    }


    override fun onCreateTopViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder? {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.excelpanel_top_header_item, parent, false)
        return TopHolder(layout)
    }

    override fun onBindTopViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val rowTitle = getTopItem(position)
        if (null == holder || holder !is TopHolder || rowTitle == null) {
            return
        }

        holder.rlCell.tag = rowTitle
        holder.rlCell.setOnClickListener(topClickListener)
        holder.rlCell.setOnLongClickListener(topLongClickListener)

        holder.tvText.text = rowTitle.text

        if (rowTitle.colLength > 40) when (rowTitle.sortOrder) {
            "" -> {
                holder.tvText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sort, 0)
            }
            "asc" -> {
                holder.tvText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0)
            }
            "desc" -> {
                holder.tvText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0)
            }
        }

        if (!rowTitle.subText.equals("")) {
            holder.tvSubText.text = rowTitle.subText
            holder.tvSubText.visibility = View.VISIBLE
        }

        if (rowTitle.colLength > 0) holder.rlCell.getLayoutParams().width = dpToPx(rowTitle.colLength)
    }

    internal class TopHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val tvText: TextView
        val tvSubText: TextView
        val rlCell: RelativeLayout

        init {
            tvText = itemView.findViewById(R.id.tvText) as TextView
            tvSubText = itemView.findViewById(R.id.tvSubText) as TextView
            rlCell = itemView.findViewById(R.id.rlCell) as RelativeLayout
        }
    }

    private val topLongClickListener = View.OnLongClickListener { view ->
        if (rptList.size <= 1) return@OnLongClickListener true
        try {
            val rowTitle = view.tag as RowTitle
            openFilterDialog(rowTitle)
        } catch (e: Exception) {

        }
        return@OnLongClickListener true
    }

    private fun openFilterDialog(rowTitle: RowTitle) {

        //multiple dropdown and two button dialog Clear PO# to display all records
        //Filter to display filtered records

        var builder =
            MaterialDialog.Builder(context).customView(R.layout.excelpanel_dialog_filter, true).positiveText("Filter").negativeText(android.R.string.cancel).onNegative { dialog, which -> dialog.cancel() }.autoDismiss(false).title("Filter ${rowTitle.text}")

        if (!rowTitle.filterText.equals("")) builder.neutralText("Clear Filter")

        val materialDialog = builder.show()
        materialDialog.setCancelable(false)
        val view = materialDialog.getCustomView()

        val selectSpinner: SelectSpinner = view!!.findViewById(R.id.selectSpinner)
        //selectSpinner.setMultiSelect(true)

        val arrItems = ArrayList<String>()
        allList.forEach { item: java.util.HashMap<String, Any>? ->
            val text = MyUtils.hasMapString(item!![rowTitle.key])
            if (!arrItems.contains(text)) arrItems.add(text)
        }
        var validation = AwesomeValidation(ValidationStyle.BASIC)
        MyUtils.SelectSpinnerValidation(validation, selectSpinner)

        selectSpinner.setArrayItems(arrItems, rowTitle.filterText)
        builder.onNeutral { dialog, which ->
            setAllData(allList, topTitles)
            dialog.dismiss()
        }

        builder.onPositive { dialog, which ->
            //filter rows here.. and store in itemList

            if (!validation.validate()) return@onPositive

            val selText = selectSpinner.text

            rptList = ArrayList()
            rowTitle.filterText = selText
            allList.forEach { item: HashMap<String, Any>? ->
                val text = MyUtils.hasMapString(item!![rowTitle.key])
                if (text.equals(selText)) rptList.add(item)
            }
            setAllData(rptList, topTitles, true)
            dialog.dismiss()
        }


    }

    private val topClickListener = View.OnClickListener { view ->
        if (rptList.size <= 1) return@OnClickListener
        try {
            val rowTitle = view.tag as RowTitle
            val sortOrder = rowTitle.sortOrder
            topTitles.forEach { t: RowTitle? ->
                t!!.sortOrder = ""
            }
            if (sortOrder.equals("asc")) {
                Collections.sort(rptList, HashMapComparator(rowTitle.key))
                rowTitle.sortOrder = "desc"
            } else {
                Collections.sort(rptList, HashMapComparator(rowTitle.key, true))
                rowTitle.sortOrder = "asc"
            }

            setAllData(rptList, topTitles)

            Toasty.info(context, rowTitle.text + " Sorted", Toast.LENGTH_SHORT).show()
            //adapter.notifyDataSetChanged()

        } catch (e: Exception) {

        }
    }


    override fun onCreateLeftViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder? {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.excelpanel_left_header_item, parent, false)
        return LeftHolder(layout)
    }

    override fun onBindLeftViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val colTitle = getLeftItem(position)
        if (null == holder || holder !is LeftHolder || colTitle == null) {
            return
        }
        holder.tvText.text = colTitle.text
        if (!colTitle.subText.equals("")) {
            holder.tvSubText.text = colTitle.subText
            holder.tvSubText.visibility = View.VISIBLE
        }
        holder.root.layoutParams = holder.root.layoutParams
    }

    internal class LeftHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val tvText: TextView
        val tvSubText: TextView
        val root: View

        init {
            root = itemView.findViewById(R.id.root)
            tvText = itemView.findViewById(R.id.tvText) as TextView
            tvSubText = itemView.findViewById(R.id.tvSubText) as TextView
        }
    }

    override fun onCreateTopLeftView(): View? {
        return LayoutInflater.from(context).inflate(R.layout.excelpanel_top_header_item, null)
    }


}
