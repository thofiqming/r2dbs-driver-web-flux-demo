package com.example.reactiverdbms;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.UUID;

@SpringBootApplication
@EnableScheduling
@EnableR2dbcRepositories
@EnableWebFlux
public class ReactiverdbmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiverdbmsApplication.class, args);
    }

}

@Data
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String isbn;

    private String details;
}

@Repository
interface BookRepository extends R2dbcRepository<Book, Long> {

}

@RestController
class BookController {

    private final BookService bookService;

    BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Scheduled(fixedDelay = 1000)
    public void findAll(){
        this.bookService.findAll().log().then().subscribe();
    }
}

@Service
class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Flux<Book> findAll() {
        return this.repository.findAll();
    }

    @Scheduled(fixedDelay = 1000)
    public void processRecords(){
        Book book = new Book();
        book.setIsbn(UUID.randomUUID().toString());
        book.setDetails(UUID.randomUUID().toString());
        this.repository.save(book).then().subscribe();
    }
}
