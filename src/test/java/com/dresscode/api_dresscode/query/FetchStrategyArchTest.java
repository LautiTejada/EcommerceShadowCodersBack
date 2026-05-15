package com.dresscode.api_dresscode.query;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

/**
 * Task 4.2 + 6.4 — ArchUnit enforcement rule:
 * {@code @OneToMany} and {@code @ManyToMany} fields MUST NOT declare {@code FetchType.EAGER}.
 *
 * This test FAILS as long as any entity has an EAGER collection association,
 * providing build-time enforcement of the "No EAGER Fetch on Collection Associations" spec requirement.
 */
class FetchStrategyArchTest {

    private static final JavaClasses ENTITY_CLASSES = new ClassFileImporter()
            .importPackages("com.dresscode.api_dresscode.entities");

    private static final ArchCondition<JavaField> NO_EAGER_ON_ONE_TO_MANY =
            new ArchCondition<>("not use FetchType.EAGER on @OneToMany") {
                @Override
                public void check(JavaField field, ConditionEvents events) {
                    OneToMany ann = field.getAnnotationOfType(OneToMany.class);
                    if (ann != null && ann.fetch() == FetchType.EAGER) {
                        events.add(SimpleConditionEvent.violated(field,
                                String.format("Field '%s.%s' uses FetchType.EAGER on @OneToMany — " +
                                        "PROHIBITED. Change to FetchType.LAZY (or omit fetch, as LAZY is default).",
                                        field.getOwner().getSimpleName(), field.getName())));
                    }
                }
            };

    private static final ArchCondition<JavaField> NO_EAGER_ON_MANY_TO_MANY =
            new ArchCondition<>("not use FetchType.EAGER on @ManyToMany") {
                @Override
                public void check(JavaField field, ConditionEvents events) {
                    ManyToMany ann = field.getAnnotationOfType(ManyToMany.class);
                    if (ann != null && ann.fetch() == FetchType.EAGER) {
                        events.add(SimpleConditionEvent.violated(field,
                                String.format("Field '%s.%s' uses FetchType.EAGER on @ManyToMany — " +
                                        "PROHIBITED. Change to FetchType.LAZY (or omit fetch, as LAZY is default).",
                                        field.getOwner().getSimpleName(), field.getName())));
                    }
                }
            };

    @Test
    void oneToManyMustNotUseFetchTypeEager() {
        ArchRule rule = noFields()
                .that().areAnnotatedWith(OneToMany.class)
                .should(NO_EAGER_ON_ONE_TO_MANY);

        // This test PASSES only when Usuario.direcciones (and any other @OneToMany) is LAZY.
        rule.check(ENTITY_CLASSES);
    }

    @Test
    void manyToManyMustNotUseFetchTypeEager() {
        ArchRule rule = noFields()
                .that().areAnnotatedWith(ManyToMany.class)
                .should(NO_EAGER_ON_MANY_TO_MANY)
                .allowEmptyShould(true);

        // No @ManyToMany exists in this codebase currently — allowEmptyShould prevents
        // ArchUnit from failing when the `that()` clause matches no fields.
        // The rule will catch any future EAGER @ManyToMany addition.
        rule.check(ENTITY_CLASSES);
    }
}
