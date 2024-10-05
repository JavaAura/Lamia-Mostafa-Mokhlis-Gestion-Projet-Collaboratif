package controller;

import model.Task;
import model.enums.TaskPriority;
import model.enums.TaskStatus;
import service.TaskService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TaskServlet
 */
//@WebServlet("/")
public class TaskServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final TaskService taskService;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaskServlet() {
        this.taskService = new TaskService();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
       
        switch (action) {
        case "get":
        	getTaskByID(request, response);
            break;
        case "edit":
            editTask(request, response);
            break;
        case "list":
        	listTasks(request, response);
            break;
        default:
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            break;
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "insert":
                insertTask(request, response);
                break;
            case "update":
                updateTask(request, response);
                break;
            case "delete":
                deleteTask(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                break;
        }
    }


    private void insertTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String priorityStr = request.getParameter("priority");
        String dueDateStr = request.getParameter("dueDate");

        TaskPriority priority = TaskPriority.valueOf(priorityStr.toUpperCase());
        TaskStatus status = TaskStatus.TO_DO;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr, formatter);
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Invalid due date format. Please use MM/DD/YYYY.");
            request.getRequestDispatcher("/jsp/errorPage.jsp").forward(request, response);
            return;
        }

        Task newTask = new Task();
        newTask.setTitle(title);
        newTask.setDescription(description);
        newTask.setPriority(priority);
        newTask.setDueDate(dueDate);
        newTask.setStatus(status);
        newTask.setCreationDate(LocalDate.now());

        try {
            taskService.createTask(newTask);
            response.sendRedirect(request.getContextPath() + "/jsp/tasks.jsp");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/jsp/errorPage.jsp").forward(request, response);
        }
    }

    private void editTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int taskID = Integer.parseInt(request.getParameter("taskId"));

        Task task = taskService.getTask(taskID);

        request.setAttribute("task", task);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/editTask.jsp");
        dispatcher.forward(request, response);
    }

    private void updateTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        int taskID = Integer.parseInt(request.getParameter("taskId"));
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String priorityStr = request.getParameter("priority");
        String dueDateStr = request.getParameter("dueDate");

        TaskPriority priority = TaskPriority.valueOf(priorityStr.toUpperCase());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr, formatter);
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Invalid due date format. Please use MM/DD/YYYY.");
            request.getRequestDispatcher("/jsp/errorPage.jsp").forward(request, response);
            return;
        }

        Task task = taskService.getTask(taskID);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);

        taskService.updateTask(task);

        // Redirect to the task list page after update
        response.sendRedirect(request.getContextPath() + "/tasks?action=list");
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
    
    private void getTaskByID(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    private void listTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int projectID = Integer.parseInt(request.getParameter("projectID"));
        int page = 1;
        int size = 5; 
        
        if (request.getParameter("page") != null) {   
           page = Integer.parseInt(request.getParameter("page"));                      
        }

        if (request.getParameter("size") != null) { 
           size = Integer.parseInt(request.getParameter("size")); 
        }

        List<Task> tasks = taskService.getPaginatedProjectTasks(projectID, page, size);
        int totalTasks = taskService.getTotalTasksForProject(projectID);
        int totalPages = (int) Math.ceil((double) totalTasks / size);

        request.setAttribute("tasks", tasks);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // Forward to JSP for rendering
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/taskList.jsp");
        dispatcher.forward(request, response);
    }

}
