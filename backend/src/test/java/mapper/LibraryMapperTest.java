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
        library.setAddress("address");
        library.setLatitude(11.1);
        library.setLongitude(11.2);

        libraryDto = new LibraryDto();
        libraryDto.setId(1L);
        libraryDto.setName(library.getName());
        libraryDto.setAddress(library.getAddress());
        libraryDto.setLatitude(library.getLatitude());
        libraryDto.setLongitude(library.getLongitude());

        when(library.getId()).thenReturn(1L);
    }

    @Test
    public void testGenerateLibraryDTO() {
        LibraryDto generated = mapper.generateLibraryDTO(library);
        assertEquals(libraryDto.getId(), generated.getId());
        assertEquals(libraryDto.getName(), generated.getName());
        assertEquals(libraryDto.getAddress(), generated.getAddress());
        assertEquals(libraryDto.getLatitude(), generated.getLatitude());
        assertEquals(libraryDto.getLongitude(), generated.getLongitude());
    }

    @Test
    public void testGenerateWrongLibraryDTO() {
        library.setName("another name");
        library.setAddress("another address");
        library.setLatitude(16.2);
        library.setLongitude(18.3);

        LibraryDto generated = mapper.generateLibraryDTO(library);
        assertNotEquals(libraryDto.getName(), generated.getName());
        assertNotEquals(libraryDto.getAddress(), generated.getAddress());
        assertNotEquals(libraryDto.getLatitude(), generated.getLatitude());
        assertNotEquals(libraryDto.getLongitude(), generated.getLongitude());
    }

    @Test
    public void testGenerateLibraryFromDTO() {
        Library generated = mapper.generateLibraryFromDTO(libraryDto);
        assertEquals(library.getName(), generated.getName());
        assertEquals(library.getAddress(), generated.getAddress());
        assertEquals(library.getLatitude(), generated.getLatitude());
        assertEquals(library.getLongitude(), generated.getLongitude());
    }

    @Test
    public void testGenerateWrongLibraryFromDTO() {
        libraryDto.setName("another name");
        libraryDto.setAddress("another address");
        libraryDto.setLatitude(19.2);
        libraryDto.setLongitude(9.3);

        Library generated = mapper.generateLibraryFromDTO(libraryDto);
        assertNotEquals(library.getName(), generated.getName());
        assertNotEquals(library.getAddress(), generated.getAddress());
        assertNotEquals(library.getLatitude(), generated.getLatitude());
        assertNotEquals(library.getLongitude(), generated.getLongitude());
    }

}
