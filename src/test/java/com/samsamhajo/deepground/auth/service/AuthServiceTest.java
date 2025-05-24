package com.samsamhajo.deepground.auth.service;

import com.samsamhajo.deepground.auth.dto.*;
import com.samsamhajo.deepground.auth.exception.AuthErrorCode;
import com.samsamhajo.deepground.auth.exception.AuthException;
import com.samsamhajo.deepground.auth.jwt.JwtProvider;
import com.samsamhajo.deepground.auth.repository.RefreshTokenRepository;
import com.samsamhajo.deepground.member.entity.Member;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member member;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    void setup() {
        member = Member.createLocalMember(
                "test@example.com",
                passwordEncoder.encode("password123"),
                "test01"
        );

        member.verify();
        memberRepository.save(member);
    }

    @Test
    void 회원가입_성공() {
        // given
        RegisterRequest request = new RegisterRequest(
                "new@example.com",
                "password123",
                "test02"
        );

        // when
        Long memberId = authService.register(request);

        // then
        Member savedMember = memberRepository.findById(memberId)
                .orElseThrow();

        assertThat(savedMember.getEmail()).isEqualTo(request.getEmail());
        assertThat(savedMember.getNickname()).isEqualTo(request.getNickname());
        assertThat(savedMember.isVerified()).isFalse();
    }
    @Test
    void 이메일_중복_검사_성공() {
        // when & then
       assertDoesNotThrow(() ->
               authService.checkEmailDuplicate("test123@example.com"));
    }

    @Test
    void 이메일_중복_검사_실패() {
        // when & then
        AuthException exception = assertThrows(AuthException.class, () ->
                authService.checkEmailDuplicate(member.getEmail()));

        assertEquals(AuthErrorCode.DUPLICATE_EMAIL, exception.getErrorCode());
    }

    @Test
    void 닉네임_중복_검사_성공() {
        // when & then
        assertDoesNotThrow(() ->
                authService.checkNicknameDuplicate("new"));
    }

    @Test
    void 닉네임_중복_검사_실패() {
        // when & then
        AuthException exception = assertThrows(AuthException.class, () ->
                authService.checkNicknameDuplicate(member.getNickname()));

        assertEquals(AuthErrorCode.DUPLICATE_NICKNAME, exception.getErrorCode());
    }

    @Test
    void 중복된_이메일로_회원가입시_실패() {
        // given
        RegisterRequest request = new RegisterRequest(
                member.getEmail(),
                "password123",
                "test03"
        );

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.register(request));

        assertEquals(AuthErrorCode.DUPLICATE_EMAIL, exception.getErrorCode());
    }

    @Test
    void 중복된_닉네임으로_회원가입시_실패() {
        // given
        RegisterRequest request = new RegisterRequest(
                "test3@example.com",
                "password123",
                member.getNickname()
        );

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.register(request));

        assertEquals(AuthErrorCode.DUPLICATE_NICKNAME, exception.getErrorCode());
    }

    @Test
    void 로그인_성공() {
        // given
        LoginRequest request = new LoginRequest(
                member.getEmail(),
                "password123"
        );

        // when
        LoginResponse response = authService.login(request);

        //then
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(member.getId(), response.getMemberId());
        assertEquals(member.getEmail(), response.getEmail());
        assertEquals(member.getNickname(), response.getNickname());
    }

    @Test
    void 존재하지_않는_이메일로_로그인_실패() {
        // given
        LoginRequest request = new LoginRequest(
                "notfound@example.com",
                "password123"
        );

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.login(request));

        assertEquals(AuthErrorCode.INVALID_EMAIL, exception.getErrorCode());
    }

    @Test
    void 잘못된_비밀번호로_로그인_실패() {
        // given
        LoginRequest request = new LoginRequest(
                member.getEmail(),
                "wrongpassword"
        );

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.login(request));

        assertEquals(AuthErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }

    @Test
    void 로그인_성공_시_리프레시_토큰_저장() {
        // given
        LoginRequest request = new LoginRequest(
                member.getEmail(),
                "password123"
        );

        // when
        LoginResponse response = authService.login(request);

        // then
        String savedRefreshToken = refreshTokenRepository.findByMemberId(member.getId());
        assertNotNull(savedRefreshToken);
        assertEquals(response.getRefreshToken(), savedRefreshToken);
    }

    @Test
    void 액세스토큰_재발급_성공() {
        // given
        String refreshToken = jwtProvider.createRefreshToken(member.getId());
        refreshTokenRepository.save(member.getId(), refreshToken, 3600L);
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        // when
        TokenRefreshResponse response = authService.refreshAccessToken(request);

        // then
        assertNotNull(response.getAccessToken());
        assertTrue(jwtProvider.validateToken(response.getAccessToken()));
        assertEquals(member.getId(), jwtProvider.getMemberId(response.getAccessToken()));
    }

    @Test
    void 리프레시토큰_만료임박_시_모든토큰_재발급() {
        // given
        long shortExpirationSeconds = 3L; // 3초짜리 만료시간
        String oldRefreshToken = jwtProvider.createTestRefreshToken(member.getId(), shortExpirationSeconds);
        refreshTokenRepository.save(member.getId(), oldRefreshToken, shortExpirationSeconds);

        TokenRefreshRequest request = new TokenRefreshRequest(oldRefreshToken);

        // when
        TokenRefreshResponse response = authService.refreshAccessToken(request);

        // then
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertNotEquals(oldRefreshToken, response.getRefreshToken());

        String newSavedRefreshToken = refreshTokenRepository.findByMemberId(member.getId());
        assertEquals(response.getRefreshToken(), newSavedRefreshToken);
    }


    @Test
    void 리프레시토큰_유효기간_충분_시_액세스토큰만_재발급() {
        // given
        String refreshToken = jwtProvider.createRefreshToken(member.getId());
        refreshTokenRepository.save(member.getId(), refreshToken, 604800L); // 7일
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        // when
        TokenRefreshResponse response = authService.refreshAccessToken(request);

        // then
        assertNotNull(response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());

        // Redis의 리프레시 토큰이 그대로인지 확인
        String savedRefreshToken = refreshTokenRepository.findByMemberId(member.getId());
        assertEquals(refreshToken, savedRefreshToken);
    }

    @Test
    void 저장되지_않은_리프레시토큰으로_재발급_실패() {
        // given
        String refreshToken = jwtProvider.createRefreshToken(member.getId());
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.refreshAccessToken(request));
        assertEquals(AuthErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    }

    @Test
    void 유효하지_않은_리프레시토큰으로_재발급_실패() {
        // given
        TokenRefreshRequest request = new TokenRefreshRequest("invalid_token");

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.refreshAccessToken(request));
        assertEquals(AuthErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    }

    @Test
    void 다른_리프레시토큰_값으로_재발급_실패() {
        // given
        String savedRefreshToken = jwtProvider.createRefreshToken(member.getId());
        refreshTokenRepository.save(member.getId(), savedRefreshToken, 3600L);

        Member member2 = Member.createLocalMember(
                "test12345@example.com",
                passwordEncoder.encode("password123"),
                "test12345"
        );

        member2.verify();
        memberRepository.save(member2);

        String differentRefreshToken = jwtProvider.createRefreshToken(member2.getId());
        TokenRefreshRequest request = new TokenRefreshRequest(differentRefreshToken);

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.refreshAccessToken(request));
        assertEquals(AuthErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    }
}
