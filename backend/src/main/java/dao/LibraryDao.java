package dao;

import model.Library;

import javax.ejb.Stateless;

@Stateless
public class LibraryDao extends BaseDao<Library> {

    public LibraryDao() {
        super(Library.class);
    }

}
