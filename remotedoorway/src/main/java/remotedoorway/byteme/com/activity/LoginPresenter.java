package remotedoorway.byteme.com.activity;

import remotedoorway.byteme.com.R;

/**
 * Created by shsus_000 on 22/03/2017.
 */

public class LoginPresenter {
    private LoginService service;
    private LoginView view;

    public LoginPresenter(LoginView view, LoginService service){
        this.view = view;
        this.service = service;
    }

    public void onClick() {
        String username = view.getUserName();
        if(username.isEmpty()){
            view.showUsernameError(R.string.username_error);
            return;
        }

        String password = view.getPassword();
        if(password.isEmpty()){
            view.showPasswordError(R.string.password_error);
            return;
        }

    }
}
