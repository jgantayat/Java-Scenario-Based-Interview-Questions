# JVM MEMORY MANAGEMENT — DEEP DIVE

## 1️⃣ Big Picture: JVM Memory Architecture

When a Java program runs, JVM divides memory into **runtime data areas**.

```
+---------------------------------------------------+
|                    JVM MEMORY                     |
+---------------------------------------------------+
|  Method Area / Metaspace (Class-level data)       |
|---------------------------------------------------|
|  Heap (Objects & instance data)                   |
|   - Young Generation                               |
|       * Eden                                      |
|       * Survivor S0                               |
|       * Survivor S1                               |
|   - Old Generation                                 |
|---------------------------------------------------|
|  Stack (Per-thread method execution)               |
|---------------------------------------------------|
|  PC Register (Per-thread)                          |
|---------------------------------------------------|
|  Native Method Stack                               |
+---------------------------------------------------+
```

---

## 2️⃣ Stack Memory (Thread-Scoped)

### What is stored?

* Local variables
* Method parameters
* Object references (NOT objects)
* Method call frames

### Key properties

* **One stack per thread**
* LIFO (Last In First Out)
* Automatically cleaned when method exits
* Very fast
* ❌ Small size → `StackOverflowError`

### Example Code

```java
public void calculate() {
    int a = 10;          // primitive in stack
    User user = new User(); // reference in stack, object in heap
}
```

### Stack Visualization

```
Thread Stack
-------------
| calculate() |
| a = 10      |
| user ----+  | ----> Heap(User object)
-------------
```

📌 **Important Interview Point**

> Stack never stores objects, only references.

---

## 3️⃣ Heap Memory (Shared, GC Managed)

### What is stored?

* Objects
* Instance variables
* Arrays

### Shared across all threads

### Managed by Garbage Collector

---

## 4️⃣ Heap Structure (Very Important)

```
Heap
------------------------------------------------
| Young Generation                              |
|  ------------------------------------------  |
|  | Eden | Survivor S0 | Survivor S1         | |
|  ------------------------------------------  |
|                                              |
| Old Generation (Tenured)                      |
------------------------------------------------
```

---

## 5️⃣ Object Creation Flow (Step-by-Step)

### Code

```java
User user = new User();
```

### JVM Flow

```
1. Class loaded (if not already)
2. Memory allocated in Eden space
3. Constructor executed
4. Reference stored in stack
```

### Diagram

```
Stack                     Heap (Young Gen)
------                    ----------------
user  ------------------>  User object (Eden)
```

---

## 6️⃣ Young Generation (Minor GC)

### Why Young Gen?

* Most objects die young (95%+)
* Optimized for fast allocation & cleanup

### Eden Space

* All new objects are created here

### Survivor Spaces (S0 & S1)

* Objects that survive GC cycles

---

## 7️⃣ Minor GC Flow (CRITICAL)

### Scenario

```java
for (int i = 0; i < 100000; i++) {
    new User();
}
```

### Flow Diagram

```
Eden Full
   |
   v
Minor GC Triggered
   |
   v
Live Objects -> Survivor S0
Dead Objects -> Removed
   |
   v
Next GC: S0 -> S1 (Age++)
```

### Object Aging

```
Age 1 -> Age 2 -> Age 3 -> ... -> Promotion
```

If age exceeds threshold → **Promoted to Old Generation**

---

## 8️⃣ Old Generation (Major / Full GC)

### What goes here?

* Long-lived objects
* Large objects
* Promoted survivor objects

### Major GC

* Slower
* More expensive
* Can cause **STW (Stop-The-World)** pauses

---

## 9️⃣ Garbage Collection Roots (MOST IMPORTANT CONCEPT)

GC decides liveness based on **reachability**, not usage.

### GC Roots:

* Local variables (stack)
* Static variables
* Active threads
* JNI references

### Diagram

```
GC Root
  |
  v
static cache ---> Object A ---> Object B
```

➡️ All reachable → NOT GC’d

---

## 🔟 Static Variables & Memory Leak Scenario

### Code (Classic Production Bug)

```java
public class CacheManager {
    public static Map<String, User> cache = new HashMap<>();
}
```

```java
cache.put("1", new User());
```

### Memory Flow

```
GC Root
  |
static cache
  |
User object (Heap)
```

Even if:

```java
user = null;
```

❌ Object stays alive → memory leak

---

## 1️⃣1️⃣ Metaspace (Method Area Replacement)

### Stores:

* Class metadata
* Method info
* Constant pool
* Static variables (references)

### Before Java 8

* PermGen (fixed size)

### Java 8+

* Metaspace (native memory, grows dynamically)

### Metaspace OOM

```
java.lang.OutOfMemoryError: Metaspace
```

Often caused by:

* Dynamic class loading
* Static references to ClassLoader

---

## 1️⃣2️⃣ ClassLoader Memory Leak (Advanced)

### Problem Code

```java
public class ConfigHolder {
    public static Config config = new Config();
}
```

On app redeploy:

* ClassLoader should be GC’d
* Static reference blocks it
* Metaspace leak occurs

---

## 1️⃣3️⃣ Weak, Soft & Phantom References

### Weak Reference (GC-friendly cache)

```java
WeakHashMap<String, User> cache = new WeakHashMap<>();
```

If key is GC’d → entry removed automatically

### Reference Strength

```
Strong > Soft > Weak > Phantom
```

---

## 1️⃣4️⃣ Full GC Flow Diagram

```
GC Trigger
   |
   v
Stop-The-World
   |
   v
Mark GC Roots
   |
   v
Traverse Object Graph
   |
   v
Sweep / Compact
   |
   v
Resume Threads
```

---

## 1️⃣5️⃣ OutOfMemoryError Types (Production Insight)

| Error                | Cause            |
| -------------------- | ---------------- |
| Java heap space      | Object retention |
| GC overhead exceeded | Too frequent GC  |
| Metaspace            | ClassLoader leak |
| Direct buffer memory | NIO misuse       |
| StackOverflowError   | Deep recursion   |

---

## 1️⃣6️⃣ Best Coding Standards to Avoid Memory Issues

### ✅ Avoid static object references

### ✅ Clear caches explicitly

### ✅ Use WeakHashMap for caches

### ✅ Deregister listeners

### ✅ Avoid memory-heavy singletons

### ✅ Monitor heap & GC logs

### ✅ Use proper scopes in Spring

---

## 1️⃣7️⃣ Interview-Perfect Summary (Say This)

> “JVM memory is divided into stack, heap, and metaspace. Objects are allocated in the heap, while references live in stack or static areas. Garbage collection works based on reachability from GC roots. Static variables act as GC roots, so if they hold object references, those objects cannot be garbage collected, which often leads to memory leaks and OutOfMemoryError in production. Proper lifecycle management, weak references, and avoiding static object retention are essential.”

---

