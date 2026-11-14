package com.petshop.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para validar las reglas de la política de contraseñas de Huellitas Shop.
 */
class PasswordPolicyTest {

    @Test
    @DisplayName("Acepta contraseña que cumple con todas las reglas de complejidad")
    void acceptsValidPassword() {
        assertTrue(PasswordPolicy.failedRules("Huellitas#2026x").isEmpty());
    }

    @Test
    @DisplayName("Reporta todas las reglas faltantes cuando la contraseña es muy débil")
    void reportsEveryMissingRule() {
        assertEquals(
                List.of(PasswordRule.LENGTH, PasswordRule.UPPERCASE, PasswordRule.DIGIT, PasswordRule.SPECIAL),
                PasswordPolicy.failedRules("abc"));
    }

    @Test
    @DisplayName("Rechaza contraseñas que exceden la longitud máxima permitida")
    void rejectsTooLong() {
        assertEquals(List.of(PasswordRule.LENGTH), PasswordPolicy.failedRules("Aa1#" + "x".repeat(22)));
    }
}
