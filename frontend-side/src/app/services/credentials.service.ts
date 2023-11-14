import {Injectable} from '@angular/core';
import {RequestService} from "./request.service";
import {Router} from "@angular/router";
import {NotificationService} from "./notification.service";
import {TokenModel} from "../model/token.model";

@Injectable({
    providedIn: 'root'
})
export class CredentialsService {

    constructor(private requestService: RequestService, private router: Router, private notificationService: NotificationService) {
    }

    registerAndRedirect(registerData: FormData): void {
        this.requestService.register(registerData).subscribe(
            {
                next: (response: any) => {
                    if (response.username) {
                        this.router.navigate(['/main']).then(() => {
                            console.log('Redirected to main page');
                        })
                    } else {
                        this.notificationService.showNotification('username or email already exists', true);
                    }
                },
                error: (error: any) =>
                    console.log('Error during login ' + error.error.message)
            }
        );
    }

    loginAndRedirect(loginData: { username: string, password: string }): void {
        this.requestService.login(loginData).subscribe(
            {
                next: (response: any) => {
                    if (response.username) {
                        console.log(response.username)
                        localStorage.setItem("username", response.username);
                        this.router.navigate(['/main']).then(() => {
                            console.log('Redirected to main page');
                        })
                    } else {
                        this.notificationService.showNotification('Wrong username or password', true);
                    }
                },
                error: (error: any) =>
                    console.log('Error during login ' + error.error.message),
            })
    }

    loginViaGoogleAndRedirect(token: string): void {
        this.requestService.loginViaGoogle(token).subscribe(
            {
                next: (response: any) => {
                    if (response.token) {
                        localStorage.setItem("username", response.username);
                        this.router.navigate(['/main'])
                    } else {
                        this.notificationService.showNotification('Something went wrong with your google auth', true);
                    }
                },
                error: (error: any) =>
                    console.log('Error during login ' + error.error.message)
            }
        );
    }
}
