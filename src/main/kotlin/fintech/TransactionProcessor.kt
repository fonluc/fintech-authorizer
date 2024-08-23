package fintech

import java.math.BigDecimal

fun processTransaction(transaction: Transaction) {
    val categoryFromMerchant = merchantToCategory[transaction.merchant]
    val category = categoryFromMerchant ?: mccToCategory[transaction.mcc]

    if (category != null) {
        val categoryBalance = categoryBalances.getOrDefault(category, BigDecimal.ZERO)
        if (categoryBalance >= transaction.totalAmount) {
            approveTransaction(transaction)
            deductFromCategoryBalance(category, transaction.totalAmount)
        } else {
            val remainingAmount = transaction.totalAmount - categoryBalance
            if (remainingAmount <= cashBalance) {
                approveTransaction(transaction)
                deductFromCategoryBalance(category, categoryBalance)
                deductFromCashBalance(remainingAmount)
            } else {
                rejectTransaction(transaction)
            }
        }
    } else if (transaction.totalAmount <= cashBalance) {
        approveTransaction(transaction)
        deductFromCashBalance(transaction.totalAmount)
    } else {
        rejectTransaction(transaction)
    }
}
