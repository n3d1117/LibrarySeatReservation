import {environment} from './../../environments/environment';
import {Injectable} from '@angular/core';
import {RSocketClient} from "rsocket-core";
import RSocketWebsocketClient from "rsocket-websocket-client";
import {Payload, Responder} from 'rsocket-types';
import {Single, Flowable} from 'rsocket-flowable';

@Injectable({
  providedIn: 'root'
})
export class AdminMonitorService {

  private onReceivedNotification: (message: string) => void;
  private rSocketUrl = environment.ADMIN_NOTIFICATION_RSOCKET_URL;
  private client: RSocketClient<string, string> | null = null;
  private setupMessage = "setup";

  constructor() {
    this.onReceivedNotification = (message) => console.log(message);
  }

  stopMonitoring(): void {
    this.client?.close();
  }

  startMonitoring(onReceivedNotification: (message: string) => void): void {

    this.onReceivedNotification = onReceivedNotification;

    const transportOptions = {
      url: this.rSocketUrl,
      wsCreator: (url: string) => {
        return new WebSocket(url);
      }
    };
    const setup = {
      keepAlive: 60000,
      lifetime: 180000,
      dataMimeType: 'application/json',
      metadataMimeType: 'application/json'
    };

    const transport = new RSocketWebsocketClient(transportOptions);
    const responder = new RSocketResponder(this.onReceivedNotification, this.setupMessage);

    this.client = new RSocketClient({setup: setup, transport: transport, responder: responder});

    this.client.connect().subscribe({
      onComplete: (rsocket) => {
        rsocket.fireAndForget({data: this.setupMessage});
      }
    })
  }
}

class RSocketResponder implements Responder<string, string> {

  private readonly callback: (message: string) => void;
  private readonly setupMessage: string;

  constructor(callback: (message: string) => void, setupMessage: string) {
    this.callback = callback;
    this.setupMessage = setupMessage;
  }

  fireAndForget(payload: Payload<string, string>): void {
    if (payload.data != null && payload.data != this.setupMessage) {
      this.callback(payload.data);
    }
  }

  metadataPush(payload: Payload<string, string>): Single<void> {
    return Single.error(new Error());
  }

  requestChannel(payloads: Flowable<Payload<string, string>>): Flowable<Payload<string, string>> {
    return Flowable.error(new Error());
  }

  requestResponse(payload: Payload<string, string>): Single<Payload<string, string>> {
    return Single.error(new Error());
  }

  requestStream(payload: Payload<string, string>): Flowable<Payload<string, string>> {
    return Flowable.error(new Error());
  }
}
