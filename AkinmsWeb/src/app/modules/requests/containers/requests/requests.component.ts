import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Pedido } from 'src/app/core/models/productos.model';
import { ProductoService } from 'src/app/core/services/producto.service';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.scss']
})
export class RequestsComponent implements OnInit {
  pedidos$!: Observable<Pedido[]>;
  pedido:Pedido | undefined
  showDetalles:boolean = false
  mensaje$!: Observable<string>;
  constructor(
    private productoService: ProductoService
  ) {
    
   }
   
  ngOnInit(): void {
    this.pedidos$ = this.productoService.getAllOrders();
    //console.log(this.pedidos$);
  }

  showDetallesPedido(p:Pedido){
    this.pedido = p
    this.showDetalles = true
  }
  closeDetalles(){
    this.showDetalles = false;
  }
  orderReady(id:number,p:Pedido){
    this.productoService.updateOrderState(id,p)
    console.log(this.mensaje$)
  }
  
}
