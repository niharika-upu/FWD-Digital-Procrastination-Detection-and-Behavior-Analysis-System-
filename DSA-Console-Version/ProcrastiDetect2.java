package DSA;
import java.util.*;
import java.text.*;
import java.time.*;
// ─── POLYNOMIAL ADT (Lists Application) ────────────────────────
class Polynomial {
    int coeff, exp;
    Polynomial next;
    Polynomial(int c, int e) { coeff = c; exp = e; }
    public String toString() { return coeff + "x^" + exp; }
}

// ─── DOUBLY LINKED LIST NODE ────────────────────────────────────
class Task {
    String id, name, description, priority, status;
    long deadline, createdAt, completionTime;
    int estimatedTime, delay;
    Task prev, next;

    Task(String id, String name, String desc, String priority, long deadline, int estTime) {
        this.id = id; this.name = name; this.description = desc;
        this.priority = priority; this.deadline = deadline;
        this.estimatedTime = estTime; this.status = "pending";
        this.createdAt = System.currentTimeMillis();
        this.completionTime = 0; this.delay = 0;
    }
    public String toString() {
        return String.format("[%s] %-20s | %-6s | %-9s | delay:%dh", id, name, priority, status, delay);
    }
}

// ─── DOUBLY LINKED LIST (Task List ADT) ────────────────────────
class TaskList {
    Task head, tail;
    int size;

    void addLast(Task t) {                        // O(1)
        if (head == null) { head = tail = t; }
        else { tail.next = t; t.prev = tail; tail = t; }
        size++;
    }

    boolean remove(String id) {                   // O(n)
        for (Task t = head; t != null; t = t.next) {
            if (t.id.equals(id)) {
                if (t.prev != null) t.prev.next = t.next; else head = t.next;
                if (t.next != null) t.next.prev = t.prev; else tail = t.prev;
                t.prev = t.next = null; size--; return true;
            }
        }
        return false;
    }

    Task find(String id) {                        // O(n)
        for (Task t = head; t != null; t = t.next) if (t.id.equals(id)) return t;
        return null;
    }

    Task[] toArray() {
        Task[] arr = new Task[size]; int i = 0;
        for (Task t = head; t != null; t = t.next) arr[i++] = t;
        return arr;
    }
}

// ─── CIRCULAR LINKED LIST (Recent 5 tasks) ─────────────────────
class CircularList {
    Task head; int size, maxSize;
    CircularList(int max) { maxSize = max; }

    void add(Task t) {
        Task node = new Task(t.id, t.name, t.description, t.priority, t.deadline, t.estimatedTime);
        node.status = t.status; node.delay = t.delay;
        if (head == null) { head = node; node.next = head; size = 1; return; }
        if (size == maxSize) {
            // remove oldest (head), add new
            Task cur = head;
            while (cur.next != head) cur = cur.next;
            head = head.next; cur.next = head; size--;
        }
        Task cur = head;
        while (cur.next != head) cur = cur.next;
        cur.next = node; node.next = head; size++;
    }

    void print() {
        if (head == null) { System.out.println("  (empty)"); return; }
        Task cur = head; int i = 0;
        do { System.out.printf("  %d. %s%n", ++i, cur); cur = cur.next; } while (cur != head);
    }
}

// ─── STACK ADT (Undo / Infix→Postfix / Balancing) ──────────────
class Stack<T> {
    private Object[] data; private int top;
    Stack(int cap) { data = new Object[cap]; top = -1; }
    void push(T v) { if (top < data.length-1) data[++top] = v; }
    @SuppressWarnings("unchecked") T pop() { return top >= 0 ? (T) data[top--] : null; }
    @SuppressWarnings("unchecked") T peek() { return top >= 0 ? (T) data[top] : null; }
    boolean isEmpty() { return top < 0; }

