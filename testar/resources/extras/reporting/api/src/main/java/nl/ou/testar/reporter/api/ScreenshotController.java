package nl.ou.testar.reporter.api;

import javax.print.attribute.standard.MediaTray;
import javax.servlet.http.HttpServletRequest;

import nl.ou.testar.reporter.model.PostEntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.models.media.MediaType;
import nl.ou.testar.reporter.service.ScreenshotService;

/**
 * ScreenshotController
 */
@RestController
@RequestMapping("/screenshots")
public class ScreenshotController {

    @Autowired
    private ScreenshotService service;

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@Parameter @PathVariable String filename) {
        Resource rfile = service.loadAsResource(filename);
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.IMAGE_PNG).body(rfile);
    }

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<PostEntityResponse> uploadImage(HttpServletRequest request, @RequestParam(name="file") MultipartFile file) {
        service.store(file);
        return ResponseEntity.ok(PostEntityResponse.builder().uri(String.format("%s%s", request.getRequestURI(), file.getOriginalFilename())).build());
    }
}
