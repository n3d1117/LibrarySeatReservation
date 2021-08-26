import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibraryListItemComponent } from './library-list-item.component';

describe('LibraryListItemComponent', () => {
  let component: LibraryListItemComponent;
  let fixture: ComponentFixture<LibraryListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LibraryListItemComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LibraryListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
