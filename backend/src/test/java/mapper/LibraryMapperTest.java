package mapper;

import dto.LibraryDto;
import model.Library;
import model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class LibraryMapperTest {

    private LibraryMapper mapper;
    private Library library;
    private LibraryDto libraryDto;

    @BeforeEach
    public void setUp() {
        mapper = new LibraryMapper();

        library = spy(ModelFactory.initializeLibrary());
        library.setName("name");
        library.setImgFilename("img.jpg");
        library.setAddress("address");
        library.setCapacity(50);

        libraryDto = new LibraryDto();
        libraryDto.setId(1L);
        libraryDto.setName(library.getName());
        libraryDto.setImgFilename(library.getImgFilename());
        libraryDto.setAddress(library.getAddress());
        libraryDto.setCapacity(library.getCapacity());

        when(library.getId()).thenReturn(1L);
    }

    @Test
    public void testGenerateLibraryDTO() {
        LibraryDto generated = mapper.generateLibraryDTO(library);
        assertEquals(libraryDto.getId(), generated.getId());
        assertEquals(libraryDto.getName(), generated.getName());
        assertEquals(libraryDto.getImgFilename(), generated.getImgFilename());
        assertEquals(libraryDto.getAddress(), generated.getAddress());
        assertEquals(libraryDto.getCapacity(), generated.getCapacity());
    }

    @Test
    public void testGenerateWrongLibraryDTO() {
        library.setName("another name");
        library.setImgFilename("img2.jpg");
        library.setAddress("another address");
        library.setCapacity(70);

        LibraryDto generated = mapper.generateLibraryDTO(library);
        assertNotEquals(libraryDto.getName(), generated.getName());
        assertNotEquals(libraryDto.getImgFilename(), generated.getImgFilename());
        assertNotEquals(libraryDto.getAddress(), generated.getAddress());
        assertNotEquals(libraryDto.getCapacity(), generated.getCapacity());
    }

    @Test
    public void testGenerateLibraryFromDTO() {
        Library generated = mapper.generateLibraryFromDTO(libraryDto);
        assertEquals(library.getName(), generated.getName());
        assertEquals(library.getImgFilename(), generated.getImgFilename());
        assertEquals(library.getAddress(), generated.getAddress());
        assertEquals(library.getCapacity(), generated.getCapacity());
    }

    @Test
    public void testGenerateWrongLibraryFromDTO() {
        libraryDto.setName("another name");
        libraryDto.setImgFilename("img2.jpg");
        libraryDto.setAddress("another address");
        libraryDto.setCapacity(70);

        Library generated = mapper.generateLibraryFromDTO(libraryDto);
        assertNotEquals(library.getName(), generated.getName());
        assertNotEquals(library.getImgFilename(), generated.getImgFilename());
        assertNotEquals(library.getAddress(), generated.getAddress());
        assertNotEquals(library.getCapacity(), generated.getCapacity());
    }

}
