package com.example.software.views.login;

import com.example.software.security.AuthenticatedUser;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Value;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    LoginForm loginForm;
    private Button googleLoginButton;
    private Checkbox checkbox;

    private static final String OAUTH_URL = "/oauth2/authorization/google";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleAppKey;

    @Value ("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleAppSecret;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        loginForm = new LoginForm ();
        LoginI18n i18n = LoginI18n.createDefault ();
        i18n.getForm ()
                .setTitle ("DBW SOFTWARE");
        loginForm.setForgotPasswordButtonVisible (false);
        i18n.setAdditionalInformation ("Please fill the form or just click on Google Auth for login. :)");
        loginForm.setAction (RouteUtil.getRoutePath (VaadinService.getCurrent ()
                .getContext (), getClass ()));
        loginForm.setI18n (i18n);
        add (loginForm);

        setPadding (true);
        setAlignItems (FlexComponent.Alignment.CENTER);
        setSizeFull ();
        setHeightFull ();
        getStyle ().set ("display", "flex")
                .set ("justify-content", "center")
                .set ("padding", "var(--lumo-space-l)");
        loginForm.getElement ()
                .setAttribute ("no-autofocus", "");


        // Initialize the Google OAuth login button
        googleLoginButton = new Button("Login with Google");
        googleLoginButton.addClickListener(this::onGoogleLogin);
        googleLoginButton.setVisible(false); // Hide the button initially
        add(googleLoginButton);

        // Initialize the toggle button
        checkbox = new Checkbox ("Use Google login");
        checkbox.addValueChangeListener (this::onToggle);
        add (checkbox);

        setAlignItems (FlexComponent.Alignment.CENTER);
        setJustifyContentMode (FlexComponent.JustifyContentMode.CENTER);
        setWidthFull ();
        setHeightFull ();
    }

    private void onGoogleLogin(ClickEvent<Button> event) {

        googleLoginButton.addClickListener(e -> {
            if(googleAppKey ==null || googleAppKey.isEmpty ()) {
                Paragraph text = new Paragraph("Could not find OAuth client key in application.properties. "
                        + "Please double-check the key and refer to the README.md file for instructions.");
                text.getStyle().set("padding-top", "100px");
                add(text);
            } else {
                UI.getCurrent ()
                        .getPage ()
                        .setLocation (OAUTH_URL);
                Notification.show ("Please, wait!");
                add (checkbox);
            }
        });
    }

    private void onToggle(HasValue.ValueChangeEvent<Boolean> event) {
        boolean isToggled = event.getValue ();
        if (isToggled) {
            // Show the Google OAuth login button
            googleLoginButton.setVisible (true);
            loginForm.setVisible (false);
        } else {
            // Show the fill form login
            loginForm.setVisible (true);
            googleLoginButton.setVisible (false);
        }
    }

    @Override
    public void beforeEnter (BeforeEnterEvent event){
        if (authenticatedUser.get ()
                .isPresent ()) {
            // Already logged in
            event.forwardTo ("about");
        }
        loginForm.setError (event.getLocation ()
                .getQueryParameters ()
                .getParameters ()
                .containsKey ("error"));
    }
}
