package ru.skillbox.books.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.books.dto.BookRequest;
import ru.skillbox.books.dto.BookResponse;
import ru.skillbox.books.model.Book;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    @Mapping(target = "category.name", source = "category")
    Book toEntity(BookRequest bookRequest);

    BookResponse toResponse(Book book);

}
