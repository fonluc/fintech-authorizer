package fintech

import java.math.BigDecimal

// Mapeamento de MCC para categorias
val mccToCategory = mapOf(
    "5811" to "Food",
    "5411" to "Grocery"
)

// Simulação de saldo
val categoryBalances = mutableMapOf("Food" to BigDecimal("500.00"), "Grocery" to BigDecimal("300.00"))

// Definição da classe de dados Transaction
data class Transaction(val mcc: String, val totalAmount: BigDecimal)

// Função para processar transações
fun processTransaction(transaction: Transaction) {
    val category = mccToCategory[transaction.mcc]
    if (category != null && checkCategoryBalance(category, transaction.totalAmount)) {
        approveTransaction(transaction)
        deductFromCategoryBalance(category, transaction.totalAmount)
    } else {
        rejectTransaction(transaction)
    }
}

// Funções auxiliares
fun checkCategoryBalance(category: String, amount: BigDecimal): Boolean {
    return categoryBalances.getOrDefault(category, BigDecimal.ZERO) >= amount
}

fun approveTransaction(transaction: Transaction) {
    println("Transaction approved: $transaction")
}

fun deductFromCategoryBalance(category: String, amount: BigDecimal) {
    categoryBalances[category] = categoryBalances.getOrDefault(category, BigDecimal.ZERO) - amount
}

fun rejectTransaction(transaction: Transaction) {
    println("Transaction rejected: $transaction")
}
