package fintech

import java.math.BigDecimal

// Definição da classe de dados Transaction
data class Transaction(val mcc: String, val totalAmount: BigDecimal)

// Mapeamento de MCC para categorias
val mccToCategory = mapOf(
    "5811" to "Food",
    "5411" to "Grocery"
)

// Simulação de saldo
val categoryBalances = mutableMapOf("Food" to BigDecimal("500.00"), "Grocery" to BigDecimal("300.00"))
var cashBalance = BigDecimal("1000.00") // Saldo inicial de CASH

// Funções auxiliares
fun checkCategoryBalance(category: String, amount: BigDecimal): Boolean {
    return categoryBalances.getOrDefault(category, BigDecimal.ZERO) >= amount
}

fun deductFromCategoryBalance(category: String, amount: BigDecimal) {
    categoryBalances[category] = categoryBalances.getOrDefault(category, BigDecimal.ZERO) - amount
}

fun deductFromCashBalance(amount: BigDecimal) {
    cashBalance -= amount
    println("Deducted $amount from CASH")
}

fun approveTransaction(transaction: Transaction) {
    println("Transaction approved: $transaction")
}

fun rejectTransaction(transaction: Transaction) {
    println("Transaction rejected: $transaction")
}
