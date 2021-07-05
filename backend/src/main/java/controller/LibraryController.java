package controller;

import com.google.gson.Gson;
import dao.LibraryDao;
import dto.LibraryDto;
import mapper.LibraryMapper;
import model.Library;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class LibraryController {

    @Inject
    private LibraryMapper libraryMapper;

    @Inject
    private LibraryDao libraryDao;

    public String all() {
        List<LibraryDto> libraryDtoList = new ArrayList<>();
        for (Library library: libraryDao.all())
            libraryDtoList.add(libraryMapper.generateLibraryDTO(library));
        return new Gson().toJson(libraryDtoList);
    }

    public String find(Long id) {
        Library library = libraryDao.findById(id);
        return new Gson().toJson(libraryMapper.generateLibraryDTO(library));
    }

    public String add(String json) {
        Gson gson = new Gson();
        LibraryDto libraryDto = gson.fromJson(json, LibraryDto.class);
        Library newLibrary = libraryMapper.generateLibraryFromDTO(libraryDto);
        libraryDao.save(newLibrary);
        if (newLibrary.getId() != null)
            libraryDto.setId(newLibrary.getId());
        return gson.toJson(libraryDto);
    }

    public void update(String body) {
        Gson gson = new Gson();
        LibraryDto libraryDto = gson.fromJson(body, LibraryDto.class);
        Library updatedLibrary = libraryMapper.generateLibraryFromDTO(libraryDto);
        libraryDao.update(updatedLibrary, libraryDto.getId());
    }

    public void delete(Long id) {
        libraryDao.delete(id);
    }
}
