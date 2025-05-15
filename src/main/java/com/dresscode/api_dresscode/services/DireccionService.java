package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class DireccionService {

    private final DireccionRepository direccionRepository;

    public List<Direccion> gertAllDirecciones() {
        return direccionRepository.findAll();
    }

    public Direccion getDireccionById(Long id) {
        return direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Direccion no encontrada: "+ id));
    }

    @Transactional
    public Direccion editarDireccion(Long direccionId, Direccion datosActualizados) {
        Direccion direccionExistente = getDireccionById(direccionId);

        direccionExistente.setCalle(datosActualizados.getCalle());
        direccionExistente.setNumero(datosActualizados.getNumero());
        direccionExistente.setCodigoPostal(datosActualizados.getCodigoPostal());
        direccionExistente.setLocalidad(datosActualizados.getLocalidad());
        direccionExistente.setProvincia(datosActualizados.getProvincia());
        direccionExistente.setPais(datosActualizados.getPais());

        return direccionRepository.save(direccionExistente);
    }

    @Transactional
    public void eliminarDireccion(Long direccionId) {
        Direccion direccionExistente = getDireccionById(direccionId);

        direccionRepository.delete(direccionExistente);
    }
}
