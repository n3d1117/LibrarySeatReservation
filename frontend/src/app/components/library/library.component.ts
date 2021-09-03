import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {LibraryService} from "../../services/library.service";
import {first} from "rxjs/operators";
import {Library} from "../../models/library.model";
import {Reservation} from "../../models/reservation.model";
import {AuthenticationService} from "../../services/authentication.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CalendarComponent} from "../calendar/calendar.component";
import {ConfirmDialogComponent, ConfirmDialogModel} from "../confirm-dialog/confirm-dialog.component";
import {MatDialog} from "@angular/material/dialog";

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

  @ViewChild(CalendarComponent)
  private calendarComponent!: CalendarComponent;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public authenticationService: AuthenticationService,
    private libraryService: LibraryService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    const libraryId = this.route.snapshot.params['id'];
    this.libraryService.find(libraryId).pipe(first()).subscribe(library => {
      this.library = library;
      this.sliderValue = this.library.capacity;
    }, error => {
      this.error = error;
    });
  }

  deleteLibrary(library: Library): void {
    const dialogData = new ConfirmDialogModel("Conferma azione", `Sei sicuro di voler eliminare "${library.name}"?`);
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: dialogData
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.isDeleting = true;
        this.libraryService.delete(library.id)
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
    });
  }

  updateLibraryCapacity(): void {
    const dialogData = new ConfirmDialogModel("Conferma azione", `Sei sicuro di voler modificare la capacità di "${this.library?.name}" da ${this.library?.capacity} a ${this.sliderValue}?`);
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: dialogData
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res && this.library) {
        this.library.capacity = this.sliderValue;
        this.libraryService.update(this.library)
          .pipe(first())
          .subscribe(() => {
            this.snackBar.open('Capacità aggiornata correttamente!', '', {duration: 3000});
          }, error => {
            console.log(error);
          });
      }
    });
  }

  refreshReservations(): void {
    this.calendarComponent.onDateSelected(this.selectedDate);
  }

}
