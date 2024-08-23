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

// Função para processar transações com fallback e prioridade de comerciante
fun processTransactionWithFallback(transaction: Transaction) {
    val categoryFromMerchant = merchantToCategory[transaction.merchant]
    val category = categoryFromMerchant ?: mccToCategoryFallback[transaction.mcc]

    if (category != null) {
        val categoryBalance = categoryBalancesFallback.getOrDefault(category, BigDecimal.ZERO)
        if (categoryBalance >= transaction.totalAmount) {
            approveTransaction(transaction)
            deductFromCategoryBalanceFallback(category, transaction.totalAmount)
        } else {
            val remainingAmount = transaction.totalAmount - categoryBalance
            if (remainingAmount <= cashBalanceFallback) {
                approveTransaction(transaction)
                deductFromCategoryBalanceFallback(category, categoryBalance)
                deductFromCashBalanceFallback(remainingAmount)
            } else {
                rejectTransaction(transaction)
            }
        }
    } else if (transaction.totalAmount <= cashBalanceFallback) {
        approveTransaction(transaction)
        deductFromCashBalanceFallback(transaction.totalAmount)
    } else {
        rejectTransaction(transaction)
    }
}
