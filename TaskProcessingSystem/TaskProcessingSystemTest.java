import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaskProcessingSystemTest {
    
    @Test
    void testGenerateRandomTasks() {
        List<Task> tasks = TaskProcessingSystem.generateRandomTasks(10);
        assertEquals(10, tasks.size());
        
        for (Task task : tasks) {
            assertTrue(task.getDuration() >= 1 && task.getDuration() <= 16);
        }
    }
    
    @Test
    void testProcessWorkDay() throws InterruptedException {
        // Создаем тестовых сотрудников
        Employee emp1 = new Employee("Иванов Иван");
        Employee emp2 = new Employee("Петров Петр");
        List<Employee> employees = List.of(emp1, emp2);
        
        // Создаем тестовые задачи
        Task task1 = new Task("Task-1", 2);  // 2 часа
        Task task2 = new Task("Task-2", 10); // 10 часов
        List<Task> tasks = List.of(task1, task2);
        
        // Обрабатываем рабочий день
        TaskProcessingSystem.processWorkDay(employees, tasks);
        
        // Проверяем результаты
        assertTrue(emp1.getTotalWorkTime() > 0 || emp2.getTotalWorkTime() > 0);
        assertTrue(emp1.getTaskWorkTime() > 0 || emp2.getTaskWorkTime() > 0);
        
        // Проверяем, что задачи распределились
        assertTrue(emp1.hasTasks() || emp2.hasTasks() || 
                 (task1.isCompleted() && task2.isCompleted()));
    }
    
    @Test
    void testTaskDistribution() throws InterruptedException {
        Employee emp1 = new Employee("Сотрудник 1");
        Employee emp2 = new Employee("Сотрудник 2");
        List<Employee> employees = List.of(emp1, emp2);
        
        Task task = new Task("Единственная задача", 8);
        List<Task> tasks = List.of(task);
        
        TaskProcessingSystem.processWorkDay(employees, tasks);
        
        // Проверяем, что задача была назначена ровно одному сотруднику
        assertTrue(emp1.hasTasks() ^ emp2.hasTasks());
    }
}