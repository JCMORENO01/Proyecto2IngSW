package co.edu.javeriana.prestamos.service;

import co.edu.javeriana.prestamos.model.Libro;
import co.edu.javeriana.prestamos.model.Prestamo;
import co.edu.javeriana.prestamos.repository.FakeLibroRepository;
import co.edu.javeriana.prestamos.repository.FakePrestamoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Le dice a Spring que esto es la lógica de negocio
public class LoanService {

    // Constantes de G1 para estados de préstamo
    public static final int ESTADO_SOLICITADO = 1;
    public static final int ESTADO_ACTIVO = 2;
    public static final int ESTADO_DEVUELTO = 3;
    public static final int ESTADO_VENCIDO = 4;

    private final FakeLibroRepository libroRepository;
    private final FakePrestamoRepository prestamoRepository;

    // Inyección de dependencias (Spring te pasa los repositorios falsos)
    public LoanService(FakeLibroRepository libroRepository, FakePrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    // Esta es tu LÓGICA DE NEGOCIO (US_8)
    public Prestamo solicitarPrestamo(Integer usuarioId, Integer libroId) throws Exception {

        // 1. Validar que el libro exista y esté disponible
        Optional<Libro> libroOpt = libroRepository.findById(libroId);
        if (libroOpt.isEmpty()) {
            throw new Exception("Error 404: Libro no encontrado");
        }
        Libro libro = libroOpt.get();
        if (libro.getCantidad_disponible() <= 0) {
            throw new Exception("Error 400: Libro no disponible");
        }

        // 2. Validar que el usuario pueda pedir prestado
        List<Prestamo> prestamosDelUsuario = prestamoRepository.findActivosByUsuarioId(usuarioId);

        // 3. Regla US_22 (Must Have): Validar préstamos vencidos
        boolean tieneVencidos = prestamosDelUsuario.stream()
                .anyMatch(p -> p.getId_estado_prestamo() == ESTADO_VENCIDO);
        if (tieneVencidos) {
            throw new Exception("Error 400: El usuario tiene préstamos vencidos");
        }

        // 4. Regla US_8: Límite de 3 préstamos
        long prestamosActivos = prestamosDelUsuario.stream()
                .filter(p -> p.getId_estado_prestamo() == ESTADO_ACTIVO)
                .count();
        if (prestamosActivos >= 3) {
            throw new Exception("Error 400: Límite de 3 préstamos alcanzado");
        }

        // 5. ¡Todo en orden! Crear el préstamo
        Prestamo nuevoPrestamo = new Prestamo(0, usuarioId, libroId, ESTADO_SOLICITADO);

        // 6. Actualizar la BD (la falsa)
        libro.setCantidad_disponible(libro.getCantidad_disponible() - 1);
        libroRepository.save(libro);

        return prestamoRepository.save(nuevoPrestamo);
    }

    // Esta es tu LÓGICA DE NEGOCIO (US_22)
    public List<Prestamo> getMisPrestamos(Integer usuarioId) {
        return prestamoRepository.findActivosByUsuarioId(usuarioId);
    }
}