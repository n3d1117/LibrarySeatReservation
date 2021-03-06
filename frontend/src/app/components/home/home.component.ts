import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {first} from 'rxjs/internal/operators/first';
import {Library} from 'src/app/models/library.model';
import {LibraryService} from 'src/app/services/library.service';
import {AuthenticationService} from "../../services/authentication.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  searchBarValueHome = '';
  loading = false;
  libraries: Library[] = [];

  ngOnInit(): void {
    this.loading = true;
    this.libraryService.all().pipe(first()).subscribe(libraries => {
      this.loading = false;
      this.libraries = libraries.sort((a, b) => a.name.localeCompare(b.name));
    }, error => {
      console.log(error);
      this.loading = false;
    });
  }

  constructor(
    private libraryService: LibraryService,
    private router: Router,
    public authenticationService: AuthenticationService
  ) {
  }

  /**
   * Filter libraries by search string (matching partial name or address)
   */
  filterLibraries(): Library[] {
    return this.libraries.filter(library => {
      return library.name.toLowerCase().includes(this.searchBarValueHome.toLowerCase())
        || library.address.toLowerCase().includes(this.searchBarValueHome.toLowerCase());
    })
  }

  goToAddLibrary(): void {
    this.router.navigate(['admin/add-library']);
  }

}
