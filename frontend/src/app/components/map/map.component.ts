import {Component, Input, OnInit} from '@angular/core';
import {icon, latLng, marker, tileLayer} from "leaflet";
import {GeocodingService} from "../../services/geocoding.service";
import {first} from "rxjs/operators";
import {Library} from "../../models/library.model";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {

  @Input() library!: Library;
  showMap = false;
  options: any;
  layers: any;

  constructor(private geocoder: GeocodingService) { }

  ngOnInit(): void {
    this.geocoder.search(this.library.address).pipe(first()).subscribe(result => {
      const resultString = JSON.stringify(result[0]);
      const jsonValue = JSON.parse(resultString);

      this.options = {
        layers: [
          tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: '' })
        ],
        zoom: 15,
        center: latLng(jsonValue.lat, jsonValue.lon)
      };

      this.layers = [marker([jsonValue.lat, jsonValue.lon], {
        icon: icon({
          iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
          shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
          iconSize: [25, 41],
          iconAnchor: [12, 41],
          popupAnchor: [1, -34],
          shadowSize: [41, 41]
        })
      })];

      this.showMap = true;

      }, error => {
        console.log(error);
    });
  }

}
