# 🧮 Slow Calculator — Concurrent Task Manager in Java

This project demonstrates **concurrent computation and task management** using Java’s `ExecutorService`.  
Each “calculation” counts the **number of prime factors** of a given number, running in a separate thread that can be started, cancelled, or chained to run *after* another finishes.

---

## 🚀 Features

- Multi-threaded execution using `ExecutorService` and `Future`
- Interruptible long-running tasks (`SlowCalculator`)
- Command-based control (`start`, `cancel`, `get`, `after`, `finish`, `abort`)
- Task dependency support (`after N M` means “start M after N finishes”)
- Thread-safe state management using `ConcurrentHashMap`

---

## ⚙️ How It Works

1. Each calculation (`SlowCalculator`) runs in its own thread and computes the number of prime factors of a given integer.  
2. Commands are processed by the `Solution` class, which manages the active threads and their states.  
3. You can monitor, cancel, or queue calculations dynamically while the program is running.

---

## 💻 Example Commands

| Command | Description |
|----------|-------------|
| `start 100` | Starts calculating the number of prime factors of **100**. |
| `cancel 100` | Cancels the running calculation for **100**. |
| `running` | Lists all currently active calculations. |
| `get 100` | Returns the result if the calculation is done, or its status otherwise. |
| `after 100 200` | Queues **200** to start *after* **100** completes. |
| `finish` | Waits for all tasks to finish before exiting. |
| `abort` | Cancels all running tasks immediately. |

---

## 🧠 Key Classes

- **`SlowCalculator`** – Implements `Runnable`. Simulates a slow operation by counting factors of `N` with interruption support.  
- **`Solution`** – Implements `CommandRunner`. Manages task execution, scheduling, and inter-task communication.

---

## 🧩 Concepts Demonstrated

- Multithreading and concurrency control in Java  
- Safe task interruption and cancellation  
- Use of `ExecutorService`, `Future`, and `ConcurrentHashMap`  
- Graceful shutdown and command-based orchestration  

---

## ▶️ Run

Compile and run from the terminal:

```bash
javac SlowCalculator.java Solution.java
java Solution

