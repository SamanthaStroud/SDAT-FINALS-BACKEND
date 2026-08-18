package com.sdatfinals.backend.bookmark;

import com.sdatfinals.backend.user.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public List<String> getBookmarks(@AuthenticationPrincipal UserPrincipal principal) {
        return bookmarkService.getBookmarkedSlugs(principal.getUser());
    }

    @PostMapping("/{topicSlug}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBookmark(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String topicSlug) {
        bookmarkService.addBookmark(principal.getUser(), topicSlug);
    }

    @DeleteMapping("/{topicSlug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookmark(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String topicSlug) {
        bookmarkService.removeBookmark(principal.getUser(), topicSlug);
    }
}
