Q1. Two threads update a bank balance
Suppose you have a bank account with an initial balance of $1000. Two threads are trying to update the balance concurrently. Thread A wants to deposit $500, and Thread B wants to withdraw $300.
1. What potential issues could arise from these concurrent updates?
2. How can you ensure that the final balance is correct after both threads have completed their operations?

# Two Threads Updating a Bank Balance Concurrently

# 1️⃣ CONCEPT YOU MUST KNOW FIRST (VERY IMPORTANT)

## 🔹 The Problem Statement

> Two threads are updating a bank balance at the same time.

This is a **classic concurrency problem** involving:

* **Race Condition**
* **Thread Safety**
* **Critical Section**
* **Synchronization / Locking**
* **Atomicity**

---

## 🔹 What Actually Goes Wrong? (Core Understanding)

Let’s say:

```java
balance = 1000
```

Two threads run simultaneously:

* Thread A → Deposit ₹500
* Thread B → Withdraw ₹300

### ❌ Non-Thread-Safe Flow

Updating balance is **not a single operation**. It involves:

1. Read balance
2. Modify balance
3. Write balance back

This is called a **Read–Modify–Write** sequence.

If both threads read the balance at the same time:

```
Thread A reads: 1000
Thread B reads: 1000
```

Then:

```
Thread A writes: 1500
Thread B writes: 700
```

💥 **Final balance becomes incorrect** (depends on execution order)

This issue is called a **Race Condition**.

---

## 🔹 Key Terms You MUST Know

### ✅ Race Condition

When multiple threads access shared data and the final result depends on execution order.

### ✅ Critical Section

The part of code where shared data (bank balance) is accessed or modified.

### ✅ Thread Safety

Code that works correctly even when multiple threads run concurrently.

### ✅ Atomicity

An operation that completes fully or not at all—no intermediate state visible.

---

# 2️⃣ HOW SHOULD YOU FRAME THE ANSWER TO THE INTERVIEWER

### 🔥 Golden Structure (Use this every time)

**Step 1** – Identify the problem
👉 “This is a classic race condition due to concurrent access to shared mutable state.”

**Step 2** – Explain why it happens
👉 “Updating balance is not atomic; it involves read-modify-write.”

**Step 3** – Explain the consequence
👉 “This can lead to inconsistent or incorrect balance.”

**Step 4** – Provide solutions
👉 Synchronization / Lock / Atomic classes

**Step 5** – Compare solutions briefly
👉 Performance vs safety

---

# 3️⃣ INTERVIEW-READY ANSWER (YOU CAN SAY THIS)

> “When two threads update a bank balance simultaneously, it can lead to a race condition because the balance update is a read-modify-write operation and is not atomic.
>
> Both threads may read the same balance and overwrite each other’s updates, leading to an incorrect final balance.
>
> To solve this, we need to make the critical section thread-safe by using synchronization mechanisms like `synchronized` blocks, `ReentrantLock`, or atomic classes such as `AtomicInteger`.
>
> This ensures that only one thread can modify the balance at a time, maintaining data consistency.”

🔥 This answer already sounds **senior-level**.

---

# 4️⃣ HOW TO IMPLEMENT (CONCEPTUAL, NOT JUST CODE)

## ✅ Solution 1: `synchronized` (Most Common)

```java
public synchronized void updateBalance(int amount) {
    balance += amount;
}
```

🔹 Pros:

* Simple
* Easy to explain

🔹 Cons:

* Blocking
* Less scalable

---

## ✅ Solution 2: `ReentrantLock` (More Control)

```java
lock.lock();
try {
    balance += amount;
} finally {
    lock.unlock();
}
```

🔹 Pros:

* Try-lock
* Fairness
* Better control

🔹 Cons:

* More complex

---

## ✅ Solution 3: `AtomicInteger` (Best for Simple Counters)

```java
AtomicInteger balance = new AtomicInteger(1000);
balance.addAndGet(500);
```

🔹 Pros:

* Lock-free
* High performance

🔹 Cons:

* Limited to simple operations

---

# 5️⃣ FOLLOW-UP QUESTIONS INTERVIEWERS LOVE 😈

### ❓ Why not use `volatile`?

👉 Volatile ensures **visibility**, not **atomicity**.

### ❓ Which solution would you choose in production?

👉 Depends on complexity:

* Simple counter → `AtomicInteger`
* Business logic → `synchronized` / `Lock`

### ❓ Is synchronization costly?

👉 Yes, but correctness > performance in financial systems.

---

# 6️⃣ ONE-LINE POWER STATEMENT (USE THIS 💯)

> “In financial systems, correctness and consistency are more important than performance, so proper synchronization is mandatory.”

---

If you want, next we can do:

* ✅ **Same question but in Spring Boot**
* ✅ **Database transaction vs Java synchronization**
* ✅ **Optimistic vs pessimistic locking**
* ✅ **Real banking system design answer**

Just send the **next scenario-based question** 🚀
