package remotedoorway.byteme.com.activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import remotedoorway.byteme.com.R;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by shsus_000 on 22/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {
    @Mock
    private LoginView view;
    @Mock
    private LoginService service;
    private LoginPresenter presenter;

    @Before
    public void setUp() throws Exception {
        
        presenter = new LoginPresenter(view, service);

    }

    @Test
    public void emptyUserName() throws Exception {
        when(view.getUserName()).thenReturn("");
        presenter.onClick();
        verify(view).showUsernameError(R.string.username_error);
    }

    @Test
    public void emptyPassword() throws Exception {
        when(view.getUserName()).thenReturn("demoName");
        when(view.getPassword()).thenReturn("");
        presenter.onClick();
        verify(view).showPasswordError(R.string.password_error);
    }
}