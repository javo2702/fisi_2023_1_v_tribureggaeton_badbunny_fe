import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Producto } from 'src/app/core/models/productos.model';
import { ProductoService } from 'src/app/core/services/producto.service';


@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})

export class InventoryComponent implements OnInit {
  productos$!: Observable<Producto[]>;


  constructor(
    private productoService: ProductoService
  ) {
    
   }

  ngOnInit(): void {
    this.productos$ = this.productoService.getAllProducts();
    console.log(this.productos$);
    console.log("Jello horws")
  }

  
}
