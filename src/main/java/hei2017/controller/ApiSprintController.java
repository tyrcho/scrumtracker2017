package hei2017.controller;

import com.fasterxml.jackson.annotation.JsonView;
import hei2017.entity.Project;
import hei2017.entity.Sprint;
import hei2017.entity.Story;
import hei2017.enumeration.StoryStatus;
import hei2017.json.JsonViews;
import hei2017.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * Created by pic on 16/03/2017.
 */

@EnableWebMvc
@RestController
public class ApiSprintController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    @Inject
    ProjectService projectService;

    @Inject
    SprintService sprintService;

    @Inject
    StoryService storyService;

    @Inject
    TaskService taskService;

    @Inject
    UserService userService;

    /*
     * Requêtes SPRINT
     */

    @JsonView(JsonViews.Basique.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/sprint", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public List<Sprint> showSprints()
    {
        LOGGER.debug("ApiController - showSprints");
        return sprintService.findAll();
    }

    @JsonView(JsonViews.Sprint.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/sprint/details", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public List<Sprint> showSprintsDetails()
    {
        LOGGER.debug("ApiController - showSprintsDetails");
        return sprintService.findAllWithAll();
    }

    @JsonView(JsonViews.Basique.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/sprint/{id}", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<Sprint> showSprint(@PathVariable Long id)
    {
        LOGGER.debug("ApiController - showSprint");
        Sprint sprint = null;
        if(sprintService.exists(id))
        {
            sprint = sprintService.findOneById(id);
            return new ResponseEntity<Sprint>(sprint, HttpStatus.OK);
        }
        return new ResponseEntity<Sprint>(sprint, HttpStatus.NOT_FOUND);
    }

    @JsonView(JsonViews.Sprint.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/sprint/{id}/details", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<Sprint> showSprintWithAll(@PathVariable Long id)
    {
        LOGGER.debug("ApiController - showSprint");
        Sprint sprint = null;
        if(sprintService.exists(id))
        {
            sprint = sprintService.findOneByIdWithAll(id);
            return new ResponseEntity<Sprint>(sprint, HttpStatus.OK);
        }
        return new ResponseEntity<Sprint>(sprint, HttpStatus.NOT_FOUND);
    }

    @JsonView(JsonViews.Basique.class)
    @RequestMapping(value = "/api/sprint/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Sprint> sendSprint(@RequestBody Sprint sprint)
    {
        LOGGER.debug("ApiController - sendSprint");
        if(!sprintService.exists(sprint.getNom()))
        {
            sprintService.save(sprint);
            LOGGER.debug("ApiController - sendSprint - Sprint créé");
            return new ResponseEntity<Sprint>(sprint, HttpStatus.CREATED);
        }
        else
        {
            LOGGER.debug("ApiController - sendSprint - Sprint déjà existant");
            return new ResponseEntity<Sprint>(sprint, HttpStatus.CONFLICT);
        }
    }

    @JsonView(JsonViews.Basique.class)
    @RequestMapping(value = "/api/sprint/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Sprint> deleteSprint(@PathVariable("id") Long id)
    {
        LOGGER.debug("ApiController - deleteSprint");

        Sprint sprint = null;
        if(sprintService.exists(id))
        {
            sprint = sprintService.findOneById(id);
            sprintService.deleteOneById(id);
            return new ResponseEntity<Sprint>(sprint, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<Sprint>(sprint, HttpStatus.NOT_FOUND);
    }

    //Renvoie tous les SPRINTS attachés au projet d'id idProject
    @JsonView(JsonViews.Basique.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/sprint/project/{idProject}", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public Set<Sprint> showSprintsAssociatedToThisProject(@PathVariable Long idProject)
    {
        LOGGER.debug("ApiController - showSprintsAssociatedToThisProject");
        Set<Sprint> sprints = sprintService.findBySprintProject(idProject);
        return sprints;
    }


    @JsonView(JsonViews.Basique.class)
    @RequestMapping(value = "/api/sprint/add/project/{idProject}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Sprint> sendSprintWithProjectId(@PathVariable Long idProject, @RequestBody Sprint sprint)
    {
        LOGGER.debug("ApiController - sendSprintWithProjectId");
        Project project = projectService.findOneById(idProject);
        if(null==sprint)
        {
            LOGGER.debug("ApiController - sendSprintWithProjectId - Project inexistant");
            return new ResponseEntity<Sprint>(sprint, HttpStatus.NOT_FOUND);
        }

        sprint = sprintService.save(sprint);
        sprint.setSprintProject(project);
        sprint = sprintService.save(sprint);

        LOGGER.debug("ApiController - sendSprintWithProjectId - Sprint créé");
        return new ResponseEntity<Sprint>(sprint, HttpStatus.CREATED);
    }

}
