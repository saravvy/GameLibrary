package app.daos;

import java.util.List;
import java.util.Set;

public interface IDAO<T> {

     T create(T t);
     void remove(T t);
     T update(T t);
     T getById(int id);
     Set<T> getAll();
}
