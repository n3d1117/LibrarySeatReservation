import { Component, OnInit } from '@angular/core';
import { first } from 'rxjs/internal/operators/first';
import { Library } from 'src/app/models/library.model';
import {LibraryService} from "../../services/library.service";

@Component({
  selector: 'app-library-list-item',
  templateUrl: './library-list-item.component.html',
  styleUrls: ['./library-list-item.component.css']
})
export class LibraryListItemComponent implements OnInit {
  
  error = '';
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
  ) { }

}
