package com.ai.tester.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@JacksonXmlRootElement(localName = "Map")
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String path;
}
