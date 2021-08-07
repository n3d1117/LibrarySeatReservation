import { Component, OnInit } from '@angular/core';
import {first} from "rxjs/operators";
import {Library} from "../../models/library.model";
import {LibraryService} from "../../services/library.service";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/authentication.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  loading = false;
  libraries: Library[] = [];
  error = '';

  constructor(
    private router: Router,
    public authenticationService: AuthenticationService,
    private libraryService: LibraryService
  ) { }

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

  goToAddLibrary(): void {
    this.router.navigate(['admin/add-library']);
  }

}
