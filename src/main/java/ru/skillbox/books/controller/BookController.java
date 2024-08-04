package ru.skillbox.books.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.books.dto.BookRequest;
import ru.skillbox.books.dto.BookResponse;
import ru.skillbox.books.mapper.BookMapper;
import ru.skillbox.books.model.Book;
import ru.skillbox.books.service.BookService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final BookMapper bookMapper;

    @GetMapping("/by-title-and-author")
    public ResponseEntity<BookResponse> findByTitleAndAuthor(@RequestParam String title, @RequestParam String author) {
        Book book = bookService.findByTitleAndAuthor(title, author);
        return ResponseEntity.ok(bookMapper.toResponse(book));
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<BookResponse>> findByCategory(@RequestParam String category) {
        List<Book> books = bookService.findByCategory(category);
        return ResponseEntity.ok(books.stream().map(bookMapper::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody BookRequest request) {
        Book book = bookMapper.toEntity(request);
        book = bookService.create(book);
        return ResponseEntity.ok(bookMapper.toResponse(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @RequestBody BookRequest request) {
        Book updatedBook = bookMapper.toEntity(request);
        updatedBook = bookService.update(id, updatedBook);
        return ResponseEntity.ok(bookMapper.toResponse(updatedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFoundException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
