package com.assignment.postblog.controller;

import com.assignment.postblog.dto.LoginMemberDto;
import com.assignment.postblog.dto.SignupRequestDto;
import com.assignment.postblog.jwt.JwtTokenProvider;
import com.assignment.postblog.model.Member;
import com.assignment.postblog.model.MemberRoleEnum;
import com.assignment.postblog.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired //why? autowired 사용
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    // 회원 로그인 페이지
    @GetMapping("/member/login") //로그인을 요청하면 동시에 JWT 토큰을 반들어서 반환해줘야함
    @ResponseBody
    public String login(LoginMemberDto loginMemberDto, HttpServletResponse response) {
        Member member = memberService.login(loginMemberDto);
        //전달받은 아이디와 패스워드를 가지고 실제 DB에 존재하는 유저인지 확인 후, Member 객체로 반환
        String nickname = member.getNickname();
        MemberRoleEnum role = member.getRole();
        //멤버의 닉네임과 권한을 추출해 토큰을 만들어 준다.
        String token = jwtTokenProvider.createToken(nickname, role);
        response.setHeader("JWT",token);
        //원래는 토큰을 커스텀헤더로 만들어서 프론트엔드로 전달해줘야함
        return token;
    }
//    {return "login";}

    // 회원 가입 페이지
    @GetMapping ("/member/signup")
    public String signup() {
        return "signup";
    }

    // 회원 가입 요청 처리
    @PostMapping("/member/signup")
    public String registerMember(@RequestBody SignupRequestDto requestDto) {
        memberService.registerMember(requestDto);
        return "redirect:/member/login";
    }
}
