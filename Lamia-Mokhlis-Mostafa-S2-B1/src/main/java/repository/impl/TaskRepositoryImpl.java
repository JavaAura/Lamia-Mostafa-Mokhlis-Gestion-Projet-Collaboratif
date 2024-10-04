package repository.impl;

import dao.impl.TaskDaoImpl;
import model.Task;
import repository.Interface.TaskRepository;

import java.util.List;

public class TaskRepositoryImpl implements TaskRepository {
    private final TaskDaoImpl taskDao = new TaskDaoImpl();

    @Override
    public void create(Task task) {
        taskDao.create(task);
    }

    @Override
    public Task read(int taskID) {
        return taskDao.read(taskID);
    }

    @Override
    public List<Task> getAll() {
        return taskDao.getAll();
    }

    @Override
    public void update(Task task) {
        taskDao.update(task);
    }

    @Override
    public void delete(int taskID) {
        taskDao.delete(taskID);
    }
}

