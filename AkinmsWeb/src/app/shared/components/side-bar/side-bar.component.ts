import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.scss']
})
export class SideBarComponent implements OnInit {
  @ViewChild('side_bar_links') side_bar_links!: ElementRef;
  

  constructor() { }

  ngOnInit(): void {
  }

  openCloseMenu(){
    this.side_bar_links.nativeElement.classList.toggle('d-block')
  }
}
