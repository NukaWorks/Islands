package works.nuka.islands.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class HomeController {

    @GetMapping("/")
    public void root(HttpServletResponse response) throws IOException {
        response.sendRedirect("/dashboard");
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/instances")
    public String instances() {
        return "instances";
    }

    @GetMapping("/tasks")
    public String tasks() {
        return "tasks";
    }

    @GetMapping("/users")
    public String users() {
        return "users";
    }

    @GetMapping("/providers")
    public String providers() {
        return "providers";
    }

    @GetMapping("/admin/groups")
    public String adminGroups() {
        return "admin-groups";
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
