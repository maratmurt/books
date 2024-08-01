package ru.skillbox.books.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.skillbox.books.model.Book;
import ru.skillbox.books.model.Category;
import ru.skillbox.books.repository.BookRepository;
import ru.skillbox.books.repository.CategoryRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;

    @Cacheable("bookByTitleAndAuthor")
    public Book findByTitleAndAuthor(String title, String author) {
        log.info("findByTitleAndAuthor executed");
        return bookRepository.findByTitleAndAuthor(title, author).orElseThrow(
                () -> new NoSuchElementException(
                        MessageFormat.format("Книга {0} - \"{1}\" не найдена!", author, title)
                )
        );
    }

    @Cacheable("booksByCategory")
    public List<Book> findByCategory(String categoryName) {
        log.info("findByCategory executed");
        Optional<Category> category = categoryRepository.findByName(categoryName);
        if (category.isPresent()) {
            return bookRepository.findByCategory(category.get());
        }
        return List.of();
    }

    @CacheEvict(cacheNames = "booksByCategory", allEntries = true)
    public Book create(Book book) {
        String categoryName = book.getCategory().getName();
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
        book.setCategory(category);
        return bookRepository.save(book);
    }

    @CacheEvict(cacheNames = {"booksByCategory", "bookByTitleAndAuthor"}, allEntries = true)
    public Book update(Long id, Book book) {
        String categoryName = book.getCategory().getName();
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
        book.setCategory(category);
        book.setId(id);
        return bookRepository.save(book);
    }

    @CacheEvict(cacheNames = {"booksByCategory", "bookByTitleAndAuthor"}, allEntries = true)
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

}
