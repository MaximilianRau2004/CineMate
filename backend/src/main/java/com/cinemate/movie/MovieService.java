package com.cinemate.movie;

import com.cinemate.actor.Actor;
import com.cinemate.actor.ActorRepository;
import com.cinemate.actor.DTOs.ActorResponseDTO;
import com.cinemate.director.Director;
import com.cinemate.director.DirectorRepository;
import com.cinemate.director.DTOs.DirectorResponseDTO;
import com.cinemate.movie.DTOs.MovieRequestDTO;
import com.cinemate.movie.DTOs.MovieResponseDTO;
import com.cinemate.series.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository,
                        ActorRepository actorRepository,
                        DirectorRepository directorRepository) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
    }

    /**
     * return all movies
     * @return List<MovieResponseDTO>
     */
    public ResponseEntity<List<MovieResponseDTO>> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        List<MovieResponseDTO> movieDTOs = movies.stream()
                .map(MovieResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(movieDTOs);
    }

    /**
     * returns the movie with the given id
     * @param id
     * @return MovieResponseDTO
     */
    public Optional<MovieResponseDTO> getMovieById(String id) {
        return movieRepository.findById(id)
                .map(MovieResponseDTO::new);
    }

    /**
     * creates a movie
     * @param movieDTO
     * @return MovieResponseDTO
     */
    public ResponseEntity<MovieResponseDTO> createMovie(MovieRequestDTO movieDTO) {
        Movie movie = new Movie(movieDTO);

        Movie savedMovie = movieRepository.save(movie);
        return ResponseEntity.ok(new MovieResponseDTO(savedMovie));
    }

    /**
     * updates the movie with the given id
     * @param id
     * @param movieDTO
     * @return MovieResponseDTO
     */
    public ResponseEntity<MovieResponseDTO> updateMovie(String id, MovieRequestDTO movieDTO) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Movie existingMovie = optionalMovie.get();

        if (movieDTO.getTitle() != null) {
            existingMovie.setTitle(movieDTO.getTitle());
        }
        if (movieDTO.getDescription() != null) {
            existingMovie.setDescription(movieDTO.getDescription());
        }
        if (movieDTO.getGenre() != null) {
            existingMovie.setGenre(movieDTO.getGenre());
        }
        if (movieDTO.getReleaseDate() != null) {
            existingMovie.setReleaseDate(movieDTO.getReleaseDate());
        }
        if (movieDTO.getDuration() != null) {
            existingMovie.setDuration(movieDTO.getDuration());
        }
        if (movieDTO.getPosterUrl() != null) {
            existingMovie.setPosterUrl(movieDTO.getPosterUrl());
        }
        if (movieDTO.getCountry() != null) {
            existingMovie.setCountry(movieDTO.getCountry());
        }
        if (movieDTO.getTrailerUrl() != null) {
            existingMovie.setTrailerUrl(movieDTO.getTrailerUrl());
        }

        Movie savedMovie = movieRepository.save(existingMovie);
        return ResponseEntity.ok(new MovieResponseDTO(savedMovie));
    }

    /**
     * deletes the movie with the given id
     * @param id
     */
    public void deleteMovie(String id) {
        movieRepository.deleteById(id);
    }

    /**
     * returns the director of the movie
     * @param movieId
     * @return DirectorResponseDTO
     */
    public Optional<DirectorResponseDTO> getDirectorOfMovie(String movieId) {
        return movieRepository.findById(movieId)
                .map(Movie::getDirector)
                .filter(Objects::nonNull)
                .map(DirectorResponseDTO::new);
    }

    /**
     * returns the actors of the movie
     * @param movieId
     * @return List<ActorResponseDTO>
     */
    public Optional<List<ActorResponseDTO>> getActorsOfMovie(String movieId) {
        return movieRepository.findById(movieId)
                .map(movie -> {
                    List<Actor> actors = movie.getActors();
                    if (actors == null) {
                        return Collections.<ActorResponseDTO>emptyList();
                    }
                    return actors.stream()
                            .filter(Objects::nonNull)
                            .map(ActorResponseDTO::new)
                            .collect(Collectors.toList());
                });
    }

    /**
     * Adds an actor to a movie
     * @param movieId
     * @param actorId
     * @return the added actor
     */
    public ResponseEntity<ActorResponseDTO> addActorToMovie(String movieId, String actorId) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Optional<Actor> optionalActor = actorRepository.findById(actorId);

        if (optionalMovie.isEmpty() || optionalActor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Movie movie = optionalMovie.get();
        Actor actor = optionalActor.get();

        if (movie.getActors() == null) {
            movie.setActors(new ArrayList<>());
        }

        if (!movie.getActors().contains(actor)) {
            movie.getActors().add(actor);

            if (actor.getMovies() == null) {
                actor.setMovies(new ArrayList<>());
            }

            if (!actor.getMovies().contains(movie)) {
                actor.getMovies().add(movie);
            }

            actorRepository.save(actor);
            movieRepository.save(movie);
        }

        ActorResponseDTO addedActor = new ActorResponseDTO(actor);
        return ResponseEntity.ok(addedActor);
    }

    /**
     * Removes an actor from a movie
     * @param movieId
     * @param actorId
     * @return ResponseEntity with list of remaining actors or not found
     */
    public ResponseEntity<Map<String, Object>> removeActorFromMovie(String movieId, String actorId) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Optional<Actor> optionalActor = actorRepository.findById(actorId);

        if (optionalMovie.isEmpty() || optionalActor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Movie movie = optionalMovie.get();
        Actor actor = optionalActor.get();

        boolean removed = false;
        if (movie.getActors() != null) {
            removed = movie.getActors().removeIf(a -> a.getId().equals(actorId));

            if (removed && actor.getSeries() != null) {
                actor.getSeries().removeIf(m -> m.getId().equals(movie.getId()));
                actorRepository.save(actor);
            }

            movieRepository.save(movie);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", removed);
        response.put("message", removed ?
                "Schauspieler erfolgreich von der Serie entfernt" :
                "Schauspieler war nicht mit dieser Serie verknüpft");

        return ResponseEntity.ok(response);
    }

    /**
     * Sets a director to a movie
     * @param movieId
     * @param directorId
     * @return ResponseEntity with updated director or not found
     */
    public ResponseEntity<DirectorResponseDTO> setDirectorToMovie(String movieId, String directorId) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Optional<Director> optionalDirector = directorRepository.findById(directorId);

        if (optionalMovie.isEmpty() || optionalDirector.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Movie movie = optionalMovie.get();
        Director director = optionalDirector.get();

        Director oldDirector = movie.getDirector();
        if (oldDirector != null && oldDirector.getMovies() != null) {
            oldDirector.getMovies().remove(movie);
            directorRepository.save(oldDirector);
        }

        movie.setDirector(director);

        if (director.getMovies() == null) {
            director.setMovies(new ArrayList<>());
        }

        if (!director.getMovies().contains(movie)) {
            director.getMovies().add(movie);
        }

        directorRepository.save(director);
        movieRepository.save(movie);

        return ResponseEntity.ok(new DirectorResponseDTO(director));
    }

    /**
     * Removes the director from a movie
     * @param movieId
     * @return ResponseEntity with null director or not found
     */
    public ResponseEntity<Map<String, Object>> removeDirectorFromMovie(String movieId) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);

        if (optionalMovie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Movie movie = optionalMovie.get();
        Director director = movie.getDirector();

        boolean removed = false;

        if (director != null) {
            movie.setDirector(null);
            removed = true;

            if (director.getMovies() != null) {
                director.getMovies().removeIf(m -> m.getId().equals(movie.getId()));
                directorRepository.save(director);
            }

            movieRepository.save(movie);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", removed);
        response.put("message", removed ?
                "Regisseur erfolgreich vom Film entfernt" :
                "Kein Regisseur mit diesem Film verknüpft");

        return ResponseEntity.ok(response);
    }
}