package com.cinemate.actor;

import com.cinemate.actor.DTOs.ActorRequestDTO;
import com.cinemate.actor.DTOs.ActorResponseDTO;
import com.cinemate.movie.DTOs.MovieResponseDTO;
import com.cinemate.series.DTOs.SeriesResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    /**
     * returns all actors
     * @return list of actors
     */
    @GetMapping
    public ResponseEntity<List<ActorResponseDTO>> getAllActors() {
        List<ActorResponseDTO> actors = actorService.getAllActors();
        return ResponseEntity.ok(actors);
    }

    /**
     * returns the actor with the given id
     * @param id
     * @return the actor
     */
    @GetMapping("{id}")
    public ResponseEntity<ActorResponseDTO> getActorById(@PathVariable String id) {
        Optional<ActorResponseDTO> actor = actorService.getActorById(id);
        return actor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * creates an actor
     * @param actorRequestDTO
     * @return the created actor
     */
    @PostMapping
    public ResponseEntity<ActorResponseDTO> createActor(@RequestBody ActorRequestDTO actorRequestDTO) {
        ActorResponseDTO actorResponseDTO = actorService.createActor(actorRequestDTO);
        return ResponseEntity.ok(actorResponseDTO);
    }

    /**
     * updates the actor with the given id
     * @param id
     * @param updatedActorDTO
     * @return the updated actor
     */
    @PutMapping("{id}")
    public ResponseEntity<ActorResponseDTO> updateActor(@PathVariable String id, @RequestBody ActorRequestDTO updatedActorDTO) {
        Optional<ActorResponseDTO> updatedActor = actorService.updateActor(id, updatedActorDTO);
        return updatedActor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * deletes the actor with the given id
     * @param id
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable String id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * returns the movies of the actor
     * @param actorId
     * @return list of movies
     */
    @GetMapping("{actorId}/movies")
    public ResponseEntity<List<MovieResponseDTO>> getMoviesByActor(@PathVariable String actorId) {
        List<MovieResponseDTO> movies = actorService.getMoviesByActorId(actorId);
        return ResponseEntity.ok(movies);
    }

    /**
     * returns the series of the actor
     * @param actorId
     * @return list of series
     */
    @GetMapping("{actorId}/series")
    public ResponseEntity<List<SeriesResponseDTO>> getSeriesByActor(@PathVariable String actorId) {
        List<SeriesResponseDTO> series = actorService.getSeriesByActorId(actorId);
        return ResponseEntity.ok(series);
    }
}
