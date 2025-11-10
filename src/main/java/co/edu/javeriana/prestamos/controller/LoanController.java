package co.edu.javeriana.prestamos.controller;

import co.edu.javeriana.prestamos.model.Prestamo;
import co.edu.javeriana.prestamos.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // Le dice a Spring que esto es un controlador de API
@RequestMapping("/api") // Todas las rutas empiezan con /api
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // ************ SIMULACIÓN DE G2 (AUTENTICACIÓN) ************
    // En el mundo real, G2 te da esto. Aquí lo forzamos.
    private Integer getUsuarioSimuladoId() {
        // Probemos con el usuario 101 (el que tiene un préstamo vencido)
        // return 101;

        // Probemos con el usuario 102 (el que tiene 3 préstamos)
        // return 102;

        // Probemos con un usuario nuevo sin problemas
        return 101;
    }
    // **********************************************************


    /**
     * API 1: Solicitar Préstamo (US_8)
     */
    @PostMapping("/loans")
    public ResponseEntity<?> solicitarPrestamo(@RequestBody LoanRequest request) {
        try {
            Integer usuarioId = getUsuarioSimuladoId();
            Prestamo nuevoPrestamo = loanService.solicitarPrestamo(usuarioId, request.getLibro_id());

            // Si todo OK, devuelve 200 OK y el JSON de respuesta
            return ResponseEntity.ok(new LoanResponse(nuevoPrestamo));

        } catch (Exception e) {
            // Si tu servicio lanzó un error, devuélvelo
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * API 2: Ver Mis Préstamos (US_22)
     */
    @GetMapping("/loans/my-loans")
    public ResponseEntity<?> getMisPrestamos() {
        try {
            Integer usuarioId = getUsuarioSimuladoId();
            List<Prestamo> prestamos = loanService.getMisPrestamos(usuarioId);

            // Devuelve 200 OK y el JSON de respuesta
            return ResponseEntity.ok(new MyLoansResponse(prestamos));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}