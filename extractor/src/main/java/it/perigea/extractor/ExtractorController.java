package it.perigea.extractor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExtractorController {
    private final ExtractorService service;
    public ExtractorController(ExtractorService s){ this.service = s; }

    @GetMapping("/extract")
    public String extract() throws Exception {   // ← add throws Exception
        return service.runOnce();
    }
}

