package com.usta.apiproductos.controller;

import com.usta.apiproductos.model.Producto;
import com.usta.apiproductos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Controlador de Productos")
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    @InjectMocks
    private ProductoController controller;

    private Producto producto;
    private Producto producto2;

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

        producto2 = Producto.builder()
                .id(2L)
                .nombre("Mouse")
                .descripcion("Mouse inalambrico")
                .tipo("Accesorios")
                .precio(25.0)
                .imagenUrl("https://ejemplo.com/mouse.jpg")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Listar - Debe retornar lista de todos los productos")
    void testListarProductos() {
        List<Producto> productos = Arrays.asList(producto, producto2);
        when(service.listar()).thenReturn(productos);

        List<Producto> resultado = controller.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());
        assertEquals("Mouse", resultado.get(1).getNombre());
        verify(service, times(1)).listar();
    }

    @Test
    @DisplayName("Listar - Debe retornar lista vacia cuando no hay productos")
    void testListarProductosVacio() {
        when(service.listar()).thenReturn(Arrays.asList());

        List<Producto> resultado = controller.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(service, times(1)).listar();
    }

    @Test
    @DisplayName("Obtener por ID - Debe retornar un producto por ID")
    void testObtenerProductoPorId() {
        when(service.buscarPorId(1L)).thenReturn(producto);

        Producto resultado = controller.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop", resultado.getNombre());
        assertEquals("Laptop de alta gama", resultado.getDescripcion());
        assertEquals("Electronica", resultado.getTipo());
        assertEquals(1500.0, resultado.getPrecio());
        assertTrue(resultado.getActivo());
        verify(service, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Obtener por ID - Debe lanzar excepcion cuando el producto no existe")
    void testObtenerProductoPorIdNoEncontrado() {
        when(service.buscarPorId(999L))
                .thenThrow(new RuntimeException("Producto no encontrado"));

        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> controller.obtenerPorId(999L)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(service, times(1)).buscarPorId(999L);
    }

    @Test
    @DisplayName("Guardar - Debe crear un nuevo producto sin imagen")
    void testCrearProducto() {
        Producto nuevoProducto = Producto.builder()
                .nombre("Monitor")
                .descripcion("Monitor 4K")
                .tipo("Electronica")
                .precio(350.0)
                .activo(true)
                .build();

        Producto productoCreado = Producto.builder()
                .id(3L)
                .nombre("Monitor")
                .descripcion("Monitor 4K")
                .tipo("Electronica")
                .precio(350.0)
                .imagenUrl("https://ejemplo.com/monitor.jpg")
                .activo(true)
                .build();

        when(service.guardar(any(Producto.class))).thenReturn(productoCreado);

        Producto resultado = controller.guardar(nuevoProducto);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Monitor", resultado.getNombre());
        assertEquals(350.0, resultado.getPrecio());
        verify(service, times(1)).guardar(any(Producto.class));
    }

    @Test
    @DisplayName("Guardar - Debe crear un producto con URL de imagen")
    void testCrearProductoConImagenUrl() {
        Producto nuevoProducto = Producto.builder()
                .nombre("Teclado")
                .descripcion("Teclado mecanico")
                .tipo("Accesorios")
                .precio(85.0)
                .imagenUrl("https://cdn.ejemplo.com/teclado123.jpg")
                .activo(true)
                .build();

        Producto productoConImagen = Producto.builder()
                .id(4L)
                .nombre("Teclado")
                .descripcion("Teclado mecanico")
                .tipo("Accesorios")
                .precio(85.0)
                .imagenUrl("https://cdn.ejemplo.com/teclado123.jpg")
                .activo(true)
                .build();

        when(service.guardar(any(Producto.class))).thenReturn(productoConImagen);

        Producto resultado = controller.guardar(nuevoProducto);

        assertNotNull(resultado);
        assertEquals(4L, resultado.getId());
        assertEquals("https://cdn.ejemplo.com/teclado123.jpg", resultado.getImagenUrl());
        verify(service, times(1)).guardar(any(Producto.class));
    }

    @Test
    @DisplayName("Actualizar - Debe actualizar un producto existente")
    void testActualizarProducto() {
        Producto productoActualizado = Producto.builder()
                .id(1L)
                .nombre("Laptop Pro")
                .descripcion("Laptop de ultima generacion")
                .tipo("Electronica")
                .precio(2000.0)
                .imagenUrl("https://ejemplo.com/imagen.jpg")
                .activo(true)
                .build();

        when(service.actualizar(1L, productoActualizado))
                .thenReturn(productoActualizado);

        Producto resultado = controller.actualizar(1L, productoActualizado);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop Pro", resultado.getNombre());
        assertEquals(2000.0, resultado.getPrecio());
        verify(service, times(1)).actualizar(1L, productoActualizado);
    }

    @Test
    @DisplayName("Actualizar - Debe lanzar excepcion si el producto no existe")
    void testActualizarProductoNoEncontrado() {
        Producto productoNuevo = Producto.builder()
                .nombre("Nuevo")
                .precio(100.0)
                .build();

        when(service.actualizar(999L, productoNuevo))
                .thenThrow(new RuntimeException("Producto no encontrado"));

        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> controller.actualizar(999L, productoNuevo)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(service, times(1)).actualizar(999L, productoNuevo);
    }

    @Test
    @DisplayName("Actualizar - Debe actualizar tambien la URL de imagen")
    void testActualizarProductoCamposEspecificos() {
        Producto actualizacion = Producto.builder()
                .nombre("Laptop Actualizada")
                .descripcion("Nueva descripcion")
                .tipo("Tech")
                .precio(1800.0)
                .imagenUrl("https://cdn.ejemplo.com/laptop.jpg")
                .activo(false)
                .build();

        Producto resultado = Producto.builder()
                .id(1L)
                .nombre("Laptop Actualizada")
                .descripcion("Nueva descripcion")
                .tipo("Tech")
                .precio(1800.0)
                .imagenUrl("https://cdn.ejemplo.com/laptop.jpg")
                .activo(false)
                .build();

        when(service.actualizar(1L, actualizacion))
                .thenReturn(resultado);

        Producto respuesta = controller.actualizar(1L, actualizacion);

        assertEquals("Laptop Actualizada", respuesta.getNombre());
        assertEquals(1800.0, respuesta.getPrecio());
        assertEquals("https://cdn.ejemplo.com/laptop.jpg", respuesta.getImagenUrl());
        assertFalse(respuesta.getActivo());
        verify(service, times(1)).actualizar(1L, actualizacion);
    }

    @Test
    @DisplayName("Eliminar - Debe eliminar un producto")
    void testEliminarProducto() {
        doNothing().when(service).eliminar(1L);

        String resultado = controller.eliminar(1L);

        assertEquals("Producto eliminado correctamente", resultado);
        verify(service, times(1)).eliminar(1L);
    }

    @Test
    @DisplayName("Eliminar - Debe permitir eliminar un producto que no existe")
    void testEliminarProductoNoExistente() {
        doNothing().when(service).eliminar(999L);

        String resultado = controller.eliminar(999L);

        assertEquals("Producto eliminado correctamente", resultado);
        verify(service, times(1)).eliminar(999L);
    }

    @Test
    @DisplayName("Eliminar - Debe retornar mensaje de exito")
    void testEliminarProductoMensaje() {
        doNothing().when(service).eliminar(anyLong());

        String resultado = controller.eliminar(1L);

        assertNotNull(resultado);
        assertTrue(resultado.contains("eliminado"));
        assertTrue(resultado.contains("correctamente"));
    }

    @Test
    @DisplayName("Flujo CRUD: Crear, Leer, Actualizar, Eliminar")
    void testFlujoCRUD() {
        Producto nuevo = Producto.builder()
                .nombre("Producto CRUD")
                .descripcion("Test CRUD")
                .tipo("Test")
                .precio(99.99)
                .imagenUrl("https://cdn.ejemplo.com/crud.jpg")
                .activo(true)
                .build();

        Producto creado = Producto.builder()
                .id(100L)
                .nombre("Producto CRUD")
                .descripcion("Test CRUD")
                .tipo("Test")
                .precio(99.99)
                .imagenUrl("https://cdn.ejemplo.com/crud.jpg")
                .activo(true)
                .build();

        when(service.guardar(any(Producto.class))).thenReturn(creado);

        Producto resultadoCrear = controller.guardar(nuevo);
        assertNotNull(resultadoCrear);
        assertEquals(100L, resultadoCrear.getId());

        when(service.buscarPorId(100L)).thenReturn(creado);

        Producto resultadoLeer = controller.obtenerPorId(100L);
        assertNotNull(resultadoLeer);
        assertEquals("Producto CRUD", resultadoLeer.getNombre());

        Producto actualizado = Producto.builder()
                .id(100L)
                .nombre("Producto CRUD Actualizado")
                .descripcion("Test CRUD Actualizado")
                .tipo("Test")
                .precio(149.99)
                .imagenUrl("https://cdn.ejemplo.com/crud-actualizado.jpg")
                .activo(true)
                .build();

        when(service.actualizar(eq(100L), any(Producto.class))).thenReturn(actualizado);

        Producto resultadoActualizar = controller.actualizar(100L, actualizado);
        assertEquals("Producto CRUD Actualizado", resultadoActualizar.getNombre());

        doNothing().when(service).eliminar(100L);

        String resultadoEliminar = controller.eliminar(100L);
        assertTrue(resultadoEliminar.contains("eliminado"));

        verify(service, times(1)).guardar(any(Producto.class));
        verify(service, times(1)).buscarPorId(100L);
        verify(service, times(1)).actualizar(eq(100L), any(Producto.class));
        verify(service, times(1)).eliminar(100L);
    }
}
