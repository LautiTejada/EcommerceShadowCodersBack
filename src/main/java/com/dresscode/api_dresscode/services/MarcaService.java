package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Marca;
import com.dresscode.api_dresscode.repositories.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarcaService extends BaseServiceImpl<Marca, Long> {

    private final MarcaRepository marcaRepository;

    @Override
    protected JpaRepository<Marca, Long> getRepository() {
        return marcaRepository;
    }
}
