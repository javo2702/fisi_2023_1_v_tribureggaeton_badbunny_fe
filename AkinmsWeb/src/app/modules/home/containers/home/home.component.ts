import { Component, Input, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { Producto } from 'src/app/core/models/productos.model';
import { ProductoService } from 'src/app/core/services/producto.service';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  productos$!: Observable<Producto[]>;
  

  constructor(
    private productoService: ProductoService
  ) { }

  ngOnInit(): void {
    this.productos$ = this.productoService.getAllProducts();
    console.log(this.productos$);
    
  }

}
