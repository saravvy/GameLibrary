package app.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString

public class GameInLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate dateAdded;

    @ManyToOne
    @JoinColumn( name = "library_id")
    private Library library;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;


}
