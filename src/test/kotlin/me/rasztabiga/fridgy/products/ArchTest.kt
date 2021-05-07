package me.rasztabiga.fridgy.products

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("me.rasztabiga.fridgy.products")

        noClasses()
            .that()
            .resideInAnyPackage("me.rasztabiga.fridgy.products.service..")
            .or()
            .resideInAnyPackage("me.rasztabiga.fridgy.products.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..me.rasztabiga.fridgy.products.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }
}
