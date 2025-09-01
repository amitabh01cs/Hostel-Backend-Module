package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.entity.RegisterStudent;
import com.example.testfrontendbackenddb.service.RegisterStudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class RegisterStudentController {

    @Autowired
    private RegisterStudentService registerStudentService;

    // Add new student with optional photo
    @PostMapping("/add")
    public ResponseEntity<String> addStudent(
            @RequestPart("student") String studentJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RegisterStudent student = mapper.readValue(studentJson, RegisterStudent.class);

            if (photo != null && !photo.isEmpty()) {
                student.setPhotoBlob(photo.getBytes());
            }

            registerStudentService.insertStudent(student);
            return ResponseEntity.ok("Student registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Get image by student id for frontend display
    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Integer id) {
        RegisterStudent student = registerStudentService.getStudentById(id);
        if (student == null || student.getPhotoBlob() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(student.getPhotoBlob());
    }

    // Get all students
    @GetMapping("/all")
    public ResponseEntity<List<RegisterStudent>> getAllStudents() {
        return ResponseEntity.ok(registerStudentService.getAllStudents());
    }

    // Update student
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable Integer id,
                                                @RequestBody RegisterStudent updatedStudent) {
        boolean updated = registerStudentService.updateStudent(id, updatedStudent);
        if (updated)
            return ResponseEntity.ok("Student updated successfully!");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
    }

    // Delete student
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
        boolean deleted = registerStudentService.deleteStudent(id);
        if (deleted)
            return ResponseEntity.ok("Student deleted successfully!");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
    }
}