package remotedoorway.byteme.com.activity;

/**
 * Created by shsus_000 on 22/03/2017.
 */

interface LoginView {
    String getUserName();

    void showUsernameError(int resId);

    String getPassword();

    void showPasswordError(int resId);
}
