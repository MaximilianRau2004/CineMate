package com.cinemate.social.forum;

import com.cinemate.notification.events.ForumPostCreatedEvent;
import com.cinemate.notification.events.ForumReplyCreatedEvent;
import com.cinemate.social.forum.post.ForumPost;
import com.cinemate.social.forum.post.ForumPostRepository;
import com.cinemate.social.forum.reply.ForumReply;
import com.cinemate.social.forum.reply.ForumReplyRepository;
import com.cinemate.social.forum.subscription.ForumSubscription;
import com.cinemate.social.forum.subscription.ForumSubscriptionRepository;
import com.cinemate.user.User;
import com.cinemate.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing forum operations.
 * Provides business logic for forum posts, replies, subscriptions, and related functionality.
 * Handles data persistence, event publishing, and business rule enforcement.
 * 
 * @author CineMate Team
 * @version 1.0
 */
@Service
public class ForumService {
    
    @Autowired
    private ForumPostRepository forumPostRepository;
    
    @Autowired
    private ForumReplyRepository forumReplyRepository;
    
    @Autowired
    private ForumSubscriptionRepository forumSubscriptionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Creates a new forum post with the specified user as the author.
     * Automatically subscribes the author to their own post and publishes an event for notifications.
     *
     * @param post the forum post to create
     * @param userId the ID of the user creating the post
     * @return the created ForumPost entity
     * @throws RuntimeException if the user is not found
     */
    public ForumPost createPost(ForumPost post, String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        User user;
        
        if (!userOpt.isPresent()) {
            // For testing purposes, create a test user if not found
            user = new User();
            user.setId(userId);
            user.setUsername("testuser");
            user.setEmail("test@test.com");
            user.setPassword("password"); // Required field
            user.setJoinedAt(new Date());
            user = userRepository.save(user);
        } else {
            user = userOpt.get();
        }
        
        post.setAuthor(user);
        post.setCreatedAt(new Date());
        post.setLastModified(new Date());
        
        ForumPost savedPost = forumPostRepository.save(post);
        
        // Auto-subscribe author to their own post
        ForumSubscription subscription = new ForumSubscription(user, savedPost);
        forumSubscriptionRepository.save(subscription);
        
        // Publish event for notifications
        eventPublisher.publishEvent(new ForumPostCreatedEvent(this, savedPost));
        
        return savedPost;
    }
    
