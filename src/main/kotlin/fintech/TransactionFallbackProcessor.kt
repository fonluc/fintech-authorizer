package fintech

import java.math.BigDecimal

// Mapeamento de MCC para categorias específico para fallback
val mccToCategoryFallback = mapOf(
    "5811" to "Food",
    "5411" to "Grocery"
)

// Simulação de saldo específico para fallback
val categoryBalancesFallback = mutableMapOf("Food" to BigDecimal("500.00"), "Grocery" to BigDecimal("300.00"))
var cashBalanceFallback = BigDecimal("1000.00") // Saldo inicial de CASH

// Funções auxiliares específicas para fallback
fun checkCategoryBalanceFallback(category: String, amount: BigDecimal): Boolean {
    return categoryBalancesFallback.getOrDefault(category, BigDecimal.ZERO) >= amount
}

fun deductFromCategoryBalanceFallback(category: String, amount: BigDecimal) {
    categoryBalancesFallback[category] = categoryBalancesFallback.getOrDefault(category, BigDecimal.ZERO) - amount
}

fun deductFromCashBalanceFallback(amount: BigDecimal) {
    cashBalanceFallback -= amount
    println("Deducted $amount from CASH")
}

// Função para processar transações com fallback
fun processTransactionWithFallback(transaction: Transaction) {
    val category = mccToCategoryFallback[transaction.mcc]
    if (category != null && checkCategoryBalanceFallback(category, transaction.totalAmount)) {
        approveTransaction(transaction)
        deductFromCategoryBalanceFallback(category, transaction.totalAmount)
    } else if (cashBalanceFallback >= transaction.totalAmount) {
        approveTransaction(transaction)
        deductFromCashBalanceFallback(transaction.totalAmount)
    } else {
        rejectTransaction(transaction)
    }
}
