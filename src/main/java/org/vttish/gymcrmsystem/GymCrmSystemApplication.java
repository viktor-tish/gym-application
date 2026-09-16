package org.vttish.gymcrmsystem;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.vttish.gymcrmsystem.config.ApplicationConfig;

public class GymCrmSystemApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        System.out.println("Hello world");

        context.close();
    }
}