    // ── Balance Symbols ─────────────────────────────────────────
    static boolean balanceCheck(String expr) {
        Stack<Character> s = new Stack<>(expr.length());
        for (char c : expr.toCharArray()) {
            if (c=='('||c=='['||c=='{') s.push(c);
            else if (c==')'||c==']'||c=='}') {
                if (s.isEmpty()) return false;
                char t = s.pop();
                if ((c==')' && t!='(') || (c==']' && t!='[') || (c=='}' && t!='{')) return false;
            }
        }
        return s.isEmpty();
    }

    // ── Infix → Postfix ─────────────────────────────────────────
    static String infixToPostfix(String infix) {
        Stack<Character> s = new Stack<>(infix.length());
        StringBuilder out = new StringBuilder();
        Map<Character,Integer> prec = Map.of('+',1,'-',1,'*',2,'/',2,'^',3);
        for (char c : infix.toCharArray()) {
            if (Character.isLetterOrDigit(c)) out.append(c);
            else if (c == '(') s.push(c);
            else if (c == ')') { while (!s.isEmpty() && s.peek()!='(') out.append(s.pop()); s.pop(); }
            else if (prec.containsKey(c)) {
                while (!s.isEmpty() && s.peek()!='(' && prec.getOrDefault(s.peek(),0)>=prec.get(c)) out.append(s.pop());
                s.push(c);
            }
        }
        while (!s.isEmpty()) out.append(s.pop());
        return out.toString();
    }

    // ── Evaluate Postfix ─────────────────────────────────────────
    static int evalPostfix(String postfix) {
        Stack<Integer> s = new Stack<>(postfix.length());
        for (char c : postfix.toCharArray()) {
            if (Character.isDigit(c)) s.push(c - '0');
            else {
                int b = s.pop(), a = s.pop();
                switch (c) {
                    case '+': s.push(a+b); break; case '-': s.push(a-b); break;
                    case '*': s.push(a*b); break; case '/': s.push(a/b); break;
                    case '^': s.push((int)Math.pow(a,b)); break;
                }
            }
        }
        return s.pop();
    }
}

// ─── QUEUE ADT (Task Processing Pipeline) ──────────────────────
class Queue<T> {
    private Object[] data; private int front, rear, size;
    Queue(int cap) { data = new Object[cap]; front = 0; rear = -1; }

    void enqueue(T v) { if (size < data.length) { rear=(rear+1)%data.length; data[rear]=v; size++; } }
    @SuppressWarnings("unchecked") T dequeue() {
        if (size==0) return null; T v=(T)data[front]; front=(front+1)%data.length; size--; return v;
    }
    boolean isEmpty() { return size==0; }
    int size() { return size; }

    // Circular queue demo: process up to n tasks in round-robin
    static void demoCircularQueue(Task[] tasks) {
        Queue<Task> q = new Queue<>(tasks.length + 1);
        for (Task t : tasks) q.enqueue(t);
        System.out.println("  [Queue] Processing order:");
        int i = 1;
        while (!q.isEmpty()) System.out.printf("  %d. %s%n", i++, q.dequeue());
    }
}

// ─── DEQUEUE (Double-ended Queue) ──────────────────────────────
class Deque<T> {
    private LinkedList<T> list = new LinkedList<>();
    void addFront(T v) { list.addFirst(v); }
    void addRear(T v) { list.addLast(v); }
    T removeFront() { return list.isEmpty() ? null : list.removeFirst(); }
    T removeRear() { return list.isEmpty() ? null : list.removeLast(); }
    boolean isEmpty() { return list.isEmpty(); }
}

// ─── PRIORITY QUEUE / MIN-HEAP (Urgency Queue) ─────────────────
class MinHeap {
    private Task[] heap; private int size;
    MinHeap(int cap) { heap = new Task[cap]; }

