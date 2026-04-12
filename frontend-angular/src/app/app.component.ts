import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {CanvasAuthComponent} from './canvasTokenWidget/canvas-auth.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, CanvasAuthComponent, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'hwstr-google-auth';
}
