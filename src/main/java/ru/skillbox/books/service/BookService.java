package ru.skillbox.books.service;

import lombok.RequiredArgsConstructor;
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
public class BookService {

    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;

    public Book findByTitleAndAuthor(String title, String author) {
        return bookRepository.findByTitleAndAuthor(title, author).orElseThrow(
                () -> new NoSuchElementException(
                        MessageFormat.format("Книга {0} - \"{1}\" не найдена!", author, title)
                )
        );
    }

    public List<Book> findByCategory(String categoryName) {
        Optional<Category> category = categoryRepository.findByName(categoryName);
        if (category.isPresent()) {
            return bookRepository.findByCategory(category.get());
        }
        return List.of();
    }

    public Book create(Book book) {
        String categoryName = book.getCategory().getName();
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
        book.setCategory(category);
        return bookRepository.save(book);
    }

    public Book update(Long id, Book book) {
        String categoryName = book.getCategory().getName();
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
        book.setCategory(category);
        book.setId(id);
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

}
