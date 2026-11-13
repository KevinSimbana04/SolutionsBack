package com.int_solutions.sys.service.userService;

public interface EmailService {
    void enviarActivacion(String destinatario, String nombreEpleado, String userName, String password, String qtokenActivacion, String tipoUsuario);
    void enviarActivacionBrevo(String destinatario, String nombreEpleado, String userName, String password, String qtokenActivacion, String tipoUsuario);
}

