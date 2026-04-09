package com.ai.tester.model.api.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "Map")
public class ErrorResponseXmlModel {
    private String timestamp;
    private Integer status;
    private String error;
    private String path;
}
