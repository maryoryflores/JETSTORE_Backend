package com.idasta.jetstore.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void hash_retornaFormatoValido() {
        String hash = PasswordUtil.hash("miPassword");
        assertTrue(hash.contains(":"));
        assertEquals(2, hash.split(":").length);
        assertEquals(32 + 1 + 64, hash.length());
    }

    @Test
    void verify_correcto() {
        String hash = PasswordUtil.hash("miPassword");
        assertTrue(PasswordUtil.verify("miPassword", hash));
    }

    @Test
    void verify_incorrecto() {
        String hash = PasswordUtil.hash("miPassword");
        assertFalse(PasswordUtil.verify("otraPassword", hash));
    }

    @Test
    void hashesDistintosMismaPassword() {
        String hash1 = PasswordUtil.hash("password");
        String hash2 = PasswordUtil.hash("password");
        assertNotEquals(hash1, hash2);
    }
}
