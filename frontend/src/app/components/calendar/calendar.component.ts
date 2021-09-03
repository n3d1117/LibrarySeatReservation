import {Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatCalendar, MatCalendarCellClassFunction} from "@angular/material/datepicker";
import {CalendarHeader} from "./calendar.header.component";
import {Reservation} from "../../models/reservation.model";
import {ReservationService} from "../../services/reservation.service";
import {Library} from "../../models/library.model";
import {first} from "rxjs/operators";
import {EventEmitter} from '@angular/core';
import {DateUtilityService} from "../../services/date-utility.service";
import {ReservationsDailyAggregate} from "../../models/reservations_daily_aggregate";

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  @ViewChild(MatCalendar) calendar!: MatCalendar<Date>;
  calendarHeader = CalendarHeader;
  aggregateReservationsByMonth: ReservationsDailyAggregate[] = [];
  @Input() library!: Library;

  @Output() selectedDate = new EventEmitter<Date>();
  @Output() dayReservations = new EventEmitter<Reservation[]>();

  todayDate = new Date();

  constructor(
    private reservationService: ReservationService,
    private dateService: DateUtilityService
  ) {
  }

  ngOnInit(): void {
    this.onMonthChange(new Date());
  }

  dateClass: MatCalendarCellClassFunction<Date> = (cellDate) => {
    if (this.aggregateReservationsByMonth.length == 0 || cellDate < this.dateService.yesterday()) {
      return ''
    }
    const dayReservations = this.aggregateReservationsByMonth.filter(stat =>
      this.dateService.stringToDate(stat.date).getDate() == cellDate.getDate()
    );
    if (dayReservations.length == 0)
      return ''
    const count = dayReservations[0].count;
    if (count >= this.library.capacity * 2) // morning + afternoon
      return 'full'
    return 'available';
  }

  onMonthChange(date: Date): void {
    this.aggregateReservationsByMonth = [];
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // In Typescript, months start from 0
    this.reservationService.aggregateByLibraryAndMonth(this.library.id, year, month).pipe(first()).subscribe(stats => {
      this.aggregateReservationsByMonth = stats;
      this.calendar.updateTodaysDate();
    }, error => {
      console.log(error);
    });
  }

  onDateSelected(date: Date | null): void {
    if (!date) {
      this.dayReservations.emit([]);
    } else {
      const year = date.getFullYear();
      const month = date.getMonth() + 1; // In Typescript, months start from 0
      const day = date.getDate();
      this.reservationService.allByLibraryAndDate(this.library.id, year, month, day).pipe(first()).subscribe(reservations => {
        this.selectedDate.emit(date);
        this.dayReservations.emit(reservations);
      }, error => {
        console.log(error);
      });
    }
  }

}
