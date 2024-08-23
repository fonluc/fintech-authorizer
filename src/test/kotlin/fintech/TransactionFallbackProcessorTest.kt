package fintech

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertTrue

class TransactionFallbackProcessorTest {

    @BeforeEach
    fun setup() {
        categoryBalancesFallback["Food"] = BigDecimal("500.00")
        categoryBalancesFallback["Grocery"] = BigDecimal("300.00")
        cashBalanceFallback = BigDecimal("1000.00")
    }

    @Test
    fun testApproveTransactionWithMerchant() {
        val transaction = Transaction(mcc = "", totalAmount = BigDecimal("50.00"), merchant = "PAG*JoseDaSilva")
        processTransactionWithFallback(transaction)
        assertTrue {
            categoryBalancesFallback["Grocery"] == BigDecimal("250.00")
        }
    }

    @Test
    fun testApproveTransactionWithCategory() {
        val transaction = Transaction("5811", BigDecimal("50.00"))
        processTransactionWithFallback(transaction)
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("450.00") &&
                    cashBalanceFallback == BigDecimal("1000.00")
        }
    }

    @Test
    fun testApproveTransactionWithCashFallback() {
        categoryBalancesFallback["Food"] = BigDecimal("30.00")
        val transaction = Transaction("5811", BigDecimal("50.00"))
        processTransactionWithFallback(transaction)
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("0.00") &&
                    cashBalanceFallback == BigDecimal("980.00")
        }
    }

    @Test
    fun testRejectTransaction() {
        categoryBalancesFallback["Food"] = BigDecimal("30.00") // Saldo insuficiente
        cashBalanceFallback = BigDecimal("10.00") // Saldo insuficiente em CASH

        val transaction = Transaction("5811", BigDecimal("50.00"))
        processTransactionWithFallback(transaction)

        // Verificar se a transação foi rejeitada e nenhum saldo foi alterado
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("30.00") &&
                    cashBalanceFallback == BigDecimal("10.00")
        }
    }


    @Test
    fun testUnknownMCC() {
        cashBalanceFallback = BigDecimal("100.00")
        val transaction = Transaction("1234", BigDecimal("50.00"))
        processTransactionWithFallback(transaction)
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("500.00") &&
                    categoryBalancesFallback["Grocery"] == BigDecimal("300.00") &&
                    cashBalanceFallback == BigDecimal("50.00")
        }
    }

    @Test
    fun testFallbackToCashWithPartialCategoryBalance() {
        categoryBalancesFallback["Food"] = BigDecimal("30.00")
        cashBalanceFallback = BigDecimal("1000.00")
        val transaction = Transaction(mcc = "5811", totalAmount = BigDecimal("50.00"))
        processTransactionWithFallback(transaction)
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("0.00") &&
                    cashBalanceFallback == BigDecimal("980.00")
        }
    }
}
