package com.idasta.jetstore.helper;

import com.idasta.jetstore.model.Rol;
import com.idasta.jetstore.repository.RolRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {
    private final RolRepo rolRepo;

    public DataSeeder(RolRepo rolRepo) {
        this.rolRepo = rolRepo;
    }

    @PostConstruct
    public void seed() {
        if (rolRepo.count() == 0) {
            Rol admin = new Rol();
            admin.setNombre("ADMIN");
            rolRepo.save(admin);

            Rol cliente = new Rol();
            cliente.setNombre("CLIENTE");
            rolRepo.save(cliente);
        }
    }
}
