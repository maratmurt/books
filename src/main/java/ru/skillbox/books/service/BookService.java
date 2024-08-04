package ru.skillbox.books.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
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

    private final RedisTemplate<String, Object> redis;

    @Cacheable(value = "bookByTitleAndAuthor", key = "#title + #author")
    public Book findByTitleAndAuthor(String title, String author) {
        log.info("findByTitleAndAuthor executed");

        return bookRepository.findByTitleAndAuthor(title, author).stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        MessageFormat.format("Книга {0} - \"{1}\" не найдена!", author, title)));
    }

    @Cacheable(value = "booksByCategory", key = "#categoryName")
    public List<Book> findByCategory(String categoryName) {
        log.info("findByCategory executed");

        Optional<Category> category = categoryRepository.findByName(categoryName);
        if (category.isPresent()) {
            return bookRepository.findByCategory(category.get());
        }

        return List.of();
    }

    @CacheEvict(value = "booksByCategory", key = "#book.category.name")
    public Book create(Book book) {
        log.debug("create executed");

        String categoryName = book.getCategory().getName();
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
        book.setCategory(category);

        return bookRepository.save(book);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "bookByTitleAndAuthor", key = "#existingBook.title + #existingBook.author", beforeInvocation = true),
                    @CacheEvict(value = "booksByCategory", key = "#existingBook.category.name"),
                    @CacheEvict(value = "booksByCategory", key = "#updatedBook.category.name")
            }
    )
    public Book update(Book existingBook, Book updatedBook) {
        log.debug("update executed");

        String categoryName = updatedBook.getCategory().getName();
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
        updatedBook.setCategory(category);
        updatedBook.setId(existingBook.getId());

        return bookRepository.save(updatedBook);
    }

    @Caching(evict = {
            @CacheEvict(value = "bookByTitleAndAuthor", key = "#book.title + #book.author"),
            @CacheEvict(value = "booksByCategory", key = "#book.category.name")
    })
    public void delete(Book book) {
        log.debug("delete executed");

        bookRepository.delete(book);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                MessageFormat.format("Книга с ID {0} не найдена!", id)));
    }

}
