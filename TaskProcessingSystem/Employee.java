import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Employee {
    private String name;
    private AtomicInteger totalWorkTime = new AtomicInteger(0);
    private AtomicInteger taskWorkTime = new AtomicInteger(0);
    private AtomicInteger idleTime = new AtomicInteger(0);
    private Queue<Task> tasks = new ConcurrentLinkedQueue<>();

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    public Task getNextTask() {
        return tasks.poll();
    }

    public void addWorkTime(int minutes) {
        totalWorkTime.addAndGet(minutes);
        taskWorkTime.addAndGet(minutes);
    }

    public void addIdleTime(int minutes) {
        totalWorkTime.addAndGet(minutes);
        idleTime.addAndGet(minutes);
    }

    public int getTotalWorkTime() {
        return totalWorkTime.get();
    }

    public int getTaskWorkTime() {
        return taskWorkTime.get();
    }

    public int getIdleTime() {
        return idleTime.get();
    }

    public double getEfficiency() {
        if (totalWorkTime.get() == 0) return 0;
        return (double) taskWorkTime.get() / totalWorkTime.get() * 100;
    }
}