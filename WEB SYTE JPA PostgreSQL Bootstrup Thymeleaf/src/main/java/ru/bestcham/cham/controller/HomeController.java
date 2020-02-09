package ru.bestcham.cham.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.Role;
import ru.bestcham.cham.form.HomeForm;
import ru.bestcham.cham.repository.CategoryFilmRepository;
import ru.bestcham.cham.repository.CategorySyteRepository;
import ru.bestcham.cham.repository.CategoryYoutubeRepository;
import ru.bestcham.cham.repository.FilmRepository;
import ru.bestcham.cham.repository.SyteRepository;
import ru.bestcham.cham.repository.YoutubeRepository;
import ru.bestcham.cham.service.CategoryFilmService;
import ru.bestcham.cham.service.CategoryFilmService.MenuCategoryFilm;
import ru.bestcham.cham.service.CategorySyteService;
import ru.bestcham.cham.service.CategorySyteService.MenuCategorySyte;
import ru.bestcham.cham.service.CategoryYoutubeService;
import ru.bestcham.cham.service.CategoryYoutubeService.MenuCategoryYoutube;
import ru.bestcham.cham.service.FilmService;
import ru.bestcham.cham.service.FilmService.MenuFilm;
import ru.bestcham.cham.service.SyteService;
import ru.bestcham.cham.service.SyteService.MenuSyte;
import ru.bestcham.cham.service.YoutubeService;
import ru.bestcham.cham.service.YoutubeService.MenuYoutube;

@Slf4j
@Controller
// @RequestMapping("/home")
public class HomeController {
  private final Integer pageSize = 5;
  private String homeFindString; 
  
  private String menuItem;
  private Integer numberPageFilm;
  private Integer numberPageSyte;
  private Integer numberPageYoutube;
  private List<MenuCategoryFilm> menuCategoryFilms;
  private List<MenuCategorySyte> menuCategorySytes;
  private List<MenuCategoryYoutube> menuCategoryYoutubes;
  private List<MenuFilm> menuFilms;
  private List<MenuSyte> menuSytes;
  private List<MenuYoutube> menuYoutubes;

  private FilmRepository filmRepo;
  private SyteRepository syteRepo;
  private YoutubeRepository youtubeRepo;
  private CategoryFilmRepository categoryFilmRepo;
  private CategorySyteRepository categorySyteRepo;
  private CategoryYoutubeRepository categoryYoutubeRepo;

  private FilmService filmService;
  private SyteService syteService;
  private YoutubeService youtubeService;
  private CategoryFilmService categoryFilmService;
  private CategorySyteService categorySyteService;
  private CategoryYoutubeService categoryYoutubeService;

  public HomeController(FilmRepository filmRepo, SyteRepository syteRepo,
      YoutubeRepository youtubeRepo, CategoryFilmRepository categoryFilmRepo,
      CategorySyteRepository categorySyteRepo, CategoryYoutubeRepository categoryYoutubeRepo) {
    this.filmRepo = filmRepo;
    this.syteRepo = syteRepo;
    this.youtubeRepo = youtubeRepo;
    this.categoryFilmRepo = categoryFilmRepo;
    this.categorySyteRepo = categorySyteRepo;
    this.categoryYoutubeRepo = categoryYoutubeRepo;

    this.filmService = new FilmService(this.filmRepo);
    this.syteService = new SyteService(this.syteRepo);
    this.youtubeService = new YoutubeService(this.youtubeRepo);
    this.categoryFilmService = new CategoryFilmService(this.categoryFilmRepo);
    this.categorySyteService = new CategorySyteService(this.categorySyteRepo);
    this.categoryYoutubeService = new CategoryYoutubeService(this.categoryYoutubeRepo);

    this.menuCategoryFilms = categoryFilmService.getmenuCategoryFilms();
    this.menuCategorySytes = categorySyteService.getmenuCategorySytes();
    this.menuCategoryYoutubes = categoryYoutubeService.getmenuCategoryYoutubes();
    this.menuFilms = filmService.getmenuFilms();
    this.menuSytes = syteService.getmenuSytes();
    this.menuYoutubes = youtubeService.getmenuYoutubes();
    this.numberPageFilm = 1;
    this.numberPageSyte = 1;
    this.numberPageYoutube = 1;
    this.homeFindString="";
  }

