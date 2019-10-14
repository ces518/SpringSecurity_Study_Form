package me.june.springsecurity.common;

import me.june.springsecurity.account.Account;
import me.june.springsecurity.account.AccountService;
import me.june.springsecurity.book.Book;
import me.june.springsecurity.book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 14/10/2019
 * Time: 8:52 오후
 **/
@Component
public class DefaultDataGenerator implements ApplicationRunner {

    @Autowired
    AccountService accountService;

    @Autowired
    BookRepository bookRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account june = createUser("june");
        Account ces518 = createUser("ces518");

        createBook(june, "JPA");
        createBook(ces518,"Hibernate");
    }

    private Book createBook(Account june, String title) {
        Book book = new Book();
        book.setAuthor(june);
        book.setTitle(title);
        return bookRepository.save(book);
    }

    private Account createUser(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword("1234");
        account.setRole("USER");
        return accountService.createAccount(account);
    }
}
