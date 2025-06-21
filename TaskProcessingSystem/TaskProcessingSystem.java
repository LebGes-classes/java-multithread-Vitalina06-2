import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskProcessingSystem {
    private static final int WORK_DAY_MINUTES = 8 * 60;
    private static final String EMPLOYEES_FILE = "employees.xlsx";
    private static final String RESULTS_FILE = "workday_results.xlsx";
    private static final Random random = new Random();

    public static void main(String[] args) {
        try {
            // 1. Создаем файл с сотрудниками
            createEmployeesFile();
            
            // 2. Читаем сотрудников из файла
            List<Employee> employees = readEmployeesFromExcel();
            
            // 3. Генерируем задачи
            List<Task> tasks = generateRandomTasks(30);
            
            // 4. Обрабатываем рабочий день
            processWorkDay(employees, tasks);
            
            // 5. Сохраняем результаты
            saveResultsToExcel(employees);
            
            // 6. Выводим статистику
            printStatistics(employees);
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void createEmployeesFile() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");
        
        // Заголовок
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Employee Name");
        
        // Данные сотрудников
        String[] names = {
            "Иванов Иван", "Петров Петр", "Сидорова Анна", 
            "Кузнецов Дмитрий", "Смирнова Елена", "Васильев Алексей",
            "Николаева Ольга", "Павлов Сергей", "Федорова Мария", 
            "Александров Андрей"
        };
        
        for (int i = 0; i < names.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(names[i]);
        }
        
        // Сохраняем файл
        try (FileOutputStream out = new FileOutputStream(EMPLOYEES_FILE)) {
            workbook.write(out);
        }
        workbook.close();
    }

    private static List<Employee> readEmployeesFromExcel() throws IOException {
        List<Employee> employees = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(EMPLOYEES_FILE)) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Пропускаем заголовок
                Cell cell = row.getCell(0);
                if (cell != null) {
                    employees.add(new Employee(cell.getStringCellValue()));
                }
            }
            workbook.close();
        }
        return employees;
    }

    private static List<Task> generateRandomTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            int duration = 1 + random.nextInt(16); // 1-16 часов
            tasks.add(new Task("Task-" + i, duration));
        }
        return tasks;
    }

    private static void processWorkDay(List<Employee> employees, List<Task> tasks) 
            throws InterruptedException {
        
        // Распределяем задачи
        for (Task task : tasks) {
            Employee employee = employees.get(random.nextInt(employees.size()));
            employee.addTask(task);
        }
        
        // Создаем пул потоков по количеству сотрудников
        ExecutorService executor = Executors.newFixedThreadPool(employees.size());
        
        // Запускаем рабочий день для каждого сотрудника
        List<Future<?>> futures = new ArrayList<>();
        for (Employee employee : employees) {
            futures.add(executor.submit(() -> {
                int workedMinutes = 0;
                
                while (workedMinutes < WORK_DAY_MINUTES && employee.hasTasks()) {
                    Task task = employee.getNextTask();
                    int timeToSpend = Math.min(60, task.getRemaining());
                    timeToSpend = Math.min(timeToSpend, WORK_DAY_MINUTES - workedMinutes);
                    
                    if (timeToSpend > 0) {
                        // Симулируем работу
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        employee.addWorkTime(timeToSpend);
                        task.workOn(timeToSpend);
                        workedMinutes += timeToSpend;
                        
                        if (!task.isCompleted()) {
                            employee.addTask(task); // Возвращаем невыполненную задачу
                        }
                    }
                }
                
                // Учет простоя
                if (workedMinutes < WORK_DAY_MINUTES) {
                    employee.addIdleTime(WORK_DAY_MINUTES - workedMinutes);
                }
            }));
        }
        
        // Ожидаем завершения всех потоков
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        
        executor.shutdown();
    }

    private static void saveResultsToExcel(List<Employee> employees) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Results");
        
        // Заголовок
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Employee", "Total Hours", "Task Hours", "Idle Hours", "Efficiency"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        // Данные
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            Row row = sheet.createRow(i + 1);
            
            row.createCell(0).setCellValue(emp.getName());
            row.createCell(1).setCellValue(emp.getTotalWorkTime() / 60.0);
            row.createCell(2).setCellValue(emp.getTaskWorkTime() / 60.0);
            row.createCell(3).setCellValue(emp.getIdleTime() / 60.0);
            row.createCell(4).setCellValue(emp.getEfficiency());
        }
        
        // Автонастройка ширины столбцов
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Сохраняем файл
        try (FileOutputStream out = new FileOutputStream(RESULTS_FILE)) {
            workbook.write(out);
        }
        workbook.close();
    }

    private static void printStatistics(List<Employee> employees) {
        System.out.println("\nWork Day Statistics:");
        System.out.println("==================================================");
        System.out.printf("%-20s %12s %12s %12s %12s%n",
                "Employee", "Total", "Task", "Idle", "Efficiency");
        System.out.printf("%-20s %12s %12s %12s %12s%n",
                "", "Hours", "Hours", "Hours", "%");
        System.out.println("--------------------------------------------------");
        
        for (Employee emp : employees) {
            System.out.printf("%-20s %12.1f %12.1f %12.1f %12.1f%n",
                    emp.getName(),
                    emp.getTotalWorkTime() / 60.0,
                    emp.getTaskWorkTime() / 60.0,
                    emp.getIdleTime() / 60.0,
                    emp.getEfficiency());
        }
        
        System.out.println("==================================================");
    }
}