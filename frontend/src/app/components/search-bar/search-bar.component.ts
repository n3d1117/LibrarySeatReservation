import {Component, OnInit, EventEmitter, Output, ViewChild, ElementRef} from '@angular/core';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {

  @Output() searchBarValue = new EventEmitter<string>();
  @ViewChild('searchBar') searchBar!: ElementRef;

  constructor() {
  }

  ngOnInit(): void {
  }

  // Emit the new string text when input changes
  onSearchChange(searchValue: EventTarget | null): void {
    const target = searchValue as HTMLTextAreaElement;
    this.searchBarValue.emit(target.value);
  }

  // Reset the search bar when the clear button is clicked
  resetSearchBar(): void {
    this.searchBar.nativeElement.value = '';
    this.searchBarValue.emit("");
  }

}
