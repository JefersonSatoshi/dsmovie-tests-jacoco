package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private ScoreRepository repository;
	
	@Mock
	private UserService userService;
	
	@Mock
	private MovieRepository movieRepository;
	
	private ScoreEntity score;
	private ScoreDTO dto;
	private UserEntity user;
	private MovieEntity movie;
	
	@BeforeEach
	private void setUp() throws Exception {
		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();
		score = ScoreFactory.createScoreEntity();
		dto = ScoreFactory.createScoreDTO();
		
		
		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.save(any())).thenReturn(movie);
		Mockito.when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
		    ScoreEntity s = invocation.getArgument(0);
		    s.getId().getMovie().getScores().add(s);
		    return s;
		});
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		
		MovieDTO result = service.saveScore(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test Movie", result.getTitle());
        Assertions.assertEquals(4.5, result.getScore());
        Assertions.assertEquals(1, result.getCount());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		
		ScoreDTO invalidDto = new ScoreDTO(2L, 4.0);
	    
	    Mockito.when(movieRepository.findById(2L)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(invalidDto);
		});
	}
}
