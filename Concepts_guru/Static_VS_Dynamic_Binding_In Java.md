## Question
What is static vs dynamic binding in Java?
---

## What is Binding in Java?

**Binding** is the process of **linking a method call to its method definition**.

In Java, this linking can happen at:

* **Compile time → Static Binding**
* **Runtime → Dynamic Binding**

---

## 1️⃣ Static Binding (Compile-Time Binding)

### 👉 Definition

**Static Binding** occurs when the method call is resolved **at compile time**.

The compiler knows **exactly which method** to call.

---

### ✅ Happens with:

* `static` methods
* `final` methods
* `private` methods
* **Method overloading**

---

### 🧠 Why?

Because these methods **cannot be overridden**, so there is no ambiguity.

---

### 📌 Example

```java
class Calculator {
    static void add(int a, int b) {
        System.out.println(a + b);
    }
}

public class Test {
    public static void main(String[] args) {
        Calculator.add(10, 20); // Bound at compile time
    }
}
```

---

### 📌 Overloading Example

```java
class MathUtil {
    void sum(int a, int b) {
        System.out.println(a + b);
    }

    void sum(double a, double b) {
        System.out.println(a + b);
    }
}
```

➡ The compiler decides **which method to call based on parameters**.

---

### 🔑 Key Points (Static Binding)

✔ Faster
✔ No runtime overhead
✔ Less flexible
✔ No polymorphism involved

---

## 2️⃣ Dynamic Binding (Runtime Binding)

### 👉 Definition

**Dynamic Binding** happens when the method call is resolved **at runtime**, based on the **actual object**.

---

### ✅ Happens with:

* **Method overriding**
* **Non-static, non-final, non-private methods**

---

### 🧠 Why?

Because the object type is known **only at runtime**, not at compile time.

---

### 📌 Example

```java
class Animal {
    void sound() {
        System.out.println("Animal sound");
    }
}

class Dog extends Animal {
    void sound() {
        System.out.println("Dog barks");
    }
}

public class Test {
    public static void main(String[] args) {
        Animal a = new Dog();
        a.sound(); // Runtime decides → Dog's sound()
    }
}
```

➡ Even though reference type is `Animal`, the **object is `Dog`**, so Dog’s method runs.

---

### 🔑 Key Points (Dynamic Binding)

✔ Supports runtime polymorphism
✔ More flexible
✔ Slight runtime overhead
✔ Enables loose coupling

---

## 3️⃣ Key Differences (Interview Table)

| Feature      | Static Binding         | Dynamic Binding    |
| ------------ | ---------------------- | ------------------ |
| Binding Time | Compile time           | Runtime            |
| Methods      | static, final, private | Overridden methods |
| Polymorphism | ❌ No                   | ✅ Yes              |
| Performance  | Faster                 | Slightly slower    |
| Flexibility  | Less                   | More               |

---

## 4️⃣ One-Line Interview Answer

> **Static binding** resolves method calls at compile time, while **dynamic binding** resolves them at runtime based on the actual object.

---

## 5️⃣ Real-World Understanding

* **Static Binding** → Fixed behavior (utility methods, helpers)
* **Dynamic Binding** → Flexible behavior (Spring services, interfaces, strategy pattern)

---