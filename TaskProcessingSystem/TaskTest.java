import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    
    @Test
    void testTaskCreation() {
        Task task = new Task("Task-1", 8);
        assertEquals("Task-1", task.getName());
        assertEquals(8, task.getDuration());
        assertEquals(480, task.getRemaining()); // 8 часов в минутах
    }
    
    @Test
    void testWorkOnTask() {
        Task task = new Task("Task-2", 4); // 4 часа = 240 минут
        
        task.workOn(60); // Работаем 1 час
        assertEquals(180, task.getRemaining());
        assertFalse(task.isCompleted());
        
        task.workOn(180); // Работаем оставшиеся 3 часа
        assertEquals(0, task.getRemaining());
        assertTrue(task.isCompleted());
    }
    
    @Test
    void testOverworkTask() {
        Task task = new Task("Task-3", 2); // 2 часа = 120 минут
        task.workOn(180); // Работаем 3 часа
        assertEquals(0, task.getRemaining());
        assertTrue(task.isCompleted());
    }
}