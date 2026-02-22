package works.nuka.islands.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/components")
    public String components() {
        return "components-page";
    }

    @GetMapping("/theme")
    public String theme() {
        return "theme";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
}
