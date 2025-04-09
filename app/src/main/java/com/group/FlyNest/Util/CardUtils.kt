package com.group.FlyNest.util

object CardUtils {

    enum class CardType {
        VISA, MASTERCARD, AMEX, UNKNOWN
    }

    fun getCardType(cardNumber: String): CardType {
        return when {
            cardNumber.startsWith("4") && (cardNumber.length == 13 || cardNumber.length == 16) -> CardType.VISA
            cardNumber.startsWith("5") && cardNumber.length == 16 -> CardType.MASTERCARD
            cardNumber.startsWith("34") || cardNumber.startsWith("37") && cardNumber.length == 15 -> CardType.AMEX
            else -> CardType.UNKNOWN
        }
    }

    fun isValidCardNumber(cardNumber: String): Boolean {
        if (cardNumber.length < 13) return false

        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber.substring(i, i + 1).toInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        return (sum % 10 == 0)
    }
}