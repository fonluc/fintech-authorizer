package fintech

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertTrue

class TransactionProcessorTest {

    @BeforeEach
    fun setup() {
        categoryBalances["Food"] = BigDecimal("500.00")
        categoryBalances["Grocery"] = BigDecimal("300.00")
        cashBalance = BigDecimal("1000.00")
    }

    @Test
    fun testApproveTransactionWithMerchant() {
        val transaction = Transaction(mcc = "", totalAmount = BigDecimal("50.00"), merchant = "UBER EATS")
        processTransaction(transaction)
        assertTrue {
            categoryBalances["Food"] == BigDecimal("450.00")
        }
    }

    @Test
    fun testRejectTransaction() {
        categoryBalances["Food"] = BigDecimal("30.00") // Saldo insuficiente
        cashBalance = BigDecimal("10.00") // Saldo insuficiente em CASH

        val transaction = Transaction("5811", BigDecimal("50.00"))
        processTransaction(transaction)

        // Verificar se a transação foi rejeitada e nenhum saldo foi alterado
        assertTrue {
            categoryBalances["Food"] == BigDecimal("30.00") &&
                    cashBalance == BigDecimal("10.00")
        }
    }


    @Test
    fun testUnknownMCC() {
        val transaction = Transaction("1234", BigDecimal("50.00"))
        processTransaction(transaction)
        assertTrue {
            categoryBalances["Food"] == BigDecimal("500.00")
            categoryBalances["Grocery"] == BigDecimal("300.00")
        }
    }

    @Test
    fun testApproveTransactionWithExactBalance() {
        categoryBalances["Food"] = BigDecimal("50.00")
        val transaction = Transaction(mcc = "5811", totalAmount = BigDecimal("50.00"))
        processTransaction(transaction)
        assertTrue {
            categoryBalances["Food"] == BigDecimal("0.00")
        }
    }

    @Test
    fun testFallbackToCashWithPartialCategoryBalance() {
        categoryBalances["Food"] = BigDecimal("30.00")
        cashBalance = BigDecimal("1000.00")
        val transaction = Transaction(mcc = "5811", totalAmount = BigDecimal("50.00"))
        processTransaction(transaction)
        assertTrue {
            categoryBalances["Food"] == BigDecimal("0.00") &&
                    cashBalance == BigDecimal("980.00")
        }
    }
}
