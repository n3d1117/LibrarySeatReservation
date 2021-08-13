import {Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatCalendar, MatCalendarCellClassFunction} from "@angular/material/datepicker";
import {CalendarHeader} from "./calendar.header.component";
import {Reservation} from "../../models/reservation.model";
import {ReservationService} from "../../services/reservation.service";
import {Library} from "../../models/library.model";
import {first} from "rxjs/operators";
import { EventEmitter } from '@angular/core';
import {DateUtilityService} from "../../services/date-utility.service";

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  @ViewChild(MatCalendar) calendar!: MatCalendar<Date>;
  calendarHeader = CalendarHeader;
  reservationsByMonth: Reservation[] = [];
  @Input() library!: Library;

  @Output() selectedDate = new EventEmitter<Date>();
  @Output() dayReservations = new EventEmitter<Reservation[]>();

  todayDate = new Date();

  constructor(
    private reservationService: ReservationService,
    private dateService: DateUtilityService
  ) { }

  ngOnInit(): void {
    this.onMonthChange(new Date());
  }

  dateClass: MatCalendarCellClassFunction<Date> = (cellDate) => {
    if (this.reservationsByMonth.length == 0 || cellDate < this.dateService.yesterday()) {
      return ''
    }
    const daysReservations = this.reservationsByMonth.filter(r =>
      this.dateService.stringToDate(r.datetime).getDate() == cellDate.getDate()
    );
    const morningReservations = daysReservations.filter(r =>
      this.dateService.stringToDate(r.datetime).getHours() == 8
    );
    const afternoonReservations = daysReservations.filter(r =>
      this.dateService.stringToDate(r.datetime).getHours() == 13
    );
    if (morningReservations.length >= this.library.capacity && afternoonReservations.length >= this.library.capacity)
      return 'full'
    return 'available';
  }

  onMonthChange(date: Date): void {
    this.reservationsByMonth = [];
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // In Typescript, months start from 0
    this.reservationService.allByLibraryAndDate(this.library.id, year, month).pipe(first()).subscribe(reservations => {
      this.reservationsByMonth = reservations;
      this.calendar.updateTodaysDate();
    }, error => {
      console.log(error);
    });
  }

  onDateSelected(date: Date | null): void {
    if (!date) {
      this.dayReservations.emit([]);
    } else {
      this.selectedDate.emit(date);
      this.dayReservations.emit(
        this.reservationsByMonth.filter(r =>
          this.dateService.stringToDate(r.datetime).getDate() == date.getDate()
        )
      );
    }
  }

}
