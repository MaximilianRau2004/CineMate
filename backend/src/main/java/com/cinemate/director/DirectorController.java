package com.cinemate.director;

import com.cinemate.director.DTOs.DirectorRequestDTO;
import com.cinemate.director.DTOs.DirectorResponseDTO;
import com.cinemate.movie.DTOs.MovieResponseDTO;
import com.cinemate.series.DTOs.SeriesResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directors")
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    /**
     * returns all directors
     * @return list of directors
     */
    @GetMapping
    public ResponseEntity<List<DirectorResponseDTO>> getAllDirectors() {
        return ResponseEntity.ok(directorService.getAllDirectors());
    }

    /**
     * returns the director with the given id
     * @param id
     * @return the director
     */
    @GetMapping("/{id}")
    public ResponseEntity<DirectorResponseDTO> getDirectorById(@PathVariable String id) {
        return directorService.getDirectorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * creates a director
     * @param dto
     * @return the created director
     */
    @PostMapping
    public ResponseEntity<DirectorResponseDTO> createDirector(@RequestBody DirectorRequestDTO dto) {
        return ResponseEntity.ok(directorService.createDirector(dto));
    }

    /**
     * updates the director with the given ic
     * @param id
     * @param dto
     * @return the updated director
     */
    @PutMapping("/{id}")
    public ResponseEntity<DirectorResponseDTO> updateDirector(@PathVariable String id, @RequestBody DirectorRequestDTO dto) {
        return directorService.updateDirector(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * deletes the direcotr with the given id
     * @param id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirector(@PathVariable String id) {
        directorService.deleteDirector(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * returns the movies of a director
     * @param id
     * @return list of movies
     */
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieResponseDTO>> getMoviesByDirector(@PathVariable String id) {
        return ResponseEntity.ok(directorService.getMoviesByDirector(id));
    }

    /**
     * returns the series of a director
     * @param id
     * @return list of series
     */
    @GetMapping("/{id}/series")
    public ResponseEntity<List<SeriesResponseDTO>> getSeriesByDirector(@PathVariable String id) {
        return ResponseEntity.ok(directorService.getSeriesByDirector(id));
    }
}
