package controller;

import com.google.gson.Gson;
import dao.LibraryDao;
import dto.LibraryDto;
import mapper.LibraryMapper;
import model.Library;
import model.ModelFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class LibraryControllerTest {
    private LibraryController controller;

    private LibraryDao dao;
    private LibraryDto dto;
    private Library library;

    @BeforeEach
    public void setup() throws IllegalAccessException {
        controller = new LibraryController();

        dao = mock(LibraryDao.class);

        library = spy(ModelFactory.initializeLibrary());
        library.setName("name");
        library.setAddress("address");
        library.setCapacity(50);
        when(library.getId()).thenReturn(1L);

        dto = new LibraryDto();
        dto.setId(1L);
        dto.setName(library.getName());
        dto.setAddress(library.getAddress());
        dto.setCapacity(library.getCapacity());

        FieldUtils.writeField(controller, "libraryMapper", new LibraryMapper(), true);
        FieldUtils.writeField(controller, "libraryDao", dao, true);
    }

    @Test
    public void testRetrieveAllLibraries() {
        Library anotherLibrary = spy(ModelFactory.initializeLibrary());
        anotherLibrary.setName("another name");
        anotherLibrary.setAddress("another address");
        anotherLibrary.setCapacity(70);
        when(anotherLibrary.getId()).thenReturn(2L);

        LibraryDto anotherDto = new LibraryDto();
        anotherDto.setId(2L);
        anotherDto.setName(anotherLibrary.getName());
        anotherDto.setAddress(anotherLibrary.getAddress());
        anotherDto.setCapacity(anotherLibrary.getCapacity());

        when(dao.all()).thenReturn(Arrays.asList(library, anotherLibrary));
        assertEquals(controller.all(), new Gson().toJson(Arrays.asList(dto, anotherDto)));
    }

    @Test
    public void testFindById() {
        when(dao.findById(1L)).thenReturn(library);
        assertEquals(controller.find(1L), new Gson().toJson(dto));
    }

    @Test
    public void testAddLibrary() {
        String dtoJson = new Gson().toJson(dto);

        assertEquals(controller.add(dtoJson), dtoJson);

        ArgumentCaptor<Library> argument = ArgumentCaptor.forClass(Library.class);
        verify(dao).save(argument.capture());
        assertEquals(library.getName(), argument.getValue().getName());
        assertEquals(library.getAddress(), argument.getValue().getAddress());
        assertEquals(library.getCapacity(), argument.getValue().getCapacity());
    }

    @Test
    public void testUpdateLibrary() {
        library.setAddress("another address");
        dto.setAddress(library.getAddress());
        String dtoJson = new Gson().toJson(dto);

        controller.update(dtoJson);

        ArgumentCaptor<Library> argument = ArgumentCaptor.forClass(Library.class);
        verify(dao).update(argument.capture(), eq(dto.getId()));
        assertEquals(library.getAddress(), argument.getValue().getAddress());
    }

    @Test
    public void testDeleteLibrary() {
        controller.delete(dto.getId());
        verify(dao).delete(dto.getId());
    }
}
