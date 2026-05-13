package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.ImagenProductoDTO;
import com.dresscode.api_dresscode.dtos.ProductoDTO;
import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController extends BaseController<Producto, Long> {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        super(productoService);
        this.productoService = productoService;
    }

    /** Devuelve solo productos activos (uso público / frontend). */
    @Override
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productoService.getProductosActivos());
    }

    /** Devuelve solo productos activos (alias heredado — mismo comportamiento). */
    @Override
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {
        return ResponseEntity.ok(productoService.getProductosActivos());
    }

    /** Alias para compatibilidad con el frontend (/activos → igual que /active). */
    @GetMapping("/activos")
    public ResponseEntity<?> getActivos() {
        return ResponseEntity.ok(productoService.getProductosActivos());
    }

    /**
     * Paged active-only products with optional filters.
     * Overrides BaseController#getAllPaged(Pageable) to:
     *   1. Return ONLY active products (the base returns all, including inactive).
     *   2. Accept sortBy/sortDir params (frontend convention) instead of Spring's sort=field,dir.
     *   3. Accept tipoIds and categoriaIds for view-level filtering (calzados, ropa, etc).
     */
    @Override
    @GetMapping("/paged")
    public ResponseEntity<?> getAllPaged(org.springframework.data.domain.Pageable pageable) throws Exception {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        jakarta.servlet.http.HttpServletRequest request =
                ((org.springframework.web.context.request.ServletRequestAttributes)
                        org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        String sortBy  = request.getParameter("sortBy")  != null ? request.getParameter("sortBy")  : "id";
        String sortDir = request.getParameter("sortDir") != null ? request.getParameter("sortDir") : "asc";

        // Parse optional filter arrays (multi-value params: tipoIds=1&tipoIds=2)
        String[] tipoIdParams  = request.getParameterValues("tipoIds");
        String[] catIdParams   = request.getParameterValues("categoriaIds");
        String[] marcaIdParams = request.getParameterValues("marcaIds");

        List<Long> tipoIds = tipoIdParams != null
                ? java.util.Arrays.stream(tipoIdParams).map(Long::parseLong).collect(java.util.stream.Collectors.toList())
                : null;
        List<Long> categoriaIds = catIdParams != null
                ? java.util.Arrays.stream(catIdParams).map(Long::parseLong).collect(java.util.stream.Collectors.toList())
                : null;
        List<Long> marcaIds = marcaIdParams != null
                ? java.util.Arrays.stream(marcaIdParams).map(Long::parseLong).collect(java.util.stream.Collectors.toList())
                : null;

        Page<Producto> result = productoService.getProductosActivosPagedFiltrados(
                page, size, sortBy, sortDir, tipoIds, categoriaIds, marcaIds);
        return ResponseEntity.ok(result);
    }

    /** Devuelve TODOS los productos (activos + inactivos) — solo para el panel admin. */
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Producto>> getTodosLosProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @PostMapping("/{categoriaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO producto, @PathVariable Long categoriaId){
        Producto nuevoProducto = productoService.createProducto(producto, categoriaId);
        return ResponseEntity.status(201).body(nuevoProducto);
    }


    @PutMapping("/{productoId}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> editarProducto(@PathVariable Long productoId, @RequestBody ProductoDTO producto) {
        Producto actualizado = productoService.updateProducto(productoId, producto);
        return ResponseEntity.ok(actualizado);
    }


    @PutMapping("/{productoId}/cambiar-etado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> cambiarEstadoProducto(@PathVariable Long productoId, @RequestParam Boolean nuevoEstado){
        Producto productoActualizado = productoService.cambiarEstadoProducto(productoId, nuevoEstado);
        return ResponseEntity.ok(productoActualizado);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> traerProductosPorCategoria (@PathVariable Long categoriaId){
        List<Producto> productos = productoService.getProductosByCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/imagen/{productoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImagenProducto> createImagen(@PathVariable Long productoId, @Valid @RequestBody ImagenProductoDTO imagenProducto) {
        ImagenProducto imagen = ImagenProducto.builder()
                .urlImagen(imagenProducto.getUrlImagen())
                // .principal(imagenProducto.getPrincipal()) // Si el método principal no existe, comentar o implementar
                .build();
        ImagenProducto nuevaImagen = productoService.agregarImagenAProducto(productoId, imagen);
        return ResponseEntity.ok(nuevaImagen);
    }

    @PutMapping("/imagen/{imagenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImagenProducto> editarImagen(@PathVariable Long imagenId, @Valid @RequestBody ImagenProductoDTO imagenProductoDTO) {
        ImagenProducto imagenActualizada = productoService.editarImagenProducto(imagenId, imagenProductoDTO);
        return ResponseEntity.ok(imagenActualizada);
    }

    @DeleteMapping("/imagen/{imagenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> eliminarImagen(@PathVariable Long imagenId) {
        Producto producto = productoService.eliminarImagenProducto(imagenId);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/imagen/{productoId}")
    public ResponseEntity<List<ImagenProducto>> getImagenes(@PathVariable Long productoId) {
        List<ImagenProducto> imagenes = productoService.getImagenesByProducto(productoId);
        return ResponseEntity.ok(imagenes);
    }

}
