package com.loancaculator.ui.screen.finance

import org.junit.Assert.assertEquals
import org.junit.Test

class FinanceCommonTest {
    @Test
    fun formatAmountInputGroupsThousands() {
        assertEquals("1,234,567", formatAmountInput("1234567"))
        assertEquals("1,234,567.89", formatAmountInput("1234567.89"))
    }

    @Test
    fun formatAmountInputKeepsEmptyAndTrailingDecimal() {
        assertEquals("", formatAmountInput(""))
        assertEquals("0.", formatAmountInput("0."))
    }

    @Test
    fun parseNumberIgnoresGroupingSeparators() {
        assertEquals(1234567.89, parseNumber("1,234,567.89")!!, 0.001)
    }
}
