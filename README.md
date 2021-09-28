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

## Modules
Check out the following modules: 
- [frontend](frontend) built with [Angular](https://angular.io)
- [backend](backend) built with [Java EE 8](https://www.oracle.com/it/java/technologies/java-ee-glance.html)
- [gateway](gateway) built with [Java EE 8](https://www.oracle.com/it/java/technologies/java-ee-glance.html)

## Documents (pdf)
- [Mockup](blob/master/documents/PDF%20Reports/LibrarySeatReservationMockup.pdf)
- [Requirements Analysis](blob/master/documents/PDF%20Reports/Requirements%20Analysis.pdf)
- [System Design](blob/master/documents/PDF%20Reports/System%20Design.pdf)
- [TimescaleDB](blob/master/documents/PDF%20Reports/TimescaleDB.pdf)
- [WebSocket vs RSocket](blob/master/documents/PDF%20Reports/WebSocket%20vs%20RSocket.pdf)

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

## Screenshot

<img width="1287" alt="Screenshot 2021-09-24 at 00 26 08" src="https://user-images.githubusercontent.com/11541888/135116285-8fe8e081-cf9f-4e6f-b0e0-d07ab0c77864.png">