package xyz.sadiulhakim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class LearnAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnAopApplication.class, args);
    }

    @RequireName
    @GetMapping("hi")
    ResponseEntity<?> hi(@RequestParam(defaultValue = "") String name) {
        return ResponseEntity.ok("Hello " + name);
    }

    @GetMapping("ping")
    ResponseEntity<?> ping() {
        return ResponseEntity.ok("Pong");
    }
}
