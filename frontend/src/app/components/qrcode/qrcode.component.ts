import {Component, Inject, OnInit} from '@angular/core';
import {MAT_BOTTOM_SHEET_DATA} from "@angular/material/bottom-sheet";
import {Reservation} from "../../models/reservation.model";

@Component({
  selector: 'app-qrcode',
  templateUrl: './qrcode.component.html',
  styleUrls: ['./qrcode.component.css']
})
export class QrcodeComponent implements OnInit {

  constructor(@Inject(MAT_BOTTOM_SHEET_DATA) public data: {reservation: Reservation}) { }

  ngOnInit(): void {
  }

  stringToDate(date: string): Date {
    return new Date(date.replace(' ', 'T'));
  }

  humanReadableDate(date: Date): string {
    return date.toLocaleDateString('it',
      {weekday: "long", month: "long", day: "numeric"}
    ).split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.substring(1))
      .join(' ');
  }
}