    void insert(Task t) {                         // O(log n)
        heap[size++] = t; siftUp(size-1);
    }
    Task extractMin() {                           // O(log n)
        if (size==0) return null;
        Task min = heap[0]; heap[0] = heap[--size]; if (size>0) siftDown(0);
        return min;
    }
    private void siftUp(int i) {
        while (i>0) { int p=(i-1)/2;
            if (heap[p].deadline > heap[i].deadline) { swap(p,i); i=p; } else break; }
    }
    private void siftDown(int i) {
        while (2*i+1 < size) {
            int l=2*i+1, r=2*i+2, s=l;
            if (r<size && heap[r].deadline < heap[l].deadline) s=r;
            if (heap[i].deadline > heap[s].deadline) { swap(i,s); i=s; } else break;
        }
    }
    private void swap(int a, int b) { Task t=heap[a]; heap[a]=heap[b]; heap[b]=t; }
    boolean isEmpty() { return size==0; }
}

// ─── HASH TABLE (Task ID → Task, Separate Chaining) ────────────
class HashTable {
    private static final int CAPACITY = 16;
    private LinkedList<Task>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    HashTable() { table = new LinkedList[CAPACITY]; }

    private int hash(String key) { return Math.abs(key.hashCode()) % CAPACITY; }

    void put(Task t) {
        int idx = hash(t.id);
        if (table[idx] == null) table[idx] = new LinkedList<>();
        for (Task x : table[idx]) if (x.id.equals(t.id)) { table[idx].remove(x); break; }
        table[idx].add(t); size++;
        if ((float)size/CAPACITY > 0.75) rehash(); // rehash at 75% load
    }

    Task get(String id) {
        int idx = hash(id);
        if (table[idx] == null) return null;
        for (Task t : table[idx]) if (t.id.equals(id)) return t;
        return null;
    }

    void remove(String id) {
        int idx = hash(id);
        if (table[idx] != null) table[idx].removeIf(t -> t.id.equals(id));
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        LinkedList<Task>[] old = table;
        table = new LinkedList[old.length * 2]; size = 0;
        for (LinkedList<Task> bucket : old)
            if (bucket != null) for (Task t : bucket) put(t);
        System.out.println("  [Hash] Rehashed to capacity " + table.length);
    }
}

// ═══════════════════════════════════════════════════════════════
//  SEARCHING & SORTING (Algorithm Analysis)
//  T(n) complexities noted for each
// ═══════════════════════════════════════════════════════════════
class Algorithms {

    // ── Linear Search O(n) ──────────────────────────────────────
    static Task[] linearSearch(Task[] arr, String query) {
        if (query == null || query.isEmpty()) return arr;
        String q = query.toLowerCase();
        List<Task> res = new ArrayList<>();
        for (Task t : arr) if (t.name.toLowerCase().contains(q)) res.add(t);
        return res.toArray(new Task[0]);
    }

