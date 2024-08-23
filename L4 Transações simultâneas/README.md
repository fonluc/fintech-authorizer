# **L4. Transações Simultâneas**

Em um sistema de autorização de transações, é essencial garantir que apenas uma transação por conta seja processada de cada vez, especialmente quando múltiplas transações podem ocorrer simultaneamente.

## **Problema**

Precisamos garantir que uma única transação por conta seja processada por vez, com todas as transações sendo processadas rapidamente (menos de 100 ms).

## **Diagrama**

```
  +------------------+
  |   Cliente A      |
  +------------------+
         |
         | Solicitação de Transação
         |
  +------------------+
  |   Servidor       |
  |  (Controle de    |
  |  Concorrência)   |
  +------------------+
         |
         | Verifica Estado da Conta
         |
  +------------------+
  | Banco de Dados   |
  +------------------+
         |
         | Atualiza Saldo
         |
  +------------------+
  |   Servidor       |
  |  (Controle de    |
  |  Concorrência)   |
  +------------------+
         |
         | Resposta ao Cliente
         |
  +------------------+
  |   Cliente B      |
  +------------------+
```

## **Serviços**

### **Redis:**

Oferece suporte a locks atômicos e contadores para gerenciar concorrência.

### **Amazon SQS:**

Sistema de filas gerenciado para processamento sequencial de transações.

### **Apache Kafka:**

Plataforma de streaming para gerenciar transações em tempo real e garantir ordem.

## **Abordagens**

### **1. Uso de Locking (Bloqueio) em Nível de Banco de Dados**

### **Descrição:**

Utiliza bloqueios no banco de dados para garantir que apenas uma transação possa acessar e modificar o saldo da conta ao mesmo tempo.

### **Tipos:**

### **Bloqueio Pessimista:**

Bloqueia a conta durante a transação, impedindo outras até que o bloqueio seja liberado.

### **Bloqueio Otimista:**

Verifica se há conflitos antes de confirmar a transação, rejeitando ou reprocessando se necessário.

### **Vantagens:**

- Simples e garante consistência.

### **Desvantagens:**

- Pode causar espera e aumentar o tempo de resposta.

### **2. Uso de Sistemas de Filas (Queues)**

### **Descrição:**

Implementa uma fila para gerenciar as transações, processando-as uma por vez.

### **Tipos:**

### **Fila Única:**

Todas as transações são processadas na ordem em que entram.

### **Fila por Conta:**

Cada conta tem sua própria fila para processar transações de forma independente.

### **Vantagens:**

- Evita conflitos e garante ordem.

### **Desvantagens:**

- Introduz latência e complexidade.

### **3. Uso de Algoritmos de Controle de Concorrência**

### **Descrição:**

Implementa algoritmos para controlar e evitar interferências entre transações simultâneas.

### **Algoritmos:**

### **Two-Phase Locking (2PL):**

Usa um protocolo de bloqueio em duas fases para garantir a exclusividade.

### **Timestamp Ordering (TSO):**

Processa transações com base em timestamps para evitar conflitos.

### **Vantagens:**

- Controle fino sobre concorrência.

### **Desvantagens:**

- Complexo e requer gerenciamento cuidadoso.

### **4. Uso de Tecnologias de Cache Distribuído**

### **Descrição:**

Utiliza um cache distribuído para gerenciar o estado das transações com suporte a operações atômicas.

### **Tipos:**

### **Cache com Locks Atômicos:**

Implementa bloqueios atômicos para garantir exclusividade.

### **Cache com Contadores:**

Usa contadores atômicos para validar e gerenciar transações.

### **Vantagens:**

- Alta performance e escalabilidade.

### **Desvantagens:**

- Requer configuração e manutenção do cache distribuído.