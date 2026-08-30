package app.daos;

import app.entities.Review;
import app.entities.Wishlist;

import java.util.List;

public class WishlistDAO implements IDAO<Wishlist>{
    @Override
    public Wishlist create(Wishlist wishlist) {
        return null;
    }

    @Override
    public void remove(Wishlist wishlist) {

    }

    @Override
    public Wishlist update(Wishlist wishlist) {
        return null;
    }

    @Override
    public Wishlist getById(int id) {
        return null;
    }

    @Override
    public List<Wishlist> getAll(int id) {
        return List.of();
    }
}
