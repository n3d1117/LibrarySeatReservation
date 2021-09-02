import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {LibraryService} from "../../services/library.service";
import {first} from "rxjs/operators";
import {Library} from "../../models/library.model";
import {Reservation} from "../../models/reservation.model";
import {AuthenticationService} from "../../services/authentication.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.css']
})
export class LibraryComponent implements OnInit {

  isDeleting = false;
  library: Library | undefined;
  error = '';
  dayReservations!: Reservation[];
  selectedDate!: Date;

  sliderValue = 10;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public authenticationService: AuthenticationService,
    private libraryService: LibraryService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    const libraryId = this.route.snapshot.params['id'];
    this.libraryService.find(libraryId).pipe(first()).subscribe(library => {
      this.library = library;
      this.sliderValue = this.library.capacity;
    }, error => {
      this.error = error;
    });
  }

  deleteLibrary(libraryId: number): void {
    this.isDeleting = true;
    this.libraryService.delete(libraryId)
      .pipe(first())
      .subscribe(() => {
        this.isDeleting = false;
        this.snackBar.open('Biblioteca eliminata correttamente!', '', {duration: 3000});
        this.router.navigate(['home']);
      }, error => {
        console.log(error);
        this.isDeleting = false;
      });
  }

  updateLibraryCapacity(): void {
    if (this.library) {
      this.library.capacity = this.sliderValue;
      this.libraryService.update(this.library)
        .pipe(first())
        .subscribe(() => {
          this.snackBar.open('Capacità aggiornata correttamente!', '', {duration: 3000});
        }, error => {
          console.log(error);
        });

    }
  }

}
