package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Color;
import com.dresscode.api_dresscode.repositories.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColorService extends BaseServiceImpl<Color, Long> {

    private final ColorRepository colorRepository;

    @Override
    protected JpaRepository<Color, Long> getRepository() {
        return colorRepository;
    }
}
