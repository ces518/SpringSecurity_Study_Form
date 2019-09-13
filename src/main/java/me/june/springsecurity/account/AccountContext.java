package me.june.springsecurity.account;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 13/09/2019
 * Time: 10:47 오후
 **/
public class AccountContext {
    private static final ThreadLocal<Account> ACCOUNT_THREAD_LOCAL = new ThreadLocal<>();

    public static void setAccount (Account account) {
        ACCOUNT_THREAD_LOCAL.set(account);
    }

    public static Account getAccount () {
        return ACCOUNT_THREAD_LOCAL.get();
    }
}
