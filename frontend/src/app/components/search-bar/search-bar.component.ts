import { Component, OnInit, EventEmitter, Output, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {

  //value = ''
  @Output() searchBarValue = new EventEmitter<string>();
  @ViewChild('searchBar') searchBar!: ElementRef;

  constructor() {}

  onSearchChange(searchValue: EventTarget | null): void {
    const target = searchValue as HTMLTextAreaElement;
    console.log(target.value);
    this.searchBarValue.emit(target.value)
  }

  resetSearchBar() {
    this.searchBar.nativeElement.value = '';
    this.searchBarValue.emit("");
  }

  ngOnInit(): void {
  }

}
