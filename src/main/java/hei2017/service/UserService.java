package hei2017.service;

import hei2017.entity.User;

import java.util.List;

/**
 * Created by pic on 02/03/2017.
 */
public interface UserService
{
    long count();

    void delete(User user);

    void deleteOneById(Long id);

    Boolean exists(Long id);

    Boolean exists(String email);

    List<User> findAll();

    List<User> findAllWithAll();

    User findOneById(Long id);

    User findOneByIdWithAll(Long id);

    User findOneByNomAndPrenom(String nom, String prenom);

    User findOneByPseudo(String pseudo);

    User save(User user);
}
