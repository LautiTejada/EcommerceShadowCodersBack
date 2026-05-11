package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD TRIANGULATE: Unit test for UsuarioService.privilegeLevel pure function.
 * No Spring context — pure logic.
 *
 * Spec: Req — Role Self-Assignment Prevention
 *   Privilege ordering: ADMIN (highest, level 0) > USER (lowest, level 1).
 */
class PrivilegeLevelTest {

    @Test
    void adminHasLowestLevelNumber() {
        // ADMIN = most privileged = lowest number
        int adminLevel = UsuarioService.privilegeLevel(Usuario.Rol.ADMIN);
        assertEquals(0, adminLevel, "ADMIN must be level 0 (highest privilege)");
    }

    @Test
    void userHasHigherLevelNumber() {
        // USER = least privileged = higher number
        int userLevel = UsuarioService.privilegeLevel(Usuario.Rol.USER);
        assertEquals(1, userLevel, "USER must be level 1 (lower privilege than ADMIN)");
    }

    @Test
    void adminHasHigherPrivilegeThanUser() {
        int adminLevel = UsuarioService.privilegeLevel(Usuario.Rol.ADMIN);
        int userLevel = UsuarioService.privilegeLevel(Usuario.Rol.USER);
        assertTrue(adminLevel < userLevel,
                "ADMIN (level " + adminLevel + ") must have lower number than USER (level " + userLevel + ")");
    }
}
