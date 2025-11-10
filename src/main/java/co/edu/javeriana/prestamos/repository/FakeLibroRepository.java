package co.edu.javeriana.prestamos.repository;

import co.edu.javeriana.prestamos.model.Libro;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository // Le dice a Spring que esto maneja datos
public class FakeLibroRepository {
    // Simulación de la tabla 'libro'
    private final List<Libro> libros = new ArrayList<>();

    public FakeLibroRepository() {
        // Datos de prueba
        libros.add(new Libro(1, "Ingeniería de Software", "Ian Sommerville", 3));
        libros.add(new Libro(2, "Clean Code", "Robert C. Martin", 0));
        libros.add(new Libro(3, "El Quijote", "Cervantes", 1));
    }

    public Optional<Libro> findById(Integer id) {
        return libros.stream().filter(l -> l.getId_libro().equals(id)).findFirst();
    }

    public void save(Libro libro) {
        // Simula el guardado (en este caso, actualización)
        libros.stream()
                .filter(l -> l.getId_libro().equals(libro.getId_libro()))
                .findFirst()
                .ifPresent(l -> l.setCantidad_disponible(libro.getCantidad_disponible()));
    }
}