    // ── Binary Search O(log n) — on sorted-by-name array ────────
    static int binarySearch(Task[] sorted, String name) {
        int lo = 0, hi = sorted.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            int cmp = sorted[mid].name.compareToIgnoreCase(name);
            if (cmp == 0) return mid;
            else if (cmp < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    // ── Bubble Sort O(n²) — by deadline ─────────────────────────
    static Task[] bubbleSort(Task[] arr) {
        Task[] a = arr.clone();
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length-i-1; j++)
                if (a[j].deadline > a[j+1].deadline) { Task t=a[j]; a[j]=a[j+1]; a[j+1]=t; }
        return a;
    }

    // ── Insertion Sort O(n²) — by priority ──────────────────────
    static int priorityVal(String p) { return p.equals("high")?3:p.equals("medium")?2:1; }
    static Task[] insertionSort(Task[] arr) {
        Task[] a = arr.clone();
        for (int i = 1; i < a.length; i++) {
            Task key = a[i]; int j = i-1;
            while (j >= 0 && priorityVal(a[j].priority) < priorityVal(key.priority)) { a[j+1]=a[j]; j--; }
            a[j+1] = key;
        }
        return a;
    }

    // ── Selection Sort O(n²) — by delay ─────────────────────────
    static Task[] selectionSort(Task[] arr) {
        Task[] a = arr.clone();
        for (int i = 0; i < a.length; i++) {
            int mx = i;
            for (int j = i+1; j < a.length; j++) if (a[j].delay > a[mx].delay) mx = j;
            Task t = a[i]; a[i] = a[mx]; a[mx] = t;
        }
        return a;
    }

    // ── Merge Sort O(n log n) — by estimated time ───────────────
    static Task[] mergeSort(Task[] arr) {
        if (arr.length <= 1) return arr;
        int mid = arr.length/2;
        Task[] left  = mergeSort(Arrays.copyOfRange(arr, 0, mid));
        Task[] right = mergeSort(Arrays.copyOfRange(arr, mid, arr.length));
        return merge(left, right);
    }
    private static Task[] merge(Task[] l, Task[] r) {
        Task[] res = new Task[l.length+r.length]; int i=0,j=0,k=0;
        while (i<l.length && j<r.length)
            res[k++] = (l[i].estimatedTime <= r[j].estimatedTime) ? l[i++] : r[j++];
        while (i<l.length) res[k++]=l[i++];
        while (j<r.length) res[k++]=r[j++];
        return res;
    }

    // ── Quick Sort O(n log n) avg — by createdAt ────────────────
    static Task[] quickSort(Task[] arr) {
        Task[] a = arr.clone(); qs(a, 0, a.length-1); return a;
    }
    private static void qs(Task[] a, int lo, int hi) {
        if (lo >= hi) return;
        int p = partition(a, lo, hi); qs(a, lo, p-1); qs(a, p+1, hi);
    }
    private static int partition(Task[] a, int lo, int hi) {
        long pivot = a[hi].createdAt; int i = lo-1;
        for (int j=lo; j<hi; j++) if (a[j].createdAt <= pivot) { i++; Task t=a[i];a[i]=a[j];a[j]=t; }
        Task t=a[i+1];a[i+1]=a[hi];a[hi]=t; return i+1;
    }
}

// ═══════════════════════════════════════════════════════════════
//  MAIN — ProcrastiDetect
// ═══════════════════════════════════════════════════════════════
public class ProcrastiDetect2 {

    static TaskList taskList  = new TaskList();
    static HashTable hashMap  = new HashTable();
    static Stack<String> undo = new Stack<>(100);
    static Queue<Task> pipeline = new Queue<>(100);
    static Deque<Task> recentDeque = new Deque<>();
    static MinHeap urgencyHeap = new MinHeap(100);
    static CircularList recentCirc = new CircularList(5);
    static int idCounter = 1;
    static Scanner sc = new Scanner(System.in);

    // ── Polynomial Demo (List Application) ──────────────────────
    static void demoPolynomial() {
        System.out.println("\n  [Polynomial ADT Demo]  p = 3x^2 + 5x^1 + 2x^0");
        Polynomial p = new Polynomial(3,2);
        p.next = new Polynomial(5,1); p.next.next = new Polynomial(2,0);
        System.out.print("  Poly: ");
        for (Polynomial t=p; t!=null; t=t.next) System.out.print(t + (t.next!=null?" + ":""));
        System.out.println();
    }

