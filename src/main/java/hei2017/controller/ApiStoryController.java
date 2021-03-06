package hei2017.controller;

import com.fasterxml.jackson.annotation.JsonView;
import hei2017.entity.Sprint;
import hei2017.entity.Story;
import hei2017.enumeration.StoryStatus;
import hei2017.json.JsonViews;
import hei2017.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pic on 16/03/2017.
 */

@EnableWebMvc
@RestController
public class ApiStoryController {

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
     * Requêtes STORY
     */

    @JsonView(JsonViews.Basique.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/story", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public List<Story> showStories()
    {
        LOGGER.debug("ApiController - showStories");
        return storyService.findAll();
    }

    @JsonView(JsonViews.Story.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/story/details", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public List<Story> showStoriesDetails()
    {
        LOGGER.debug("ApiController - showStoriesDetails");
        return storyService.findAllWithAll();
    }

    @JsonView(JsonViews.Basique.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/story/{id}", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<Story> showStory(@PathVariable Long id)
    {
        LOGGER.debug("ApiController - showStory");
        Story story = null;
        if(storyService.exists(id))
        {
            story = storyService.findOneById(id);
            return new ResponseEntity<Story>(story, HttpStatus.OK);
        }
        return new ResponseEntity<Story>(story,HttpStatus.NOT_FOUND);
    }

    @JsonView(JsonViews.Basique.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/story/{id}/details", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<Story> showStoryWithAll(@PathVariable Long id)
    {
        LOGGER.debug("ApiController - showStoryWithAll");
        Story story = null;
        if(storyService.exists(id))
        {
            story = storyService.findOneByIdWithAll(id);
            return new ResponseEntity<Story>(story, HttpStatus.OK);
        }
        return new ResponseEntity<Story>(story, HttpStatus.NOT_FOUND);
    }

    @JsonView(JsonViews.Basique.class)
    @RequestMapping(value = "/api/story/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Story> sendStory(@RequestBody Story story)
    {
        LOGGER.debug("ApiController - sendStory");
        if(!storyService.exists(story.getNom()))
        {
            storyService.save(story);
            LOGGER.debug("ApiController - sendStory - Story créé");
            return new ResponseEntity<Story>(story, HttpStatus.CREATED);
        }
        else
        {
            LOGGER.debug("ApiController - sendStory - Story déjà existante");
            return new ResponseEntity<Story>(story, HttpStatus.CONFLICT);
        }
    }

    @JsonView(JsonViews.Basique.class)
    @RequestMapping(value = "/api/story/add/sprint/{idSprint}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Story> sendStoryWithSprintId(@PathVariable Long idSprint, @RequestBody Story story)
    {
        LOGGER.debug("ApiController - sendStoryWithSprintId");
        Sprint sprint = sprintService.findOneById(idSprint);
        if(null==sprint)
        {
            LOGGER.debug("ApiController - sendStoryWithSprintId - Sprint inexistant");
            return new ResponseEntity<Story>(story, HttpStatus.NOT_FOUND);
        }
        if(story.getStatus()==null)
            story.setStatus(StoryStatus.TODO);

        story = storyService.save(story);
        story.setStorySprint(sprint);
        story = storyService.save(story);

        LOGGER.debug("ApiController - sendStoryWithSprintId - Story créé");
        return new ResponseEntity<Story>(story, HttpStatus.CREATED);
    }

    @JsonView(JsonViews.Basique.class)
    @RequestMapping(value = "/api/story/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Story> deleteStory(@PathVariable("id") Long id)
    {
        LOGGER.debug("ApiController - deleteStory");

        Story story = null;
        if(storyService.exists(id))
        {
            story = storyService.findOneById(id);
            story.setStorySprint(null);
            story.setStoryTasks(null);
            storyService.save(story);
            storyService.deleteOneById(id);
            return new ResponseEntity<Story>(story, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<Story>(story, HttpStatus.NOT_FOUND);
    }

    //Renvoie toutes les STORIES attachées au SPRINT d'id idSprint
    @JsonView(JsonViews.Basique.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/story/sprint/{idSprint}", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public Set<Story> showStoriesAssociatedToThisSprint(@PathVariable Long idSprint)
    {
        LOGGER.debug("ApiController - showStoriesAssociatedToThisSprint");
        Set<Story> stories = storyService.findByStorySprint(idSprint);
        return stories;
    }

    //Renvoie toutes les STORIES qui ne sont pas attachées à un SPRINT
    @JsonView(JsonViews.Basique.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/story/sprint/null", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    public List<Story> showStoriesWithNoSprint()
    {
        LOGGER.debug("ApiController - showStoriesWithNoSprint");
        List<Story> stories = storyService.findAllWithoutSprint();
        return stories;
    }

}
