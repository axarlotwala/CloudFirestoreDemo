package `in`.apparray.utis.classes

import `in`.apparray.mylibrary.utils.MyUtils


class HashMapComparator(private val key: String, private val descFlag: Boolean = false) : Comparator<Map<String, Any>> {
    override fun compare(p0: Map<String, Any>?, p1: Map<String, Any>?): Int {
        val firstValue = p0!![key]
        val secondValue = p1!![key]

        //if(firstValue is Float || firstValue is Int || firstValue is Long || firstValue is Double){
        if (firstValue is Float) {
            val fValue = MyUtils.hasMapFloat(firstValue)
            val tValue = MyUtils.hasMapFloat(secondValue)
            if (descFlag)
                return if (tValue > fValue) 1 else -1
            else
                return if (fValue > tValue) 1 else -1
        } else if (firstValue is Double) {
            val fValue = MyUtils.hasMapDouble(firstValue)
            val tValue = MyUtils.hasMapDouble(secondValue)
            if (descFlag)
                return if (tValue > fValue) 1 else -1
            else
                return if (fValue > tValue) 1 else -1
        } else if (firstValue is Int) {
            val fValue = MyUtils.hasMapInt(firstValue)
            val tValue = MyUtils.hasMapInt(secondValue)
            if (descFlag)
                return if (tValue > fValue) 1 else -1
            else
                return if (fValue > tValue) 1 else -1
        } else if (firstValue is Long) {
            val fValue = MyUtils.hasMapLong(firstValue)
            val tValue = MyUtils.hasMapLong(secondValue)
            if (descFlag)
                return if (tValue > fValue) 1 else -1
            else
                return if (fValue > tValue) 1 else -1
        } else {
            if (descFlag)
                return secondValue.toString().compareTo(firstValue.toString())
            else
                return firstValue.toString().compareTo(secondValue.toString())
        }

//        if (descFlag)
//            return secondValue.toString().compareTo(firstValue.toString())
//        else
//            return firstValue.toString().compareTo(secondValue.toString())

//        if (intCompare)
//            return if (firstValue. > secondValue) 1 else -1
//        else
//            return firstValue.toString().compareTo(secondValue.toString())
    }


}