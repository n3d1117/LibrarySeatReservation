export class ReservationsDailyAggregate {
  date: string;
  countMorning: number;
  countAfternoon: number;

  constructor(datetime: string, countMorning: number, countAfternoon: number) {
    this.date = datetime;
    this.countMorning = countMorning;
    this.countAfternoon = countAfternoon;
  }
}
