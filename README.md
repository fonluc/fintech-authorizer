# L1. Autorizador simples

O **autorizador simples** deve funcionar da seguinte forma:
-  Recebe a transação
-  Usa **apenas** a MCC para mapear a transação para uma categoria de benefícios
-  Aprova ou rejeita a transação
-  Caso a transação seja aprovada, o saldo da categoria mapeada deverá ser diminuído em **totalAmount**.

## Estrutura de Pastas e Arquivos

```
/fintech
├── build.gradle.kts
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── src
│   ├── main
│   │   └── kotlin
│   │       └── fintech
│   │           └── TransactionProcessor.kt
│   └── test
│       └── kotlin
│           └── fintech
│               └── TransactionProcessorTest.kt
├── settings.gradle.kts
└── README.md
```

## Resumo da Implementação

### **Arquivo `TransactionProcessor.kt`**:

- **Mapeamento MCC**: `mccToCategory`
- **Saldo**: `categoryBalances`
- **Função de Processamento**: `processTransaction()`
- **Funções Auxiliares**: `checkCategoryBalance()`, `approveTransaction()`, `deductFromCategoryBalance()`, `rejectTransaction()`

### **Arquivo `TransactionProcessorTest.kt`**:

- **Teste de Aprovação**: `testApproveTransaction`
- **Teste de Rejeição por Saldo Insuficiente**: `testRejectTransaction`
- **Teste de Rejeição por MCC Desconhecido**: `testUnknownMCC`

## Explicação dos Testes

### **`testApproveTransaction`**:

- **Configuração**: Define um saldo inicial para "Food".
- **Ação**: Processa uma transação que deve ser aprovada.
- **Verificação**: Garante que o saldo da categoria "Food" foi reduzido corretamente após a transação.

### **`testRejectTransaction`**:

- **Configuração**: Define um saldo inicial insuficiente para "Food".
- **Ação**: Processa uma transação que deve ser rejeitada.
- **Verificação**: Garante que o saldo não foi alterado após a transação rejeitada.

### **`testUnknownMCC`**:

- **Configuração**: Define saldos iniciais padrão.
- **Ação**: Processa uma transação com um MCC desconhecido.
- **Verificação**: Garante que o saldo não foi alterado e a transação foi rejeitada.

## Resultado dos Testes

### Saída do build

Mostra que os testes foram executados com sucesso e que o sistema está funcionando conforme o esperado. Aqui está um resumo dos resultados:

![Captura de tela 2024-08-21 173802.png](assets%2FCaptura%20de%20tela%202024-08-21%20173802.png)

### **Transação aprovada**:

Para uma transação com MCC "5811" e valor de 50.00, o sistema aprovou a transação e o saldo foi ajustado corretamente.

### **Transação rejeitada**:

- Para uma transação com MCC "1234" (não mapeado), o sistema rejeitou a transação corretamente.
- Para uma transação com MCC "5811" e valor maior que o saldo disponível, a transação foi rejeitada, como esperado.