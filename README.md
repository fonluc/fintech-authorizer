# Fintech Authorizer
<a id="topo"></a>

---

# Estrutura do Projeto:

---

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

## Índice

1. [L1. Autorizador Simples](#l1-autorizador-simples)
2. [L2. Autorizador com Fallback](#l2-autorizador-com-fallback)
3. [L3. Dependente do Comerciante](#l3-dependente-do-comerciante)
4. [L4. Transações Simultâneas](#l4-transações-simultâneas)

---

# L1. Autorizador simples

O **autorizador simples** deve funcionar da seguinte forma:
-  Recebe a transação
-  Usa **apenas** a MCC para mapear a transação para uma categoria de benefícios
-  Aprova ou rejeita a transação
-  Caso a transação seja aprovada, o saldo da categoria mapeada deverá ser diminuído em **totalAmount**.

## Resumo da Implementação

**Arquivo `TransactionProcessor.kt`**:

- **Mapeamento MCC**: `mccToCategory`
- **Saldo**: `categoryBalances`
- **Função de Processamento**: `processTransaction()`
- **Funções Auxiliares**: `checkCategoryBalance()`, `approveTransaction()`, `deductFromCategoryBalance()`, `rejectTransaction()`

**Arquivo `TransactionProcessorTest.kt`**:

- **Teste de Aprovação**: `testApproveTransaction`
- **Teste de Rejeição por Saldo Insuficiente**: `testRejectTransaction`
- **Teste de Rejeição por MCC Desconhecido**: `testUnknownMCC`

## Explicação dos Testes

**`testApproveTransaction`**:

- **Configuração**: Define um saldo inicial para "Food".
- **Ação**: Processa uma transação que deve ser aprovada.
- **Verificação**: Garante que o saldo da categoria "Food" foi reduzido corretamente após a transação.

**`testRejectTransaction`**:

- **Configuração**: Define um saldo inicial insuficiente para "Food".
- **Ação**: Processa uma transação que deve ser rejeitada.
- **Verificação**: Garante que o saldo não foi alterado após a transação rejeitada.

**`testUnknownMCC`**:

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

[Voltar ao topo](#topo)

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

[Voltar ao topo](#topo)

---

# **L3. Dependente do Comerciante**

As vezes, os MCCs estão incorretos e uma transação deve ser processada levando em consideração também os dados do comerciante. Crie um mecanismo para substituir MCCs com base no nome do comerciante. O nome do comerciante tem maior precedência sobre as MCCs.

### Exemplos:

- `UBER TRIP                   SAO PAULO BR`
- `UBER EATS                   SAO PAULO BR`
- `PAG*JoseDaSilva          RIO DE JANEI BR`
- `PICPAY*BILHETEUNICO           GOIANIA BR`

## Resumo da Implementação

**Objetivo:** Implementar L3 (prioridade do comerciante) enquanto mantém a funcionalidade existente de L1 e L2.

### **Alterações Realizadas:**

**Adição de `merchantToCategory` em `Common.kt`:**

- Mapeia comerciantes específicos para categorias de benefícios, permitindo que o nome do comerciante tenha prioridade sobre a MCC.

**Modificação de `processTransaction` em `TransactionProcessor.kt`:**

- Atualizado para verificar a categoria baseada no nome do comerciante antes de usar a MCC.
- Se a categoria baseada no comerciante não estiver disponível, o método utiliza a MCC para determinar a categoria.

**Atualização de `processTransactionWithFallback` em `TransactionFallbackProcessor.kt`:**

- Inclui lógica similar para a prioridade do comerciante ao processar transações com fallback para o saldo de CASH.

**Ajustes nos Testes em `TransactionProcessorTest.kt` e `TransactionFallbackProcessorTest.kt`:**

- Verificam a nova lógica de prioridade do comerciante, garantindo que as transações sejam processadas corretamente com base no comerciante ou MCC.

## Explicação Testes e Resultados

### Saída do build

Mostra que os testes foram executados com sucesso e que o sistema está funcionando conforme o esperado. Aqui está um resumo dos resultados:

![Captura de tela 2024-08-22 215814.png](assets%2FCaptura%20de%20tela%202024-08-22%20215814.png)

### **1. Testes em `TransactionProcessorTest.kt`:**

**`testApproveTransactionWithMerchant`:**

- Verifica se uma transação é aprovada corretamente quando o nome do comerciante (`UBER EATS`) é fornecido e corresponde a uma categoria ("Food").
- Resultado: A transação foi aprovada e o saldo da categoria "Food" foi reduzido conforme esperado.

**`testRejectTransaction`:**

- Verifica se uma transação é rejeitada quando o saldo da categoria é insuficiente.
- Resultado: A transação foi rejeitada e o saldo da categoria não foi alterado.

**`testUnknownMCC`:**

- Verifica se uma transação com um MCC desconhecido é rejeitada e o saldo de categoria permanece inalterado.
- Resultado: A transação foi rejeitada e o saldo das categorias permaneceu o mesmo.

### **2. Testes em `TransactionFallbackProcessorTest.kt`:**

**`testApproveTransactionWithMerchant`:**

- Verifica se uma transação é aprovada e deduzida corretamente quando o nome do comerciante (`PAG*JoseDaSilva`) é fornecido e corresponde a uma categoria ("Grocery").
- Resultado: A transação foi aprovada e o saldo da categoria "Grocery" foi reduzido conforme esperado.

**`testApproveTransactionWithCategory`:**

- Verifica se uma transação é aprovada e deduzida corretamente de uma categoria quando a MCC é conhecida e o saldo é suficiente.
- Resultado: A transação foi aprovada e o saldo da categoria foi ajustado conforme esperado.

**`testApproveTransactionWithCashFallback`:**

- Verifica se uma transação é aprovada e deduzida do saldo de CASH quando o saldo da categoria é insuficiente.
- Resultado: A transação foi aprovada e o saldo de CASH foi reduzido conforme esperado.

**`testRejectTransaction`:**

- Verifica se uma transação é rejeitada quando tanto o saldo da categoria quanto o saldo de CASH são insuficientes.
- Resultado: A transação foi rejeitada e os saldos permaneceram inalterados.

**`testUnknownMCC`:**

- Verifica se uma transação com um MCC desconhecido é aprovada e deduzida do saldo de CASH quando o saldo é suficiente.
- Resultado: A transação foi aprovada e o saldo de CASH foi reduzido conforme esperado.

## **Criação da Feature L3**

### **1. Criação da Branch**:

Eu criei uma branch chamada `feature/implement-l3` para desenvolver a feature L3, que adiciona a lógica de prioridade de comerciantes ao autorizador existente.

### **Refatoração e Criação de Arquivos**:

- **`Common.kt`**: Adicionei um mecanismo para mapear comerciantes a categorias específicas, permitindo que nomes de comerciantes substituam MCCs durante o processamento de transações.
- **`TransactionFallbackProcessor.kt`**: Implementei a lógica que dá prioridade aos nomes dos comerciantes sobre os MCCs ao categorizar transações. Se um comerciante específico estiver mapeado para uma categoria, essa categoria será usada em vez do MCC.
- **`TransactionFallbackProcessorTest.kt`**: Criei testes específicos para validar a nova lógica de prioridade dos comerciantes, cobrindo diversos cenários de transações.

### **2. Documentação**

- **Atualização da Documentação**: Documentei as alterações, descrevendo a nova lógica de substituição de MCCs por nomes de comerciantes e os testes realizados para validar essa funcionalidade.

### **3. Criação do Pull Request**

- **Descrição Detalhada**: No pull request, forneci uma descrição detalhada das mudanças, incluindo as novas implementações e testes.
- **Revisão e Aprovação**: Como estou trabalhando sozinho, realizei a revisão das mudanças antes de aprovar o pull request.

### **4. Merge e Fechamento do Pull Request**

- **Merge do Pull Request**: O pull request foi fundido com sucesso, integrando a feature L3 ao branch principal.
- **Branch Final**: Decidi manter o branch `feature/implement-l3` para fins de documentação e histórico.

---

[Voltar ao topo](#topo)

---

## **L4. Transações Simultâneas**

A seguir está uma questão aberta sobre um recurso importante de um autorizador completo (que você não precisa implementar, apenas discuta da maneira que achar adequada, como texto, diagramas, etc.).

### Transações simultâneas:

Dado que o mesmo cartão de crédito pode ser utilizado em diferentes serviços online, existe uma pequena mas existente probabilidade de ocorrerem duas transações ao mesmo tempo. O que você faria para garantir que apenas uma transação por conta fosse processada em um determinado momento? Esteja ciente do fato de que todas as solicitações de transação são síncronas e devem ser processadas rapidamente (menos de 100 ms), ou a transação atingirá o timeout.

Para garantir a integridade e a exclusividade das transações simultâneas em nosso sistema de autorização, exploramos várias abordagens para lidar com transações concorrentes de maneira eficiente.

### **Solução Implementada**

Criamos uma pasta chamada [L4 Transações Simultâneas](https://github.com/fonluc/fintech-authorizer/tree/main/L4%20Transa%C3%A7%C3%B5es%20simult%C3%A2neas) dentro do repositório, que contém uma documentação detalhada sobre as abordagens e técnicas para gerenciar transações simultâneas. Nesta pasta, você encontrará:

- **Descrição dos Problemas:** Identificação dos desafios associados às transações simultâneas.
- **Abordagens Propostas:** Métodos para garantir a exclusividade, como locking em nível de banco de dados, sistemas de filas, algoritmos de controle de concorrência e tecnologias de cache distribuído.
- **Diagramas:** Representações visuais do processamento de transações e das técnicas de controle de concorrência.
- **Serviços Utilizados:** Recomendação de serviços e ferramentas que podem ser aplicadas para implementar as soluções discutidas.

---

[Voltar ao topo](#topo)

---
