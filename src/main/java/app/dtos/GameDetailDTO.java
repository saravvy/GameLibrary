package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameDetailDTO(
        int id,
        String name,
        String released,
        @JsonProperty("description_raw")
        String descriptionRaw,
        List<DeveloperDTO> developers
) {}