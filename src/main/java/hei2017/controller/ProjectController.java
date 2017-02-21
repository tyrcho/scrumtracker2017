package hei2017.controller;

import hei2017.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by pic on 09/02/2017.
 */
@Controller
public class ProjectController {

    @Inject
    ProjectService projectService;

    @RequestMapping("/projects")
    public String goProjects(Model model,
                            HttpServletRequest request,
                            HttpServletResponse response)
    {
        model.addAttribute("isProjectPage", true);

        model.addAttribute("projects", projectService.findAll());

        return "projects";
    }

}