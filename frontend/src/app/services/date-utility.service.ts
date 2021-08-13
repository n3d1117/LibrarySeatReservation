import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DateUtilityService {

  constructor() { }

  yesterday(): Date {
    return new Date(Date.now() - 86400000)
  }

  isOlderThanToday(first: string): boolean {
    return this.stringToDate(first) < this.yesterday();
  }

  sort(first: string, second: string): number {
    return this.stringToDate(first).getTime() - this.stringToDate(second).getTime()
  }

  stringToDate(date: string): Date {
    return new Date(date.replace(' ', 'T'));
  }

  dateToHumanReadableString(date: Date): string {
    return date.toLocaleDateString('it',
      {weekday: "long", month: "long", day: "numeric"}
    ).split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.substring(1))
      .join(' ');
  }

  dateToHumanReadableHourString(date: Date): string {
    return date.getHours() + ":00";
  }

  prepareDateForBackend(date: Date, isMorning: boolean): string {
    //yyyy-MM-dd HH:mm:ss
    date.setHours(isMorning ? 8 : 13);
    return new Date(
      date.getTime() - (date.getTimezoneOffset() * 60000 )
    ).toISOString().replace('T', ' ').split('.')[0];
  }
}
