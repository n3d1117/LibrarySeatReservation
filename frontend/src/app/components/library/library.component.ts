import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {LibraryService} from "../../services/library.service";
import {first} from "rxjs/operators";
import {Library} from "../../models/library.model";
import {ReservationService} from "../../services/reservation.service";
import {Reservation} from "../../models/reservation.model";

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.css']
})
export class LibraryComponent implements OnInit {

  loading = false;
  library: Library | undefined;
  reservations: Reservation[] = [];
  error = '';

  constructor(
    private route: ActivatedRoute,
    private libraryService: LibraryService,
    private reservationsService: ReservationService
  ) { }

  ngOnInit(): void {
    const libraryId = this.route.snapshot.params['id'];
    this.libraryService.find(libraryId).pipe(first()).subscribe(library => {

      this.library = library;

      this.reservationsService.allByLibrary(libraryId).pipe(first()).subscribe(reservations => {
        this.reservations = reservations;
        this.loading = false;
      }, error => {
        this.loading = false;
        this.error = error;
      });

    }, error => {
      this.loading = false;
      this.error = error;
    });
  }

}
