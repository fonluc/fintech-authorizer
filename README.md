# Fintech Authorizer

# Estrutura de Pastas e Arquivos

```
fintech/
│
├── src/
│   └── main/
│       └── kotlin/
│           └── fintech/
│               ├── Common.kt
│               ├── TransactionFallbackProcessor.kt
│               └── TransactionProcessor.kt
│
├── src/
│   └── test/
│       └── kotlin/
│           └── fintech/
│               ├── TransactionFallbackProcessorTest.kt
│               └── TransactionProcessorTest.kt
│
├── build.gradle.kts
└── settings.gradle.kts
```

---

# L1. Autorizador simples

O **autorizador simples** deve funcionar da seguinte forma:
-  Recebe a transação
-  Usa **apenas** a MCC para mapear a transação para uma categoria de benefícios
-  Aprova ou rejeita a transação
-  Caso a transação seja aprovada, o saldo da categoria mapeada deverá ser diminuído em **totalAmount**.

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

---

# L2. Autorizador com fallback

Para despesas não relacionadas a benefícios, criamos outra categoria, chamada **CASH**.
O autorizador com fallback deve funcionar como o autorizador simples, com a seguinte diferença:
- Se a MCC não puder ser mapeado para uma categoria de benefícios ou se o saldo da categoria fornecida não for suficiente para pagar a transação inteira, verifica o saldo de **CASH** e, se for suficiente, debita esse saldo.

## Resumo da Implementação

### **Common.kt**

- **Propósito:** Define funções e variáveis comuns usadas para mapear MCCs para categorias, gerenciar saldos das categorias e do saldo de **CASH**.
- **Conteúdo:** Inclui mapeamento de MCC para categorias, saldos iniciais de categorias e **CASH**, e funções auxiliares para verificar e deduzir saldos.

### **TransactionFallbackProcessor.kt**

- **Propósito:** Implementa a lógica de processamento de transações com fallback.
- **Conteúdo:** Define a função `processTransactionWithFallback` que verifica o saldo da categoria mapeada para o MCC da transação e usa o saldo de **CASH** se o saldo da categoria não for suficiente. Caso contrário, a transação é rejeitada.

### **TransactionProcessor.kt**

- **Propósito:** Contém a lógica original para processamento de transações sem fallback.
- **Conteúdo:** Define a função `processTransaction` que verifica o saldo da categoria e aprova ou rejeita a transação com base nesse saldo.

### **TransactionFallbackProcessorTest.kt**

- **Propósito:** Testa a funcionalidade do processador com fallback.
- **Conteúdo:** Contém testes para verificar se o fallback para o saldo de **CASH** funciona corretamente e se as transações são aprovadas, rejeitadas ou processadas conforme esperado.

### **TransactionProcessorTest.kt**

- **Propósito:** Testa a funcionalidade do processador original de transações.
- **Conteúdo:** Testa o processamento de transações sem fallback, verificando a aprovação e rejeição baseadas apenas no saldo da categoria.

## Explicação dos Testes

### **TransactionFallbackProcessorTest.kt**

- **testApproveTransactionWithCategory:** Verifica se uma transação é aprovada e deduzida corretamente da categoria quando o saldo da categoria é suficiente.
- **testApproveTransactionWithCashFallback:** Verifica se uma transação é aprovada e deduzida do saldo de **CASH** quando o saldo da categoria é insuficiente.
- **testRejectTransaction:** Verifica se a transação é rejeitada quando tanto o saldo da categoria quanto o saldo de **CASH** são insuficientes.
- **testUnknownMCC:** Verifica se a transação é rejeitada quando o MCC é desconhecido, e confirma que os saldos permanecem inalterados.

### **TransactionProcessorTest.kt**

- **testApproveTransactionWithCategory:** Verifica a aprovação da transação baseada no saldo da categoria.
- **testRejectTransaction:** Verifica a rejeição da transação quando o saldo da categoria e o saldo de **CASH** são insuficientes (aplicável apenas ao processador original).

## Resultados dos Testes

### Saída do build

Mostra que os testes foram executados com sucesso e que o sistema está funcionando conforme o esperado. Aqui está um resumo dos resultados:

![Captura de tela 2024-08-21 201623.png](assets%2FCaptura%20de%20tela%202024-08-21%20201623.png)

### **TransactionFallbackProcessorTest.kt**

- Todos os testes passaram com sucesso:
    - **testApproveTransactionWithCategory:** Confirmou que a transação foi aprovada e deduzida corretamente da categoria.
    - **testApproveTransactionWithCashFallback:** Confirmou que a transação foi aprovada e deduzida do saldo de **CASH** quando o saldo da categoria era insuficiente.
    - **testRejectTransaction:** Confirmou que a transação foi rejeitada corretamente quando tanto o saldo da categoria quanto o saldo de **CASH** eram insuficientes.
    - **testUnknownMCC:** Confirmou que a transação foi rejeitada corretamente para um MCC desconhecido, com saldos permanecendo inalterados.

### **TransactionProcessorTest.kt**

- Todos os testes passaram com sucesso:
    - **testApproveTransactionWithCategory:** Confirmou a aprovação da transação quando o saldo da categoria era suficiente.
    - **testRejectTransaction:** Confirmou a rejeição da transação quando os saldos eram insuficientes (sem fallback).

## **Criação da Feature L2**

### **1. Criação da Branch**:

Foi criada uma branch chamada `feature/l2-fallback-implementation` para desenvolver a feature L2, que adiciona um autorizador com fallback à aplicação existente.

### **Implementação**: Foram feitas as seguintes mudanças na branch:

### **Refatoração e Criação de Arquivos**:

- **`Common.kt`**: Adicionou funções e variáveis comuns usadas em diferentes partes do projeto.
- **`TransactionFallbackProcessor.kt`**: Implementou a lógica do autorizador com fallback, que verifica saldos de categorias e, se necessário, utiliza um saldo de reserva (CASH).
- **`TransactionFallbackProcessorTest.kt`**: Criou testes específicos para o processador de transações com fallback, verificando diferentes cenários de transações e saldos.
- **`TransactionProcessor.kt`** e **`TransactionProcessorTest.kt`**: Atualizou para garantir a integração com a nova lógica de fallback.

### **Estrutura de Pastas e Arquivos**:

Atualizou a estrutura para acomodar as novas funcionalidades e organizar melhor o código.

### 2. **Documentação**

- **Atualização da Documentação**: Documentou as alterações, incluindo a estrutura de pastas, novos arquivos, e a lógica usada para implementar o fallback. Também descreveu os testes realizados e os resultados obtidos.

### 3. **Criação do Pull Request**

- **Descrição Detalhada**: No pull request, foi fornecida uma descrição detalhada das alterações feitas, incluindo a estrutura do projeto, novas implementações e testes realizados.
- **Revisão e Aprovação**: Solicitou a revisão e aprovação de revisores específicos, conforme necessário.

### 4. **Merge e Fechamento do Pull Request**

- **Merge do Pull Request**: O pull request foi fundido com sucesso, integrando a feature L2 ao branch principal.
- **Branch Final**: O branch `feature/l2-fallback-implementation` foi mantido para fins de documentação e histórico.

---