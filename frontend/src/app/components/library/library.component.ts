import {Component, OnInit, ViewChild} from '@angular/core';
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
import {ConcurrentUsersService} from "../../services/concurrent-users.service";
import {Subscription} from "rxjs";
import {timer} from 'rxjs';
import { AdminMonitorService } from 'src/app/services/admin-monitor.service';

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

  subscribeTimer!: Subscription;
  timeLeft = 120; // todo parametrize
  progressValue = 100;
  isSubscribedToSocket = false;

  @ViewChild(CalendarComponent)
  private calendarComponent!: CalendarComponent;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public authenticationService: AuthenticationService,
    private libraryService: LibraryService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private concurrentUsersService: ConcurrentUsersService,
    private adminMonitorService: AdminMonitorService
  ) {
  }

  ngOnDestroy(): void {
    if (!this.authenticationService.isAdmin()) {
      this.subscribeTimer?.unsubscribe();
      if (this.isSubscribedToSocket) {
        this.concurrentUsersService.socket$.complete();
        this.isSubscribedToSocket = false;
      }
    } else {
      this.adminMonitorService.stopMonitoring();
    }
  }

  ngOnInit(): void {

    if (!this.authenticationService.currentUserValue) {
      this.snackBar.open('Devi essere autenticato per verificare la disponibilità di posti.', '', {duration: 3000});
      this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
      return;
    }

    const libraryId = this.route.snapshot.params['id'];
    this.libraryService.find(libraryId).pipe(first()).subscribe(library => {
      this.library = library;
      this.sliderValue = this.library.capacity;

      if (!this.authenticationService.isAdmin() && !this.isSubscribedToSocket) {
        this.concurrentUsersService.socket$.subscribe();
        this.isSubscribedToSocket = true;

        // primo 1000: ms dopo quanto parte il timer (~1s giusto per caricare la pagina)
        // secondo 1000: ms ogni quanto si aggiorna il timer
        this.subscribeTimer = timer(1000, 1000).subscribe(() => {
          if (this.timeLeft > 0) {
            this.timeLeft--;
            this.progressValue -= 100 / 120; // todo use parameter instead of 120
          } else {
            this.sessionExpired();
          }
        })
      }

      if (this.authenticationService.isAdmin()) {
        this.adminMonitorService.startMonitoring((receivedMessage => {
          const json = JSON.parse(receivedMessage);
          if (json.libraryId == libraryId) {
            this.snackBar.open(json.notificationMessage, '', {duration: 3000});
            this.refreshCalendar();
            this.refreshReservations();
          }
        }));
      }

    }, error => {
      if (error.status == 429) { // 429 HTTP Too Many Requests
        this.router.navigate(['queue'], {queryParams: {returnUrl: this.router.url}});
      } else {
        this.error = error.error || error.statusText;
      }
    });
  }

  sessionExpired(): void {
    this.snackBar.open('Hai esaurito il tempo a disposizione per effettuare la prenotazione.', '', {duration: 3000});
    this.router.navigate(['home']);
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

  refreshCalendar() : void {
    this.calendarComponent.onMonthChange(this.selectedDate);
  }

}
