package `in`.apparray.mylibrary.classes

import java.util.*

class NumberToWordsConverter {

    companion object {

        val units = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
        val tens = arrayOf("", // 0
                "", // 1
                "Twenty", // 2
                "Thirty", // 3
                "Forty", // 4
                "Fifty", // 5
                "Sixty", // 6
                "Seventy", // 7
                "Eighty", // 8
                "Ninety"    // 9
        )

        fun convert(n: Double): String {
            var pass = n.toString() + ""
            val token = StringTokenizer(pass, ".")
            val first = token.nextToken()
            val last = token.nextToken()

            try {
                pass = convert(Integer.parseInt(first))

                var lastI = Integer.parseInt(last)
                if (lastI == 0)
                    return "$pass Rs Only" //pass

                if (!last.startsWith("0") && lastI < 10) {
                    lastI = lastI * 10
                }

//                if (lastI == 0) {
//                    return pass
//                } else if (lastI < 10) {
//                    lastI = lastI * 10
//                }
                pass = pass + " & " + convert(lastI) + " Paise Only" //POINT

//                for (i in 0 until last.length) {
//                    val get = convert(Integer.parseInt(last.get(i) + ""))
//                    if (get.isEmpty()) {
//                        pass = "$pass ZERO"
//                    } else {
//                        pass = "$pass $get"
//                    }
//                }

            } catch (nf: NumberFormatException) {
            }

            return pass
        }

        fun convert(n: Int): String {
            if (n < 0) {
                return "Minus " + convert(-n)
            }

            if (n < 20) {
                return units[n]
            }

            if (n < 100) {
                return tens[n / 10] + (if (n % 10 != 0) " " else "") + units[n % 10]
            }

            if (n < 1000) {
                return units[n / 100] + " Hundred" + (if (n % 100 != 0) " " else "") + convert(n % 100)
            }

            if (n < 100000) {
                return convert(n / 1000) + " Thousand" + (if (n % 10000 != 0) " " else "") + convert(n % 1000)
            }

            return if (n < 10000000) {
                convert(n / 100000) + " Lakh" + (if (n % 100000 != 0) " " else "") + convert(n % 100000)
            } else convert(n / 10000000) + " Crore" + (if (n % 10000000 != 0) " " else "") + convert(n % 10000000)

        }
    }


}