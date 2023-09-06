import {ChangeDetectorRef, Component, Input, SimpleChanges} from '@angular/core';
import {
  Observable,
  ReplaySubject, shareReplay, Subject,
  Subscription,
  take, tap
} from "rxjs";
import {Page} from "../../../model/page.model";
import {CommentService} from "../../../service/comment.service";
import {MatDialogService} from "../../../service/mat-dialog.service";
import {AuthService} from "../../../service/auth.service";
import {RequestService} from "../../../service/request.service";
import {NotificationService} from "../../../service/notification.service";

@Component({
  selector: 'app-comment',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss']
})
export class CommentsComponent {
  @Input() postIdentifier: string;
  postComments: ReplaySubject<Page>;
  @Input() currentCommentPage: { [postId: string]: number };
  isCommentPaginationVisible$: Observable<boolean>;
  @Input() commentVisibility: boolean;
  currentUser: string | null;
  subscription: Subscription;
  constructor(private commentService: CommentService, private matDialogService: MatDialogService,
              private changeDetectorRef: ChangeDetectorRef, private authService: AuthService, private requestService: RequestService,
              private notificationService: NotificationService) {
    this.postIdentifier = '';
    this.postComments = new ReplaySubject<Page>(1);
    this.currentCommentPage = {};
    this.isCommentPaginationVisible$ = new Observable<boolean>();
    this.commentVisibility = false;
    this.currentUser = this.authService.getUsername();
    this.subscription = new Subscription();
  }

  async ngOnInit() {
    this.currentCommentPage[this.postIdentifier] = 0;
    this.fetchComments(this.postIdentifier);
    this.commentService.commentCreated$.subscribe((comment) => {
      if (this.postIdentifier) {
        this.fetchComments(this.postIdentifier);
        let token = this.authService.getAuthToken();

          if(token && comment.postAuthorUsername != comment.username) {
          this.subscription = this.requestService.sendCommentNotification(token, comment.identifier, comment.username + ' commented on your post')
              .pipe(take(1), shareReplay(1)).subscribe();
        }
      } else {
        this.notificationService.sendErrorNotificationToSlack("PostIdentifier not found",
            "Angular during comment component creation subscription", new Date());
      }
    });
  }

  fetchComments(postId: string): void {
    const sub = this.commentService.getComments(postId, this.currentCommentPage[postId]).pipe(
        take(1),
        tap(commentPage =>
            this.postComments.next(commentPage)
        )).subscribe();
    this.subscription.add(sub);
    this.isCommentPaginationVisible$ = this.isCommentPaginationVisible(this.postComments);
  }

  previousCommentPage(postId: string) {
    const currentPage = this.currentCommentPage[postId];
    if (currentPage > 0) {
      this.currentCommentPage[postId] = currentPage - 1;
      this.fetchComments(postId);
    }
  }

  async nextCommentPage(postId: string) {
    const currentPage = this.currentCommentPage[postId] || 0;
    const postComments = this.postComments;
    if (postComments) {
      const sub = postComments.pipe(take(1)).subscribe(async (page: Page) => {
        const totalPages = page?.totalPages || 0;
        if (currentPage < totalPages - 1) {
          this.currentCommentPage[postId] = currentPage + 1;
          await this.fetchComments(postId);
        }
      });
      this.subscription.add(sub);
    }
  }

  createComment(id: string) {
    this.matDialogService.createComment(id);
  }

  updateComment(commentId: string, commentTitle: string, commentDescription: string) {
    this.matDialogService.updateComment(commentId, commentTitle, commentDescription);
  }

  isCommentPaginationVisible(postComments: Observable<Page>): Observable<boolean> {
    return this.commentService.isCommentPaginationVisible(postComments);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
    this.postComments = new ReplaySubject<Page>(1);
    this.commentService.clearSubject();
  }
}