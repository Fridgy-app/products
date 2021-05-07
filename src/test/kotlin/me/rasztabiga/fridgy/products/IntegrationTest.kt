package me.rasztabiga.fridgy.products

import me.rasztabiga.fridgy.products.config.TestSecurityConfiguration
import org.springframework.boot.test.context.SpringBootTest

/**
 * Base composite annotation for integration tests.
 */
@kotlin.annotation.Target(AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(classes = [ProductsApp::class, TestSecurityConfiguration::class])
annotation class IntegrationTest
