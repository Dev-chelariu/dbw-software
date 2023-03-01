package com.example.software.views.user;

import com.example.software.data.entity.User;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;

public class AvatarRenderer extends ComponentRenderer<VerticalLayout, User> {

    @Override
    public VerticalLayout createComponent(User user) {
        StreamResource resource = new StreamResource(user.getUsername() + ".jpg",
                () -> new ByteArrayInputStream(user.getProfilePicture()));

        Image image = new Image(resource, "User avatar");
        image.setAlt(user.getName());
        image.setClassName("avatar-image");
        image.setWidth("10");
        image.setHeight("20");

        VerticalLayout layout = new VerticalLayout(image);
        layout.setClassName("avatar-layout");

        return layout;
    }
}
