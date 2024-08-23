package fintech

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import java.math.BigDecimal

class TransactionFallbackProcessorTest {

    @BeforeEach
    fun setup() {
        // Resetar os saldos antes de cada teste
        categoryBalancesFallback["Food"] = BigDecimal("500.00")
        categoryBalancesFallback["Grocery"] = BigDecimal("300.00")
        cashBalanceFallback = BigDecimal("1000.00")
    }

    @Test
    fun testApproveTransactionWithMerchant() {
        val transaction = Transaction(mcc = "", totalAmount = BigDecimal("50.00"), merchant = "PAG*JoseDaSilva")
        processTransactionWithFallback(transaction)

        // Verificar se a transação foi aprovada e deduzida da categoria Grocery
        assertTrue {
            categoryBalancesFallback["Grocery"] == BigDecimal("250.00")
        }
    }

    @Test
    fun testApproveTransactionWithCategory() {
        val transaction = Transaction("5811", BigDecimal("50.00"))
        processTransactionWithFallback(transaction)

        // Verificar se a transação foi aprovada e deduzida da categoria
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("450.00") &&
                    cashBalanceFallback == BigDecimal("1000.00") // CASH não deve ser alterado
        }
    }

    @Test
    fun testApproveTransactionWithCashFallback() {
        // Configurar o saldo da categoria para um valor insuficiente
        categoryBalancesFallback["Food"] = BigDecimal("30.00")

        val transaction = Transaction("5811", BigDecimal("50.00"))
        processTransactionWithFallback(transaction)

        // Verificar se a transação foi aprovada e deduzida do CASH
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("30.00") &&
                    cashBalanceFallback == BigDecimal("950.00")
        }
    }

    @Test
    fun testRejectTransaction() {
        // Configurar o saldo da categoria e do CASH para valores insuficientes
        categoryBalancesFallback["Food"] = BigDecimal("30.00")
        cashBalanceFallback = BigDecimal("40.00")

        val transaction = Transaction("5811", BigDecimal("50.00"))
        processTransactionWithFallback(transaction)

        // Verificar se a transação foi rejeitada
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("30.00") &&
                    cashBalanceFallback == BigDecimal("40.00")
        }
    }

    @Test
    fun testUnknownMCC() {
        // Configurar o saldo do CASH para um valor suficiente
        cashBalanceFallback = BigDecimal("100.00")

        val transaction = Transaction("1234", BigDecimal("50.00"))
        processTransactionWithFallback(transaction)

        // Verificar se a transação foi aprovada e deduzida do CASH
        assertTrue {
            categoryBalancesFallback["Food"] == BigDecimal("500.00") &&
                    categoryBalancesFallback["Grocery"] == BigDecimal("300.00") &&
                    cashBalanceFallback == BigDecimal("50.00")
        }
    }
}
