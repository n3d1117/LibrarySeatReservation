import { Component, Input, OnInit } from '@angular/core';
import { Library } from 'src/app/models/library.model';
@Component({
  selector: 'app-library-list-item',
  templateUrl: './library-list-item.component.html',
  styleUrls: ['./library-list-item.component.css']
})
export class LibraryListItemComponent implements OnInit{

  @Input() libraries! : Library[]; 
  @Input() searchValue! : String;

  constructor() { }

  ngOnInit(): void {}

}
