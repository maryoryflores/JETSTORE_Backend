package com.idasta.jetstore.helper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
    public static Path url(String ruta){
        return Paths.get("src/main/java/com/idasta/jetstore/" + ruta.trim());
    }
    public static boolean esNullVacio(String txt){
        return (txt.isBlank() || txt == null);
    }
}
