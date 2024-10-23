package se.seb.embedded.coding_assignment.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@RestController
public class DeleteFolder {

    private static final String OUTPUT_DIR = "output/";

    @DeleteMapping("/deleteFolder")
    public ResponseEntity<String> deleteFolder() throws IOException {
        Files.walk(Paths.get(OUTPUT_DIR))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(file -> file.delete());
        return ResponseEntity.status(HttpStatus.OK).body("Output Folder Deleted");
    }

}
