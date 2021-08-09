import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {LibraryService} from "../../services/library.service";
import {first} from "rxjs/operators";
import {Library} from "../../models/library.model";
import {Reservation} from "../../models/reservation.model";

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.css']
})
export class LibraryComponent implements OnInit {

  loading = false;
  library: Library | undefined;
  error = '';
  dayReservations!: Reservation[];
  selectedDate!: Date;

  constructor(
    private route: ActivatedRoute,
    private libraryService: LibraryService
  ) { }

  ngOnInit(): void {
    const libraryId = this.route.snapshot.params['id'];
    this.libraryService.find(libraryId).pipe(first()).subscribe(library => {
      this.library = library;
    }, error => {
      this.loading = false;
      this.error = error;
    });
  }

}
