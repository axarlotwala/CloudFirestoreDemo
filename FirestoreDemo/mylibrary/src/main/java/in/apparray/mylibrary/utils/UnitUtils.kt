package `in`.apparray.mylibrary.utils

class UnitUtils {

    fun pointToPixel(point: Double): Int {
        val value = point * 1.33
        return value.toInt()
        //return Utils.toBigDecimal(value).intValue()
    }

    fun pixelToPoint(pixel: Double): Int {
        val value = pixel * 0.75
        return value.toInt()
        //return Utils.toBigDecimal(value).intValue()
    }

    fun pointToInche(value: Float): Float {
        return value / 72f
    }

    fun incheToPoint(value: Double): Double {
        return value * 72.0
    }

    fun pointToTwip(point: Int): Int {
        return point * 20
    }

}