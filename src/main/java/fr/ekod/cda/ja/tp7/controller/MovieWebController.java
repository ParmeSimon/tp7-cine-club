package fr.ekod.cda.ja.tp7.controller;

import fr.ekod.cda.ja.tp7.dto.movie.CreateMovieDTO;
import fr.ekod.cda.ja.tp7.service.DirectorService;
import fr.ekod.cda.ja.tp7.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MovieWebController {

    private final MovieService movieService;
    private final DirectorService directorService;

    public MovieWebController(MovieService movieService, DirectorService directorService) {
        this.movieService = movieService;
        this.directorService = directorService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/movies")
    public String movies(Model model) {
        model.addAttribute("movies", movieService.findAll());
        return "movies/list";
    }

    @GetMapping("/movies/{id}")
    public String movieDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("movie", movieService.findById(id));
        return "movies/detail";
    }

    @GetMapping("/directors")
    public String directors(Model model) {
        model.addAttribute("directors", directorService.findAll());
        return "directors/list";
    }

    @GetMapping("/movies/new")
    public String newMovieForm(Model model) {
        model.addAttribute("movieForm", new CreateMovieDTO(null, null, null, null, null, null));
        model.addAttribute("directors", directorService.findAll());
        return "movies/form";
    }

    @PostMapping("/movies/new")
    public String createMovie(
            @Valid @ModelAttribute("movieForm") CreateMovieDTO dto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("directors", directorService.findAll());
            return "movies/form";
        }
        movieService.create(dto);
        return "redirect:/movies";
    }
}