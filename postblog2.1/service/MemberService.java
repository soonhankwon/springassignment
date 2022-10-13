package com.assignment.postblog.service;

import com.assignment.postblog.dto.LoginMemberDto;
import com.assignment.postblog.dto.SignupRequestDto;
import com.assignment.postblog.exception.CustomException;
import com.assignment.postblog.exception.ErrorCode;
import com.assignment.postblog.model.Member;
import com.assignment.postblog.model.MemberRoleEnum;
import com.assignment.postblog.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public void registerMember(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        // 회원 ID 중복 확인
        Optional<Member> found = memberRepository.findByNickname(nickname);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다");
        }
        String password = passwordEncoder.encode(requestDto.getPassword());
        String passwordConfirm = passwordEncoder.encode(requestDto.getPasswordConfirm());
        // 사용자 ROLE 확인
        MemberRoleEnum role = MemberRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = MemberRoleEnum.ADMIN;
        }
        Member member = new Member(nickname, password, passwordConfirm, role);
        memberRepository.save(member);
    }
    // 로그인
    public Member login(LoginMemberDto loginMemberDto) {
        Member member = memberRepository.findByNickname(loginMemberDto.getNickname()).orElseThrow(
                () -> new CustomException(ErrorCode.NO_USER));
        if (!passwordEncoder.matches(loginMemberDto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.NO_USER);
        }
        return member;
    }
}
