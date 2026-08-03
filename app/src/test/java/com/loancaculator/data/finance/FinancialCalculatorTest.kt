package com.loancaculator.data.finance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialCalculatorTest {
    @Test
    fun zeroInterestLoanUsesPrincipalDividedByMonths() {
        val result = FinancialCalculator.loan(LoanInput(1_200_000.0, 0.0, 12))
        assertEquals(100_000.0, result.monthlyPayment, 0.01)
        assertEquals(1_200_000.0, result.totalPayment, 0.01)
    }

    @Test
    fun interestBearingLoanReportsInterest() {
        val result = FinancialCalculator.loan(LoanInput(100_000_000.0, 8.5, 60))
        assertTrue(result.monthlyPayment > 0.0)
        assertTrue(result.totalInterest > 0.0)
        assertEquals(60, result.payoffMonths)
    }

    @Test
    fun zeroRateDepositKeepsDepositedAmount() {
        val result = FinancialCalculator.deposit(DepositInput(1_000_000.0, 0.0, 12, 100_000.0))
        assertEquals(2_200_000.0, result.maturityValue, 0.01)
        assertEquals(0.0, result.interestEarned, 0.01)
    }
}
