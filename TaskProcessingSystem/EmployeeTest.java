import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {
    
    @Test
    void testAddAndGetTask() {
        Employee emp = new Employee("Иванов Иван");
        Task task = new Task("Task-1", 5);
        
        emp.addTask(task);
        assertTrue(emp.hasTasks());
        assertEquals(task, emp.getNextTask());
        assertFalse(emp.hasTasks());
    }
    
    @Test
    void testWorkTimeTracking() {
        Employee emp = new Employee("Петров Петр");
        
        emp.addWorkTime(120); // 2 часа работы
        emp.addIdleTime(60);  // 1 час простоя
        
        assertEquals(180, emp.getTotalWorkTime());
        assertEquals(120, emp.getTaskWorkTime());
        assertEquals(60, emp.getIdleTime());
        assertEquals(66.67, emp.getEfficiency(), 0.01);
    }
    
    @Test
    void testEfficiencyWithNoWork() {
        Employee emp = new Employee("Сидорова Анна");
        assertEquals(0.0, emp.getEfficiency());
    }
}