package com.college;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)//עדיפות צד שרת בשפות אחרות ולא ספרינגבוט, ספרינגבוט בעיקר מעכב פיתוח. ספרינגבוט טוב בליירט ג'ייסונים לפני שמגיש לשרת, אפשר לתפוס ולשנות דברים
@EnableScheduling
public class Main {
    public static boolean applicationStarted = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static long startTime;

    public static void main(MysqlxDatatypes.Scalar.String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        LOGGER.info("Application started.");
        applicationStarted = true;
        startTime = System.currentTimeMillis();

    }

}
