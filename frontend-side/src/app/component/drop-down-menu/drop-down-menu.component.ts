import {Component, ElementRef, EventEmitter, HostListener, Input, Output, ViewChild} from '@angular/core';
import {MatDialogService} from "../../services/mat-dialog.service";
import {AuthService} from "../../services/auth.service";
import {RequestService} from "../../services/request.service";
import {Router} from "@angular/router";
import {ServerSendEventService} from "../../services/server-send-event.service";

@Component({
    selector: 'app-drop-down-menu',
    templateUrl: './drop-down-menu.component.html',
    styleUrls: ['./drop-down-menu.component.scss']
})
export class DropDownMenuComponent {
    @Input() isProfileMenu: boolean;
    showConfirmation: boolean;
    confirmed: boolean;
    username: string | null;
    @Input() usernameToDisplay: string;
    @Input() isMenuOpen: boolean;
    @Output() isMenuClose = new EventEmitter<boolean>();

    constructor(private matDialogService: MatDialogService, private authService: AuthService,
                private requestService: RequestService, private router: Router,
                private sseService: ServerSendEventService) {
        this.isProfileMenu = false;
        this.showConfirmation = false;
        this.confirmed = false;
        this.username = localStorage.getItem('username');
        this.usernameToDisplay = '';
        this.isMenuOpen = false;
    }

    displayPostWindow(username: string | null) {
        if (username) {
            this.matDialogService.showPostsByUsername(username);
        }
    }

    createPost() {
        this.matDialogService.createPost();
        if (this.isMenuOpen)
            this.isMenuClose.emit(false);
    }

    confirmLogout(): void {
        this.showConfirmation = true;
    }

    onConfirm(): void {
        this.confirmed = true;
        this.showConfirmation = false;
        this.logout();
    }

    onCancel(): void {
        this.showConfirmation = false;
    }

    logout(): void {
        this.authService.logout();
        this.sseService.completeSSENotificationConnection(this.username);
        this.requestService.logout().subscribe({
            next: () => {
                this.requestService.getCsrf();
                this.router.navigate(["/login"]);
            },
            error: (error: any) => console.log(error.error.message)
        });
    }

}
