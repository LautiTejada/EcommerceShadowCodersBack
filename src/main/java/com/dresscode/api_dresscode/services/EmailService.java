package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.DetalleOrden;
import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    // ─────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────

    @Async
    public void enviarBienvenida(String toEmail, String username) {
        String subject = "¡Bienvenido a DressCode, " + username + "!";
        String body = buildBienvenida(username);
        send(toEmail, subject, body);
    }

    @Async
    public void enviarConfirmacionOrden(OrdenDeCompra orden) {
        String toEmail = orden.getUsuario().getEmail();
        String username = orden.getUsuario().getUsername();
        String subject = "Confirmación de tu orden #" + orden.getId() + " | DressCode";
        String body = buildConfirmacionOrden(username, orden);
        send(toEmail, subject, body);
    }

    @Async
    public void enviarCambioEstadoOrden(OrdenDeCompra orden) {
        String toEmail = orden.getUsuario().getEmail();
        String username = orden.getUsuario().getUsername();
        String subject = "Tu orden #" + orden.getId() + " fue actualizada | DressCode";
        String body = buildCambioEstado(username, orden);
        send(toEmail, subject, body);
    }

    @Async
    public void enviarRecuperacionPassword(String toEmail, String username, String token) {
        String subject = "Recuperá tu contraseña | DressCode";
        String body = buildRecuperacionPassword(username, token);
        send(toEmail, subject, body);
    }

    // ─────────────────────────────────────────────
    // SEND
    // ─────────────────────────────────────────────

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(msg);
            log.info("[EmailService] Enviado a {} — {}", to, subject);
        } catch (Exception e) {
            log.error("[EmailService] Error enviando a {}: {}", to, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────

    private String formatPrecio(Double precio) {
        return NumberFormat.getNumberInstance(new Locale("es", "AR")).format(precio.longValue());
    }

    private String estadoLabel(EstadoOrden estado) {
        return switch (estado) {
            case PEDIDO     -> "Pedido recibido";
            case EN_PROCESO -> "En proceso";
            case EN_CAMINO  -> "En camino";
            case ENTREGADO  -> "Entregado";
        };
    }

    private String estadoColor(EstadoOrden estado) {
        return switch (estado) {
            case PEDIDO     -> "#f59e0b";
            case EN_PROCESO -> "#3b82f6";
            case EN_CAMINO  -> "#8b5cf6";
            case ENTREGADO  -> "#10b981";
        };
    }

    private String buildDetallesTabla(List<DetalleOrden> detalles) {
        if (detalles == null || detalles.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("""
            <table style="width:100%;border-collapse:collapse;margin:16px 0;">
              <thead>
                <tr style="background:#1a1a1a;color:#fff;">
                  <th style="padding:10px 14px;text-align:left;font-size:12px;text-transform:uppercase;letter-spacing:1px;">Producto</th>
                  <th style="padding:10px 14px;text-align:center;font-size:12px;text-transform:uppercase;letter-spacing:1px;">Talle</th>
                  <th style="padding:10px 14px;text-align:center;font-size:12px;text-transform:uppercase;letter-spacing:1px;">Cant.</th>
                  <th style="padding:10px 14px;text-align:right;font-size:12px;text-transform:uppercase;letter-spacing:1px;">Precio unit.</th>
                  <th style="padding:10px 14px;text-align:right;font-size:12px;text-transform:uppercase;letter-spacing:1px;">Subtotal</th>
                </tr>
              </thead>
              <tbody>
            """);
        for (DetalleOrden d : detalles) {
            String nombreProducto = d.getProductoTalle() != null && d.getProductoTalle().getProducto() != null
                    ? d.getProductoTalle().getProducto().getNombre()
                    : "Producto";
            String talle = d.getProductoTalle() != null && d.getProductoTalle().getTalle() != null
                    ? d.getProductoTalle().getTalle().getTipoTalle()
                    : "-";
            double subtotal = d.getCantidad() * d.getPrecioUnitario();
            sb.append(String.format("""
                <tr style="border-bottom:1px solid #2a2a2a;">
                  <td style="padding:10px 14px;color:#e0e0e0;">%s</td>
                  <td style="padding:10px 14px;text-align:center;color:#aaa;">%s</td>
                  <td style="padding:10px 14px;text-align:center;color:#aaa;">%d</td>
                  <td style="padding:10px 14px;text-align:right;color:#aaa;">$%s</td>
                  <td style="padding:10px 14px;text-align:right;color:#fff;font-weight:600;">$%s</td>
                </tr>
                """, nombreProducto, talle, d.getCantidad(),
                    formatPrecio(d.getPrecioUnitario()), formatPrecio(subtotal)));
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    // ─────────────────────────────────────────────
    // TEMPLATES
    // ─────────────────────────────────────────────

    private String wrapper(String content) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
            <body style="margin:0;padding:0;background:#0a0a0a;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#0a0a0a;padding:40px 0;">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%%;">
                    <!-- HEADER -->
                    <tr>
                      <td style="background:#810000;padding:28px 40px;text-align:center;border-radius:8px 8px 0 0;">
                        <h1 style="margin:0;color:#fff;font-size:28px;letter-spacing:4px;font-weight:900;text-transform:uppercase;">DRESSCODE</h1>
                        <p style="margin:4px 0 0;color:rgba(255,255,255,0.7);font-size:12px;letter-spacing:2px;text-transform:uppercase;">Moda Urbana</p>
                      </td>
                    </tr>
                    <!-- BODY -->
                    <tr>
                      <td style="background:#111;padding:40px;border-radius:0 0 8px 8px;">
                        %s
                        <!-- FOOTER -->
                        <hr style="border:none;border-top:1px solid #222;margin:32px 0;">
                        <p style="margin:0;color:#555;font-size:11px;text-align:center;line-height:1.6;">
                          Este correo fue enviado por DressCode · No respondas este email<br>
                          <a href="%s" style="color:#810000;text-decoration:none;">Visitá nuestra tienda</a>
                        </p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(content, frontendUrl);
    }

    private String buildBienvenida(String username) {
        String content = """
            <h2 style="color:#fff;font-size:22px;margin:0 0 8px;">¡Hola, %s!</h2>
            <p style="color:#aaa;font-size:15px;line-height:1.7;margin:0 0 24px;">
              Tu cuenta en <strong style="color:#fff;">DressCode</strong> fue creada exitosamente.
              Ya podés explorar todo el catálogo, guardar tus favoritos y hacer tu primera compra.
            </p>
            <div style="text-align:center;margin:32px 0;">
              <a href="%s/catalog"
                 style="background:#810000;color:#fff;text-decoration:none;padding:14px 36px;border-radius:4px;font-weight:700;font-size:15px;letter-spacing:1px;text-transform:uppercase;">
                Explorar el catálogo
              </a>
            </div>
            <p style="color:#555;font-size:13px;line-height:1.6;margin:24px 0 0;">
              Si no creaste esta cuenta, ignorá este mensaje.
            </p>
            """.formatted(username, frontendUrl);
        return wrapper(content);
    }

    private String buildConfirmacionOrden(String username, OrdenDeCompra orden) {
        String detallesHtml = buildDetallesTabla(orden.getDetalles());
        String direccion = orden.getDireccion() != null
                ? orden.getDireccion().getCalle() + " " + orden.getDireccion().getNumero()
                + ", " + orden.getDireccion().getLocalidad()
                : "—";
        String content = """
            <h2 style="color:#fff;font-size:22px;margin:0 0 8px;">¡Gracias por tu compra, %s!</h2>
            <p style="color:#aaa;font-size:15px;line-height:1.7;margin:0 0 24px;">
              Recibimos tu orden correctamente. Te avisaremos cuando haya novedades.
            </p>

            <div style="background:#1a1a1a;border-radius:6px;padding:20px 24px;margin-bottom:24px;">
              <table style="width:100%%;">
                <tr>
                  <td style="color:#888;font-size:13px;padding-bottom:8px;">N° de orden</td>
                  <td style="color:#fff;font-size:13px;font-weight:700;text-align:right;padding-bottom:8px;">#%d</td>
                </tr>
                <tr>
                  <td style="color:#888;font-size:13px;padding-bottom:8px;">Fecha</td>
                  <td style="color:#fff;font-size:13px;text-align:right;padding-bottom:8px;">%s</td>
                </tr>
                <tr>
                  <td style="color:#888;font-size:13px;padding-bottom:8px;">Estado</td>
                  <td style="text-align:right;padding-bottom:8px;">
                    <span style="background:%s;color:#fff;padding:3px 10px;border-radius:20px;font-size:12px;font-weight:600;">%s</span>
                  </td>
                </tr>
                <tr>
                  <td style="color:#888;font-size:13px;padding-bottom:8px;">Dirección de entrega</td>
                  <td style="color:#fff;font-size:13px;text-align:right;padding-bottom:8px;">%s</td>
                </tr>
              </table>
            </div>

            %s

            <div style="background:#1a1a1a;border-radius:6px;padding:16px 24px;text-align:right;">
              <span style="color:#888;font-size:14px;">Total pagado: </span>
              <span style="color:#810000;font-size:20px;font-weight:900;">$%s</span>
            </div>

            <div style="text-align:center;margin:32px 0 0;">
              <a href="%s/profile"
                 style="background:#810000;color:#fff;text-decoration:none;padding:14px 36px;border-radius:4px;font-weight:700;font-size:14px;letter-spacing:1px;text-transform:uppercase;">
                Ver mis órdenes
              </a>
            </div>
            """.formatted(
                username,
                orden.getId(),
                orden.getFecha().toString(),
                estadoColor(orden.getEstadoOrden()), estadoLabel(orden.getEstadoOrden()),
                direccion,
                detallesHtml,
                formatPrecio(orden.getPrecioTotal()),
                frontendUrl
        );
        return wrapper(content);
    }

    private String buildCambioEstado(String username, OrdenDeCompra orden) {
        String content = """
            <h2 style="color:#fff;font-size:22px;margin:0 0 8px;">Tu orden fue actualizada</h2>
            <p style="color:#aaa;font-size:15px;line-height:1.7;margin:0 0 24px;">
              Hola <strong style="color:#fff;">%s</strong>, tu orden <strong style="color:#fff;">#%d</strong>
              cambió de estado.
            </p>

            <div style="text-align:center;margin:32px 0;">
              <div style="display:inline-block;background:%s;color:#fff;padding:12px 32px;border-radius:30px;font-size:18px;font-weight:700;letter-spacing:1px;">
                %s
              </div>
            </div>

            %s

            <div style="text-align:center;margin:32px 0 0;">
              <a href="%s/profile"
                 style="background:#810000;color:#fff;text-decoration:none;padding:14px 36px;border-radius:4px;font-weight:700;font-size:14px;letter-spacing:1px;text-transform:uppercase;">
                Ver el detalle
              </a>
            </div>
            """.formatted(
                username,
                orden.getId(),
                estadoColor(orden.getEstadoOrden()), estadoLabel(orden.getEstadoOrden()),
                buildMensajeEstado(orden.getEstadoOrden()),
                frontendUrl
        );
        return wrapper(content);
    }

    private String buildMensajeEstado(EstadoOrden estado) {
        String mensaje = switch (estado) {
            case EN_PROCESO -> "Estamos preparando tu pedido. Pronto estará listo para enviarse.";
            case EN_CAMINO  -> "Tu pedido ya está en camino. ¡Pronto lo tendrás en tus manos!";
            case ENTREGADO  -> "Tu pedido fue entregado. ¡Esperamos que lo disfrutes!";
            default         -> "Revisá el estado de tu pedido en tu perfil.";
        };
        return "<p style=\"color:#aaa;font-size:15px;line-height:1.7;text-align:center;\">" + mensaje + "</p>";
    }

    private String buildRecuperacionPassword(String username, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        String content = """
            <h2 style="color:#fff;font-size:22px;margin:0 0 8px;">Recuperá tu contraseña</h2>
            <p style="color:#aaa;font-size:15px;line-height:1.7;margin:0 0 24px;">
              Hola <strong style="color:#fff;">%s</strong>, recibimos una solicitud para restablecer
              la contraseña de tu cuenta. Si fuiste vos, hacé clic en el botón de abajo.
            </p>

            <div style="text-align:center;margin:32px 0;">
              <a href="%s"
                 style="background:#810000;color:#fff;text-decoration:none;padding:14px 36px;border-radius:4px;font-weight:700;font-size:15px;letter-spacing:1px;text-transform:uppercase;">
                Restablecer contraseña
              </a>
            </div>

            <p style="color:#555;font-size:13px;line-height:1.6;margin:24px 0 0;text-align:center;">
              Este enlace expira en 1 hora.<br>
              Si no solicitaste este cambio, ignorá este mensaje.
            </p>
            """.formatted(username, resetUrl);
        return wrapper(content);
    }
}
