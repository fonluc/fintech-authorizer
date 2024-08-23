package fintech

import java.math.BigDecimal

// Função para processar transações simples e com prioridade de comerciante
fun processTransaction(transaction: Transaction) {
    // Tenta obter a categoria a partir do nome do comerciante
    val categoryFromMerchant = merchantToCategory[transaction.merchant]

    // Se o nome do comerciante fornece uma categoria, use essa categoria
    val category = categoryFromMerchant ?: mccToCategory[transaction.mcc]

    if (category != null && checkCategoryBalance(category, transaction.totalAmount)) {
        approveTransaction(transaction)
        deductFromCategoryBalance(category, transaction.totalAmount)
    } else {
        rejectTransaction(transaction)
    }
}
