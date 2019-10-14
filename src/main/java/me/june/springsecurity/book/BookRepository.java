package me.june.springsecurity.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 14/10/2019
 * Time: 8:52 오후
 **/
public interface BookRepository extends JpaRepository<Book, Integer> {
    /* Spring Security 는 Query 애노테이션 내부에서 principal을 제공한다.*/
    @Query("select b from Book b where b.author.id = ?#{principal.account.id}")
    List<Book> findCurrentUserBooks();
}
