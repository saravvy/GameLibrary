package app.entities;

import app.dtos.GameDetailDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String developer;
    private LocalDate dateReleased;
    private String description;


    public static Game fromDTO(GameDetailDTO dto) {
        Game game = new Game();
        game.setName(dto.name());
        game.setDescription(dto.descriptionRaw());

        if (dto.released() != null && !dto.released().isBlank()) {
            game.setDateReleased(LocalDate.parse(dto.released()));
        }

        if (dto.developers() != null && !dto.developers().isEmpty()) {
            game.setDeveloper(dto.developers().get(0).name());
        }

        return game;
    }
}