    // ── Add Task ─────────────────────────────────────────────────
    static void addTask() {
        System.out.print("  Task name: "); String name = sc.nextLine().trim();
        System.out.print("  Description: "); String desc = sc.nextLine().trim();
        System.out.print("  Priority (high/medium/low): "); String pri = sc.nextLine().trim();
        System.out.print("  Days until deadline: "); int days = Integer.parseInt(sc.nextLine().trim());
        System.out.print("  Estimated time (hours): "); int est = Integer.parseInt(sc.nextLine().trim());

        long dl = System.currentTimeMillis() + (long)days*86400000;
        String id = "T" + idCounter++;
        Task t = new Task(id, name, desc, pri, dl, est);

        taskList.addLast(t);
        hashMap.put(t);
        urgencyHeap.insert(t);
        recentCirc.add(t);
        recentDeque.addFront(t);
        pipeline.enqueue(t);
        undo.push("ADD:" + id);

        System.out.println("\n  Task saved successfully!");
        System.out.printf("  ID          : %s%n", t.id);
        System.out.printf("  Name        : %s%n", t.name);
        System.out.printf("  Description : %s%n", t.description);
        System.out.printf("  Priority    : %s%n", t.priority);
        System.out.printf("  Est. Time   : %dh%n", t.estimatedTime);
        System.out.printf("  Status      : %s%n", t.status);
    }

    // ── Complete Task ────────────────────────────────────────────
    static void completeTask() {
        System.out.print("  Task ID to complete: "); String id = sc.nextLine().trim();
        Task t = hashMap.get(id);
        if (t == null) { System.out.println("  ✗ Not found."); return; }
        t.completionTime = System.currentTimeMillis();
        t.delay = (int)((t.completionTime - t.deadline) / 3600000);
        t.status = t.delay > 0 ? "delayed" : "completed";
        hashMap.put(t); undo.push("COMPLETE:" + id);
        System.out.printf("  ✓ Completed. Delay: %dh%n", t.delay);
    }

    // ── Delete Task ──────────────────────────────────────────────
    static void deleteTask() {
        System.out.print("  Task ID to delete: "); String id = sc.nextLine().trim();
        if (taskList.remove(id)) { hashMap.remove(id); undo.push("DEL:"+id); System.out.println("  ✓ Deleted."); }
        else System.out.println("  ✗ Not found.");
    }

    // ── Undo ─────────────────────────────────────────────────────
    static void undoLast() {
        String op = undo.pop();
        System.out.println("  [Undo Stack] Last operation: " + (op != null ? op : "nothing to undo"));
    }

    // ── Search ───────────────────────────────────────────────────
    static void searchTasks() {
        System.out.print("  Query: "); String q = sc.nextLine().trim();
        Task[] arr = taskList.toArray();
        Task[] res = Algorithms.linearSearch(arr, q);          // O(n)
        System.out.println("  Linear Search results (" + res.length + "):");
        for (Task t : res) System.out.println("  " + t);

        // Binary search on name-sorted array
        Task[] sorted = arr.clone();
        Arrays.sort(sorted, Comparator.comparing(t -> t.name.toLowerCase()));
        int idx = Algorithms.binarySearch(sorted, q);          // O(log n)
        System.out.println("  Binary Search exact match: " + (idx>=0 ? sorted[idx] : "not found"));
    }

    // ── Sort & View ──────────────────────────────────────────────
    static void sortView() {
        System.out.println("  Sort by: 1=Deadline(Bubble) 2=Priority(Insertion) 3=Delay(Selection) 4=EstTime(Merge) 5=CreatedAt(Quick)");
        System.out.print("  Choice: "); int c = Integer.parseInt(sc.nextLine().trim());
        Task[] arr = taskList.toArray(), sorted;
        String label;
        switch (c) {
            case 1: sorted=Algorithms.bubbleSort(arr);    label="Deadline (Bubble Sort O(n²))";    break;
            case 2: sorted=Algorithms.insertionSort(arr); label="Priority (Insertion Sort O(n²))"; break;
            case 3: sorted=Algorithms.selectionSort(arr); label="Delay (Selection Sort O(n²))";    break;
            case 4: sorted=Algorithms.mergeSort(arr);     label="Est.Time (Merge Sort O(n log n))";break;
            default: sorted=Algorithms.quickSort(arr);    label="Created (Quick Sort O(n log n))"; break;
        }
        System.out.println("  Sorted by " + label + ":");
        for (Task t : sorted) System.out.println("    " + t);
    }

