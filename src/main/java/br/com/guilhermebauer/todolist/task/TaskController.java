package br.com.guilhermebauer.todolist.task;

import br.com.guilhermebauer.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        System.out.println("chegou no controller" + request.getAttribute("idUser"));
        var idUser = request.getAttribute("idUser");
        taskModel.setUserId((UUID) idUser);

        var currentData = LocalDateTime.now();

        if (currentData.isAfter(taskModel.getStartAt())
                || currentData.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio / data de fim da tarefa deve ser maior que a data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("the start date of the task must be before the end data of the task");
        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByUserId((UUID) idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity update(@RequestBody TaskModel taskModel , HttpServletRequest request , @PathVariable UUID id){

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null){
            return ResponseEntity.badRequest().body("The task has not been found or does not exist");
        }
        var idUSer = request.getAttribute("idUser");

        if (!task.getUserId().equals(idUSer)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The user doesn't have authorization to alter that task.");

        }

        Utils.copyNonNullProperties(taskModel , task);

        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);

    }
}
