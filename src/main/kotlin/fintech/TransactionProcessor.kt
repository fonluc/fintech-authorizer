package fintech

import java.math.BigDecimal

// Função para processar transações simples
fun processTransaction(transaction: Transaction) {
    val category = mccToCategory[transaction.mcc]
    if (category != null && checkCategoryBalance(category, transaction.totalAmount)) {
        approveTransaction(transaction)
        deductFromCategoryBalance(category, transaction.totalAmount)
    } else {
        rejectTransaction(transaction)
    }
}
