# Project Bank Core 🏛🇪🇸💶️ Scala 3, Pekko

A high-performance, fault-tolerant reactive backend (Core Banking) simulating instant P2P transfers, inspired by payment systems like **Bizum** and **Revolut P2P** widely used in the Spanish market 🇪🇸.

The project is designed following the principles of **Domain-Driven Design (DDD)** and **Hexagonal Architecture (Ports & Adapters)** using the **Apache Pekko** actor model and **Scala 3**.

---

## 🎯 Business Context & Objectives
In modern FinTech platforms, key requirements include transaction isolation, strict balance auditing, and resilience to failures. This project simulates a bank's core ledger, handling highly concurrent transfer requests between thousands of virtual accounts per second without operating system thread blocking (Lock-free / Non-blocking).

<p align="left">
  <img src="doc/images/bank1.png" width="400" alt="Sol y Pago 1" />
  <img src="doc/images/bank2.png" width="400" alt="Sol y Pago 2" />
</p>

---

## 📐 Architecture & System Design

The project is strictly separated into three layers according to Hexagonal Architecture, isolating business logic from external frameworks:

1. **Domain (Pure Domain):** Encapsulates business rules, entities, and Value Objects. It has zero dependencies on Pekko or databases, written in pure Scala 3.
2. **Application (Application Layer):** The orchestration layer where the Pekko Actor Model manages the lifecycle of domain entities (accounts) and transaction flows (Saga pattern).
3. **Infrastructure (Infrastructure Layer):** The system's external interfaces, including a REST API powered by Pekko HTTP and a built-in event log simulation (Event Store).

### Components & Actor Model Design:
*   **`AccountActor` (Aggregate Root):** Represents a bank account. Ensures thread-safe, sequential processing of withdrawals and deposits from its `Mailbox`, eliminating race conditions.
*   **`ProcessingCenterActor` (Process Manager / Saga Orchestrator):** Coordinates distributed transactions between accounts and handles automatic rollbacks (compensating actions) upon failures.
*   **`FraudMonitorActor` (Anti-Fraud System):** Asynchronously analyzes transaction streams using sliding window algorithms.
*   **`AlertNotifierActor` (Notification Service):** An isolated notification delivery actor with a configured *Supervision Strategy* to handle network drops.
*   **`DatabaseSimActor` (Ledger Storage Simulation):** Implements an *Event Sourcing* concept to recover account states from event logs after application crashes.

---

## 🧠 Applied Algorithms (Computer Science)
*   **Luhn Algorithm:** Validates card numbers at the domain service level before initiating any transfer.
*   **Sliding Window Rate Limiter:** Monitors transaction frequency and amounts in real-time inside the `FraudMonitorActor` to flag anomalous activities.

---

## 📁 Project Directory Structure (DDD)

```text
src/main/scala/com/banking/
├── Main.scala                     # Entry point (initializes Pekko ActorSystem)
├── domain/                        # 1. PURE DOMAIN LAYER (0% frameworks)
│   ├── account/                   # Account entities & Value Objects (Money, AccountState)
│   ├── transfer/                  # Transaction logic & Luhn Algorithm
│   └── fraud/                     # Anti-fraud rules and business metrics
├── application/                   # 2. APPLICATION LAYER (Pekko Orchestration)
│   ├── account/                   # AccountActor and its message protocol
│   ├── transfer/                  # ProcessingCenterActor (Saga Orchestrator)
│   └── fraud/                     # Actor-based monitoring and alert notification dispatchers
└── infrastructure/                # 3. INFRASTRUCTURE LAYER (Adapters)
    ├── http/                      # Pekko HTTP routes and request DTOs
    └── repository/                # DatabaseSimActor (Event Sourcing simulation)
```

---

## 🛠️ Technology Stack
*   **Programming Language:** Scala 3.3.4 (Strict compilation flags)
*   **Actor Framework:** Apache Pekko Actor Typed 1.1.2
*   **I/O & HTTP Interface:** Apache Pekko HTTP 1.1.0 & Spray JSON
*   **Build Tool:** sbt 1.10.1
*   **Logging:** Pekko SLF4J & Logback Classic
*   **Testing Framework:** ScalaTest & Pekko Actor TestKit

