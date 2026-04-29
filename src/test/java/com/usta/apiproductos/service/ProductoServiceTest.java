package com.usta.apiproductos.service;

import com.usta.apiproductos.model.Producto;
import com.usta.apiproductos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Productos")
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = Producto.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Laptop de alta gama")
                .tipo("Electronica")
                .precio(1500.0)
                .imagenUrl("https://ejemplo.com/imagen.jpg")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe listar todos los productos exitosamente")
    void testListarTodos() {
        Producto producto2 = Producto.builder()
                .id(2L)
                .nombre("Mouse")
                .descripcion("Mouse inalambrico")
                .tipo("Accesorios")
                .precio(25.0)
                .activo(true)
                .build();

        List<Producto> productos = Arrays.asList(producto, producto2);
        when(repository.findAll()).thenReturn(productos);

        List<Producto> resultado = service.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());
        assertEquals("Mouse", resultado.get(1).getNombre());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacia cuando no hay productos")
    void testListarVacio() {
        when(repository.findAll()).thenReturn(Arrays.asList());

        List<Producto> resultado = service.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un producto por ID exitosamente")
    void testBuscarPorIdExitoso() {
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop", resultado.getNombre());
        assertEquals(1500.0, resultado.getPrecio());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el producto no existe")
    void testBuscarPorIdNoEncontrado() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> service.buscarPorId(999L)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(repository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe guardar un producto sin imagen exitosamente")
    void testGuardarSinImagen() {
        Producto nuevoProducto = Producto.builder()
                .nombre("Teclado")
                .descripcion("Teclado mecanico")
                .tipo("Accesorios")
                .precio(85.0)
                .activo(true)
                .build();

        when(repository.save(nuevoProducto)).thenReturn(producto);

        Producto resultado = service.guardar(nuevoProducto);

        assertNotNull(resultado);
        assertEquals("Laptop", resultado.getNombre());
        verify(repository, times(1)).save(nuevoProducto);
    }

    @Test
    @DisplayName("Debe guardar un producto con URL de imagen exitosamente")
    void testGuardarConImagenUrl() {
        Producto productoConImagen = Producto.builder()
                .nombre("Monitor")
                .descripcion("Monitor 4K")
                .tipo("Electronica")
                .precio(350.0)
                .imagenUrl("https://cdn.ejemplo.com/monitor.jpg")
                .activo(true)
                .build();

        when(repository.save(productoConImagen)).thenReturn(productoConImagen);

        Producto resultado = service.guardar(productoConImagen);

        assertNotNull(resultado);
        assertEquals("https://cdn.ejemplo.com/monitor.jpg", resultado.getImagenUrl());
        verify(repository, times(1)).save(productoConImagen);
    }

    @Test
    @DisplayName("Debe guardar un producto con validacion de datos")
    void testGuardarConDatosCompletos() {
        Producto productoCompleto = Producto.builder()
                .nombre("Impresora")
                .descripcion("Impresora multifuncion")
                .tipo("Oficina")
                .precio(200.0)
                .imagenUrl("https://ejemplo.com/impresora.jpg")
                .activo(true)
                .build();

        when(repository.save(productoCompleto)).thenReturn(productoCompleto);

        Producto resultado = service.guardar(productoCompleto);

        assertNotNull(resultado);
        assertNotNull(resultado.getNombre());
        assertNotNull(resultado.getDescripcion());
        assertNotNull(resultado.getPrecio());
        assertTrue(resultado.getActivo());
        assertEquals("https://ejemplo.com/impresora.jpg", resultado.getImagenUrl());
        verify(repository, times(1)).save(productoCompleto);
    }

    @Test
    @DisplayName("Debe actualizar un producto exitosamente")
    void testActualizarExitoso() {
        Producto productoActualizado = Producto.builder()
                .id(1L)
                .nombre("Laptop Pro")
                .descripcion("Laptop de ultima generacion")
                .tipo("Electronica")
                .precio(2000.0)
                .imagenUrl("https://ejemplo.com/imagen.jpg")
                .activo(true)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenReturn(productoActualizado);

        Producto resultado = service.actualizar(1L, productoActualizado);

        assertNotNull(resultado);
        assertEquals("Laptop Pro", resultado.getNombre());
        assertEquals(2000.0, resultado.getPrecio());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al actualizar producto no existente")
    void testActualizarNoEncontrado() {
        Producto productoNuevo = Producto.builder()
                .nombre("Nuevo Producto")
                .precio(100.0)
                .build();

        when(repository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> service.actualizar(999L, productoNuevo)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar tambien la URL de la imagen")
    void testActualizarCamposEspecificos() {
        Producto actualizacion = Producto.builder()
                .nombre("Laptop Actualizada")
                .descripcion("Nueva descripcion")
                .tipo("Tech")
                .precio(1800.0)
                .imagenUrl("https://cdn.ejemplo.com/laptop.jpg")
                .activo(false)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = service.actualizar(1L, actualizacion);

        assertEquals("Laptop Actualizada", resultado.getNombre());
        assertEquals("Nueva descripcion", resultado.getDescripcion());
        assertEquals("Tech", resultado.getTipo());
        assertEquals(1800.0, resultado.getPrecio());
        assertEquals("https://cdn.ejemplo.com/laptop.jpg", resultado.getImagenUrl());
        assertFalse(resultado.getActivo());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe eliminar un producto exitosamente")
    void testEliminarExitoso() {
        service.eliminar(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe poder eliminar un producto que existe")
    void testEliminarProductoExistente() {
        doNothing().when(repository).deleteById(1L);

        service.eliminar(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe permitir eliminar sin validar si existe previamente")
    void testEliminarSinValidacion() {
        service.eliminar(999L);

        verify(repository, times(1)).deleteById(999L);
    }
}