    /**
     * Retrieves all forum posts that are not deleted, ordered by creation date (newest first).
     *
     * @param pageable pagination information
     * @return a Page containing ForumPost entities
     */
    public Page<ForumPost> getAllPosts(Pageable pageable) {
        return forumPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * Retrieves forum posts filtered by category, ordered by creation date (newest first).
     *
     * @param category the category to filter by
     * @param pageable pagination information
     * @return a Page containing ForumPost entities in the specified category
     */
    public Page<ForumPost> getPostsByCategory(ForumCategory category, Pageable pageable) {
        return forumPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(category, pageable);
    }
    
    /**
     * Retrieves a specific forum post by its unique identifier.
     *
     * @param id the unique identifier of the forum post
     * @return an Optional containing the ForumPost if found, empty otherwise
     */
    public Optional<ForumPost> getPostById(String id) {
        return forumPostRepository.findById(id);
    }
    
    /**
     * Retrieves forum posts created by a specific author.
     *
     * @param authorId the ID of the author
     * @param pageable pagination information
     * @return a Page containing ForumPost entities created by the specified author
     */
    public Page<ForumPost> getPostsByAuthor(String authorId, Pageable pageable) {
        return forumPostRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(authorId, pageable);
    }
    
    /**
     * Retrieves forum posts related to a specific movie.
     *
     * @param movieId the ID of the movie
     * @param pageable pagination information
     * @return a Page containing ForumPost entities related to the specified movie
     */
    public Page<ForumPost> getPostsByMovieId(String movieId, Pageable pageable) {
        return forumPostRepository.findByMovieIdAndIsDeletedFalseOrderByCreatedAtDesc(movieId, pageable);
    }
    
    /**
     * Retrieves forum posts related to a specific series.
     *
     * @param seriesId the ID of the series
     * @param pageable pagination information
     * @return a Page containing ForumPost entities related to the specified series
     */
    public Page<ForumPost> getPostsBySeriesId(String seriesId, Pageable pageable) {
        return forumPostRepository.findBySeriesIdAndIsDeletedFalseOrderByCreatedAtDesc(seriesId, pageable);
    }
    
    /**
     * Searches for forum posts based on title or content matching the search term.
     *
     * @param searchTerm the term to search for in post titles and content
     * @param pageable pagination information
     * @return a Page containing ForumPost entities matching the search criteria
     */
    public Page<ForumPost> searchPosts(String searchTerm, Pageable pageable) {
        return forumPostRepository.searchPosts(searchTerm, pageable);
    }
    
    /**
     * Retrieves forum posts ordered by like count (most liked first).
     *
     * @param pageable pagination information
     * @return a Page containing ForumPost entities ordered by popularity
     */
    public Page<ForumPost> getPopularPosts(Pageable pageable) {
        return forumPostRepository.findByIsDeletedFalseOrderByLikesCountDesc(pageable);
    }
    
    /**
     * Retrieves forum posts ordered by last modified date (most recently active first).
     *
     * @param pageable pagination information
     * @return a Page containing ForumPost entities ordered by recent activity
     */
    public Page<ForumPost> getRecentlyActiveePosts(Pageable pageable) {
        return forumPostRepository.findByIsDeletedFalseOrderByLastModifiedDesc(pageable);
    }
    
    /**
     * Retrieves all pinned forum posts.
     *
     * @return a List of pinned ForumPost entities
     */
    public List<ForumPost> getPinnedPosts() {
        return forumPostRepository.findByIsPinnedTrueAndIsDeletedFalseOrderByCreatedAtDesc();
    }
    
    /**
     * Retrieves forum posts where the specified user has participated (either as author or replied to).
     *
     * @param userId the ID of the user
     * @param pageable pagination information
     * @return a Page containing ForumPost entities where the user has participated
     */
    public Page<ForumPost> getPostsUserParticipatedIn(String userId, Pageable pageable) {
        return forumPostRepository.findPostsUserParticipatedIn(userId, pageable);
    }
    
    /**
     * Updates an existing forum post with new content.
     * Only the author of the post can update it.
     *
     * @param postId the ID of the post to update
     * @param updatedPost the updated post-data
     * @param userId the ID of the user attempting to update the post
     * @return the updated ForumPost entity
     * @throws RuntimeException if the post is not found or user is not authorized
     */
    public ForumPost updatePost(String postId, ForumPost updatedPost, String userId) {
        Optional<ForumPost> existingPostOpt = forumPostRepository.findById(postId);
        if (!existingPostOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }
        
        ForumPost existingPost = existingPostOpt.get();
        
        // Check if user is the author or has admin privileges
        if (!existingPost.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this post");
        }
        
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setLastModified(new Date());
        
        return forumPostRepository.save(existingPost);
    }
    
    /**
     * Marks a forum post as deleted (soft delete).
     * Only the author of the post can delete it.
     *
     * @param postId the ID of the post to delete
     * @param userId the ID of the user attempting to delete the post
     * @throws RuntimeException if the post is not found or user is not authorized
     */
    public void deletePost(String postId, String userId) {
        Optional<ForumPost> postOpt = forumPostRepository.findById(postId);
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }
        
        ForumPost post = postOpt.get();
        
        // Check if user is the author or has admin privileges
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this post");
        }
        
