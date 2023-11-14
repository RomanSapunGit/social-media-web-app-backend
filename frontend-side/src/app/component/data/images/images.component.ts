import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output, SimpleChanges} from '@angular/core';
import {Observable} from "rxjs";
import {FileDTO} from "../../../model/file.model";
import {ImageService} from "../../../services/image.service";

@Component({
  selector: 'app-image',
  templateUrl: './images.component.html',
  styleUrls: ['./images.component.scss'],
})
export class ImagesComponent {
  @Input() image: FileDTO;
  @Input() images: FileDTO[];
  imagesToDisplay: Observable<string[]>;
  imageToDisplay: Observable<string>;
  showMenu: boolean;
  isClicked: boolean;
  currentImageIndex: number = 0;
  @Input() usernameToDisplay: string;
  @Input() isUserImage: boolean;

  public slideConfig = {
    slidesToShow: 1,
    slidesToScroll: 1,
    infinite: true,
    adaptiveHeight: true,
    lazyLoad: 'ondemand',
    arrows: false,
  };

  constructor(private imageService: ImageService) {
    this.isUserImage = false;
    this.images = [];
    this.image = new FileDTO();
    this.imagesToDisplay = new Observable<string[]>();
    this.imageToDisplay = new Observable<string>();
    this.showMenu = false;
    this.isClicked = false;
    this.usernameToDisplay = '';
  }

  ngAfterViewInit() {
    if (this.isUserImage) {
      const registerImageContainers = document.querySelectorAll('.register-image-container');
      registerImageContainers.forEach((container: Element) => {
        const element = container as HTMLElement;
        element.style.width = '35px';
        element.style.height = '35px';
      });
    }
  }


  onAfterChange(event: any) {
    this.currentImageIndex = event.currentSlide;
  }

  onBeforeChange(event: any) {
    this.currentImageIndex = event.currentSlide;
  }

  ngOnInit() {
    if (this.images && this.images.length > 0) {
      this.imagesToDisplay = this.imageService.fetchImagesFromModel(this.images);
    } else if (this.image && this.image.fileType) {
      this.imageToDisplay = this.imageService.fetchImageFromModel(this.image);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if(changes['images']) {
      this.imagesToDisplay = this.imageService.fetchImagesFromModel(this.images);
      this.currentImageIndex = 0;
    }
  }

  trackByFn(index: any, item: any) {
    return this.currentImageIndex;
  }

  toggleProfileMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.showMenu = !this.showMenu;
  }

  openUserPostsWindow() {

  }


  adjustImageSize(event: MouseEvent) {
    const imageElement = event.target as HTMLImageElement;
    const container = document.querySelector('.carousel-container');

    if (!container) return;

    const containerWidth = container.clientWidth;
    const containerHeight = container.clientHeight;

    imageElement.style.maxWidth = `${containerWidth}px`;
    imageElement.style.maxHeight = `${containerHeight}px`;
  }
  closeDropDown() {
    this.showMenu = false;
  }
}
