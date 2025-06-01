//// Create this configuration file in your Spring Boot project
//package com.Looksy.Backend.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import org.bson.types.ObjectId;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import java.io.IOException;
//
//@Configuration
//public class ObjectIdSerializationConfig {
//
//    @Bean
//    @Primary
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(ObjectId.class, new ObjectIdSerializer());
//        mapper.registerModule(module);
//        return mapper;
//    }
//
//    public static class ObjectIdSerializer extends StdSerializer<ObjectId> {
//        public ObjectIdSerializer() {
//            this(null);
//        }
//
//        public ObjectIdSerializer(Class<ObjectId> t) {
//            super(t);
//        }
//
//        @Override
//        public void serialize(ObjectId objectId, JsonGenerator jsonGenerator,
//                              SerializerProvider serializerProvider) throws IOException {
//            if (objectId != null) {
//                jsonGenerator.writeString(objectId.toHexString());
//            } else {
//                jsonGenerator.writeNull();
//            }
//        }
//    }
//}