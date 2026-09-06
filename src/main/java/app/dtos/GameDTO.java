package app.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameDTO(
         int id,
         String name,
         String description,
         String released,
         Double rating,
         String rawgId

) {


}
