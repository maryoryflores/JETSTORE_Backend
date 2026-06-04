package com.idasta.jetstore.helper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;

public class Jetstore {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Path config_path = Util.url("helper/config.json");

    public static ObjectNode config(){
        if(!Files.exists(config_path)){
            throw new IllegalStateException("No existe el archivo config.json en "+ config_path);
        }

        JsonNode node = mapper.readTree(config_path.toFile());
        if(!(node instanceof ObjectNode)){
            throw new IllegalStateException("El archivo razon de config.json debe ser un objecto JSON");
        }
        return (ObjectNode) node;
    }

    public static void guardarConfigJSON(ObjectNode configJson){
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(config_path.toFile(), configJson);
    }
}
