package com.servicebackend.config;


import com.servicebackend.dto.FileUploadedDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    @Bean
    public KafkaTemplate<String, FileUploadedDto> kafkaTemplateFileUploaded( ProducerFactory<String, FileUploadedDto> producerFactory ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
