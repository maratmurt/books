package ru.skillbox.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.books.model.Book;
import ru.skillbox.books.model.Category;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleAndAuthor(String title, String author);

    List<Book> findByCategory(Category category);
}
