package `in`.apparray.mylibrary.utils


import `in`.apparray.mylibrary.R
import `in`.apparray.mylibrary.classes.RowTitle
import `in`.apparray.mylibrary.classes.SelectSpinner
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.text.Layout
import android.util.TypedValue
import android.view.View
import android.widget.TextView

import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationHolder
import com.basgeekball.awesomevalidation.utility.custom.CustomErrorReset
import com.basgeekball.awesomevalidation.utility.custom.CustomValidation
import com.basgeekball.awesomevalidation.utility.custom.CustomValidationCallback
import com.rilixtech.materialfancybutton.CoreIcon
import es.dmoral.toasty.Toasty
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.streaming.SXSSFSheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFPrintSetup
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

/**
 * Created by Umang on 1/21/2018.
 */

object MyUtils {


    val dtFormat = "dd-MM-yyyy"
    val timeFormat = "HH:mm:ss"
    val dateTimeFormat = "dd-MM-yy HH:mm:ss"

    val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/bizTRA"
    var mastersMap = HashMap<String, Any>()

    fun curMoment(
        dateStr: String = "", timeInMilis: Long = 0, hourOfDay: Boolean = false, dtformat: String = dtFormat
    ): Calendar {
        val calendar = Calendar.getInstance()
        if (timeInMilis > 0) calendar.timeInMillis = timeInMilis
        else if (dateStr != "") {
            val sdf = SimpleDateFormat(dtformat)
            try {
                var date1: Date? = null
                date1 = sdf.parse(dateStr)
                calendar.time = date1
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }

        if (hourOfDay) {
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
        }
        return calendar
    }

    fun curMomentLong(date: String = "", hourOfDay: Boolean = false): Long {
        val calendar = curMoment(date, 0, hourOfDay)
        return calendar.timeInMillis
    }

    fun curMomentString(timeInMilis: Long = 0, format: String = dtFormat): String {
        val format1 = SimpleDateFormat(format)
        if (timeInMilis <= 0) return format1.format(curMomentLong("", false))
        else return format1.format(timeInMilis)
    }

    fun hashKeySet(any: Any?): String {
        try {
            var keys = ""
            var bTopic = any as Map<String, Any>
            bTopic.keys.forEach { t -> keys += ",$t" }
            return keys
        } catch (e: Exception) {
            return ""
        }
    }

    fun hasMapString(map: Any?): String {
        var value: String = map.toString()
        if (value == "null") value = ""
        return value
    }

    fun hasMapFloat(map: Any?): Float {
        try {
            val value: Float = map.toString().toFloat()
            val decimalFormat = DecimalFormat("#.##")
            return java.lang.Float.valueOf(decimalFormat.format(value))
        } catch (e: Exception) {
            return 0f
        }
    }

    fun hasMapDouble(map: Any?, formatDigit: Boolean = false): Double {
        try {
            val value = map.toString().toDouble()
            if (!formatDigit) return value
            else {
                val decimalFormat = DecimalFormat("#.##")
                return java.lang.Double.valueOf(decimalFormat.format(value))
            }
        } catch (e: Exception) {
            return 0.0
        }
    }

    fun hasMapLong(map: Any?): Long {
        try {
            val value: Long = map.toString().toLong()
            return value
        } catch (e: Exception) {
            return 0
        }
    }

    fun hasMapBoolean(map: Any?): Boolean {
        try {
            val value: Boolean = map.toString().toBoolean()
            return value
        } catch (e: Exception) {
            return false
        }
    }

    fun hasMapInt(map: Any?): Int {
        try {
            val value: Int = map.toString().toInt()
            return value
        } catch (e: Exception) {
            return 0
        }
    }

    fun indianNumberWithDecimal(number: Float): String {
        try {
            val nu = NumberFormat.getNumberInstance(Locale("EN", "IN")).format(java.lang.Double.parseDouble(String.format("%.1f", Math.abs(number))))
            if (number >= 0) return nu
            else return "($nu)"

            //return NumberFormat.getNumberInstance(Locale("EN", "IN")).format(java.lang.Double.parseDouble(String.format("%.1f", number)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    fun indianNumber(number: Double): String {
        val nu = NumberFormat.getNumberInstance(Locale("EN", "IN")).format(Math.abs(Math.round(Math.ceil(number))))
        if (number >= 0) return nu
        else return "($nu)"
    }

    fun indianNumberWithDecimal(number: Double, inrSymbolFlag: Boolean = false): String {
        try {
            //val nu = NumberFormat.getNumberInstance(Locale("EN", "IN")).format(java.lang.Double.parseDouble(String.format("%.1f", Math.abs(number))))
            //val nu = NumberFormat.getNumberInstance(Locale("EN", "IN")).format(DecimalFormat("0.00").format(number))

            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN")) as DecimalFormat
            if (!inrSymbolFlag) {
                val symbols = formatter.decimalFormatSymbols
                symbols.currencySymbol = "" // Don't use null.
                formatter.decimalFormatSymbols = symbols
            }
            val nu = formatter.format(Math.abs(number))

            //val nu = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(number)
            if (number >= 0) return nu
            else return "($nu)"

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    fun isValidMobileNo(mobile: String): Boolean {
        val regEx = "^[0-9]{10}$"
        return mobile.matches(regEx.toRegex())
    }

    fun isValidName(name: String): Boolean {
        val regEx = "^[a-zA-Z0-9_ &-.]*$"
        return name.matches(regEx.toRegex())
    }

    fun getFirstEntry(data1: Any?, key: String = ""): Any {
        var data: HashMap<String, Any>
        try {
            data = data1 as HashMap<String, Any>
            val entry = data.entries.first()
            try {
                val ent = entry.value as HashMap<String, Any>
                if (!key.equals("") && ent.containsKey(key)) {
                    return ent[key]!!
                } else {
                    val a1 = ent.entries.first()
                    return a1.value
                }
            } catch (e: Exception) {
                return entry.value
            }
        } catch (e: Exception) {
            return ""
        }
    }

    fun share(file: File, context: Context) {
        val fileUri = Uri.fromFile(file)
        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        shareIntent.setType("application/octet-stream")
        context.startActivity(Intent.createChooser(shareIntent, "Share"))
    }

    fun docHasMap(key: String): HashMap<String, Any> {
        if (!mastersMap!!.contains(key)) {
            var keyMap: HashMap<String, Any> = HashMap()
            mastersMap!![key] = keyMap //added new master key so we can get updated data for blank key
        }
        return mastersMap!![key] as HashMap<String, Any>
    }

    fun docHasMapList(key: String, doc: HashMap<String, Any>? = null): java.util.ArrayList<HashMap<String, Any>> {
        val listT: java.util.ArrayList<HashMap<String, Any>> = java.util.ArrayList()
        var keyMap = doc
        if (keyMap == null) {
            if (!mastersMap!!.contains(key)) return listT

            keyMap = mastersMap!![key] as HashMap<String, Any>?
        }

        keyMap!!.forEach { entry ->
            try {
                var lT: java.util.HashMap<String, Any> = java.util.HashMap()
                lT.put("key", entry.key)
                lT.put("value", entry.value)

                listT!!.add(lT)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return listT
    }

    fun textViewValidation(validation: AwesomeValidation, textView: TextView, defaultTxt: String) {
        validation.addValidation(textView.rootView, object : CustomValidation {
            override fun compare(validationHolder: ValidationHolder): Boolean {
                if (textView.visibility == View.GONE) return true
                return !textView.text.equals(defaultTxt)
            }
        }, object : CustomValidationCallback {
            override fun execute(validationHolder: ValidationHolder) {
                //val textViewError = (validationHolder.getView() as SelectSpinner).getSelectedView() as AppCompatTextView
                //val textViewError = textView.selectedView as TextView
                textView.setError(validationHolder.getErrMsg())
                textView.setTextColor(Color.RED)
            }
        }, object : CustomErrorReset {
            override fun reset(validationHolder: ValidationHolder) {
                //val textViewError = (validationHolder.getView() as SelectSpinner).getSelectedView() as AppCompatTextView
                //val textViewError = selectSpinner.selectedView as TextView
                textView.setError(null)
                textView.setTextColor(Color.BLACK)
            }
        }, "Required")
    }

    fun hasMapList(data: Map<String, Any>?): ArrayList<HashMap<String, Any>> {
        var dataList = ArrayList<HashMap<String, Any>>()
        if (data == null) return dataList
        data!!.forEach { entry ->
            if (entry.value is HashMap<*, *>) {
                val data = entry.value as HashMap<String, Any>
                data["docId"] = entry.key
                dataList!!.add(data)
            } else {
                val ent = HashMap<String, Any>()
                ent["key"] = entry.key
                ent["value"] = entry.value
                dataList!!.add(ent)
            }
        }
        return dataList
    }

    fun titleCase(string: String): String {
        return string[0].toUpperCase() + string.substring(1, string.length)
    }

    fun SelectSpinnerValidation(validation: AwesomeValidation, selectSpinner: SelectSpinner, context: Context? = null) {
        validation.addValidation(selectSpinner.rootView, object : CustomValidation {
            override fun compare(validationHolder: ValidationHolder): Boolean {
                if (selectSpinner.visibility == View.GONE) return true

                val parent = selectSpinner.parent
                if (parent is View && parent.visibility == View.GONE) return true

                return selectSpinner.validate
            }
        }, object : CustomValidationCallback {
            override fun execute(validationHolder: ValidationHolder) {
                //val textViewError = (validationHolder.getView() as SelectSpinner).getSelectedView() as AppCompatTextView
                if (selectSpinner.selectedView != null) {
                    val textViewError = selectSpinner.selectedView as TextView
                    textViewError.setError(validationHolder.getErrMsg())
                    textViewError.setTextColor(Color.RED)
                } else {
                    //toast here....
                    if (context != null) Toasty.error(context, selectSpinner.defaultText + " Required.").show()
                }
            }
        }, object : CustomErrorReset {
            override fun reset(validationHolder: ValidationHolder) {
                //val textViewError = (validationHolder.getView() as SelectSpinner).getSelectedView() as AppCompatTextView
                val textViewError = selectSpinner.selectedView as TextView
                textViewError.setError(null)
                textViewError.setTextColor(Color.BLACK)
            }
        }, "Required")
    }

    fun createExcel(
        rptList: ArrayList<HashMap<String, Any>>,
        topTitles: MutableList<RowTitle>,
        title: String,
        subTitle: String,
        fileName: String,
        fontHeight: Short = 9,
        rowHeight: Float = 0f,
        paperSize: PaperSize = PaperSize.A4_PAPER,
        printOrientation: PrintOrientation = PrintOrientation.PORTRAIT,
        srNoStart: Int = 1
    ): File? {
        val workbook = SXSSFWorkbook()
        workbook.setCompressTempFiles(true);

        val sheet = workbook.createSheet() as SXSSFSheet
        //sheet.setRandomAccessWindowSize(100)

        //Print Page Setup of Excel Sheet
        val unitUtils = UnitUtils()
        val printSetup = sheet.getPrintSetup() as XSSFPrintSetup
        printSetup.setPaperSize(paperSize)
        printSetup.setOrientation(printOrientation)
        sheet.setMargin(Sheet.LeftMargin, 0.45)
        sheet.setMargin(Sheet.RightMargin, 0.45)
        sheet.setMargin(Sheet.TopMargin, 0.5)
        sheet.setMargin(Sheet.BottomMargin, 0.25)


        val titleStyle = getCellStyle(workbook, (fontHeight + 3).toShort(), true, "center")
        val subTitleStyle = getCellStyle(workbook, (fontHeight + 2).toShort(), true, "")
        val hedCellStyle = getCellStyle(workbook, (fontHeight + 1).toShort(), true, "center")
        val totalCellStyle = getCellStyle(workbook, fontHeight, true, "center")
        val cellStyle = getCellStyle(workbook, fontHeight, allign = "center")
        val footerStyle = getCellStyle(workbook, (fontHeight - 1).toShort(), allign = "center")

        var rowDist = sheet.createRow(0)
        var rowTitle = sheet.createRow(1)
        var rowClm = sheet.createRow(2)

        val dCell0 = rowDist.createCell(0)
        dCell0.setCellStyle(titleStyle)
        dCell0.setCellValue(title)

        val tCell0 = rowTitle.createCell(0)
        tCell0.setCellStyle(subTitleStyle)
        tCell0.setCellValue(subTitle)

        val cCell0 = rowClm.createCell(0)
        cCell0.setCellStyle(hedCellStyle)
        cCell0.setCellValue("#")

        //total 195 for landscape
        //75.87 for portrait

        var maxExcelWidth = 75.87
        var totExcelWidth = 3.0 //first SrNo column width
        if (printOrientation == PrintOrientation.LANDSCAPE) maxExcelWidth = 127.0

        var clmIndex = 1
        for (clm in topTitles) {
            val dCell = rowDist.createCell(clmIndex)
            dCell.setCellStyle(titleStyle)
            dCell.setCellValue("")

            val tCell = rowTitle.createCell(clmIndex)
            tCell.setCellStyle(subTitleStyle)
            tCell.setCellValue("")

            val cCell = rowClm.createCell(clmIndex)
            cCell.setCellStyle(hedCellStyle)
            cCell.setCellValue(clm.text)

            if (clm.excelColLength > 255) clm.excelColLength = 10

            totExcelWidth += clm.excelColLength
            clmIndex += 1
        }

        val wDiff = maxExcelWidth - totExcelWidth
        val eWidthDiff = Math.abs(wDiff) / (topTitles.size - 1)
        topTitles.forEach { rowTitle: RowTitle? ->
            if (wDiff < 0) rowTitle!!.excelColLength -= eWidthDiff.roundToInt()
            else rowTitle!!.excelColLength += eWidthDiff.roundToInt()
        }


//        rowClm.forEachIndexed { index, cell ->
//            //sheet.autoSizeColumn(index)
//            sheet.setColumnWidth(index, 50)
//        }

        var srNo = srNoStart
        for (rowIndex in 0 until rptList.size) {
            val row = sheet.createRow(rowIndex + 3)
            if (rowHeight > 0) row.heightInPoints = rowHeight

            var cStyle = cellStyle
            var cell = row.createCell(0)
            if (srNo <= 0) {
                cStyle = totalCellStyle
                cell.setCellValue("-")
            } else {
                cell.setCellStyle(cStyle)
                cell.setCellValue(srNo.toString())
            }
            srNo += 1


            val data = rptList.get(rowIndex)
            clmIndex = 1
            for (clm in topTitles) {
                cell = row.createCell(clmIndex) // as XSSFCell
                cell.setCellStyle(cStyle)
                val cData = data[clm.key]
                if (cData is Double || cData is Long || cData is Int) {
                    cell.setCellValue(hasMapDouble(cData))
                } else if (cData is Boolean) {
                    cell.setCellValue(hasMapBoolean(cData))
                } else {
                    cell.setCellValue(hasMapString(cData))
                }
                //cell.setCellValue(hasMapString(data[clm.key]))
                clmIndex += 1
            }
        }

        val footerRow = rptList.size + 3

        val row = sheet.createRow(footerRow)

        val cell = row.createCell(0)
        cell.setCellStyle(footerStyle)
        cell.setCellValue("")
        topTitles.forEachIndexed { index, rowTitle ->
            val cell = row.createCell(index + 1)
            cell.setCellStyle(footerStyle)
            cell.setCellValue("")

            //set excel column width
            //https://poi.apache.org/apidocs/dev/org/apache/poi/xssf/usermodel/XSSFSheet.html#setColumnWidth-int-int-
            sheet.setColumnWidth(index + 1, rowTitle.excelColLength * 256) // * 256 represents characters logic.
        }
        sheet.setColumnWidth(0, 3 * 256) //SrNo Column Width set

        //MEARGING CELLS
        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, topTitles.size))
        sheet.addMergedRegion(CellRangeAddress(1, 1, 0, topTitles.size))
        sheet.addMergedRegion(CellRangeAddress(footerRow, footerRow, 0, topTitles.size))
        sheet.fitToPage = true

        // val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/aaApp"
        val outFileName = "$fileName.xlsx"
        try {
            val pdfDir = File(dirPath)
            if (!pdfDir.exists()) pdfDir.mkdir()

            val outFile = File(dirPath, outFileName)
            val outputStream = FileOutputStream(outFile.getAbsolutePath())
            workbook.write(outputStream)
            outputStream.flush()
            outputStream.close()
            return outFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
            //Toasty.error(context, "Excel file not created.").show()
        }

    }

    private fun getCellStyle(
        workbook: SXSSFWorkbook, fontHeight: Short = 9, bold: Boolean = false, allign: String = "center", dateFormat: String = ""
    ): CellStyle? {

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

    fun fawiTextViewIcon(context: Context, view: TextView, iconText: Int) {
        val font = CoreIcon.findFont(context.applicationContext, "fawi")
        val icon = font.getIcon(context.resources.getString(iconText))

        val mTypeFace = font.getTypeface(context.applicationContext)
        view.setTypeface(mTypeFace)
        view.setText(icon.character.toString())


    }

    fun fawiDrawableIcon(
        context: Context, view: TextView, iconText: Int, size: Float = 16f, color: Int = R.color.material_white
    ) {

        val font = CoreIcon.findFont(context.applicationContext, "fawi")
        val icon = font.getIcon(context.resources.getString(iconText))
        val mTypeFace = font.getTypeface(context.applicationContext)

        val gIcon = TextDrawable(context)
        gIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
        gIcon.setTextAlign(Layout.Alignment.ALIGN_CENTER)
        gIcon.setTypeface(mTypeFace)
        gIcon.setText(icon.character.toString())
        gIcon.setTextColor(color)

        view.setCompoundDrawablesWithIntrinsicBounds(gIcon, null, null, null);

    }



}
