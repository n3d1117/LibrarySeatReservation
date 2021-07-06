package mapper;

import dto.LibraryDto;
import model.Library;
import model.ModelFactory;

public class LibraryMapper {

    public LibraryDto generateLibraryDTO(Library library) {
        LibraryDto libraryDto = new LibraryDto();
        libraryDto.setId(library.getId());
        libraryDto.setName(library.getName());
        libraryDto.setAddress(library.getAddress());
        libraryDto.setCapacity(library.getCapacity());
        return libraryDto;
    }

    public Library generateLibraryFromDTO(LibraryDto dto) {
        Library library = ModelFactory.initializeLibrary();
        library.setName(dto.getName());
        library.setAddress(dto.getAddress());
        library.setCapacity(dto.getCapacity());
        return library;
    }

}
