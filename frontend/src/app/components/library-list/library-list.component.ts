import { Component, Input, OnInit} from '@angular/core';
import { ResizeEvent } from 'leaflet';
import { Library } from 'src/app/models/library.model';
@Component({
  selector: 'app-library-list-item',
  templateUrl: './library-list.component.html',
  styleUrls: ['./library-list.component.css']
})
export class LibraryListComponent implements OnInit{

  @Input() libraries! : Library[];
  columns!: number;

  constructor() {}

  ngOnInit(): void {
    this.breakPoints();
  }

  // Cards
  breakPoints() {
    switch(true) {
        case (window.innerWidth <= 380):
          this.columns = 1;
          break;
        case (window.innerWidth > 380 && window.innerWidth <= 640):
          this.columns = 2;
          break;
        case (window.innerWidth > 640 && window.innerWidth <= 992):
          this.columns = 3;
          break;
        default:
          this.columns = 4;
      }
    }
  
  onResize(event: any) {
    console.log("Resize...")
    this.breakPoints();
  }
}
