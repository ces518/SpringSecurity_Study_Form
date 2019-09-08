package me.june.springsecurity.account;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 08/09/2019
 * Time: 3:23 오후
 **/
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByUsername(String username);
}
