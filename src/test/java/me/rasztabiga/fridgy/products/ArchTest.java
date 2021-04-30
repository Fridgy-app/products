package me.rasztabiga.fridgy.products;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("me.rasztabiga.fridgy.products");

        noClasses()
            .that()
            .resideInAnyPackage("me.rasztabiga.fridgy.products.service..")
            .or()
            .resideInAnyPackage("me.rasztabiga.fridgy.products.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..me.rasztabiga.fridgy.products.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
