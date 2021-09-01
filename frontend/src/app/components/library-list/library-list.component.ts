import {Component, Input, OnInit} from '@angular/core';
import {Library} from 'src/app/models/library.model';

@Component({
  selector: 'app-library-list',
  templateUrl: './library-list.component.html',
  styleUrls: ['./library-list.component.css']
})
export class LibraryListComponent implements OnInit {

  @Input() libraries!: Library[];
  columns!: number;

  constructor() {
  }

  ngOnInit(): void {
  }
}
