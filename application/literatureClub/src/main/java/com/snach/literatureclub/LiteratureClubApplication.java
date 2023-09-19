package com.snach.literatureclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LiteratureClubApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiteratureClubApplication.class, args);
    }

}
