package `in`.apparray.mylibrary.classes

class Cell internal constructor(val clmIndex: Int,val clmKey: String, val rowIndex: Int, val text: String, val subText: String = "", val colLength: Int = 0, var bgColor: Int = 0, var selected : Boolean = false, val icon : Int = 0, val gravity: Int = 0, val cellFormat: String = "")