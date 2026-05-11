package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD RED: Role-assignment service must reject privilege escalation.
 *
 * Spec: Req — Role Self-Assignment Prevention
 *   Scenario: User attempts to self-assign ADMIN role → SecurityException (403)
 *   Scenario: Admin assigns a lower role to another user → succeeds
 *   Scenario: Admin attempts to assign ADMIN to another user → SecurityException (403)
 *
 * Privilege ordering: ADMIN (level 0, highest) > USER (level 1, lowest).
 * A caller MAY only assign roles with a HIGHER level number (lower privilege).
 */
class RoleAssignmentServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void userAttemptsSelfAssignAdmin_throwsSecurityException() {
        // Given: a USER caller tries to assign themselves ADMIN
        Usuario caller = buildUser(1L, Usuario.Rol.USER);
        Usuario target = buildUser(1L, Usuario.Rol.USER); // same user

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(target));

        // When / Then
        assertThrows(SecurityException.class,
                () -> usuarioService.assignRole(1L, Usuario.Rol.ADMIN, caller),
                "A USER must not be able to assign ADMIN to themselves");
    }

    @Test
    void adminAssignsLowerRoleToOtherUser_succeeds() {
        // Given: an ADMIN caller assigns USER to a different user
        Usuario caller = buildUser(99L, Usuario.Rol.ADMIN);
        Usuario target = buildUser(2L, Usuario.Rol.USER);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(target));
        when(usuarioRepository.save(any())).thenReturn(target);

        // When
        usuarioService.assignRole(2L, Usuario.Rol.USER, caller);

        // Then: save was called (role assignment persisted)
        verify(usuarioRepository, times(1)).save(target);
    }

    @Test
    void adminAttemptsToAssignAdminToOtherUser_throwsSecurityException() {
        // Given: an ADMIN caller tries to escalate another user to ADMIN
        Usuario caller = buildUser(99L, Usuario.Rol.ADMIN);
        Usuario target = buildUser(2L, Usuario.Rol.USER);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(target));

        // When / Then
        assertThrows(SecurityException.class,
                () -> usuarioService.assignRole(2L, Usuario.Rol.ADMIN, caller),
                "An ADMIN must not be able to assign ADMIN to another user (no lateral escalation)");
    }

    private Usuario buildUser(Long id, Usuario.Rol rol) {
        Usuario u = Usuario.builder()
                .username("user-" + id)
                .email("user" + id + "@test.com")
                .password("encoded-pass")
                .rol(rol)
                .build();
        // Manually set ID since Lombok builder doesn't set inherited Base.id
        u.setId(id);
        return u;
    }
}
