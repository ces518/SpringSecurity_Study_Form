package me.june.springsecurity.book;

import lombok.Getter;
import lombok.Setter;
import me.june.springsecurity.account.Account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 14/10/2019
 * Time: 8:51 오후
 **/
@Entity
@Getter @Setter
public class Book {

    @Id @GeneratedValue
    private Integer id;

    private String title;

    /* 1:N 관계로 Account를 참조한다. */
    @ManyToOne
    private Account author;
}
