package co.edu.javeriana.prestamos.repository;

import co.edu.javeriana.prestamos.model.Prestamo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FakePrestamoRepository {
    // Simulación de la tabla 'prestamo'
    private final List<Prestamo> prestamos = new ArrayList<>();
    private int idCounter = 100; // Simula el autoincremento

    public FakePrestamoRepository() {
        // Datos de prueba: Usuario 101 tiene un préstamo vencido
        prestamos.add(new Prestamo(98, 101, 99, 4)); // 4 = "VENCIDO"
        // Usuario 102 tiene 3 préstamos activos
        prestamos.add(new Prestamo(99, 102, 98, 2)); // 2 = "ACTIVO"
        prestamos.add(new Prestamo(100, 102, 97, 2));
        prestamos.add(new Prestamo(101, 102, 96, 2));
    }

    public List<Prestamo> findActivosByUsuarioId(Integer usuarioId) {
        return prestamos.stream()
                .filter(p -> p.getId_usuario().equals(usuarioId))
                .collect(Collectors.toList());
    }

    public Prestamo save(Prestamo prestamo) {
        // Simula la creación de un nuevo préstamo
        idCounter++;
        prestamo.setId_prestamo(idCounter);
        prestamos.add(prestamo);
        return prestamo;
    }
}