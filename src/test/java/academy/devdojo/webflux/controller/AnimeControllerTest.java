package academy.devdojo.webflux.controller;

import academy.devdojo.webflux.domain.Anime;
import academy.devdojo.webflux.repository.AnimeRepository;
import academy.devdojo.webflux.service.AnimeService;
import academy.devdojo.webflux.util.AnimeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeService animeServiceMock;

    private final Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }


    @BeforeEach
    public void setUp() {
        BDDMockito.when(animeServiceMock.findAll()).thenReturn(Flux.just(anime));
        BDDMockito.when(animeServiceMock.findById(ArgumentMatchers.anyInt())).thenReturn(Mono.just(anime));
        BDDMockito.when(animeServiceMock.save(AnimeCreator.createAnimeToBeSaved())).thenReturn(Mono.just(anime));
        BDDMockito.when(animeServiceMock.delete(ArgumentMatchers.anyInt())).thenReturn(Mono.empty());
        BDDMockito.when(animeServiceMock.update(AnimeCreator.createValidAnime())).thenReturn(Mono.empty());

    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(100); // Use a longer sleep duration
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("Should fail due to blocking operation");

        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError, "Expected BlockingOperationError, but got: " + e.getCause());
        }
    }

    @Test
    @DisplayName("listAll return Flux of Anime when successful")
    public void listAll_ReturnFluxAnime_WhenSuccessful() {
        StepVerifier.create(animeController.listAll())
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns Mono of Anime when successful")
    public void findById_ReturnMonoAnime_WhenSuccessful() {
        StepVerifier.create(animeController.findById(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns a Mono with Anime when is exist")
    public void findById_removeMonoAnime_WhenSuccessful() {
        StepVerifier.create(animeController.findById(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("save creates an Anime when successful")
    public void save_CreateAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        StepVerifier.create(animeController.save(animeToBeSaved))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes the Anime when successful")
    public void delete_removesAnime_WhenSuccessful() {
        StepVerifier.create(animeController.delete(1))
                .expectSubscription()
                .verifyComplete();
    }



    @Test
    @DisplayName("update save update anime and returns empty Mono when successful")
    public void update_SaveUpdateAnime_WhenSuccessful() {

        StepVerifier.create(animeController.update(1, AnimeCreator.createValidAnime()))
                .expectSubscription()
                .verifyComplete();
    }


}