  private Model Dnone(Model model, HomeForm homeForm) {

    model.addAttribute("admindnone", SecurityContextHolder.getContext().getAuthentication()
        .getAuthorities().stream().anyMatch(s -> (Role.ADMIN.equals(s))));

    menuCategoryFilms.stream().filter(f -> f.getActive() == "active").forEach(t -> model.addAttribute("categoryfilmid", t.getId()));
    menuCategorySytes.stream().filter(f -> f.getActive() == "active").forEach(t -> model.addAttribute("categorysyteid", t.getId()));
    menuCategoryYoutubes.stream().filter(f -> f.getActive() == "active").forEach(t -> model.addAttribute("categoryyoutubeid", t.getId()));
    model.addAttribute("numberpagefilm", numberPageFilm);
    model.addAttribute("numberpagesyte", numberPageSyte);
    model.addAttribute("numberpageyoutube", numberPageYoutube);
    model.addAttribute("homefind", homeForm.getHomefind());
    
    log.info("homeFindString="+homeFindString);
    switch (menuItem) {
      case "mfilm":
        model.addAttribute("menuitemfilm", "active");
        model.addAttribute("categoryfilms", menuCategoryFilms);

        var menuFindFilms = new ArrayList<MenuFilm>();        
        if ("".equals(homeFindString)) {
          menuCategoryFilms.stream().filter(f -> f.getActive() == "active").forEach(t ->
          menuFilms.stream().skip((numberPageFilm - 1) * pageSize).limit(pageSize).filter(f -> f.getCategoryfilms().contains(t.getName())).forEach(f -> menuFindFilms.add(f)));
        } else {
          menuCategoryFilms.stream().filter(f -> f.getActive() == "active").forEach(t ->
          menuFilms.stream().skip((numberPageFilm - 1) * pageSize).limit(pageSize).filter(f -> f.getCategoryfilms().contains(t.getName())&&f.getName().toLowerCase().contains(homeFindString.toLowerCase())).forEach(f -> menuFindFilms.add(f)));
        }
        model.addAttribute("films", menuFindFilms);

        break;
      case "msyte":
        model.addAttribute("menuitemsyte", "active");
        model.addAttribute("categorysytes", menuCategorySytes);

        var menuFindSytes = new ArrayList<MenuSyte>();        
        if ("".equals(homeFindString)) {
          menuCategorySytes.stream().filter(f -> f.getActive() == "active").forEach(t ->
          menuSytes.stream().skip((numberPageSyte - 1) * pageSize).limit(pageSize).filter(f -> f.getCategorysytes().contains(t.getName())).forEach(f -> menuFindSytes.add(f)));
        } else {
          menuCategorySytes.stream().filter(f -> f.getActive() == "active").forEach(t ->
          menuSytes.stream().skip((numberPageSyte - 1) * pageSize).limit(pageSize).filter(f -> f.getCategorysytes().contains(t.getName())&&f.getName().toLowerCase().contains(homeFindString.toLowerCase())).forEach(f -> menuFindSytes.add(f)));
        }
        model.addAttribute("sytes", menuFindSytes);
        
        break;
      case "myoutube":
        model.addAttribute("menuitemyoutube", "active");
        model.addAttribute("categoryyoutubes", menuCategoryYoutubes);

        var menuFindYoutubes = new ArrayList<MenuYoutube>();        
        if ("".equals(homeFindString)) {
          menuCategoryYoutubes.stream().filter(f -> f.getActive() == "active").forEach(t ->
          menuYoutubes.stream().skip((numberPageYoutube - 1) * pageSize).limit(pageSize).filter(f -> f.getCategoryyoutubes().contains(t.getName())).forEach(f -> menuFindYoutubes.add(f)));
        } else {
          menuCategoryYoutubes.stream().filter(f -> f.getActive() == "active").forEach(t ->
          menuYoutubes.stream().skip((numberPageYoutube - 1) * pageSize).limit(pageSize).filter(f -> f.getCategoryyoutubes().contains(t.getName())&&f.getName().toLowerCase().contains(homeFindString.toLowerCase())).forEach(f -> menuFindYoutubes.add(f)));
        }
        model.addAttribute("youtubes", menuFindYoutubes);
        
        break;
    }
    homeFindString="";

    return model;
  }

  @GetMapping("/mfilm/{id}/page/{page}")
  public String senDnoneFilmId(@PathVariable Integer id, @PathVariable Integer page, HomeForm homeForm, Model model) {
    menuItem = "mfilm";
    if (page > 0) numberPageFilm = page; else numberPageFilm = 1;
    if (id != 0) {
      menuCategoryFilms.stream().filter(f -> f.getActive() == "active")
          .forEach(f -> f.setActive(""));
      menuCategoryFilms.stream().filter(f -> f.getId() == id).forEach(f -> f.setActive("active"));
    }
    model = Dnone(model, homeForm);

    return "home";
  }

  @GetMapping("/msyte/{id}/page/{page}")
  public String senDnoneSyte(@PathVariable Integer id, @PathVariable Integer page, HomeForm homeForm, Model model) {
    menuItem = "msyte";
    if (page > 0) numberPageSyte = page; else numberPageSyte = 1;
    if (id != 0) {
      menuCategorySytes.stream().filter(f -> f.getActive() == "active")
          .forEach(f -> f.setActive(""));
      menuCategorySytes.stream().filter(f -> f.getId() == id).forEach(f -> f.setActive("active"));
    }
    model = Dnone(model, homeForm);

    return "home";
  }

  @GetMapping("/myoutube/{id}/page/{page}")
  public String senDnoneYoutube(@PathVariable Integer id, @PathVariable Integer page, HomeForm homeForm, Model model) {
    menuItem = "myoutube";
    if (page > 0) numberPageYoutube = page; else numberPageYoutube = 1;
    if (id != 0) {
      menuCategoryYoutubes.stream().filter(f -> f.getActive() == "active")
          .forEach(f -> f.setActive(""));
      menuCategoryYoutubes.stream().filter(f -> f.getId() == id)
          .forEach(f -> f.setActive("active"));
    }
    model = Dnone(model, homeForm);

    return "home";
  }

  @GetMapping("/home")
  public String senDnone(HomeForm homeForm, Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
    log.info(" --- Designing BestCHAM На сайте зарегистрирован " + username);

    if (menuItem == null)
      menuItem = "mfilm";

    model = Dnone(model, homeForm);


    return "home";
  }

  @PostMapping("/home")
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value,
      HomeForm homeForm, Map<String, Object> model) {
    model.put("admindnone", SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        .stream().anyMatch(s -> (Role.ADMIN.equals(s))));

    if ("interlogin".equals(value)) {
      return "login";
    }
    if ("interadmin".equals(value))
      return "redirect:/design";
    if ("interfind".equals(value)) {
      if (!"Поиск по сайту".equals(homeForm.getHomefind())) {
        homeFindString = homeForm.getHomefind();
      }
      
      return "redirect:/home";
    }  

    return "redirect:/home";
  }

}
