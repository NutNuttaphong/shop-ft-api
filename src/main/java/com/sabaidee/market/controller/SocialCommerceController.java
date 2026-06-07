package com.sabaidee.market.controller;

import com.sabaidee.market.dto.response.ApiResponse;
import com.sabaidee.market.model.FeedPost;
import com.sabaidee.market.model.LiveStream;
import com.sabaidee.market.model.ShopeeVideo;
import com.sabaidee.market.repository.FeedPostRepository;
import com.sabaidee.market.repository.LiveStreamRepository;
import com.sabaidee.market.repository.ShopeeVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialCommerceController {

    private final LiveStreamRepository liveStreamRepository;
    private final ShopeeVideoRepository shopeeVideoRepository;
    private final FeedPostRepository feedPostRepository;

    // --- LIVE STREAMS ---

    @GetMapping("/live")
    public ResponseEntity<ApiResponse<List<LiveStream>>> getLiveStreams() {
        List<LiveStream> streams = liveStreamRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(streams));
    }

    @PostMapping("/live")
    public ResponseEntity<ApiResponse<LiveStream>> createLiveStream(@RequestBody LiveStream stream) {
        stream.setCreatedAt(Instant.now());
        stream.setStatus("STREAMING");
        stream.setViewerCount(45 + (int) (Math.random() * 20)); // Base initial viewers
        stream.setLikeCount(0);
        LiveStream saved = liveStreamRepository.save(stream);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(saved));
    }

    @PutMapping("/live/{id}/status")
    public ResponseEntity<ApiResponse<LiveStream>> updateLiveStatus(
            @PathVariable String id, 
            @RequestParam String status) {
        LiveStream stream = liveStreamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบไลฟ์ไอดี: " + id));
        stream.setStatus(status);
        if ("ENDED".equals(status)) {
            stream.setViewerCount(0);
        }
        return ResponseEntity.ok(ApiResponse.success(liveStreamRepository.save(stream)));
    }

    @PutMapping("/live/{id}/pin")
    public ResponseEntity<ApiResponse<LiveStream>> pinProduct(
            @PathVariable String id, 
            @RequestParam(required = false) String productId) {
        LiveStream stream = liveStreamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบไลฟ์ไอดี: " + id));
        stream.setPinnedProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(liveStreamRepository.save(stream)));
    }

    @PutMapping("/live/{id}/interact")
    public ResponseEntity<ApiResponse<LiveStream>> interactLive(
            @PathVariable String id, 
            @RequestParam(required = false) Integer addLikes,
            @RequestParam(required = false) Integer addViewers) {
        LiveStream stream = liveStreamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบไลฟ์ไอดี: " + id));
        if (addLikes != null) {
            stream.setLikeCount(stream.getLikeCount() + addLikes);
        }
        if (addViewers != null) {
            stream.setViewerCount(Math.max(0, stream.getViewerCount() + addViewers));
        }
        return ResponseEntity.ok(ApiResponse.success(liveStreamRepository.save(stream)));
    }

    // --- SHOPEE VIDEOS ---

    @GetMapping("/videos")
    public ResponseEntity<ApiResponse<List<ShopeeVideo>>> getVideos() {
        List<ShopeeVideo> videos = shopeeVideoRepository.findAll();
        // Seed default mock videos if DB is empty
        if (videos.isEmpty()) {
            List<ShopeeVideo> defaults = List.of(
                ShopeeVideo.builder()
                    .title("รีวิวของกินอร่อยๆ สดใหม่จากสวนกล้วย!")
                    .videoUrl("https://assets.mixkit.co/videos/preview/mixkit-fresh-red-apples-covered-in-water-droplets-34287-large.mp4")
                    .productId("prod-4")
                    .viewCount(1245)
                    .likeCount(340)
                    .clickCount(84)
                    .createdAt(Instant.now().minusSeconds(86400 * 2))
                    .build(),
                ShopeeVideo.builder()
                    .title("ช้อปปิ้งของชำราคาส่ง สบายดีโชห่วย")
                    .videoUrl("https://assets.mixkit.co/videos/preview/mixkit-grocery-shopping-in-the-supermarket-41584-large.mp4")
                    .productId("prod-1")
                    .viewCount(2405)
                    .likeCount(720)
                    .clickCount(230)
                    .createdAt(Instant.now().minusSeconds(86400 * 5))
                    .build()
            );
            shopeeVideoRepository.saveAll(defaults);
            videos = shopeeVideoRepository.findAll();
        }
        return ResponseEntity.ok(ApiResponse.success(videos));
    }

    @PostMapping("/videos")
    public ResponseEntity<ApiResponse<ShopeeVideo>> createVideo(@RequestBody ShopeeVideo video) {
        video.setCreatedAt(Instant.now());
        video.setViewCount(0);
        video.setLikeCount(0);
        video.setClickCount(0);
        ShopeeVideo saved = shopeeVideoRepository.save(video);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(saved));
    }

    @PutMapping("/videos/{id}/interact")
    public ResponseEntity<ApiResponse<ShopeeVideo>> interactVideo(
            @PathVariable String id, 
            @RequestParam String type) {
        ShopeeVideo video = shopeeVideoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิดีโอไอดี: " + id));
        if ("view".equals(type)) {
            video.setViewCount(video.getViewCount() + 1);
        } else if ("like".equals(type)) {
            video.setLikeCount(video.getLikeCount() + 1);
        } else if ("click".equals(type)) {
            video.setClickCount(video.getClickCount() + 1);
        }
        return ResponseEntity.ok(ApiResponse.success(shopeeVideoRepository.save(video)));
    }

    // --- FEED POSTS ---

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<List<FeedPost>>> getFeedPosts() {
        List<FeedPost> posts = feedPostRepository.findAllByOrderByCreatedAtDesc();
        // Seed default mock feed posts if DB is empty
        if (posts.isEmpty()) {
            List<FeedPost> defaults = List.of(
                FeedPost.builder()
                    .caption("กล้วยหอมทองตัดสดใหม่วันนี้ส่งตรงจากสวนลุงพงษ์เลยจ้า! หวาน นุ่ม อร่อย พร้อมส่งด่วนถึงบ้าน 🍌✨")
                    .imageUrl("https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?auto=format&fit=crop&w=600&q=80")
                    .productId("prod-4")
                    .likeCount(45)
                    .createdAt(Instant.now().minusSeconds(3600 * 3))
                    .build(),
                FeedPost.builder()
                    .caption("วัตถุดิบทำกับข้าวคุณภาพระดับพรีเมียม ซื้อตุนไว้ทำมื้อเช้าแสนอร่อยได้ง่ายๆ ช้อปเลย! 🍳🥬")
                    .imageUrl("https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=600&q=80")
                    .productId("prod-3")
                    .likeCount(82)
                    .createdAt(Instant.now().minusSeconds(3600 * 24))
                    .build()
            );
            feedPostRepository.saveAll(defaults);
            posts = feedPostRepository.findAllByOrderByCreatedAtDesc();
        }
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @PostMapping("/feed")
    public ResponseEntity<ApiResponse<FeedPost>> createFeedPost(@RequestBody FeedPost post) {
        post.setCreatedAt(Instant.now());
        post.setLikeCount(0);
        FeedPost saved = feedPostRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(saved));
    }

    @PutMapping("/feed/{id}/like")
    public ResponseEntity<ApiResponse<FeedPost>> likeFeedPost(@PathVariable String id) {
        FeedPost post = feedPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบโพสต์ไอดี: " + id));
        post.setLikeCount(post.getLikeCount() + 1);
        return ResponseEntity.ok(ApiResponse.success(feedPostRepository.save(post)));
    }
}
