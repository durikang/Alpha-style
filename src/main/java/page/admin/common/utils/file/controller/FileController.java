package page.admin.common.utils.file.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import page.admin.common.utils.file.FileStore;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileController {

    private final FileStore fileStore;

    public FileController(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    @GetMapping("/files/{filename}")
    @ResponseBody
    public Resource serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(fileStore.getFileDir()).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
