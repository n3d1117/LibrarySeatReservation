import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { first } from 'rxjs/internal/operators/first';
import { Library } from 'src/app/models/library.model';
import { LibraryService } from 'src/app/services/library.service';
import { AuthenticationService } from "../../services/authentication.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  error = '';
  searchValue = '';
  loading = false;
  libraries: Library[] = [];

  ngOnInit(): void {
    this.loading = true;
    this.libraryService.all().pipe(first()).subscribe(libraries => {
      this.loading = false;
      this.libraries = libraries;
    }, error => {
      this.loading = false;
      this.error = error.message || error;
    });
  }

  constructor(
    private libraryService: LibraryService,
    private router: Router,
    public authenticationService: AuthenticationService,
  ) { }

  goToAddLibrary(): void {
    this.router.navigate(['admin/add-library']);
  }

}
