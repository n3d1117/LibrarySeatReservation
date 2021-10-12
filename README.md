<p align="center">
<img height="200" alt="logo" src="https://user-images.githubusercontent.com/11541888/135117175-7fea4534-1d39-4246-a91d-bf78fe8405ac.png">
</p>

# LibrarySeatReservation
[![Angular](https://img.shields.io/badge/Angular-12.0.4-red)](https://angular.io)
[![JavaEE](https://img.shields.io/badge/JavaEE-8-blue)](https://www.oracle.com/java/technologies/java-ee-glance.html)
[![TimescaleDB](https://img.shields.io/badge/TimescaleDB-2.3.1-orange)](https://www.timescale.com)
[![RSocket](https://img.shields.io/badge/RSocket-0.0.27-ff69b4)](https://rsocket.io)
[![Javax Websocket](https://img.shields.io/badge/Javax%20Websocket-1.1-blueviolet)](https://docs.oracle.com/javaee/7/api/javax/websocket/package-summary.html)

LibrarySeatReservation is a web application for the management of seat reservations in the study rooms of the libraries of Florence.

## Features
- Configurable queue system to prevent server overload
- [TimscaleDB](https://www.timescale.com) database integration for storing and retrieving time-based reservations
- Real-time admin notifications using [RSocket](https://rsocket.io) protocol
- Use of [JWT](https://jwt.io) for authentication and authorization

## Modules
Check out the following modules: 
- [frontend](frontend) built with [Angular](https://angular.io)
- [backend](backend) built with [Java EE 8](https://www.oracle.com/it/java/technologies/java-ee-glance.html)
- [gateway](gateway) built with [Java EE 8](https://www.oracle.com/it/java/technologies/java-ee-glance.html)

## Documents (pdf, italian)
- [Requirements Analysis](documents/PDF%20Reports/Requirements%20Analysis.pdf)
- [Mockup](documents/PDF%20Reports/Mockup.pdf)
- [System Design](documents/PDF%20Reports/System%20Design.pdf)
- [TimescaleDB](documents/PDF%20Reports/TimescaleDB.pdf)
- [WebSocket vs RSocket](documents/PDF%20Reports/WebSocket%20vs%20RSocket.pdf)
- [HyperSQL](documents/PDF%20Reports/HyperSQL.pdf)
- [Full Report](documents/PDF%20Reports/LSR%20Report.pdf)
- [Presentation](documents/PDF%20Reports/LSR%20Presentation.pdf)

## Build and run
- Start [Wildfly](https://www.wildfly.org) instance with:
```bash
cd backend
docker-compose up
```
- Build `.war` archives for backend (`lsr.war`) and gateway (`gateway.war`) and move them to `backend/workdir/deploy/wildfly`
- Start the frontend with 
```bash
cd frontend
ng serve
```
-  Navigate to `http://localhost:4200/`

## Screenshots

<img width="1287" alt="Screenshot 2021-09-24 at 00 26 08" src="https://user-images.githubusercontent.com/11541888/135116285-8fe8e081-cf9f-4e6f-b0e0-d07ab0c77864.png">

<img width="1283" alt="Screenshot 2021-09-24 at 00 28 14" src="https://user-images.githubusercontent.com/11541888/136655543-6b2d8e18-bf84-4991-9abe-3d0b144a4f45.png">

<img width="1283" alt="Screenshot 2021-09-24 at 00 30 12" src="https://user-images.githubusercontent.com/11541888/136655548-49a554de-e016-4e52-b5b9-162c2dc01fd3.png">

<img width="1281" alt="Screenshot 2021-09-24 at 00 27 41" src="https://user-images.githubusercontent.com/11541888/136655559-ce4afe7e-8ea7-47fa-ba3d-09d3626d3022.png">