    // ── Dashboard ────────────────────────────────────────────────
    static void dashboard() {
        Task[] arr = taskList.toArray();
        long done    = Arrays.stream(arr).filter(t->t.completionTime>0).count();
        long pending = Arrays.stream(arr).filter(t->t.status.equals("pending")).count();
        long delayed = Arrays.stream(arr).filter(t->t.status.equals("delayed")).count();
        double avgDelay = Arrays.stream(arr).mapToInt(t->t.delay).filter(d->d>0).average().orElse(0);
        int score = arr.length==0 ? 0 : (int)(delayed*100/arr.length);

        System.out.println("\n  Dashboard");
        System.out.printf("  Total: %d  Done: %d%n", arr.length, done);
        System.out.printf("  Pending: %d  Delayed: %d%n", pending, delayed);
        System.out.printf("  Avg Delay: %.1fh  Score: %d%%%n", avgDelay, score);
        String lvl = score<=20?"Excellent":score<=50?"Moderate":"Severe";
        System.out.println("  Level: " + lvl);

        System.out.println("\n  [Heap] Most Urgent Task:");
        MinHeap tmp = new MinHeap(arr.length+1);
        for (Task t : arr) tmp.insert(new Task(t.id,t.name,t.description,t.priority,t.deadline,t.estimatedTime));
        Task top = tmp.extractMin();
        System.out.println("    → " + (top!=null ? top.name : "none"));

        System.out.println("\n  [Circular List] Recent 5 Tasks:");
        recentCirc.print();
    }

    // ── Menu ─────────────────────────────────────────────────────
    static void printMenu() {
        System.out.println("\n       ProcrastiDetect");
        System.out.println();
        System.out.println("1. Add Task");
        System.out.println("2. Complete Task");
        System.out.println("3. Delete Task");
        System.out.println("4. Search Tasks");
        System.out.println("5. Sort & View Tasks");
        System.out.println("6. Dashboard");
        System.out.println("7. Undo Last Operation");
        System.out.println("0. Exit");
        System.out.print("Choice: ");
    }

    // ── Seed Demo Data ───────────────────────────────────────────
    static void seed() {
        String[][] data = {
            {"Assignment","Submit DSA project","high","2","5"},
            {"Reading","Read chapter 4","low","7","2"},
            {"Meeting","Prepare slides","medium","1","3"},
            {"Exam Prep","Revise all topics","high","3","8"},
            {"Email","Reply to professor","low","1","1"},
        };
        long now = System.currentTimeMillis();
        for (String[] d : data) {
            long dl = now + Long.parseLong(d[3])*86400000L;
            Task t = new Task("T"+idCounter++, d[0], d[1], d[2], dl, Integer.parseInt(d[4]));
            taskList.addLast(t); hashMap.put(t); urgencyHeap.insert(t);
            recentCirc.add(t); recentDeque.addFront(t); pipeline.enqueue(t);
        }
        // Manually mark some completed/delayed for demo
        Task t1 = hashMap.get("T1"); t1.completionTime = now - 3600000*2; t1.delay = 2; t1.status = "delayed";
        Task t3 = hashMap.get("T3"); t3.completionTime = now - 3600000;   t3.delay = 0; t3.status = "completed";
    }

    public static void main(String[] args) {
        System.out.println("  [ProcrastiDetect] Loading demo data...");
        seed();
        System.out.println("  ✓ Ready.\n");

        while (true) {
            printMenu();
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                switch (choice) {
                    case 1: addTask();      break;
                    case 2: completeTask(); break;
                    case 3: deleteTask();   break;
                    case 4: searchTasks();  break;
                    case 5: sortView();     break;
                    case 6: dashboard();    break;
                    case 7: undoLast();     break;
                    case 0: System.out.println("  Goodbye!"); return;
                    default: System.out.println("  Invalid choice.");
                }
            } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
        }
    }
}
