import {Injectable} from "@angular/core";
import {Location} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class RoutingService {
  constructor(private location: Location) {
  }

  clearPathVariable() {
    this.location.go('/main')
  }

  setPathVariable(pathVariable: string) {
    this.location.go('/main/' + pathVariable)
  }
}
