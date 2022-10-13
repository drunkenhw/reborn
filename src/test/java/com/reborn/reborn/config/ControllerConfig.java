package com.reborn.reborn.config;

import com.reborn.reborn.member.domain.Member;
import com.reborn.reborn.member.domain.MemberRole;
import com.reborn.reborn.member.domain.repository.MemberRepository;
import com.reborn.reborn.security.domain.AuthToken;
import com.reborn.reborn.security.application.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import({RestDocsConfig.class})
@SpringBootTest
public class ControllerConfig extends AbstractContainerBaseTest {

    private AuthToken token;
    @Autowired
    private MemberRepository memberRepository;
    @Value("${spring.jwt.secret-key}")
    private String value;

    @BeforeEach
    void before() {
        memberRepository.deleteAll();
        Member member = getMember();
        memberRepository.save(member);
        TokenProvider tokenProvider = new TokenProvider(value);
        Date now = new Date();
        token = tokenProvider.createAuthToken("email@naver.com", MemberRole.USER, new Date(now.getTime()+100000000000L));
    }

    public String getToken(Member member) {
        Date now = new Date();
        return token.createToken("email@naver.com", member.getMemberRole(), new Date(now.getTime()+100000000000L));
    }

    public static Member getMember() {
        return Member.builder().email("email@naver.com").password("pass").nickname("nick").memberRole(MemberRole.USER).build();
    }
}
