package co.edu.javeriana.prestamos.security;

import co.edu.javeriana.prestamos.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

  private final Usuario usuario;
  private final List<GrantedAuthority> authorities;

  public CustomUserDetails(Usuario usuario) {
    this.usuario = usuario;
    this.authorities = new ArrayList<>();
    
    // Asignar autoridades basadas en el tipo de usuario
    switch (usuario.getId_tipo_usuario()) {
      case 1: // Estudiante
        authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
        break;
      case 2: // Bibliotecario
        authorities.add(new SimpleGrantedAuthority("ROLE_BIBLIOTECARIO"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE")); // También tiene permisos de estudiante
        break;
      case 3: // Admin
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_BIBLIOTECARIO"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE")); // Tiene todos los permisos
        break;
      default:
        authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
    }
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return usuario.getContrasena(); // <-- CORRECCIÓN APLICADA AQUÍ
  }

  @Override
  public String getUsername() {
    return usuario.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // Bloquear si hay más de 5 intentos fallidos
    return usuario.getIntentos_fallidos() < 5;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    // Estado 1 = activo
    return usuario.getId_estado_usuario() != null && usuario.getId_estado_usuario() == 1;
  }
  
  public Usuario getUsuario() {
    return usuario;
  }
  
  public Integer getUserId() {
    return usuario.getId_usuario();
  }
}