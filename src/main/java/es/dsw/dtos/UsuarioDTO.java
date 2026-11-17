package es.dsw.dtos;

/** DTO de usuario para la API REST (sin contraseña). */
public record UsuarioDTO(int id, String nombre, String email, String rol) {
}
