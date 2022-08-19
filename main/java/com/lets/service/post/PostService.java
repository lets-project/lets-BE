package com.lets.service.post;

import com.lets.domain.comment.Comment;
import com.lets.domain.comment.CommentCustomRepository;
import com.lets.domain.comment.CommentRepository;
import com.lets.domain.likePost.LikePost;

import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.likePost.LikePostStatus;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;

import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.domain.user.User;
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;
import com.lets.util.CloudinaryUtil;
import com.lets.web.dto.*;
import com.lets.web.dto.comment.CommentSearchRequestDto;
import com.lets.web.dto.likepost.ChangeLikePostStatusResponseDto;
import com.lets.web.dto.post.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {
    private final LikePostRepository likePostRepository;
    private final PostTechStackRepository postTechStackRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CloudinaryUtil cloudinaryUtil;
    private final CommentRepository commentRepository;

    public List<PostResponseDto> searchPosts(PostSearchRequestDto search, Pageable pageable){

        //postTechStack 한번에 구해와서 애플리케이션에서 각 post 의 태그 정보 조립
        List<PostTechStack> postTechStacks = postTechStackRepository.findPostTechStacks(search, pageable);

        //각 post 의 태그 정보 조립
        return findPostsTags(postTechStacks);

    }
    public List<PostResponseDto> findPosts(User user){
        //postTechStack 한번에 구해와서 애플리케이션에서 각 post 의 태그 정보 조립
        List<PostTechStack> postTechStacks = postTechStackRepository.findAllByUser(user);

        if(postTechStacks == null) return null;

        //각 post 의 태그 정보 조립
        return findPostsTags(postTechStacks);
    }

    public List<PostResponseDto> findPostsTags(List<PostTechStack> postTechStacks){
        //postTechStack에서 post정보만 리스트로 추출
        LinkedHashSet<Post> posts = postTechStacks.stream().map(PostTechStack::getPost).collect(Collectors.toCollection(LinkedHashSet::new));


        //각 post 의 태그 정보 조립
        ArrayList<PostResponseDto> postDtos = new ArrayList<>();
        for(Post post : posts){
            List<Tag> tags = new ArrayList<>();
            for(PostTechStack postTechStack : postTechStacks){
                if(postTechStack.getPost().getId() == post.getId())
                    tags.add(postTechStack.getTag());
            }
            Long commentCount = commentRepository.countByPost(post);
            postDtos.add(PostResponseDto.PostToDto(post, tags, null, commentCount));
        }
        return postDtos;
    }

    public PostResponseDto savePost(User user, PostSaveRequestDto postSaveRequestDto){
        Post post = Post.createPost(user, postSaveRequestDto.getTitle(), postSaveRequestDto.getContent());
        postRepository.save(post);

        List<Tag> tags = tagRepository.findAllByName(postSaveRequestDto.getTags());
        List<PostTechStack> postTechStackList = new ArrayList<>();
        for(Tag tag : tags){
            PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
            postTechStackList.add(postTechStack);
        }

        String profile = cloudinaryUtil.findFileURL(user.getPublicId());
        postTechStackRepository.saveAll(postTechStackList);

        likePostRepository.save(LikePost.createLikePost(user, post));

        return PostResponseDto.PostToDto(post, tags, profile, 0L);
    }

    public PostResponseDto updatePost(User user, Long postId, PostUpdateRequestDto postUpdateRequestDto){
        Post post = postRepository.findOneById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(post.getUser().getId() != user.getId())
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);

        post.change(postUpdateRequestDto.getTitle(), postUpdateRequestDto.getContent());
        List<Tag> tags = tagRepository.findAllByName(postUpdateRequestDto.getTags());

        postTechStackRepository.deleteAllByPost(Arrays.asList(post));

        List<PostTechStack> postTechStackList = new ArrayList<>();
        for(Tag tag : tags){
            PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
            postTechStackList.add(postTechStack);
        }
        String profile = cloudinaryUtil.findFileURL(user.getPublicId());
        postTechStackRepository.saveAll(postTechStackList);

        return PostResponseDto.PostToDto(post, tags, profile, 0L);
    }

    public void deletePost(Long userId,  Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        LikePost likePost = likePostRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_USER));

        postTechStackRepository.deleteAllByPost(Arrays.asList(post));
        likePostRepository.delete(likePost);
        commentRepository.deleteAllByPost(post);
        postRepository.delete(post);
    }

    public PostCommentResponseDto findPost(User user, Long postId) {
        Post post = postRepository.findOneById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        LikePost likePost = null;
        String profile = null;
        String nickname = null;

        if (!ObjectUtils.isEmpty(post.getUser())){
            profile = cloudinaryUtil.findFileURL(post.getUser().getPublicId());
            nickname = post.getUser().getNickname();
        }


        if(user != null) {
             likePost = likePostRepository.findByUserIdAndPostId(user.getId(), postId)
                    .orElseGet(() -> {
                        LikePost likePostCreate = LikePost.createLikePost(user, post);
                        post.addView();
                        return likePostRepository.save(likePostCreate);
                    });
        }

        List<PostTechStack> postTechStackList = postTechStackRepository.findAllByPosts(Collections.singletonList(post));
        List<Tag> tags = new ArrayList<>();
        for (PostTechStack postTechStack : postTechStackList) {
            tags.add(postTechStack.getTag());

        }

        List<Comment> comments = commentRepository.findComments(new CommentSearchRequestDto(post));
        return PostCommentResponseDto.PostToDto(post, likePost == null ? LikePostStatus.INACTIVE : likePost.getStatus() , tags, comments, profile, cloudinaryUtil, nickname);
    }


    public ChangeLikePostStatusResponseDto changeLikeStatus(Long userId, Long postId){
        LikePost likePost = likePostRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        likePost.changeLikeStatus();

        return new ChangeLikePostStatusResponseDto(likePost.getPost().getLikeCount(), likePost.getStatus());
    }

    public List<PostRecommendResponseDto> recommendedPosts(Long userId, Long id, PostRecommendRequestDto postRecommendRequestDto) {
        List<PostTechStack> recommendedPosts = postTechStackRepository.findRecommendedPosts(postRecommendRequestDto, userId, id);

        LinkedHashSet<Post> posts = recommendedPosts.stream().map(PostTechStack::getPost).filter(p -> p.getUser().getId() != userId).collect(Collectors.toCollection(LinkedHashSet::new));

        if(posts.size() < 4){
            List<PostTechStack> recommendedPost2 = postTechStackRepository.findRecommendedPosts(new PostRecommendRequestDto(new ArrayList<>()), userId, id);
            LinkedHashSet<Post> collect = recommendedPost2.stream().map(PostTechStack::getPost).collect(Collectors.toCollection(LinkedHashSet::new));
            for(Post post : collect){
                posts.add(post);
                if(posts.size() >= 4) break;
            }
        }
        List<PostRecommendResponseDto> list = new ArrayList<>();

        for(Post post : posts){
            list.add(PostRecommendResponseDto.PostRecommendToDto(post));
            if(list.size() >= 4) break;
        }

        return list;
    }
}
