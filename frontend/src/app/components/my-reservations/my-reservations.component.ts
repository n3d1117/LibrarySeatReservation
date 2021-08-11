import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {first} from "rxjs/operators";
import {Reservation} from "../../models/reservation.model";
import {ReservationService} from "../../services/reservation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {PageEvent} from "@angular/material/paginator";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatBottomSheet} from "@angular/material/bottom-sheet";
import {QrcodeComponent} from "../qrcode/qrcode.component";

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrls: ['./my-reservations.component.css']
})
export class MyReservationsComponent implements OnInit {

  loading = false;
  reservations: Reservation[] = [];
  error = '';

  displayedColumns: string[] = ['hour', 'datetime', 'libraryName', 'qr', 'action'];
  lowValue = 0;
  highValue = 5;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private reservationService: ReservationService,
    private snackBar: MatSnackBar,
    private bottomSheet: MatBottomSheet
  ) { }

  pastReservations(): Reservation[] {
    const yesterday = new Date(Date.now() - 86400000);
    return this.reservations.filter((item) => {
      return this.stringToDate(item.datetime) < yesterday;
    }).sort((a, b) => {
      return this.stringToDate(a.datetime).getTime() - this.stringToDate(b.datetime).getTime();
    })
  }

  futureReservations(): Reservation[] {
    const yesterday = new Date(Date.now() - 86400000);
    return this.reservations.filter((item) => {
      return this.stringToDate(item.datetime) >= yesterday;
    }).sort((a, b) => {
      return this.stringToDate(a.datetime).getTime() - this.stringToDate(b.datetime).getTime();
    })
  }

  ngOnInit(): void {
    this.loading = true;
    this.reservationService.allByUser(this.authenticationService.currentUserValue.id)
      .pipe(first()).subscribe(reservations => {
      this.reservations = reservations;
      this.loading = false;
    }, error => {
      this.loading = false;
      this.error = error;
    });
  }

  stringToDate(date: string): Date {
    return new Date(date.replace(' ', 'T'));
  }

  public getPaginatorData(event: PageEvent): PageEvent {
    this.lowValue = event.pageIndex * event.pageSize;
    this.highValue = this.lowValue + event.pageSize;
    return event;
  }

  deleteReservation(reservationId: number): void {
    this.reservationService.delete(reservationId)
      .pipe(first())
      .subscribe(() => {
        this.snackBar.open('Prenotazione cancellata!', '', {duration: 5000});
      }, error => {
        console.log(error);
      });
  }

  humanReadableDate(date: Date): string {
    return date.toLocaleDateString('it',
      {weekday: "long", month: "long", day: "numeric"}
    ).split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.substring(1))
      .join(' ');
  }

  openQrCodeBottomSheet(reservation: Reservation): void {
      this.bottomSheet.open(QrcodeComponent, {
        data: { reservation: reservation }
      });
  }

}
