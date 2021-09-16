import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-splash-screen',
  templateUrl: './splash-screen.component.html',
  styleUrls: ['./splash-screen.component.css']
})
export class SplashScreenComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
    const intro = document.querySelector<HTMLElement>(".intro");
    window.addEventListener("DOMContentLoaded", () => {
      setTimeout(() => {
        if (intro)
          intro.style.top = "-100vh";
      }, 3000);
    });
  }

}
