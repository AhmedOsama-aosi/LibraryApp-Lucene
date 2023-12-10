package org.example;


import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
      //  SpringApplication.run(Main.class, args);
        ApplicationContext contexto = new SpringApplicationBuilder(Main.class)
                .headless(false)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
//        System.out.println("Hello world!");
    }
}