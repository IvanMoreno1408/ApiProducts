package com.usta.apiproductos.service;

import com.usta.apiproductos.integration.ImgBBService;
import com.usta.apiproductos.model.Producto;
import com.usta.apiproductos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ProductoService {

    private final ProductoRepository repository;
    private final ImgBBService imgBBService;

    // LISTAR TODOS
    public List<Producto> listar() {
        return repository.findAll();
    }

    // BUSCAR POR ID
    public Producto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // CREAR
    public Producto guardar(Producto producto, String imagenBase64) {

        if(imagenBase64 != null) {
            String url = imgBBService.subirImagen(imagenBase64);
            producto.setImagenUrl(url);
        }

        return repository.save(producto);
    }

    // ACTUALIZAR
    public Producto actualizar(Long id, Producto nuevo) {

        Producto producto = buscarPorId(id);

        producto.setNombre(nuevo.getNombre());
        producto.setDescripcion(nuevo.getDescripcion());
        producto.setTipo(nuevo.getTipo());
        producto.setPrecio(nuevo.getPrecio());
        producto.setActivo(nuevo.getActivo());

        return repository.save(producto);
    }

    // ELIMINAR
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

}
