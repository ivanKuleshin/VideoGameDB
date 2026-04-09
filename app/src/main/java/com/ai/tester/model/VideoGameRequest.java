package com.ai.tester.model;

import com.ai.tester.model.adapter.LocalDateAdapter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import lombok.Data;

@Data
@XmlRootElement(name = "videoGame")
@JacksonXmlRootElement(localName = "videoGame")
public class VideoGameRequest {

    private int id;

    @NotBlank
    private String name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate releaseDate;

    @Min(0)
    @Max(100)
    private int reviewScore;

    @NotBlank
    private String category;

    @NotBlank
    private String rating;
}
