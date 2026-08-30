package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
 @Setter
@ToString
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int stars;
    private String description;
    private LocalDate added;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;


    public Review(int stars, String description, LocalDate added, User user, Game game) {
        this.stars = stars;
        this.description = description;
        this.added = added;
        this.user = user;
        this.game = game;
    }
}
