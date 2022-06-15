package com.lets.web.controller;



import com.lets.domain.user.User;
import com.lets.domain.userTechStack.UserTechStack;
import com.lets.security.UserPrincipal;
import com.lets.service.likePost.LikePostService;
import com.lets.service.post.PostService;
import com.lets.service.user.UserService;
import com.lets.service.userTechStack.UserTechStackService;

import com.lets.util.CloudinaryUtil;
import com.lets.util.FileUtil;
import com.lets.web.dto.*;

import com.lets.web.dto.likepost.LikePostResponseDto;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.user.SettingRequestDto;
import com.lets.web.dto.user.SettingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final LikePostService likePostService;
    private final UserTechStackService userTechStackService;
    private final CloudinaryUtil cloudinaryUtil;
    private final FileUtil fileUtil;

    /**
     * 작성 글 조회
     */
    @GetMapping("/myPosts")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<PostResponseDto> findMyPosts(@AuthenticationPrincipal UserPrincipal principal){


        //유저 조회
        User findUser = userService.findOneById(principal.getId());

        //작성 글 조회
        return postService.findPosts(findUser);

    }


    /**
     * 관심 글 조회
     */
    @GetMapping("/myLikes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<LikePostResponseDto> findMyLikes(@AuthenticationPrincipal UserPrincipal principal){


        //유저 조회
        User findUser = userService.findOneById(principal.getId());

        //관심 글 조회
        return likePostService.findLikePosts(findUser);

    }

    @GetMapping("/setting")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SettingResponseDto getSetting(@AuthenticationPrincipal UserPrincipal principal){
        //유저 정보 조회
        User findUser = userService.findOneById(principal.getId());

        //유저 태그 정보 조회
        List<UserTechStack> userTechStacks = userTechStackService.findAllByUser(findUser);

        //프로필 URI
        String profile = cloudinaryUtil.findFileURL(findUser.getPublicId());

        return SettingResponseDto.toDto(profile, findUser.getNickname(), userTechStacks);

    }

    @PatchMapping("/setting")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponseDto setSetting(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody SettingRequestDto settingDto){


        File file = null;
        String profileStatus = "PRIVATE";

        String profile = settingDto.getProfile();

        //"KEEP" -> 기존 이미지 유지
        //"PUBLIC" -> 기본 이미지로 변경
        //그 외 -> 새로운 이미지로 변경
        if(profile.equals("KEEP") || profile.equals("PUBLIC")){
            profileStatus = profile;
        }else{
            //file변환
            file = fileUtil.decodeFile(settingDto.getProfile());
        }


        //유저 조회
        User findUser = userService.findOneById(principal.getId());

        //설정 변경
        userService.change(findUser, profileStatus, file, settingDto);

        return new ApiResponseDto(true, "수정 성공하였습니다.");
    }

}
