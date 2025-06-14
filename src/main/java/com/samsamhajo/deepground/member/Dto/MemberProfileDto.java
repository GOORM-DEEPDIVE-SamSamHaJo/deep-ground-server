package com.samsamhajo.deepground.member.Dto;

import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.entity.MemberProfile;
import com.samsamhajo.deepground.techStack.entity.MemberTechStack;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.stream.Collectors;

import static ch.qos.logback.classic.spi.ThrowableProxyVO.build;

@Setter
@Getter
@Builder
public class MemberProfileDto {

    private String profileImage;
    @NotBlank (message = "닉네임을 입력해주세요")
    private String nickname;
    private String introduction;
    private String job;
    private String company;
    @NotBlank (message = "사는 지역을 입력해주세요")
    private String liveIn;
    private String education;
    @NotNull (message = "한가지 이상의 기술 스택을 입력해주세요")
    private List<String> techStack ;
    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String githubUrl;
    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String linkedInUrl;
    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String websiteUrl;
    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String twitterUrl;

    public static MemberProfileDto from(MemberProfile profile, Member member) {

        return MemberProfileDto.builder()
                .profileImage(profile.getProfileImage())
                .nickname(member.getNickname())
                .introduction(profile.getIntroduction())
                .job(profile.getJob())
                .company(profile.getCompany())
                .liveIn(profile.getLiveIn())
                .education(profile.getEducation())
                .techStack(profile.getMemberTechStacks().stream()
                        .map(stack -> stack.getTechStack().getName())
                        .collect(Collectors.toList()))
                .githubUrl(profile.getGithubUrl())
                .linkedInUrl(profile.getLinkedInUrl())
                .websiteUrl(profile.getWebsiteUrl())
                .twitterUrl(profile.getTwitterUrl())
                .build();
    }

}
