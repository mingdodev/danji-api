package danji.danjiapi;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = "danji.danjiapi")
public class ArchitectureTest {
    @ArchTest
    void layeredArchitectureTest(JavaClasses classes) {
        Architectures.layeredArchitecture()
                .consideringAllDependencies()
                .layer("presentation").definedBy("..controller..", "..dto..")
                .layer("application").definedBy("..service..")
                .layer("domain").definedBy("..entity..", "..repository..")

                .whereLayer("presentation").mayNotBeAccessedByAnyLayer()
                .whereLayer("application").mayOnlyBeAccessedByLayers("presentation")
                .whereLayer("domain").mayOnlyBeAccessedByLayers("application", "presentation")

                .check(classes);
    }
}

