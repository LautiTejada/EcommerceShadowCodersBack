package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Color;
import com.dresscode.api_dresscode.entities.Marca;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.repositories.CategoriaRepository;
import com.dresscode.api_dresscode.repositories.ColorRepository;
import com.dresscode.api_dresscode.repositories.ImagenProductoRepository;
import com.dresscode.api_dresscode.repositories.MarcaRepository;
import com.dresscode.api_dresscode.repositories.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private ImagenProductoRepository imagenProductoRepository;
    @Mock
    private ColorRepository colorRepository;
    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private ProductoService productoService;

    public ProductoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProducto() {
        com.dresscode.api_dresscode.dtos.ProductoDTO productoDTO = new com.dresscode.api_dresscode.dtos.ProductoDTO();
        productoDTO.setNombre("Remera");
        productoDTO.setPrecio(100.0);
        productoDTO.setDescripcion("Test");
        productoDTO.setColor("Negro");
        productoDTO.setMarca("Nike");
        // Mock categoría
        com.dresscode.api_dresscode.entities.Categoria categoria = new com.dresscode.api_dresscode.entities.Categoria();
        categoria.setId(1L);
        Color color = new Color();
        Marca marca = new Marca();
        when(categoriaRepository.findById(1L)).thenReturn(java.util.Optional.of(categoria));
        when(colorRepository.findByNombreColor("Negro")).thenReturn(java.util.Optional.of(color));
        when(marcaRepository.findByNombreMarca("Nike")).thenReturn(java.util.Optional.of(marca));
        when(productoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Producto result = productoService.createProducto(productoDTO, 1L);
        assertEquals("Remera", result.getNombre());
    }
}
