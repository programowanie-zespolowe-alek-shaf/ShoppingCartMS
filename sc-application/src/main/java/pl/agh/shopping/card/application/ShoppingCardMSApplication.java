package pl.agh.shopping.card.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "pl.agh.shopping.card"
        }
)
@EntityScan("pl.agh.shopping.card")
@EnableJpaRepositories("pl.agh.shopping.card")
@EnableEurekaClient
public class ShoppingCardMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCardMSApplication.class, args);
    }

}


