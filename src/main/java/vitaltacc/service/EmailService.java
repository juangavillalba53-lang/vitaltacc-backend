package vitaltacc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // AHORA RECIBE: Email, Nombre, DNI y Total
    public void enviarCorreoConfirmacion(String destinatario, String nombre, String dni, double total) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Confirmación de tu compra en VitalTacc! 🌾");
        mensaje.setText("Hola " + nombre + ",\n\n" +
                "¡Gracias por tu compra!\n" +
                "Hemos registrado tu pedido asociado al DNI: " + dni + ".\n" +
                "Total de la operación: $" + total + "\n\n" +
                "Pronto nos pondremos en contacto para coordinar la entrega.\n" +
                "Equipo VitalTacc.");

        mailSender.send(mensaje);
    }
}
