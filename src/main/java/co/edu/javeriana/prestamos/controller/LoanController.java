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

    private Integer getUsuarioSimuladoId() {
        return 101;
    }


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
            // Si lanza un error, devuelve
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
