package fintech

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertTrue

class TransactionProcessorTest {

    @BeforeEach
    fun setup() {
        // Resetar os saldos antes de cada teste
        categoryBalances["Food"] = BigDecimal("500.00")
        categoryBalances["Grocery"] = BigDecimal("300.00")
        cashBalance = BigDecimal("1000.00")
    }

    @Test
    fun testApproveTransactionWithMerchant() {
        val transaction = Transaction(mcc = "", totalAmount = BigDecimal("50.00"), merchant = "UBER EATS")
        processTransaction(transaction)

        // Verificar se a transação foi aprovada e deduzida da categoria Food
        assertTrue {
            categoryBalances["Food"] == BigDecimal("450.00")
        }
    }

    @Test
    fun testRejectTransaction() {
        // Configurar o saldo para um valor insuficiente
        categoryBalances["Food"] = BigDecimal("30.00")

        val transaction = Transaction("5811", BigDecimal("50.00"))
        processTransaction(transaction)

        // Verificar se a transação foi rejeitada
        assertTrue {
            categoryBalances["Food"] == BigDecimal("30.00")
        }
    }

    @Test
    fun testUnknownMCC() {
        val transaction = Transaction("1234", BigDecimal("50.00"))
        processTransaction(transaction)

        // Verificar se a transação foi rejeitada devido a um MCC desconhecido
        assertTrue {
            categoryBalances["Food"] == BigDecimal("500.00")
            categoryBalances["Grocery"] == BigDecimal("300.00")
        }
    }
}