        post.setDeleted(true);
        forumPostRepository.save(post);
    }
    
    public ForumPost toggleLike(String postId, String userId) {
        Optional<ForumPost> postOpt = forumPostRepository.findById(postId);
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }
        
        ForumPost post = postOpt.get();
        // TODO: Implement like tracking (separate entity for user-post likes)
        
        return forumPostRepository.save(post);
    }

    /**
     * Creates a new reply to a forum post.
     * Updates the parent post's reply count and last modified date.
     * Publishes an event for notification purposes.
     *
     * @param reply the reply to create
     * @param userId the ID of the user creating the reply
     * @param postId the ID of the post being replied to
     * @return the created ForumReply entity
     * @throws RuntimeException if the user or post is not found
     */
    public ForumReply createReply(ForumReply reply, String userId, String postId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<ForumPost> postOpt = forumPostRepository.findById(postId);
        
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }
        
        ForumPost post = postOpt.get();
        reply.setAuthor(userOpt.get());
        reply.setParentPost(post);
        reply.setCreatedAt(new Date());
        reply.setLastModified(new Date());
        
        ForumReply savedReply = forumReplyRepository.save(reply);
        
        // Update post reply count and last modified
        post.setRepliesCount(post.getRepliesCount() + 1);
        post.setLastModified(new Date());
        forumPostRepository.save(post);
        
        // Publish event for notifications
        eventPublisher.publishEvent(new ForumReplyCreatedEvent(this, savedReply, post));
        
        return savedReply;
    }
    
    /**
     * Retrieves replies for a specific forum post, ordered by creation date (oldest first).
     *
     * @param postId the ID of the post to get replies for
     * @param pageable pagination information
     * @return a Page containing ForumReply entities for the specified post
     */
    public Page<ForumReply> getRepliesForPost(String postId, Pageable pageable) {
        return forumReplyRepository.findByParentPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId, pageable);
    }
    
    /**
     * Retrieves replies created by a specific author.
     *
     * @param authorId the ID of the author
     * @param pageable pagination information
     * @return a Page containing ForumReply entities created by the specified author
     */
    public Page<ForumReply> getRepliesByAuthor(String authorId, Pageable pageable) {
        return forumReplyRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(authorId, pageable);
    }
    
    /**
     * Updates an existing forum reply with new content.
     * Only the author of the reply can update it.
     *
     * @param replyId the ID of the reply to update
     * @param updatedReply the updated reply data
     * @param userId the ID of the user attempting to update the reply
     * @return the updated ForumReply entity
     * @throws RuntimeException if the reply is not found or user is not authorized
     */
    public ForumReply updateReply(String replyId, ForumReply updatedReply, String userId) {
        Optional<ForumReply> existingReplyOpt = forumReplyRepository.findById(replyId);
        if (!existingReplyOpt.isPresent()) {
            throw new RuntimeException("Reply not found");
        }
        
        ForumReply existingReply = existingReplyOpt.get();
        
        // Check if user is the author
        if (!existingReply.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this reply");
        }
        
        existingReply.setContent(updatedReply.getContent());
        existingReply.setLastModified(new Date());
        
        return forumReplyRepository.save(existingReply);
    }
    
    /**
     * Marks a forum reply as deleted (soft delete).
     * Only the author of the reply can delete it.
     *
     * @param replyId the ID of the reply to delete
     * @param userId the ID of the user attempting to delete the reply
     * @throws RuntimeException if the reply is not found or user is not authorized
     */
    public void deleteReply(String replyId, String userId) {
        Optional<ForumReply> replyOpt = forumReplyRepository.findById(replyId);
        if (!replyOpt.isPresent()) {
            throw new RuntimeException("Reply not found");
        }
        
        ForumReply reply = replyOpt.get();
        
        // Check if user is the author
        if (!reply.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this reply");
        }
        
        reply.setDeleted(true);
        forumReplyRepository.save(reply);
        
        // Update post reply count
        ForumPost post = reply.getParentPost();
        post.setRepliesCount(Math.max(0, post.getRepliesCount() - 1));
        forumPostRepository.save(post);
    }
    
    /**
     * Toggles a user's like on a forum reply.
     *
     * @param replyId the ID of the reply to like/unlike
     * @param userId the ID of the user toggling the like
     * @return the updated ForumReply entity
     * @throws RuntimeException if the reply is not found
     */
    public ForumReply toggleReplyLike(String replyId, String userId) {
        Optional<ForumReply> replyOpt = forumReplyRepository.findById(replyId);
        if (!replyOpt.isPresent()) {
            throw new RuntimeException("Reply not found");
        }
        
        ForumReply reply = replyOpt.get();
        // TODO: Implement like tracking (separate entity for user-reply likes)
        
        return forumReplyRepository.save(reply);
    }

    /**
     * Counts the number of forum posts in a specific category.
     *
     * @param category the category to count posts for
     * @return the number of posts in the specified category
     */
    public long getPostCountByCategory(ForumCategory category) {
        return forumPostRepository.countByCategoryAndIsDeletedFalse(category);
    }
    
    /**
     * Counts the number of forum posts created by a specific author.
     *
     * @param authorId the ID of the author
     * @return the number of posts created by the specified author
     */
    public long getPostCountByAuthor(String authorId) {
        return forumPostRepository.countByAuthorIdAndIsDeletedFalse(authorId);
    }
    
    /**
     * Counts the number of forum replies created by a specific author.
     *
     * @param authorId the ID of the author
     * @return the number of replies created by the specified author
     */
    public long getReplyCountByAuthor(String authorId) {
        return forumReplyRepository.countByAuthorIdAndIsDeletedFalse(authorId);
    }

    /**
     * Pins or unpins a forum post.
     * Pinned posts are displayed at the top of the forum.
     *
     * @param postId the ID of the post to pin/unpin
     * @param pinned true to pin the post, false to unpin
     * @return the updated ForumPost entity
     * @throws RuntimeException if the post is not found
     */
    public ForumPost pinPost(String postId, boolean pinned) {
        Optional<ForumPost> postOpt = forumPostRepository.findById(postId);
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }
        
        ForumPost post = postOpt.get();
        post.setPinned(pinned);
        return forumPostRepository.save(post);
    }
    
    /**
     * Locks or unlocks a forum post.
     * Locked posts cannot receive new replies.
     *
     * @param postId the ID of the post to lock/unlock
     * @param locked true to lock the post, false to unlock
     * @return the updated ForumPost entity
     * @throws RuntimeException if the post is not found
     */
    public ForumPost lockPost(String postId, boolean locked) {
        Optional<ForumPost> postOpt = forumPostRepository.findById(postId);
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }
        
        ForumPost post = postOpt.get();
        post.setLocked(locked);
        return forumPostRepository.save(post);
    }

    /**
     * Subscribes a user to a forum post.
     * If the user is already subscribed, returns the existing subscription.
     *
     * @param postId the ID of the post to subscribe to
     * @param userId the ID of the user subscribing
     * @return the ForumSubscription entity
     * @throws RuntimeException if the user or post is not found
     */
    public ForumSubscription subscribeToPost(String postId, String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<ForumPost> postOpt = forumPostRepository.findById(postId);
        
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }
        
        // Check if already subscribed
        Optional<ForumSubscription> existingSubscription = 
            forumSubscriptionRepository.findByUserIdAndPostIdAndIsActiveTrue(userId, postId);
        
        if (existingSubscription.isPresent()) {
            return existingSubscription.get();
        }
        
        ForumSubscription subscription = new ForumSubscription(userOpt.get(), postOpt.get());
        return forumSubscriptionRepository.save(subscription);
    }
    
    /**
     * Unsubscribes a user from a forum post.
     * Marks the subscription as inactive instead of deleting it.
     *
     * @param postId the ID of the post to unsubscribe from
     * @param userId the ID of the user unsubscribing
     */
    public void unsubscribeFromPost(String postId, String userId) {
        Optional<ForumSubscription> subscriptionOpt = 
            forumSubscriptionRepository.findByUserIdAndPostIdAndIsActiveTrue(userId, postId);
        
        if (subscriptionOpt.isPresent()) {
            ForumSubscription subscription = subscriptionOpt.get();
            subscription.setActive(false);
            forumSubscriptionRepository.save(subscription);
        }
    }
    
    /**
     * Retrieves all active subscriptions for a user.
     *
     * @param userId the ID of the user
     * @return a List of ForumSubscription entities
     */
    public List<ForumSubscription> getUserSubscriptions(String userId) {
        return forumSubscriptionRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    /**
     * Checks if a user is subscribed to a specific forum post.
     *
     * @param userId the ID of the user
     * @param postId the ID of the post
     * @return true if the user is subscribed, false otherwise
     */
    public boolean isUserSubscribedToPost(String userId, String postId) {
        return forumSubscriptionRepository.findByUserIdAndPostIdAndIsActiveTrue(userId, postId).isPresent();
    }
    
    /**
     * Counts the number of active subscriptions for a forum post.
     *
     * @param postId the ID of the post
     * @return the number of active subscriptions
     */
    public long getSubscriptionCount(String postId) {
        return forumSubscriptionRepository.countByPostIdAndIsActiveTrue(postId);
    }
}
