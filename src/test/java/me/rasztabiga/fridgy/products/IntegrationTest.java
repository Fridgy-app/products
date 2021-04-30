package me.rasztabiga.fridgy.products;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.rasztabiga.fridgy.products.ProductsApp;
import me.rasztabiga.fridgy.products.config.TestSecurityConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { ProductsApp.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
