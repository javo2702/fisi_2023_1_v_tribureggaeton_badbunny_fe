import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import {throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import { Respuesta, PedidoRespuesta, Pedido } from '../models/productos.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  constructor(
    private http:HttpClient
  ) { }

  login(correo:string,pass:string){
    let headers = new HttpHeaders();
    headers= headers.set('Ocp-Apim-Subscription-Key', '216cf1bc91394a43a28bd73ecce8641f');
    return this.http.post<any>(`${environment.api_url}ux-gestion-productos/akinms/atencion-cliente/v1/listar-productos/4`,{headers})
    .pipe(
      map(/* 
        mensaje=>mensaje['mensaje'], */
        productos => productos['productos']),
      catchError(this.handleError)
    )
  }
  getAllProducts(){
    let headers = new HttpHeaders();
    headers= headers.set('Ocp-Apim-Subscription-Key', '216cf1bc91394a43a28bd73ecce8641f');
    return this.http.get<Respuesta>(`${environment.api_url}ux-gestion-productos/akinms/atencion-cliente/v1/listar-productos/4`,{headers})
    .pipe(
      map(/* 
        mensaje=>mensaje['mensaje'], */
        productos => productos['productos']),
      catchError(this.handleError)
    )
  }
  getAllOrders(){
    let headers = new HttpHeaders();
    headers= headers.set('Ocp-Apim-Subscription-Key', '216cf1bc91394a43a28bd73ecce8641f');
    return this.http.get<PedidoRespuesta>(`${environment.api_url}ux-gestion-pedidos/akinms/atencion-cliente/v1/consultar-pedidos/4`,{headers})
    .pipe(
      map(/* 
        mensaje=>mensaje['mensaje'], */
        pedidos => pedidos['pedidos']),
      catchError(this.handleError)
    )
  }
  updateOrderState(idpedido:number,pedido:Pedido){
    console.log(pedido)
    pedido.estado = "Listo"
    /*let headers = new HttpHeaders();
    headers.set('Ocp-Apim-Subscription-Key', '216cf1bc91394a43a28bd73ecce8641f');
    headers.set('Content-Type', 'application/json')
    headers.set('Access-Control-Allow-Origin', 'http://localhost:4200/')*/
    let headers = new HttpHeaders({
      'Ocp-Apim-Subscription-Key': '216cf1bc91394a43a28bd73ecce8641f',
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': 'https://tu-aplicacion-angular.com' // Reemplaza esto con el origen de tu aplicación Angular
    })
    return this.http.put<any>(`${environment.api_url}ux-gestion-pedidos/akinms/atencion-cliente/v1/actualizar-estado/${idpedido}`,pedido,{headers})
    .subscribe((data)=>{
      console.log(data)
    })
    /*.pipe(
      map( 
        mensaje=>mensaje),
      catchError(this.handleError)
    )*/
  }
  private handleError(err:any){
    let errorMessage: string;
    console.log(err.error)
    errorMessage= `${err.error}`;
    return throwError(errorMessage);
  }
}

export interface Estado{
  estado:string
}