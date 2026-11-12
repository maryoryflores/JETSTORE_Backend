package com.idasta.jetstore.helper;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DbConstraintFix {

    @Bean
    public ApplicationRunner fixPagoConstraint(DataSource dataSource) {
        return args -> {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            try {
                jdbc.execute("ALTER TABLE pago DROP CONSTRAINT IF EXISTS pago_metodo_pago_check");
                System.out.println("[DbFix] Constraint pago_metodo_pago_check eliminado (permite MERCADOPAGO)");
            } catch (Exception e) {
                System.out.println("[DbFix] No se pudo corregir constraint pago_metodo_pago_check: " + e.getMessage());
            }
            try {
                jdbc.execute("ALTER TABLE pago DROP CONSTRAINT IF EXISTS pago_estado_check");
                System.out.println("[DbFix] Constraint pago_estado_check eliminado");
            } catch (Exception e) {
                // ignorar si no existe
            }
            try {
                jdbc.execute("ALTER TABLE pago DROP CONSTRAINT IF EXISTS pago_carrito_id_key");
                jdbc.execute("DROP INDEX IF EXISTS pago_carrito_id_key");
                System.out.println("[DbFix] Unique constraint pago_carrito_id_key eliminado (permite múltiples pagos por carrito)");
            } catch (Exception e) {
                System.out.println("[DbFix] No se pudo eliminar unique carrito: " + e.getMessage());
            }
            try {
                jdbc.execute("ALTER TABLE venta DROP CONSTRAINT IF EXISTS venta_usuario_id_key");
                jdbc.execute("DROP INDEX IF EXISTS venta_usuario_id_key");
                System.out.println("[DbFix] Unique constraint venta_usuario_id_key eliminado (permite múltiples ventas por usuario)");
            } catch (Exception e) {
                System.out.println("[DbFix] No se pudo eliminar unique venta usuario: " + e.getMessage());
            }
        };
    }
}
