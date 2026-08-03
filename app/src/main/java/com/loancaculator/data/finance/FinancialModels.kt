package com.loancaculator.data.finance

import java.util.Locale
import kotlin.math.pow

enum class CalculatorType(val key: String, val label: String, val category: String) {
    PERSONAL("personal", "Personal Loan", "Loans"),
    BUSINESS("business", "Business Loan", "Loans"),
    AUTO("auto", "Auto Loan", "Loans"),
    MORTGAGE("mortgage", "Mortgage", "Loans"),
    FD("fd", "Fixed Deposit", "Deposits"),
    RD("rd", "Recurring Deposit", "Deposits");

    companion object {
        fun fromKey(value: String): CalculatorType = entries.firstOrNull { it.key == value } ?: PERSONAL
    }
}

data class LoanInput(
    val principal: Double,
    val annualRate: Double,
    val termMonths: Int,
    val extraPayment: Double = 0.0,
)

data class LoanResult(
    val monthlyPayment: Double,
    val totalPayment: Double,
    val totalInterest: Double,
    val payoffMonths: Int,
)

data class DepositInput(
    val principal: Double,
    val annualRate: Double,
    val termMonths: Int,
    val monthlyContribution: Double = 0.0,
    val compounding: Int = 12,
)

data class DepositResult(
    val maturityValue: Double,
    val totalDeposited: Double,
    val interestEarned: Double,
)

object FinancialCalculator {
    fun loan(input: LoanInput): LoanResult {
        val months = input.termMonths.coerceAtLeast(1)
        val rate = input.annualRate / 100.0 / 12.0
        val payment = if (rate == 0.0) input.principal / months else {
            input.principal * rate * (1 + rate).pow(months) / ((1 + rate).pow(months) - 1)
        }
        val effectivePayment = (payment + input.extraPayment).coerceAtLeast(0.01)
        var balance = input.principal.coerceAtLeast(0.0)
        var total = 0.0
        var elapsed = 0
        while (balance > 0.01 && elapsed < 1200) {
            val interest = balance * rate
            val paid = minOf(balance + interest, effectivePayment)
            balance = balance + interest - paid
            total += paid
            elapsed++
        }
        return LoanResult(payment, total, total - input.principal, elapsed)
    }

    fun deposit(input: DepositInput): DepositResult {
        val months = input.termMonths.coerceAtLeast(1)
        val periods = input.compounding.coerceAtLeast(1)
        val annualRate = input.annualRate / 100.0
        val periodicRate = annualRate / periods
        val principalGrowth = input.principal * (1 + periodicRate).pow(months / 12.0 * periods)
        var contributionGrowth = 0.0
        for (month in 1..months) {
            val periodsLeft = (months - month + 1) / 12.0 * periods
            contributionGrowth += input.monthlyContribution * (1 + periodicRate).pow(periodsLeft)
        }
        val maturity = principalGrowth + contributionGrowth
        val deposited = input.principal + input.monthlyContribution * months
        return DepositResult(maturity, deposited, maturity - deposited)
    }

    fun summary(type: CalculatorType, loan: LoanResult? = null, deposit: DepositResult? = null): String {
        val values = if (loan != null) {
            mapOf("Monthly payment" to loan.monthlyPayment, "Total payment" to loan.totalPayment,
                "Total interest" to loan.totalInterest, "Payoff months" to loan.payoffMonths.toDouble())
        } else {
            mapOf("Maturity value" to (deposit?.maturityValue ?: 0.0),
                (if (type == CalculatorType.RD) "Total deposited" else "Total invested") to (deposit?.totalDeposited ?: 0.0),
                "Interest earned" to (deposit?.interestEarned ?: 0.0))
        }
        return values.entries.joinToString("|") { "${it.key}=${String.format(Locale.US, "%.2f", it.value)}" }
    }
}
